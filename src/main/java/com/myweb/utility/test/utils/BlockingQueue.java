package com.myweb.utility.test.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BlockingQueue Implementation
 * 
 * @author Jegatheesh <br>
           Created on <b>31-Aug-2019</b>
 *
 * @param <T>
 */
public class BlockingQueue<T> {

	private Queue<T> queue = null;
	private int max = 0;
	private ReentrantLock lock = new ReentrantLock();
	private Condition notEmpty = lock.newCondition();
	private Condition notFull = lock.newCondition();

	public BlockingQueue(int size) {
		queue = new LinkedList<>();
		this.max = size;
	}

	public int size() {
		return queue.size();
	}

	public void put(T message) throws InterruptedException {
		lock.lock();
		try {
			while (size() == max) {
				notFull.await();
			}
			queue.add(message);
			// Notifying threads which is awaiting notEmpty condition
			notEmpty.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public T take() throws InterruptedException {
		lock.lock();
		try {
			while (size() == 0) {
				notEmpty.await();
			}
			T item = queue.remove();
			// Notifying threads which is awaiting notFull condition
			notFull.signalAll();
			return item;
		} finally {
			lock.unlock();
		}
	}

}
