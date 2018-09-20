package com.github.distribute.lock;

import java.util.concurrent.TimeUnit;

/**
 * 功能:分布式锁
 *
 * @author simon
 */
public interface DistributedLock {

    /**
     * 尝试获取锁,不进行等待
     *
     * @return 得到返回true
     * @throws Exception 异常
     */
    boolean tryLock() throws Exception;

    /**
     * 阻塞等待获取锁
     *
     * @throws Exception 异常
     */
    void lock() throws Exception;

    /**
     * 在规定时间内等待获取锁
     *
     * @param time 时间长度
     * @param unit 时间单位
     * @return 是否获取到锁
     * @throws Exception 异常
     */
    boolean lock(long time, TimeUnit unit) throws Exception;

    /**
     * 释放锁
     *
     * @throws Exception 异常
     */
    void unLock() throws Exception;

}
