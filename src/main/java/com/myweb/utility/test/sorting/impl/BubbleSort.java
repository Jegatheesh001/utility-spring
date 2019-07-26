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
		int temp = 0;
		for (int i = 0; i < arr.length; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] < arr[i]) {
					temp = arr[j];
					arr[j] = arr[i];
					arr[i] = temp;
				}
			}
		}
		return arr;
	}
	
	private Integer[] bestSort(Integer[] arr) {
		int i, temp = 0;
		boolean swapped = false;
		for (i = 0; i < arr.length; i++) {
			swapped = false;
			for (int j = 0; j < arr.length - (i + 1); j++) {
				if (arr[j] > arr[j + 1]) {
					temp = arr[j + 1];
					arr[j + 1] = arr[j];
					arr[j] = temp;
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
}
