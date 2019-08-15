package com.myweb.utility.test.problems;

/**
 * Linked Node Implementation
 * 
 * @author Jegatheesh <br>
           Created on <b>15-Aug-2019</b>
 *
 * @param <T>
 */
public class LinkedList<T> {
	private T value;
	private LinkedList<T> next;

	public LinkedList(T value) {
		this.value = value;
	}

	public LinkedList<T> setNext(LinkedList<T> next) {
		this.next = next;
		return next;
	}

	public String toString() {
		return value + " -> " + next;
	}

	public static void main(String[] args) {
		LinkedList<Integer> first = new LinkedList<>(2);
		first.setNext(new LinkedList<>(4)).setNext((new LinkedList<>(3)));
		LinkedList<String> second = new LinkedList<>("fg1");
		second.setNext(new LinkedList<>("fg2")).setNext((new LinkedList<>("fg3")));
		System.out.println(first);
		System.out.println(second);
	}
}
