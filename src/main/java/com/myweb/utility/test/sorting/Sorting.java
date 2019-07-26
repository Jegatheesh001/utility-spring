package com.myweb.utility.test.sorting;

import java.util.Arrays;

import com.myweb.utility.test.sorting.impl.BubbleSort;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Jul 26, 2019
 *
 */
public class Sorting {

	public static void main(String[] args) {
		Sort obj = new BubbleSort();
		Integer[] sorted = obj.sort(new Integer[] { 2, -1, 4, 3, 7 });
		Arrays.asList(sorted).stream().forEach(System.out::println);
	}

}
