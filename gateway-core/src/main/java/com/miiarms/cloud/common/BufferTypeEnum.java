package com.miiarms.cloud.common;

/**
 * netty处理请求的方式
 * <p>
 *  core: netty IO线程直接处理
 *  disruptor: disruptor环形队列缓冲
 *  MPMC：MPMC队列缓冲
 * </p>
 *
 * @author miiarms
 * @version 1.0
 * @date 2022/6/7 18:29
 */
public enum BufferTypeEnum {

    /**
     * 没有缓冲，直接netty的io线程处理
     **/
    DIRECT,

    /**
     * disruptor队列缓冲
     **/
    DISRUPTOR,

    /**
     * 自研mpmc队列缓冲
     **/
    MPMC;

}
