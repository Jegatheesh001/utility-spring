package com.myweb.utility.test.problems;

/**
 * Doubly Linked Node Implementation
 * 
 * @author Jegatheesh <br>
 *         Created on <b>16-Aug-2019</b>
 *
 * @param <T>
 */
public class DoublyLinkedList<T> {
	private T value;
	private DoublyLinkedList<T> prev;
	private DoublyLinkedList<T> next;

	public DoublyLinkedList(T value) {
		this.value = value;
	}

	public DoublyLinkedList<T> setNext(DoublyLinkedList<T> next) {
		next.prev = this;
		this.next = next;
		return next;
	}

	public String toString() {
		return (prev == null ? (null + " -> ") : "[" + prev.value + "]") + value + " -> " + next;
	}

	public static void main(String[] args) {
		DoublyLinkedList<Integer> first = new DoublyLinkedList<>(2);
		first.setNext(new DoublyLinkedList<>(4)).setNext((new DoublyLinkedList<>(3)));
		DoublyLinkedList<String> second = new DoublyLinkedList<>("fg1");
		second.setNext(new DoublyLinkedList<>("fg2")).setNext((new DoublyLinkedList<>("fg3")));
		System.out.println(first);
		System.out.println(second);
	}
}
