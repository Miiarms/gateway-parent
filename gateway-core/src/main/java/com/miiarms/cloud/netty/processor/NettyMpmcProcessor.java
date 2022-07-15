package com.miiarms.cloud.netty.processor;

import com.miiarms.cloud.config.GatewayProperties;
import com.miiarms.cloud.context.HttpRequestWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>主类名称：</B>NettyMpmcProcessor<BR>
 * <B>概要说明：</B>mpmc的核心实现处理器, 最终我们还是要使用NettyCoreProcessor<BR>
 * @author JiFeng
 * @since 2021年12月5日 下午10:13:33
 */
@Slf4j
public class NettyMpmcProcessor implements NettyProcessor {
	
	private GatewayProperties gatewayConfig;
	
	private NettyCoreProcessor nettyCoreProcessor;

	public NettyMpmcProcessor(GatewayProperties rapidConfig, NettyCoreProcessor nettyCoreProcessor, boolean usedExecutorPool) {
		this.gatewayConfig = rapidConfig;
		this.nettyCoreProcessor = nettyCoreProcessor;
	}
	
	@Override
	public void process(HttpRequestWrapper httpRequestWrapper) throws Exception {
		System.err.println("NettyMpmcProcessor put!");
	}

	@Override
	public void start() {
		this.nettyCoreProcessor.start();
	}

	@Override
	public void shutdown() {
		this.nettyCoreProcessor.shutdown();
	}
	

}
