package com.miiarms.cloud;

import static org.junit.Assert.assertTrue;

import com.miiarms.cloud.config.GatewayProperties;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue(){
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("application.yml");
        Yaml yaml = new Yaml();
        Object load = yaml.loadAs(resource, Properties.class);
        System.out.println(load);

        String dump = yaml.dump(new GatewayProperties());
        System.out.println(dump);


    }
}
