package com.myweb.utility.test.problems;

public class ShortestPalindrome {
	public String shortestPalindrome(String s) {
		if (isPalindrome(s)) {
			return s;
		} else {
			for (int i = 0; i < s.length(); i++) {
				String temp = (reverse(s.substring(s.length() - (i + 1), s.length()))).concat(s);
				if (isPalindrome(temp))
					return temp;
			}
		}
		return "-1";
	}

	boolean isPalindrome(String s) {
		System.out.print("isPalidrome: " + s);
		double max = Math.floor(s.length() / 2);
		double ex = s.length() % 2 == 0 ? 0 : 1;
		if (max > 0) {
			for (double i = 0; i < max; i++) {
				char a = s.charAt((int) (max + ex + i));
				char b = s.charAt((int) (max - i - 1));
				if (a != b) {
					System.out.println(" : " + a + "!=" + b);
					return false;
				}
			}
			System.out.println("");
		}
		return true;
	}

	String reverse(String s) {
		StringBuilder temp = new StringBuilder();
		for (int i = s.length() - 1; i >= 0; i--) {
			temp.append(s.charAt(i));
		}
		return temp.toString();
	}

	public static void main(String[] args) {
		String temp = "malayalam";
		System.out.println(new ShortestPalindrome().shortestPalindrome(temp));
	}
}
