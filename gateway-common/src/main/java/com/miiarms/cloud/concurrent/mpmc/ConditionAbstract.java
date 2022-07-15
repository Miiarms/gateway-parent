package com.miiarms.cloud.concurrent.mpmc;

import java.util.concurrent.locks.ReentrantLock;

/**
 * <B>主类名称：</B>ConditionAbstract<BR>
 * <B>概要说明：</B>use java sync to signal<BR>
 */
abstract class ConditionAbstract implements Condition {

    private final ReentrantLock queueLock = new ReentrantLock();

    private final java.util.concurrent.locks.Condition condition = queueLock.newCondition();

    /**
     * <B>方法名称：</B>awaitNanos<BR>
     * <B>概要说明：</B>wake me when the condition is satisfied, or timeout<BR>
     * @see Condition#awaitNanos(long)
     */
    @Override
    public void awaitNanos(final long timeout) throws InterruptedException {
        long remaining = timeout;
        queueLock.lock();
        try {
        	//	如果当前队列已经满了
            while(test() && remaining > 0) {
                remaining = condition.awaitNanos(remaining);
            }
        }
        finally {
            queueLock.unlock();
        }
    }

    /**
     * <B>方法名称：</B>await<BR>
     * <B>概要说明：</B>wake if signal is called, or wait indefinitely<BR>
     * @author  JiFeng
     * @since 2021年12月7日 上午11:22:24
     * @see Condition#await()
     */
    @Override
    public void await() throws InterruptedException {
        queueLock.lock();
        try {
            while(test()) {
                condition.await();
            }
        }
        finally {
            queueLock.unlock();
        }
    }

    /**
     * <B>方法名称：</B>signal<BR>
     * <B>概要说明：</B>tell threads waiting on condition to wake up<BR>
     * @author  JiFeng
     * @since 2021年12月7日 上午11:22:36
     * @see Condition#signal()
     */
    @Override
    public void signal() {
        queueLock.lock();
        try {
            condition.signalAll();
        }
        finally {
            queueLock.unlock();
        }

    }

}