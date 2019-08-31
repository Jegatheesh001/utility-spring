package com.myweb.utility.test.problems;

import java.io.IOException;

/**
 * Eidi gifts are a tradition in which children receive money from elder relatives during the Eid celebration.<br>
 *
 * Chef has three children (numbered 1,2,3) and he wants to give them Eidi gifts. 
 * The oldest child, Chefu, thinks that a distribution of money is fair only if an older child always receives more money than a younger child; 
 * if two children have the same age, they should receive the same amounts of money.
 * 
 * @author Jegatheesh <br>
           Created on <b>31-Aug-2019</b>
 * @link {@link https://www.codechef.com/LTIME75B/problems/EID2}
 *
 */
public class EidiGift {

	public static void main(String[] args) throws IOException {
		java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		// Number of test inputs
		String input = r.readLine();
		Integer count = 0;
		if (input != null)
			count = Integer.parseInt(input);
		// Accepting test inputs
		for (int i = 0; i < count; i++) {
			input = r.readLine();
			if (input != null && !input.trim().equals(""))
				System.out.println(isFair(input));
		}
	}

	private static String isFair(String input) {
		String[] nums = input.split(" ");
		int[] contents = new int[6];
		for (int i = 0; i < nums.length; i++) {
			contents[i] = Integer.parseInt(nums[i]);
		}
		boolean satisfied = (compareTo(contents[0], contents[1]) == compareTo(contents[3], contents[4]))
				&& (compareTo(contents[1], contents[2]) == compareTo(contents[4], contents[5]))
				&& (compareTo(contents[0], contents[2]) == compareTo(contents[3], contents[5]));
		return satisfied ? "FAIR" : "NOT FAIR";
	}

	private static int compareTo(int s1, int s2) {
		return s1 == s2 ? 0 : (s1 < s2 ? -1 : 1);
	}
}
