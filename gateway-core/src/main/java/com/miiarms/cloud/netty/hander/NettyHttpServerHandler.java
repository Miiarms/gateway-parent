package com.miiarms.cloud.netty.hander;

import com.miiarms.cloud.config.GatewayProperties;
import com.miiarms.cloud.context.HttpRequestWrapper;
import com.miiarms.cloud.netty.processor.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * netty http处理请求
 * @author Miiarms
 * @version 1.0
 * @date 2022/5/21
 */
@Slf4j
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    private final NettyProcessor nettyProcessor;
    private final GatewayProperties gatewayProperties;

    public NettyHttpServerHandler(GatewayProperties gatewayProperties, NettyProcessor nettyProcessor){
        this.nettyProcessor = nettyProcessor;
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

        log.info("got a request!");
        log.info(msg.toString());

        HttpRequestWrapper wrapper = new HttpRequestWrapper();
        wrapper.setCtx(ctx);
        wrapper.setFullHttpRequest(fullHttpRequest);

        //
    }
}
