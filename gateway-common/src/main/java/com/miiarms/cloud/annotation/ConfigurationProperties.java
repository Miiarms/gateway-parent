package com.miiarms.cloud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationProperties {

    /**
     * 前缀
     * @author miiarms
     * @date 2022/6/7 16:41
     **/
    String prefix() default "";
}
