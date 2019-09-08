package com.myweb.utility.test.problems;

/**
 * Multiply Two Strings
 * 
 * @author Jegatheesh <br>
 *         Created on <b>06-Sep-2019</b>
 *
 */
public class MultiplyStrings {

	public static void main(String[] args) {
		MultiplyStrings obj = new MultiplyStrings();
		System.out.println(obj.multiply("2", "12"));
		System.out.println(obj.multiply("21", "12"));
	}

	public String multiply(String num1, String num2) {
		if (num1 == null || num1.isEmpty() || num2 == null || num2.isEmpty())
			return "";
		int l1 = num1.length(), l2 = num2.length();
		int[] ret = new int[l1 + l2];
		for (int i = l1 - 1; i >= 0; --i) {
			for (int j = l2 - 1; j >= 0; --j) {
				ret[i + j + 1] += (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
			}
		}
		for (int i = ret.length - 1; i > 0; --i) {
			ret[i - 1] += ret[i] / 10;
			ret[i] %= 10;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ret.length; ++i) {
			if (sb.length() == 0 && ret[i] == 0)
				continue;
			sb.append(ret[i]);
		}
		return sb.length() == 0 ? "0" : sb.toString();
	}

}
