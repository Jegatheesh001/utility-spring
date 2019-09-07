package com.myweb.utility.test.sorting.impl;

import com.myweb.utility.test.sorting.Sort;

/**
 * Implementation of Insertion Sort
 * 
 * @author Jegatheesh<br>
 *         <b>Created</b> On Sep 7, 2019
 *
 */
public class InsertionSort implements Sort {

	@Override
	public Integer[] sort(Integer[] arr) {
		return impl(arr);
	}

	private Integer[] impl(Integer[] arr) {
		int temp = 0;
		for (int i = 1; i < arr.length - 1; i++) {
			temp = arr[i];
			for (int j = i - 1; j >= 0; j--) {
				if (arr[j] > temp) {
					arr[j + 1] = arr[j];
					arr[j] = null;
				} else {
					arr[j + 1] = temp;
					break;
				}
			}
			if (arr[0] == null) {
				arr[0] = temp;
			}
		}
		return arr;
	}

}
