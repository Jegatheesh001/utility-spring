package com.myweb.utility.test.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Experimenting CountDownLatch
 * 
 * @author Jegatheesh <br>
 *         Created on <b>08-Sep-2019</b>
 *
 */
public class CountDownLatchCheck {
	public static void main(String[] args) throws InterruptedException {
		List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch countDownLatch = new CountDownLatch(5);
		List<Thread> workers = Stream.generate(() -> new Thread(new Worker(outputScraper, countDownLatch))).limit(5)
				.collect(Collectors.toList());

		workers.forEach(Thread::start);
		// Wait until completion of process
		// countDownLatch.await();
		// Wait upto maximum of 3 seconds
		countDownLatch.await(3L, TimeUnit.SECONDS);
		outputScraper.add("Latch released");
		outputScraper.forEach(System.out::println);
		// Will print, if any count pending -> eg. if three process completed within
		// time limit, it will show 2
		System.out.println(countDownLatch.getCount());
	}
}

class Worker implements Runnable {
	private List<String> outputScraper;
	private CountDownLatch countDownLatch;

	public Worker(List<String> outputScraper, CountDownLatch countDownLatch) {
		this.outputScraper = outputScraper;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		outputScraper.add("Counted down");
		// Count downing after the completion of work
		countDownLatch.countDown();
	}
}
