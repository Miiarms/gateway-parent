package com.miiarms.cloud.concurrent.disruptor;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * disruptor封装
 * @author miiarms
 * @version 1.0
 * @date 2022/6/9 10:14
 */
public class DisruptorEventReactor<E> implements EventReactor<E>{

    private final Disruptor<Holder> disruptor;
    private RingBuffer<Holder> ringBuffer;
    private final EventListener<E> eventListener;
    private final EventTranslatorOneArg<Holder, E> eventTranslator = (holder, sequence, e) -> holder.setValue(e);


    public static <E> ReactorBuilder<E> builder(){
        return new ReactorBuilder<>();
    }

    private DisruptorEventReactor(ReactorBuilder<E> builder){
        eventListener = builder.listener;

        // 创建disruptor
        disruptor = new Disruptor<>(
                        new EventHolderFactory(),
                        builder.bufferSize,
                        new ThreadFactoryBuilder().setNameFormat(builder.workerNamePrefix + "-pool-%d").build(),
                        builder.producerType,
                        builder.waitStrategy);
        // 创建事件处理器
        @SuppressWarnings("unchecked")
        final EventHandler<Holder>[] workHandlers = new EventHandler[builder.workerThreads];
        for (int i = 0; i < builder.workerThreads; i++) {
            workHandlers[i] = new HolderWorkHandler();
        }

        disruptor.handleEventsWith(workHandlers);
        disruptor.setDefaultExceptionHandler(new HolderExceptionHandler());
    }


    @Override
    public void start() {
        ringBuffer = disruptor.start();
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
        ringBuffer = null;
    }

    @Override
    public void publishEvent(E event) {
        final RingBuffer<Holder> temp = ringBuffer;
        if(temp == null) {
            processException(this.eventListener, new IllegalStateException("disruptor is closed"), event);
            return;
        }
        ringBuffer.publishEvent(this.eventTranslator, event);
    }

    @Override
    public void publishEvent(E... events) {
        final RingBuffer<Holder> temp = ringBuffer;
        if(temp == null) {
            processException(this.eventListener, new IllegalStateException("disruptor is closed"), events);
            return;
        }
        try {
            ringBuffer.publishEvents(this.eventTranslator, events);
        } catch (NullPointerException e) {
            processException(this.eventListener, new IllegalStateException("disruptor is closed"), events);
        }
    }

    private static <E> void processException(EventListener<E> listener, Throwable e, E event) {
        listener.onException(e, -1, event);
    }

    @SafeVarargs
    private static <E> void processException(EventListener<E> listener, Throwable e, E... events) {
        if(events == null || events.length == 0){
            listener.onException(new IllegalArgumentException("publish event is null!"), -1, null);
            return;
        }
        for(E event : events) {
            processException(listener, e, event);
        }
    }


    @Override
    public boolean tryPublishEvent(E event) {
        final RingBuffer<Holder> temp = ringBuffer;
        if(temp == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvent(this.eventTranslator, event);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public  boolean tryPublishEvent(E... events) {
        final RingBuffer<Holder> temp = ringBuffer;
        if(temp == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvents(this.eventTranslator, events);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private class HolderWorkHandler implements EventHandler<Holder> {
        @Override
        public void onEvent(Holder holder, long sequence, boolean endOfBatch) throws Exception {
            eventListener.onEvent(holder.event);
            holder.setValue(null);
        }
    }
    private class HolderExceptionHandler implements ExceptionHandler<Holder> {

        @Override
        public void handleEventException(Throwable ex, long sequence, Holder holder) {
            try {
                eventListener.onException(ex, sequence, holder.event);
            }finally {
                holder.setValue(null);
            }
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }

    }

    public static interface EventListener<E> {

        void onEvent(E event) throws Exception;

        void onException(Throwable ex, long sequence, E event) ;
    }

    private class Holder {

        private E event;

        public void setValue(E event) {
            this.event = event;
        }

        public String toString() {
            return "Holder event=" + event;
        }

    }

    private class EventHolderFactory implements EventFactory<Holder> {
        @Override
        public Holder newInstance() {
            return new Holder();
        }
    }


    public static class ReactorBuilder<E> {

        private ProducerType producerType = ProducerType.MULTI;

        private int bufferSize = 16 * 1024;

        private int workerThreads = 1;

        private String workerNamePrefix = "disruptor-worker";

        private WaitStrategy waitStrategy = new BlockingWaitStrategy();

        //	消费者监听：
        private EventListener<E> listener;

        public ReactorBuilder<E> producerType(ProducerType producerType) {
            Preconditions.checkNotNull(producerType);
            this.producerType = producerType;
            return this;
        }

        public ReactorBuilder<E> workerThreads(int threads) {
            Preconditions.checkArgument(threads > 0);
            this.workerThreads = threads;
            return this;
        }

        public ReactorBuilder<E> bufferSize(int bufferSize) {
            Preconditions.checkArgument(Integer.bitCount(bufferSize) == 1);
            this.bufferSize = bufferSize;
            return this;
        }

        public ReactorBuilder<E> workerNamePrefix(String namePrefix) {
            Preconditions.checkNotNull(namePrefix);
            this.workerNamePrefix = namePrefix;
            return this;
        }

        public ReactorBuilder<E> waitStrategy(WaitStrategy waitStrategy) {
            Preconditions.checkNotNull(waitStrategy);
            this.waitStrategy = waitStrategy;
            return this;
        }

        public ReactorBuilder<E> eventListener(EventListener<E> listener) {
            Preconditions.checkNotNull(listener);
            this.listener = listener;
            return this;
        }

        public DisruptorEventReactor<E> build() {
            return new DisruptorEventReactor<>(this);
        }
    }
}
