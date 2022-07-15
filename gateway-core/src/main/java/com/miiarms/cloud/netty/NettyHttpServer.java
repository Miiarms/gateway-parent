package com.miiarms.cloud.netty;

import com.miiarms.cloud.common.GatewayLifeCycle;
import com.miiarms.cloud.config.GatewayProperties;
import com.miiarms.cloud.netty.hander.NettyHttpServerHandler;
import com.miiarms.cloud.netty.hander.NettyServerConnectManagerHandler;
import com.miiarms.cloud.netty.processor.NettyProcessor;
import com.miiarms.cloud.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * netty服务器流量入口
 * @author Miiarms
 * @version 1.0
 * @date 2022/5/21
 */
@Slf4j
public class NettyHttpServer implements GatewayLifeCycle {

    private final GatewayProperties gatewayConfig ;
    private final NettyProcessor nettyProcessor;

    private int port;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup eventLoopGroupBoss;
    private EventLoopGroup eventLoopGroupWork;

    public NettyHttpServer(GatewayProperties gatewayConfig, NettyProcessor nettyProcessor){
        this.gatewayConfig = gatewayConfig;
        this.nettyProcessor = nettyProcessor;
        if(gatewayConfig!=null && gatewayConfig.getPort() > 0 && gatewayConfig.getPort() < 65535) {
            this.port = gatewayConfig.getPort();
        }
        // 执行初始化
        init();
    }

    @Override
    public void init() {

        serverBootstrap = new ServerBootstrap();

        if(useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(gatewayConfig.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("NettyBossEpoll"));
            this.eventLoopGroupWork = new EpollEventLoopGroup(gatewayConfig.getEventLoopGroupWorkNum(),
                    new DefaultThreadFactory("NettyWorkEpoll"));
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(gatewayConfig.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("NettyBossNio"));
            this.eventLoopGroupWork = new NioEventLoopGroup(gatewayConfig.getEventLoopGroupWorkNum(),
                    new DefaultThreadFactory("NettyWorkNio"));
        }
    }


    @Override
    public void start() {

        this.serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWork)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                //启用心跳，双方TCP套接字建立连接后（即都进入ESTABLISHED状态），并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活，TCP会自动发送一个活动探测数据报文
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)   //	该参数的左右就是禁用Nagle算法，使用小数据传输时合并
                .childOption(ChannelOption.SO_SNDBUF, 65535)    //	设置发送数据缓冲区大小
                .childOption(ChannelOption.SO_RCVBUF, 65535)    //	设置接收数据缓冲区大小
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(gatewayConfig.getMaxContentLength()))
                                .addLast(new HttpServerExpectContinueHandler())
                                .addLast(new NettyServerConnectManagerHandler())
                                .addLast(new NettyHttpServerHandler(gatewayConfig, nettyProcessor));
                    }
                });

        try {
            this.serverBootstrap.bind(new InetSocketAddress(port)).sync();
            log.info("gateway Server Startup On Port: " + this.port + "......");
        } catch (Exception e) {
            throw new RuntimeException(String.format("server port[%s] bind fail!", port), e);
        }

    }

    @Override
    public void shutdown() {
        this.eventLoopGroupBoss.shutdownGracefully();
        this.eventLoopGroupBoss.shutdownGracefully();
    }

    /**
     * 判断是否支持EPoll
     * @author Miiarms
     * @date 2022/5/21 22:51
     * @return boolean
     */
    public boolean useEpoll() {
        return gatewayConfig.isUseEpoll() && RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    }

}
