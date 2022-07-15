package com.miiarms.cloud.netty.processor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.miiarms.cloud.concurrent.disruptor.DisruptorEventReactor;
import com.miiarms.cloud.config.GatewayProperties;
import com.miiarms.cloud.context.HttpRequestWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>主类名称：</B>NettyBatchEventProcessor<BR>
 * <B>概要说明：</B>flusher缓冲队列的核心实现, 最终调用的方法还是要回归到NettyCoreProcessor<BR>
 * @author JiFeng
 * @since 2021年12月5日 下午10:11:16
 */
@Slf4j
public class NettyBatchEventProcessor implements NettyProcessor {
	
	private final GatewayProperties gatewayConfig;
	
	private final NettyCoreProcessor nettyCoreProcessor;

	private final DisruptorEventReactor<HttpRequestWrapper> disruptorEventReactor;
	

	public NettyBatchEventProcessor(GatewayProperties gatewayConfig, NettyCoreProcessor nettyCoreProcessor) {
		this.gatewayConfig = gatewayConfig;
		this.nettyCoreProcessor = nettyCoreProcessor;
		this.disruptorEventReactor = DisruptorEventReactor.<HttpRequestWrapper>builder()
				.bufferSize(gatewayConfig.getBufferSize())
				.producerType((ProducerType.MULTI))
				.waitStrategy(new BlockingWaitStrategy())
				.workerThreads(gatewayConfig.getProcessThread())
				.eventListener(new HttpEventListener())
				.build();
	}

	@Override
	public void process(HttpRequestWrapper httpRequestWrapper) {
		log.debug("NettyBatchEventProcessor publish event:{}", httpRequestWrapper);
		disruptorEventReactor.publishEvent(httpRequestWrapper);
	}

	@Override
	public void start() {
		this.nettyCoreProcessor.start();
		this.disruptorEventReactor.start();
	}

	@Override
	public void shutdown() {
		this.nettyCoreProcessor.shutdown();
		this.disruptorEventReactor.shutdown();
	}

	public GatewayProperties getGatewayConfig() {
		return gatewayConfig;
	}


	private class HttpEventListener implements DisruptorEventReactor.EventListener<HttpRequestWrapper>{

		@Override
		public void onEvent(HttpRequestWrapper event) throws Exception {
			log.info("disruptor consume: {}", event);
			nettyCoreProcessor.process(event);
		}

		@Override
		public void onException(Throwable ex, long sequence, HttpRequestWrapper event) {
			log.error("disruptor consume exception, event:{}", event, ex);
		}
	}
}
