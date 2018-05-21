package com.github.distribute.lock.redis;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * redis�ֹ���ʵ�� 
 * @author linbingwen
 *
 */
public class OptimisticLockTest {

	public static void main(String[] args) throws InterruptedException {
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
			System.out.println("��" + i++ + "��������Ʒ��"+value + " ");
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
			cachedThreadPool.execute(new ClientThread(i));
		}
		cachedThreadPool.shutdown();
		
		while(true){  
	            if(cachedThreadPool.isTerminated()){  
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
class ClientThread implements Runnable {
	Jedis jedis = null;
	String key = "prdNum";// ��Ʒ����
	String clientList = "clientList";//// ��������Ʒ�Ĺ˿��б�����
	String clientName;

	public ClientThread(int num) {
		clientName = "���=" + num;
	}

	public void run() {
		try {
			Thread.sleep((int)(Math.random()*5000));// ���˯��һ��
		} catch (InterruptedException e1) {
		}
		while (true) {
			System.out.println("�˿�:" + clientName + "��ʼ����Ʒ");
			jedis = RedisUtil.getInstance().getJedis();
			try {
				jedis.watch(key);
				int prdNum = Integer.parseInt(jedis.get(key));// ��ǰ��Ʒ����
				if (prdNum > 0) {
					Transaction transaction = jedis.multi();
					transaction.set(key, String.valueOf(prdNum - 1));
					List<Object> result = transaction.exec();
					if (result == null || result.isEmpty()) {
						System.out.println("�����ˣ��˿�:" + clientName + "û��������Ʒ");// ������watch-key���ⲿ�޸ģ����������ݲ���������
					} else {
						jedis.sadd(clientList, clientName);// ������Ʒ��¼һ��
						System.out.println("�ø��ˣ��˿�:" + clientName + "������Ʒ");
						break;
					}
				} else {
					System.out.println("�����ˣ����Ϊ0���˿�:" + clientName + "û��������Ʒ");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				jedis.unwatch();
				RedisUtil.returnResource(jedis);
			}

		}
	}

}
