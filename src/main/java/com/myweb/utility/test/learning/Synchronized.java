package com.myweb.utility.test.learning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Synchronized block executions
 * 
 * @author Jegatheesh <br>
           Created on <b>31-Aug-2019</b>
 *
 */
public class Synchronized {
	
	private static int counter = 0;

	public void init() {
		counter = 0;
	}
	
	public void incrmt() {
		counter++;
	}
	
	public void inc() {
		synchronized (this) {
			counter++;
		}
	}

	public synchronized void increment() {
		counter++;
	}

	public static synchronized void doIncrement() {
		counter++;
	}
	
	public static void main(String[] args) throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 100; i++) {
			service.submit(() -> Synchronized.doIncrement());
		}
		service.shutdown();
		service.awaitTermination(1, TimeUnit.SECONDS);
		// No issues found
		System.out.println(counter);
		System.out.println(service.isTerminated());
		service = Executors.newFixedThreadPool(10);
		Synchronized obj = new Synchronized();
		obj.init();
		for (int i = 0; i < 100; i++) {
			service.submit(() -> obj.increment());
		}
		service.shutdown();
		service.awaitTermination(1, TimeUnit.SECONDS);
		// No issues found
		System.out.println(counter);
		System.out.println(service.isTerminated());
		service = Executors.newFixedThreadPool(10);
		obj.init();
		for (int i = 0; i < 100; i++) {
			service.submit(() -> new Synchronized().increment());
		}
		service.shutdown();
		service.awaitTermination(1, TimeUnit.SECONDS);
		// No issues found
		System.out.println(counter);
		System.out.println(service.isTerminated());
		service = Executors.newFixedThreadPool(10);
		obj.init();
		for (int i = 0; i < 1000; i++) {
			service.submit(() -> obj.inc());
		}
		service.shutdown();
		service.awaitTermination(2, TimeUnit.SECONDS);
		// No issues found
		System.out.println(counter);
		System.out.println(service.isTerminated());
		service = Executors.newFixedThreadPool(10);
		obj.init();
		for (int i = 0; i < 10000; i++) {
			service.submit(() -> obj.incrmt());
		}
		service.shutdown();
		service.awaitTermination(2, TimeUnit.SECONDS);
		// Issue found
		System.out.println(counter);
		System.out.println(service.isTerminated());
	}
	
}
