package com.myweb.utility.test.learning;

/**
 * Default method issue in interface method override
 * 
 * @author Jegatheesh <br>
 *         Created on <b>06-Sep-2019</b>
 *
 */
public class InterfaceDefaultOverride {

	public static void main(String[] args) {
		CAB cab = new CAB();
		cab.hello();
		CA ca = new CA();
		ca.hello();
		AB ab = new AB();
		ab.hello();
	}
}

interface A {
	default void hello() {
		System.out.println("Hello from A");
	}
}

interface B {
	default void hello() {
		System.out.println("Hello from B");
	}
}

class C {
	public void hello() {
		System.out.println("Hello from C");
	}
}

class CA extends C implements A {
	// class method have the highest priority
}

class CAB extends C implements A, B {
	// class method have the highest priority
}

class AB implements A, B {

	/**
	 * Must have to override
	 */
	@Override
	public void hello() {
		// If method is non static have to call super to access the method
		A.super.hello(); // To use any of Parent interface method
		B.super.hello();
		// Or you can write own implementation
	}
}
