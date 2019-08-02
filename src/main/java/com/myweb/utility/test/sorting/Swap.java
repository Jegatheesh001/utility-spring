package com.myweb.utility.test.sorting;

/**
 * Generic Implementation for swapping Objects in Array
 * 
 * @author Jegatheesh <br>
 *         <b>Created</b> On Aug 02, 2019
 *
 */
public interface Swap {

	default <T> void swap(T[] array, int left, int right) {
		T temp = array[right];
		array[right] = array[left];
		array[left] = temp;
	}
}
