package com.miiarms.cloud.concurrent.mpmc;

/**
 * <B>主类名称：</B>ConcurrentQueue<BR>
 * <B>概要说明：</B>A very high performance blocking buffer, based on Disruptor approach to queues<BR>
 */
public interface ConcurrentQueue<E> {

    boolean offer(E e);

    E poll();

    E peek();

    int size();

    int capacity();

    boolean isEmpty();

    boolean contains(Object o);

    int remove(E[] e);

    void clear();
    
}