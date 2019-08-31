package com.myweb.utility.test.learning;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock behaviors
 * 
 * @author Jegatheesh <br>
 *         Created on <b>31-Aug-2019</b>
 *
 */
public class ReentrantLockCheck {
	// Default fairness is false
	// If fairness is false, if any thread is trying to acquire lock at that time, will acquire the lock first (this will increase the speed) 
	ReentrantLock lock = new ReentrantLock();
	// If fairness is true, long time waited thread will acquire the lock first (this will decrease the speed, since it is checking queue)
	// lock = new ReentrantLock(true);
	
	public void doSomeTask() {
		// try to acquire the lock, if other thread is accessing the resource, it will wait
		lock.lock();
		try {
			// doing some IO operations
		} finally {
			// make sure to release the lock, if exceptions occur while processing
			lock.unlock();
		}
	}
	
	public void doOtherTask() throws InterruptedException {
		// Try lock will allow us to specify, how long it can wait before acquiring lock
		boolean acquired = lock.tryLock(5, TimeUnit.SECONDS); // fairness of lock object
		// try to acquire the lock, but it wont wait
		// boolean acquired = lock.tryLock();
		// For above case, even though the fairness of lock is true, will be act as Non-fair policy.
		// To enable fairness, should use => lock.tryLock(0, TimeUnit.SECONDS);
		if (acquired) {
			try {
				// doing some IO operations
			} finally {
				// make sure to release the lock, if exceptions occur while processing
				lock.unlock();
			}
		} else {
			// since resource is busy, do some other operations
		}
	}
	
	public void acquireCondition() throws InterruptedException {
		// Using condition we can check for particular condition to satisfy
		// Please check BlockingQueue Implementation
		lock.lock();
		Condition newCondition = lock.newCondition();
		newCondition.await();
		lock.unlock();
	}
	
	public static void main(String[] args) {
		// 
	}
}
