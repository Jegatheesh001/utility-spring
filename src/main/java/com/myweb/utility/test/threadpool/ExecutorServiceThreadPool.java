package com.myweb.utility.test.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * ExecutorService
 * 
 * @author Jegatheesh <br>
 *         Created on <b>30-Aug-2019</b>
 *
 */
public class ExecutorServiceThreadPool {

	public static void main(String[] args) throws Exception {
		LongAdder count = new LongAdder();
		Runnable task =  () -> count.increment();
		Callable<Long> task1 = () -> 100l;
		
		// Fixed thread pool -> n number of threads -> here n is 10
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		// Through execute we can submit runnable task
		executorService.execute(task);
		//== Common methods for ExecutorService
		// Through submit we can submit runnable/callable task
		executorService.submit(task1);
		// will just tell the executor service that it can't accept new tasks, but the already submitted tasks continue to run
		executorService.shutdown();
		// will do the same AND will try to cancel the already submitted tasks by interrupting the relevant threads -> will set thread interrupt flag
		executorService.shutdownNow();
		// Main thread will wait for mentioned time
		executorService.awaitTermination(100, TimeUnit.SECONDS);
		
		// SingleThreadExecutor -> one thread
		ExecutorService executorService1 = Executors.newSingleThreadExecutor();
		executorService1.submit(task);
		
		// ScheduledThreadPool -> Schedule with initial and intermediate delays
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
		// will execute after 1 second delay
		scheduledExecutor.schedule(task, 1, TimeUnit.SECONDS);
		// will execute after 1 second delay and for every 10 seconds irrespective of task completion
		scheduledExecutor.scheduleAtFixedRate(task, 1, 10, TimeUnit.SECONDS);
		// will execute after 1 second delay and for every 10 seconds after task completion
		scheduledExecutor.scheduleWithFixedDelay(task, 1, 10, TimeUnit.SECONDS);
		
		// CachedThreadPool -> 0 to n number of threads
		// New thread will be created, if no thread available in pool to reuse
		// Created Thread will be removed after 60seconds, if no longer in use
		ExecutorService cachedExcecutor = Executors.newCachedThreadPool();
		cachedExcecutor.submit(task);
	}
}
