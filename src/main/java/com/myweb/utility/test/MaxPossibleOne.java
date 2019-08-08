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
		findPossibleOnes(3, new int[] { 1, 1, 1 });
		findPossibleOnes(8, new int[] { 0, 0, 1, 1, 1, 0, 1, 0 });
	}

	public static int findPossibleOnes(int size, int[] arr) {
		int maxBound = 0;
		int start = -1;
		int end = -1;
		int ones = 0;
		// bound
		int[] current = new int[] { -1, -1 };
		int[] max = new int[] { -1, -1 };
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == 0) {
				end++;
				if (start == -1) {
					start++;
					current[0] = i;
				}
			} else {
				if (end != -1) {
					current[1] = i - 1;
					int diff = end - start + 1;
					if (diff > maxBound) {
						maxBound = diff;
						max[0] = current[0];
						max[1] = current[1];
					}
					start = -1;
					end = -1;
				}
			}
		}
		if (end != -1) {
			current[1] = arr.length - 1;
			int diff = end - start + 1;
			if (diff > maxBound) {
				maxBound = diff;
				max[0] = current[0];
				max[1] = current[1];
			}
			start = -1;
			end = -1;
		}
		System.out.print("Size: " + arr.length + " -> ");
		// No zeros
		if (max[0] == -1) {
			maxBound = arr.length;
		} else {
			int index = max[0] - 1;
			while (index >= 0) {
				if (arr[index] == 0)
					break;
				ones++;
				index--;
			}
			index = max[1] + 1;
			while (index < arr.length) {
				if (arr[index] == 0)
					break;
				ones++;
				index++;
			}
		}
		System.out.print(max[0] + ", " + max[1] + " -> ");
		System.out.println(ones + maxBound);
		return ones + maxBound;
	}

	// Fail Attempts
	public static int findPossibleOnesF1(int size, int[] arr) {
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
