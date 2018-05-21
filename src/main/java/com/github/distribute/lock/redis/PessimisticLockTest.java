package com.github.distribute.lock.redis;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

public class PessimisticLockTest {

	public static void main(String[] args) {
		long starTime=System.currentTimeMillis();
		
		initPrduct();
		initClient();
		printResult();
		 
		long endTime=System.currentTimeMillis();
		long Time=endTime-starTime;
		System.out.println("��������ʱ�䣺 "+Time+"ms");   
	}

	/**
	 * ������
	 */
	public static void printResult() {
		Jedis jedis = RedisUtil.getInstance().getJedis();
		Set<String> set = jedis.smembers("clientList");

		int i = 1;
		for (String value : set) {
			System.out.println("��" + i++ + "��������Ʒ��" + value + " ");
		}

		RedisUtil.returnResource(jedis);
	}

	/*
	 * ��ʼ���˿Ϳ�ʼ����Ʒ
	 */
	public static void initClient() {
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		int clientNum = 10000;// ģ��ͻ���Ŀ
		for (int i = 0; i < clientNum; i++) {
			cachedThreadPool.execute(new PessClientThread(i));
		}
		cachedThreadPool.shutdown();

		while (true) {
			if (cachedThreadPool.isTerminated()) {
				System.out.println("���е��̶߳������ˣ�");
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ʼ����Ʒ����
	 */
	public static void initPrduct() {
		int prdNum = 100;// ��Ʒ����
		String key = "prdNum";
		String clientList = "clientList";// ��������Ʒ�Ĺ˿��б�
		Jedis jedis = RedisUtil.getInstance().getJedis();

		if (jedis.exists(key)) {
			jedis.del(key);
		}

		if (jedis.exists(clientList)) {
			jedis.del(clientList);
		}

		jedis.set(key, String.valueOf(prdNum));// ��ʼ��
		RedisUtil.returnResource(jedis);
	}

}

/**
 * �˿��߳�
 * 
 * @author linbingwen
 *
 */
class PessClientThread implements Runnable {
	String key = "prdNum";// ��Ʒ����
	String clientList = "clientList";// // ��������Ʒ�Ĺ˿��б�����
	String clientName;
	RedisBasedDistributedLock redisBasedDistributedLock;
	Jedis jedis = null;

	public PessClientThread(int num) {
		clientName = "���=" + num;
		init();
	}

	public void init() {
		jedis = RedisUtil.getInstance().getJedis();
		redisBasedDistributedLock = new RedisBasedDistributedLock(jedis, "lock.lock", 5 * 1000);
	}

	public void run() {
		try {
			Thread.sleep((int) (Math.random() * 5000));// ���˯��һ��
		} catch (InterruptedException e1) {
		}

		while (true) {
			//���жϻ����Ƿ�����Ʒ
			if(Integer.valueOf(jedis.get(key))<= 0) {
				break;
			}
			
			//���滹����Ʒ��ȡ������Ʒ��Ŀ��ȥ1
			System.out.println("�˿�:" + clientName + "��ʼ����Ʒ");
			if (redisBasedDistributedLock.tryLock(3,TimeUnit.SECONDS)) { //�ȴ�3���ȡ�������򷵻�false
				int prdNum = Integer.valueOf(jedis.get(key)); //�ٴ�ȡ����Ʒ������Ŀ
				if (prdNum > 0) {
					jedis.decr(key);//��Ʒ����1
					jedis.sadd(clientList, clientName);// ������Ʒ��¼һ��
					System.out.println("�ø��ˣ��˿�:" + clientName + "������Ʒ");
				} else {
					System.out.println("�����ˣ����Ϊ0���˿�:" + clientName + "û��������Ʒ");
				}
				redisBasedDistributedLock.unlock();
				break;
			}
		}
		//�ͷ���Դ
		redisBasedDistributedLock = null;
		RedisUtil.returnResource(jedis);
	}

}
