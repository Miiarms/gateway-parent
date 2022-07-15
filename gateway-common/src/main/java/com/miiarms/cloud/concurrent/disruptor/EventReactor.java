package com.miiarms.cloud.concurrent.disruptor;

/**
 * disruptor 封装
 * @author miiarms
 * @version 1.0
 * @date 2022/6/9 10:06
 */
public interface EventReactor<E> {

    void start();

    void shutdown();

    /**
     * 发布事件
     * @author miiarms
     * @date 2022/6/9 10:10
     **/
    void publishEvent(E event);

    /**
     * 发布多个事件
     * @author miiarms
     * @date 2022/6/9 10:10
     **/
    void publishEvent(E... events);

    /**
     * 发布事件
     * 对应RingBuffer.tryPublishEvent
     * @author miiarms
     * @date 2022/6/9 10:10
     **/
    boolean tryPublishEvent(E event);

    boolean tryPublishEvent(E... events);
}
