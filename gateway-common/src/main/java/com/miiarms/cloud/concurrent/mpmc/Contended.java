package com.miiarms.cloud.concurrent.mpmc;

/**
 * <B>主类名称：</B>Contended<BR>
 * <B>概要说明：</B>Linux Intel CacheLine Size 64<BR>
 */
public class Contended {

    public static final int CACHE_LINE = Integer.getInteger("Intel.CacheLineSize", 64); // bytes

}
