package com.miiarms.cloud.config;

import com.miiarms.cloud.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 配置加载
 * <p>
 *     网关配置加载规则：优先级顺序如下：高的优先级会覆盖掉低的优先级
 * 		运行参数(最高) ->  jvm参数  -> 环境变量  -> 配置文件  -> 内部 GateWayProperties 对象的默认属性值(最低);
 * </p>
 * @author Miiarms
 * @version 1.0
 * @date 2022/5/21
 */
@Slf4j
public class GatewayConfigLoader {
    private GatewayConfigLoader(){}

    private static final String CONFIG_ENV_PREFIEX = "miifast_";

    private static final String CONFIG_JVM_PREFIEX = "miifast.";

    private static final String CONFIG_FILE = "miifast-gateway.properties";

    private static GatewayConfigLoader INSTANCE= new GatewayConfigLoader();

    public static GatewayConfigLoader getInstance(){
        return INSTANCE;
    }

    /**
     *
     * 按照配置优先级加载
     * @param args 运行时参数
     * @author Miiarms
     * @date 2022/5/21 22:09
     * @return com.miiarms.cloud.config.GatewayProperties
     */
    public GatewayProperties load(String args[]){

        GatewayProperties gatewayConfig = new GatewayProperties();

        // 1. 配置文件
        InputStream ins = GatewayConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if(ins != null) {
            Properties properties = new Properties();
            try {
                properties.load(ins);
                PropertiesUtils.properties2Object(properties, gatewayConfig);
            } catch (IOException e) {
                log.error("#GatewayConfigLoader# load config file: {} error!", CONFIG_FILE, e);
            } finally {
                if(ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        log.warn("close config loader stream error!", e);
                    }
                }
            }
        }

        // 2. 环境变量
        Map<String, String> env = System.getenv();
        Properties properties = new Properties();
        properties.putAll(env);
        PropertiesUtils.properties2Object(properties, gatewayConfig, CONFIG_ENV_PREFIEX);

        // 3. jvm : 例如-Dport=8088
        Properties systemProperties = System.getProperties();
        PropertiesUtils.properties2Object(systemProperties, gatewayConfig, CONFIG_JVM_PREFIEX);

        // 4. 运行参数: --xxx=xxx --enable=true  --port=1234
        if(args != null && args.length > 0) {
            Properties runtimePro = new Properties();
            for(String arg : args) {
                if(arg.startsWith("--") && arg.contains("=")) {
                    runtimePro.put(arg.substring(2, arg.indexOf("=")), arg.substring(arg.indexOf("=") + 1));
                }
            }
            PropertiesUtils.properties2Object(runtimePro, gatewayConfig);
        }

        return gatewayConfig;
    }


}
