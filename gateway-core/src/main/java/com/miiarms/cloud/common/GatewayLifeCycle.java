package com.miiarms.cloud.common;

/**
 * 网关组件生命周期抽象
 * @author Miiarms
 * @version 1.0
 * @date 2022/5/21
 */
public interface GatewayLifeCycle {

    /**
     * 初始化
     * @author Miiarms
     * @date 2022/5/21 22:23
     */
    void init();

    /**
     * 启动
     * @author Miiarms
     * @date 2022/5/21 22:23
     * @return void
     */
    void start();

    /**
     * 关闭
     * @author Miiarms
     * @date 2022/5/21 22:24
     * @return void
     */
    void shutdown();
}
