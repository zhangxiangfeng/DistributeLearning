package com.github.distribute.lock;

import java.util.concurrent.TimeUnit;

/**
 * ����:�ֲ�ʽ��
 *
 * @author simon
 */
public interface DistributedLock {

    /**
     * ���Ի�ȡ��,�����еȴ�
     *
     * @return �õ�����true
     * @throws Exception �쳣
     */
    boolean tryLock() throws Exception;

    /**
     * �����ȴ���ȡ��
     *
     * @throws Exception �쳣
     */
    void lock() throws Exception;

    /**
     * �ڹ涨ʱ���ڵȴ���ȡ��
     *
     * @param time ʱ�䳤��
     * @param unit ʱ�䵥λ
     * @return �Ƿ��ȡ����
     * @throws Exception �쳣
     */
    boolean lock(long time, TimeUnit unit) throws Exception;

    /**
     * �ͷ���
     *
     * @throws Exception �쳣
     */
    void unLock() throws Exception;

}
