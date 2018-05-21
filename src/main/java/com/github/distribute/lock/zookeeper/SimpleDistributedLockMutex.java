package com.github.distribute.lock.zookeeper;
//package com.github.distribute.zookeeper;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
//import org.I0Itec.zkclient.ZkClient;
//
//import com.github.distribute.lock.DistributedLock;
//
///**
// * ���ӿڵľ���ʵ�֣���Ҫ�����ڼ̳еĸ���BaseDistributedLock��ʵ�ֵĽӿڷ��� �ø����ǻ���Zookeeperʵ�ֲַ�ʽ���ľ���ϸ��ʵ��
// */
//public class SimpleDistributedLockMutex extends BaseDistributedLock implements DistributedLock {
//	/*
//	 * ���ڱ���Zookeeper��ʵ�ֲַ�ʽ���Ľڵ㣬������Ϊlocker��/locker��
//	 * �ýڵ�Ӧ���ǳ־ýڵ㣬�ڸýڵ����洴����ʱ˳��ڵ���ʵ�ֲַ�ʽ��
//	 */
//	private final String basePath;
//
//	/*
//	 * ������ǰ׺��locker�´�����˳��ڵ����綼��lock-��ͷ���������ڹ����޹ؽڵ�
//	 * ����������Ľڵ����ƣ�lock-00000001��lock-000000002
//	 */
//	private  static final String LOCK_NAME="lock-";
//
//	/* ���ڱ���ĳ���ͻ�����locker���洴���ɹ���˳��ڵ㣬���ں�����ز���ʹ�ã����жϣ� */
//	private String ourLockPath;
//
//	/**
//	 * ���ڻ�ȡ����Դ��ͨ������Ļ�ȡ����������ȡ��
//	 * 
//	 * @param time��ȡ���ĳ�ʱʱ��
//	 * @param unit
//	 *            time��ʱ�䵥λ
//	 * @return�Ƿ��ȡ����
//	 * @throws Exception
//	 */
//	private boolean internalLock(long time, TimeUnit unit) throws Exception {
//		// ���ourLockPath��Ϊ������Ϊ��ȡ������������ʵ��ϸ�ڼ�attemptLock��ʵ��
//		ourLockPath = attemptLock(time, unit);
//		return ourLockPath != null;
//	}
//
//	/**
//	 * ����Zookeeper�ͻ������Ӷ��󣬺�basePath
//	 * 
//	 * @param client
//	 *            Zookeeper�ͻ������Ӷ���
//	 * @param basePath
//	 *            basePath��һ���־ýڵ�
//	 */
//	public SimpleDistributedLockMutex(ZkClient client, String basePath) {
//		/*
//		 * ���ø���Ĺ��췽����Zookeeper�д���basePath�ڵ㣬����ΪbasePath�ڵ��ӽڵ�����ǰ׺
//		 * ͬʱ����basePath�����ø���ǰ������
//		 */
//		super(client, basePath, LOCK_NAME);
//		this.basePath = basePath;
//	}
//
//	/** ��ȡ����ֱ����ʱ����ʱ���׳��쳣 */
//	public void acquire() throws Exception {
//		// -1��ʾ�����ó�ʱʱ�䣬��ʱ��Zookeeper����
//		if (!internalLock(-1, null)) {
//			throw new IOException("���Ӷ�ʧ!��·��:'" + basePath + "'�²��ܻ�ȡ��!");
//		}
//	}
//
//	/**
//	 * ��ȡ�������г�ʱʱ��
//	 */
//	public boolean acquire(long time, TimeUnit unit) throws Exception {
//		return internalLock(time, unit);
//	}
//
//	/** �ͷ��� */
//	public void release() throws Exception {
//		releaseLock(ourLockPath);
//	}
//}