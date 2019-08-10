package com.myweb.utility.test.patterns.structural;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tree like pattern <br>
 * Calling parent will subsequently trigger children
 * 
 * @author Jegatheesh <br>
 *         Created on <b>10-Aug-2019</b>
 *
 */
public class CompositeDesignPatternImpl {

	public static void main(String[] args) {
		Leaf monitor = new Leaf("Monitor", 5000);
		Leaf mouse = new Leaf("Mouse", 500);
		Leaf processor = new Leaf("Intel7", 20000);
		Leaf ram = new Leaf("12GB", 10000);
		Composite cpu = new Composite("CPU");
		cpu.add(processor).add(ram);
		Composite computer = new Composite("Computer");
		computer.add(cpu).add(monitor).add(mouse);
		computer.getPrice();
	}

}

interface Component {
	int getPrice();
}

class Leaf implements Component {
	private String name;
	private int amount;

	public Leaf(String name, int amount) {
		this.name = name;
		this.amount = amount;
	}

	@Override
	public int getPrice() {
		System.out.println("Price of " + name + " is " + amount);
		return amount;
	}
}

class Composite implements Component {
	private String name;
	private List<Component> components;

	public Composite(String name) {
		this.name = name;
		components = new ArrayList<>();
	}

	@Override
	public int getPrice() {
		AtomicInteger amount = new AtomicInteger(0);
		components.stream().forEach(e -> amount.getAndAdd(e.getPrice()));
		System.out.println("Price of " + name + " is " + amount.get());
		return amount.get();
	}

	// Builder pattern
	public Composite add(Component component) {
		components.add(component);
		return this;
	}
}
