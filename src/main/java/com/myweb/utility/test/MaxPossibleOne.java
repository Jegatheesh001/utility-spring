package com.myweb.utility.test;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Aug 06, 2019
 *
 */
public class MaxPossibleOne {
	public static void main(String[] args) {
		findPossibleOnes(4, new int[] { 0, 1, 0, 0 });
		findPossibleOnes(5, new int[] { 1, 0, 1, 1, 1 });
	}

	public static int findPossibleOnes(int size, int[] arr) {
		int maxBound = 0;
		int start = -1;
		int end = -1;
		int ones = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == 0) {
				end++;
				if (start == -1)
					start++;
			} else {
				ones++;
				if (end != -1) {
					int diff = end - start + 1;
					if (diff > maxBound) {
						maxBound = diff;
					}
					start = -1;
					end = -1;
				}
			}
		}
		if (end != -1) {
			int diff = end - start + 1;
			if (diff > maxBound) {
				maxBound = diff;
			}
			start = -1;
			end = -1;
		}
		System.out.println(ones + maxBound);
		return ones + maxBound;
	}
}
