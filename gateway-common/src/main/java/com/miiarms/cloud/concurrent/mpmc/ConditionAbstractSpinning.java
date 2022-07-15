package com.miiarms.cloud.concurrent.mpmc;

/**
 * <B>主类名称：</B>ConditionAbstractSpinning<BR>
 * <B>概要说明：</B>阻塞的自旋锁抽象类<BR>
 */
public abstract class ConditionAbstractSpinning implements Condition {

	/**
	 * <B>方法名称：</B>awaitNanos<BR>
	 * <B>概要说明：</B>on spinning waiting breaking on test and expires > timeNow<BR>
	 * @see Condition#awaitNanos(long)
	 */
    @Override
    public void awaitNanos(final long timeout) throws InterruptedException {
        long timeNow = System.nanoTime();
        final long expires = timeNow+timeout;

        final Thread t = Thread.currentThread();

        while(test() && expires > timeNow && !t.isInterrupted()) {
            timeNow = System.nanoTime();
            Condition.onSpinWait();
        }

        if(t.isInterrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * <B>方法名称：</B>await<BR>
     * <B>概要说明：</B>on spinning waiting breaking on test<BR>
     * @see Condition#await()
     */
    @Override
    public void await() throws InterruptedException {
        final Thread t = Thread.currentThread();

        while(test() && !t.isInterrupted()) {
            Condition.onSpinWait();
        }

        if(t.isInterrupted()) {
            throw new InterruptedException();
        }
    }

    @Override
    public void signal() {

    }
}
