package com.miiarms.cloud.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

/**
 * netty Http请求包装类
 * @author Miiarms
 * @version 1.0
 * @date 2022/5/21
 */
@Data
public class HttpRequestWrapper {

    private ChannelHandlerContext ctx;

    private FullHttpRequest fullHttpRequest;
}
