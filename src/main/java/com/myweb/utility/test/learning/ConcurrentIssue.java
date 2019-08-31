package com.myweb.utility.test.learning;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concurrency variable issue check with ThreadLocal
 * 
 * @author Jegatheesh <br>
 *         Created on <b>24-Aug-2019</b>
 *
 */
public class ConcurrentIssue {

	public static void main(String[] args) {
		AtomicInteger id = new AtomicInteger(0);
		for (int i = 0; i < 10; i++) {
			new Thread(() -> IssueMaker.process(id.getAndIncrement())).start();
		}
	}

}

class IssueMaker {
	private static Integer ID;
	private static ThreadLocal<Integer> CONCURRENT_ID = new ThreadLocal<>();;

	public void init(Integer id) {
		ID = id;
		CONCURRENT_ID.set(id);
	}

	public static void process(Integer id) {
		IssueMaker obj = new IssueMaker();
		obj.init(id);
		System.out.println(Thread.currentThread() + " Original Passed: " + id);
		System.out.println(Thread.currentThread() + " Context Variable: " + ID);
		System.out.println(Thread.currentThread() + " Thread Variable: " + CONCURRENT_ID.get());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread() + " Original Passed: " + id);
		System.out.println(Thread.currentThread() + " Thread Variable: " + CONCURRENT_ID.get());
	}
}
