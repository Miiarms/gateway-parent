package com.miiarms.cloud.core;

import com.miiarms.cloud.common.GatewayLifeCycle;
import com.miiarms.cloud.common.BufferTypeEnum;
import com.miiarms.cloud.config.GatewayProperties;
import com.miiarms.cloud.netty.NettyHttpServer;
import com.miiarms.cloud.netty.processor.NettyBatchEventProcessor;
import com.miiarms.cloud.netty.processor.NettyCoreProcessor;
import com.miiarms.cloud.netty.processor.NettyMpmcProcessor;
import com.miiarms.cloud.netty.processor.NettyProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * 网关容器类
 * @author Miiarms
 * @version 1.0
 * @date 2022/5/21
 */
@Slf4j
public class GateWayContainer implements GatewayLifeCycle {

    private final GatewayProperties gatewayConfig;

    private NettyProcessor nettyProcessor;
    private NettyHttpServer nettyHttpServer;

    public GateWayContainer(GatewayProperties gatewayConfig){
        this.gatewayConfig =  gatewayConfig;
        init();
    }


    @Override
    public void init() {
        NettyCoreProcessor nettyCoreProcessor = new NettyCoreProcessor();

        BufferTypeEnum bufferType = gatewayConfig.getBufferType();
        if(BufferTypeEnum.MPMC == bufferType){
            nettyProcessor = new NettyMpmcProcessor(gatewayConfig, nettyCoreProcessor, false);
        }else if(BufferTypeEnum.DISRUPTOR == bufferType){
            nettyProcessor = new NettyBatchEventProcessor(gatewayConfig, nettyCoreProcessor);
        }else {
            nettyProcessor = nettyCoreProcessor;
        }

        // 初始化netty服务
        this.nettyHttpServer = new NettyHttpServer(gatewayConfig, nettyProcessor);
    }

    @Override
    public void start() {
        nettyProcessor.start();
        nettyHttpServer.start();
        log.info("gateway container started !");
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
        nettyProcessor.shutdown();
    }
}
