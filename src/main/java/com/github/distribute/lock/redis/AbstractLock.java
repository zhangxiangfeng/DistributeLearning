package com.github.distribute.lock.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * ���ĹǼ�ʵ��, �����Ļ�ȡ���Ĳ���������ȥʵ��.
 *
 * @author simon
 */
public abstract class AbstractLock implements Lock {

    /**
     * �����費��Ҫ��֤�ɼ���ֵ������, ��Ϊ�Ƿֲ�ʽ����,
     * <p>
     * 1.ͬһ��jvm�Ķ���߳�ʹ�ò�ͬ����������ʵҲ�ǿ��Ե�, ��������²���Ҫ��֤�ɼ���
     * 2.ͬһ��jvm�Ķ���߳�ʹ��ͬһ��������, �ǿɼ��Ժ�������ͱ���Ҫ��֤��.
     */
    protected volatile boolean locked;

    /**
     * ��ǰjvm�ڳ��и������߳�(if have one)
     */
    private Thread exclusiveOwnerThread;

    @Override
    public void lock() {
        try {
            lock(false, 0, null, false);
        } catch (InterruptedException e) {
            // TODO ignore
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock(false, 0, null, true);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        try {
            System.out.println("ghggggggggggggg");
            return lock(true, time, unit, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("" + e);
        }
        return false;
    }

    public boolean tryLockInterruptibly(long time, TimeUnit unit) throws InterruptedException {
        return lock(true, time, unit, true);
    }

    @Override
    public void unlock() {
        // TODO ��鵱ǰ�߳��Ƿ������
        if (Thread.currentThread() != getExclusiveOwnerThread()) {
            throw new IllegalMonitorStateException("current thread does not hold the lock");
        }

        unlock0();
        setExclusiveOwnerThread(null);
    }

    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

    protected void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    protected abstract void unlock0();

    /**
     * ����ʽ��ȡ����ʵ��
     *
     * @param useTimeout
     * @param time
     * @param unit
     * @param interrupt  �Ƿ���Ӧ�ж�
     * @return
     * @throws InterruptedException
     */
    protected abstract boolean lock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt)
            throws InterruptedException;

}