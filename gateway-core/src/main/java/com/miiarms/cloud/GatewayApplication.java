package com.miiarms.cloud;

import com.miiarms.cloud.config.GatewayConfigLoader;
import com.miiarms.cloud.config.GatewayProperties;
import com.miiarms.cloud.core.GateWayContainer;

/**
 *
 * 网关启动类
 * @author Miiarms
 * @date 2022/5/21 21:54
 */
public class GatewayApplication {

    public static void main( String[] args ) {

        // 1. 加载配置
        GatewayProperties gatewayProperties = GatewayConfigLoader.getInstance().load(args);

        // 2. 插件初始化的工作

        // 3. 初始化服务注册管理中心（服务注册管理器）, 监听动态配置的新增、修改、删除

        // 4. 启动容器
        GateWayContainer container = new GateWayContainer(gatewayProperties);
        container.start();

        // 注册服务停止钩子
        Runtime.getRuntime().addShutdownHook(new Thread(container::shutdown));
    }
}
