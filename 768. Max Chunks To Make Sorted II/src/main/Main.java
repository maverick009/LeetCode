package main;

// https://leetcode.com/problems/max-chunks-to-make-sorted-ii/discuss/298511/Java-Solution-beats-99.9-with-detailed-example-and-explanation

/* For an element, if all the numbers to its left are smaller or equal to all the numbers to its right, 
 * we can always insert a split point between the element and its next element which separate the array into two parts 
 * and after we sort the two parts individually, the concatenation of them will be a sorted array.
 * maxLeft[i] includes the i-th number itself but minRight[i +1] only counts the numbers to its right
 * 
 */

class Solution {
	// Time 0(N) Space 0(N)
    public int maxChunksToSorted(int[] arr) {
    	int ans = 0, max = Integer.MIN_VALUE;
    	if(arr == null || arr.length == 0)
    		return 0;
    	int n = arr.length;
    	int []lmax = new int[n];
    	int []rmin = new int[n];
    	
    	lmax[0] = arr[0];
    	for(int i = 1;i < n;i++) {
    		lmax[i] = Math.max(lmax[i-1], arr[i]);
    	}
    	
    	rmin[n-1] = arr[n-1];
    	for(int i = n-2;i >= 0;i--) {
    		rmin[i] = Math.min(rmin[i+1], arr[i]);
    	}
    	
    	for(int i = 0;i < n-1;i++) {
    		if(lmax[i] <= rmin[i+1])
    			ans++;
    	}
    	
    	return ans+1;
    }
}

public class Main {
	public static void main(String[] args) {
		//int []arr = {2,1,3,4,4};
		int []arr = {1,1,0,0,1};
		System.out.println(new Solution().maxChunksToSorted(arr));
	}
}