package com.myweb.utility.test.problems;

/**
 * The <b>atoi</b> function skips all white-space characters at the beginning of the string, 
 * converts the subsequent characters as part of the number, 
 * and then stops when it encounters the first character that isn't a number.
 * 
 * @link {@link https://leetcode.com/problems/string-to-integer-atoi/}
 * @author Jegatheesh <br>
 *         Created on <b>16-Aug-2019</b>
 *
 */
public class ATOI {

	public int myAtoi(String str) {
		if (str != null && str.trim().length() > 0) {
			str = str.trim();
			{
				StringBuilder num = new StringBuilder("");
				int index = 0;
				Character c = str.charAt(index);
				boolean exists = false;
				boolean last = false, current = false;
				while (isChar(c) || isNum(c)) {
					if (!exists && isNum(c))
						exists = true;
					current = isChar(c);
					if ((current && current == last) || (exists && current))
						break;
					last = current;
					num.append(c);
					if (index < str.length() - 1) {
						index++;
						c = str.charAt(index);
					} else {
						break;
					}
				}
				Double result = 0.0;
				if (exists) {
					result = Double.parseDouble(num.toString());
				}
				return result.intValue();
			}
		}
		return 0;
	}

	boolean isNum(Character c) {
		return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8'
				|| c == '9';
	}

	boolean isChar(Character c) {
		return c == '-' || c == '+';
	}

	public static void main(String[] args) {
		ATOI obj = new ATOI();
		System.out.println(obj.myAtoi("   -121d"));
		System.out.println(obj.myAtoi("   -138 1"));
		System.out.println(obj.myAtoi("--121"));
	}

}
