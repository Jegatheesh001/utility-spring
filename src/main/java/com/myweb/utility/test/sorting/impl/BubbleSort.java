package com.myweb.utility.test.sorting.impl;

import com.myweb.utility.test.sorting.Sort;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Jul 26, 2019
 *
 */
public class BubbleSort implements Sort {

	@Override
	public Integer[] sort(Integer[] arr) {
		return bestSort(arr);
	}

	private Integer[] normalSort(Integer[] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] < arr[i]) {
					swap(arr, i, j);
				}
			}
		}
		return arr;
	}
	
	private Integer[] bestSort(Integer[] arr) {
		boolean swapped = false;
		for (int i = 0; i < arr.length; i++) {
			swapped = false;
			for (int j = 0; j < arr.length - (i + 1); j++) {
				if (arr[j] > arr[j + 1]) {
					swap(arr, j, j + 1);
					swapped = true;
				}
			}
			if (!swapped) {
				System.out.println("Breaks at level: " + (i + 1));
				break;
			}
		}
		return arr;
	}
	
	private Integer[] anotherImpl(Integer[] arr) {
		boolean swapped = true;
		while (swapped) {
			swapped = false;
			for (int j = 0; j < arr.length - 1; j++) {
				if (arr[j] > arr[j + 1]) {
					swap(arr, j, j + 1);
					swapped = true;
				}
			}
		}
		return arr;
	}
}
