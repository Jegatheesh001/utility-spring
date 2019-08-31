package com.myweb.utility.test.problems;

import java.util.Scanner;

import com.myweb.utility.test.utils.BlockingQueue;

/**
 * Producer Consumer Implementation
 * 
 * @author Jegatheesh <br>
 *         Created on <b>31-Aug-2019</b>
 */
public class ProducerConsumer {

	BlockingQueue<String> queue = new BlockingQueue<>(16);

	public void produce(String message) {
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void consume() {
		try {
			while (true) {
				System.out.println("Consuming Message: " + queue.take());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ProducerConsumer obj = new ProducerConsumer();
		obj.queue.put("Welcome!!!");
		new Thread(() -> obj.consume()).start();

		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("Enter message: ");
			String message = scanner.nextLine();
			obj.produce(message);
			Thread.sleep(1000);
		}
	}

}
