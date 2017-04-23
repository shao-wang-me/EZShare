import java.awt.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class Whatever {

	/*
	 * #3 Longest Substring Without Repeating Characters Given a string, find
	 * the length of the longest substring without repeating characters.
	 */
	public static int lengthOfLongestSubstring(String s) {
		int[] count = new int[256];
		int maxLength = 0, currentLength = 0, startIndex = 0, endIndex = 0;
		for (int i = 0; i < s.length(); i++) {
			int index = s.charAt(i);
			count[index]++;
			if (count[index] == 1) {
				endIndex = i;
				currentLength = endIndex - startIndex + 1;
				maxLength = Math.max(currentLength, maxLength);
			} else {
				while (count[index] > 1) {
					count[s.charAt(startIndex)]--;
					startIndex++;
				}
			}
		}
		return maxLength;
	}

	/*
	 * #4 Median of Two Sorted Arrays There are two sorted arrays nums1 and
	 * nums2 of size m and n respectively. Find the median of the two sorted
	 * arrays.
	 */
	public static double findMedianSortedArrays(int[] nums1, int[] nums2) {

		if (nums1.length == 0) {
			if (nums2.length % 2 == 0) {
				return (nums2[nums2.length / 2] + nums2[nums2.length / 2 - 1]) / 2.0;
			} else {
				return (double) nums2[nums2.length / 2];
			}
		}
		if (nums2.length == 0) {
			if (nums1.length % 2 == 0) {
				return (nums1[nums1.length / 2] + nums1[nums1.length / 2 - 1]) / 2.0;
			} else {
				return (double) nums1[nums1.length / 2];
			}
		}
		while (true) {

		}
	}

	/*
	 * #5 Longest Palindromic Substring Given a string s, find the longest
	 * palindromic substring in s. You may assume that the maximum length of s
	 * is 1000.
	 */
	public static String longestPalindrome(String s) {
		return s;
	}

	/*
	 * #6 ZigZag Conversion
	 */
	public static String convert(String s, int numRows) {
		if (numRows == 1) {
			return s;
		}
		String result = "";
		int row = numRows;
		int column = ((s.length() / (row * 2 - 2)) + 1) * (row - 1);
		char[][] chr = new char[row][column];
		for (int i = 0; i < row; i++) {
			// initialize the two-dimension array of char
			for (int j = 0; j < column; j++) {
				chr[i][j] = ' ';
			}
		}
		for (int index = 0; index < s.length(); index++) {
			int numBlocks = index / (2 * numRows - 2);
			int remainder = index % (2 * numRows - 2);
			int deltX = remainder - numRows + 1;
			int curRow;
			if (deltX > 0) {
				curRow = numRows - 1 - deltX;
			} else {
				curRow = numRows - 1 + deltX;
			}
			deltX = deltX > 0 ? deltX : 0;
			int curColumn = (numRows - 1) * numBlocks + deltX;
			chr[curRow][curColumn] = s.charAt(index);
		}
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				if (chr[i][j] != ' ') {
					result = result + chr[i][j];
				}
			}
		}
		return result;
	}

	/*
	 * #7 Reverse Integer Reverse digits of an integer.
	 */
	public static int reverse(int x) {
		int result = 0, signal = 1;
		if (x == 0 || x == -2147483648) {
			return 0;
		}
		if (x < 0) {
			signal = -1;
			x = Math.abs(x);
		}
		final int MAX_INT = 214748364;
		while (x > 9) {
			int remainder = x % 10;
			result = result * 10 + remainder;
			x = x / 10;
		}
		if (result > MAX_INT) {
			return 0;
		} else {
			return (result * 10 + x) * signal;
		}
	}

	/*
	 * #9 Palindrome Number Determine whether an integer is a palindrome. Do
	 * this without extra space. Hint:negative numbers are never palindrome.
	 */
	public static boolean isPalindrome(int x) {
		int num = x;
		if (num < 0) {
			return false;
		}
		int res = 0;
		while (num > 0) {
			int remainder = num % 10;
			res = res * 10 + remainder;
			num = num / 10;
		}
		if (x == res) {
			return true;
		}
		return false;
	}

	/*
	 * #11 Container With Most Water Given n non-negative integers a1, a2, ...,
	 * an, where each represents a point at coordinate (i, ai). n vertical lines
	 * are drawn such that the two end points of line i is at (i, ai) and (i,
	 * 0). Find two lines, which together with x-axis forms a container, such
	 * that the container contains the most water.
	 */
	public static int maxArea(int[] height) {
		int maxArea = 0;
		int maxLine = height[0];
		int minLine = Math.min(height[0], height[height.length - 1]);
		for (int i : height) {
			if (i > maxLine) {
				maxLine = i;
			}
		}
		if (maxLine == Math.max(height[0], height[height.length - 1])) {
			return minLine * (height.length - 1);
		}
		return 0;
	}

	/*
	 * #18 4Sum
	 * Given an array S of n integers, are there elements a, b, c, and d in 
     * Such that a + b + c + d = target? Find all unique quadruplets in the 
     * array which gives the sum of target.
	 */
    public static ArrayList<ArrayList<Integer>> fourSum(int[] nums, int target) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        for (int i = 15; i <= 15 << (nums.length - 4); i++){
        	int num = i, numOfOnes = 0, index = -1, sum = 0;
        	ArrayList<Integer> cur = new ArrayList<Integer>();
        	while (num != 0){
        		int remainder = num % 2;
        		index ++;
        		if (remainder == 1){
        			numOfOnes ++;
        			sum += nums[index];
        			cur.add(nums[index]);
        		}
        		num = num / 2;
        	}
        	if (numOfOnes == 4 && sum == target){
        		result.add(cur);
        	}
        }
        return result;
    }
    
	/*
	 * #19 Remove Nth Node From End of List Given a linked list, remove the nth
	 * node from the end of list and return its head.
	 */
	public static ListNode removeNthFromEnd(ListNode head, int n) {
		if (n == 0){
			return head;
		}
		if(head.next == null){
			return null;
		}
		ListNode currentNode = head;
		int totalNode = 0;
		while (currentNode != null) {
			totalNode++;
			currentNode = currentNode.next;
		}
		int position = totalNode - n;
		currentNode = head;
		for (int i = 0; i < position - 1; i++) {
			currentNode = currentNode.next;
		}
		if (n == 1) {
			currentNode.next = null;
		} else if (n == totalNode){
			head = head.next;
		} else {
			currentNode.next = currentNode.next.next;
		}
		return head;
	}

	/*
	 * #20 Valid Parentheses Given a string containing just the characters '(',
	 * ')', '{', '}', '[' and ']', determine if the input string is valid.
	 */
	public static boolean isValid(String s) {
		LinkedList<Integer> res = new LinkedList<Integer>();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '(' || ch == '{' || ch == '[') {
				res.push((int) ch);
			} else {
				if (res.isEmpty()) {
					return false;
				}
				int chr = res.pop();
				switch (chr) {
				case '(':
					if (ch != ')')
						return false;
					break;
				case '[':
					if (ch != ']')
						return false;
					break;
				case '{':
					if (ch != '}')
						return false;
					break;
				}
			}
		}
		if (res.isEmpty())
			return true;
		return false;
	}

	/*
	 * #420 Strong Password Checker
	 */
    public static int strongPasswordChecker(String s) {
    	return 0;
    }
	
	
	public static void printList(ListNode l){
		while (l.next != null){
			System.out.print(l.val + "->");
			l  = l.next;
		}
		System.out.println(l.val);
	}
	public static void main(String[] args) {
		ListNode l1 = new ListNode(1);
		ListNode l2 = new ListNode(2);
		ListNode l3 = new ListNode(3);
		ListNode l4 = new ListNode(4);
		ListNode l5 = new ListNode(5);
	
		int[] s = {1, 0, -1, 0, -2, 2};
		ArrayList<ArrayList<Integer>> l = fourSum(s, 0);
		System.out.println(l.toArray());
	}
}