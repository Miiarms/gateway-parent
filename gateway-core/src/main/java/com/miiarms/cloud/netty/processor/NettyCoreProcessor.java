package com.miiarms.cloud.netty.processor;


import com.miiarms.cloud.context.HttpRequestWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>主类名称：</B>NettyCoreProcessor<BR>
 * <B>概要说明：</B>核心流程的主执行逻辑<BR>
 * @author JiFeng
 * @since 2021年12月5日 下午9:51:34
 */
@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

	@Override
	public void process(HttpRequestWrapper event) {
		FullHttpRequest request = event.getFullHttpRequest();
		ChannelHandlerContext ctx = event.getCtx();
		try {
			//	1. 解析FullHttpRequest, 把他转换为我们自己想要的内部对象：Context
			System.err.println("-----接收到请求----------");
			//	2. 执行整个的过滤器逻辑：FilterChain
			
		} catch (Throwable t) {
			log.error("process http request fail!", t);
		}
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
