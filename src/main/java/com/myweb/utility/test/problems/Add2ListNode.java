package com.myweb.utility.test.problems;

/**
 * Add2ListNode
 * 
 * @link https://leetcode.com/problems/add-two-numbers/submissions/
 * 
 * @author Jegatheesh <br>
 *         Created on <b>16-Aug-2019</b>
 *
 */
public class Add2ListNode {
	/**
	 * Definition for singly-linked list.
	 */
	public static class ListNode {
		int val;
		ListNode next;

		ListNode(int x) {
			val = x;
		}
		
		/**
		 * Not from the problem, (Created to see the values)
		 */
		public String toString() {
			return val + " -> " + next;
		}
	}
	
	/**
	 * Improved version
	 * 
	 * Runtime: 2 ms	Memory: 45.2 MB
	 * @param l1
	 * @param l2
	 * @return
	 */
	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		ListNode sum = new ListNode(0);
		ListNode prev = sum, p1 = l1, p2 = l2;
		int carry = 0;
		while (p1 != null || p2 != null || carry > 0) {
			// System.out.print("In: " + (p1 != null ? p1.val : -1) + "&" + (p2 != null ? p2.val : -1) + " Carry: " + carry);
			if (p2 != null) {
				carry += p2.val;
				p2 = p2.next;
			}
			if (p1 != null) {
				carry += p1.val;
				p1 = p1.next;
			}
			prev.val = carry % 10;
			carry = carry / 10;
			System.out.println(" Out: " + prev.val);
			if (p1 != null || p2 != null || carry > 0) {
				prev.next = new ListNode(0);
				prev = prev.next;
			}
		}
		return sum;
	
	}
	
	/**
	 * Runtime Improved version
	 * 
	 * Runtime: 46 ms	Memory: 46.8 MB
	 * @param l1
	 * @param l2
	 * @return
	 */
	public ListNode addTwoNumbersA2(ListNode l1, ListNode l2) {
		ListNode sum = new ListNode(0);
		ListNode prev = sum, p1 = l1, p2 = l2;
		int carry = 0;
		int tot = 0;
		while (p1 != null || p2 != null || carry > 0) {
			// System.out.print("In: " + (p1 != null ? p1.val : -1) + "&" + (p2 != null ? p2.val : -1) + " Carry: " + carry);
			if (p2 != null) {
				tot += p2.val;
				p2 = p2.next;
			}
			if (p1 != null) {
				tot += p1.val;
				p1 = p1.next;
			}
			carry = tot / 10;
			prev.val = tot % 10;
			System.out.println(" Out: " + prev.val);
			if (p1 != null || p2 != null || carry > 0) {
				prev.next = new ListNode(0);
				prev = prev.next;
				tot = carry;
			}
		}
		return sum;
	}

	/**
	 * 
	 * Runtime: 48 ms	Memory: 46.2 MB
	 * @param l1
	 * @param l2
	 * @return
	 */
	public ListNode addTwoNumbersA1(ListNode l1, ListNode l2) {
		ListNode sum = new ListNode(0);
		ListNode prev = sum, p1 = l1, p2 = l2;
		int carry = 0;
		int tot = 0;
		while (p1 != null || p2 != null) {
			if (p2 == null) {
				p2 = new ListNode(0);
			}
			if (p1 == null) {
				p1 = new ListNode(0);
			}
			tot = p1.val + p2.val + carry;
			carry = tot / 10;
			prev.val = tot % 10;
			// System.out.println("In: " + p1.val + "&" + p2.val + " Out: " + prev.val);
			p1 = p1.next;
			p2 = p2.next;
			if (p1 != null || p2 != null || carry != 0) {
				prev.next = new ListNode(0);
				prev = prev.next;
			}
		}
		while (carry > 0) {
			prev.val = carry % 10;
			carry = carry / 10;
			if (carry > 0) {
				prev.next = new ListNode(0);
				prev = prev.next;
			}
		}
		return sum;
	}

	public static void main(String[] args) {
		ListNode l1 = null, l2 = null;
		int[] val = new int[] { 2, 4, 3 };
		l1 = new ListNode(val[0]); 
		l1.next = new ListNode(val[1]);
		l1.next.next = new ListNode(val[2]);
		val = new int[] { 5, 6, 4 };
		l2 = new ListNode(val[0]); 
		l2.next = new ListNode(val[1]);
		l2.next.next = new ListNode(val[2]);
		System.out.println("Input: " + l1 + " + " + l2);
		System.out.println("Output: " + new Add2ListNode().addTwoNumbers(l1, l2));
		val = new int[] { 1, 8 };
		l1 = new ListNode(val[0]); 
		l1.next = new ListNode(val[1]);
		l2 = new ListNode(0);
		System.out.println("Input: " + l1 + " + " + l2);
		System.out.println("Output: " + new Add2ListNode().addTwoNumbers(l1, l2));
	}
}
