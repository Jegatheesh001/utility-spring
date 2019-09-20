package com.myweb.utility.test.sorting.impl;

import com.myweb.utility.test.sorting.Sort;

/**
 * Implementation of Merge Sort
 * 
 * @author Jegatheesh<br>
 *         <b>Created</b> On Sep 20, 2019
 *
 */
public class MergeSort implements Sort {

	@Override
	public Integer[] sort(Integer[] arr) {
		return mergeSort(arr, 0, arr.length - 1);
	}

	private Integer[] mergeSort(Integer[] arr, int start, int end) {
		if (start < end) {
			int mid = (start + end) / 2;
			mergeSort(arr, start, mid);
			mergeSort(arr, mid + 1, end);
			merge(arr, start, mid, end);
		}
		return arr;
	}

	private void merge(Integer[] arr, int start, int mid, int end) {
		// create a temp array
		int temp[] = new int[end - start + 1];

		// crawlers for both intervals and for temp
		int i = start, j = mid + 1, k = 0;

		// traverse both arrays and in each iteration add smaller of both elements in
		// temp
		while (i <= mid && j <= end) {
			if (arr[i] <= arr[j]) {
				temp[k] = arr[i];
				i += 1;
			} else {
				temp[k] = arr[j];
				j += 1;
			}
			k += 1;
		}

		// add elements left in the first interval
		while (i <= mid) {
			temp[k] = arr[i];
			k += 1;
			i += 1;
		}

		// add elements left in the second interval
		while (j <= end) {
			temp[k] = arr[j];
			k += 1;
			j += 1;
		}

		// copy temp to original interval
		for (i = start; i <= end; i += 1) {
			arr[i] = temp[i - start];
		}
	}
}
