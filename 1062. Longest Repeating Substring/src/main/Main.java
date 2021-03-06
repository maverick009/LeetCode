package main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// https://www.youtube.com/watch?v=OptoHwC3D-Y
// https://stackoverflow.com/questions/10355103/finding-the-longest-repeated-substring
// https://www.geeksforgeeks.org/suffix-array-set-2-a-nlognlogn-algorithm/
// https://www.geeksforgeeks.org/%C2%AD%C2%ADkasais-algorithm-for-construction-of-lcp-array-from-suffix-array/
// http://web.stanford.edu/class/cs97si/suffix-array.pdf
// https://www.youtube.com/watch?v=OptoHwC3D-Y
class Solution {
	
	// Time 0(N lg^2(N)) 
	class Suffix implements Comparable<Suffix> {
		/*
		 *  index -> start index of this suffix original index of suffix in this string
		 *  rank -> rank of this suffix
		 *  next ->
		 *  rank of next adjacent character, i.e., the rank of character at str[i + 1] 
		 *  (This is needed to sort the suffixes according to first 2 characters). 
		 *  If a character is last character, we store next rank as -1
		 */
		int rank, index, next;
		
		public Suffix(int index, int rank, int next) {
			this.index = index;
			this.next = next;
			this.rank = rank;
		}

		@Override
		public int compareTo(Suffix o) {
			// sort according to rank then rank of next character(starting point for next suffix)
			return this.rank != o.rank?this.rank - o.rank: this.next - o.next;
		}
	}
	
	public int[] suffixarray(String s) {
		int n = s.length();
		Suffix []suffix = new Suffix[n];
		// use of a unique character to assign initial rank
		// next rank is initialized to 0
		for(int i = 0;i < n;i++) {
			suffix[i] = new Suffix(i, s.charAt(i) - '$', 0);
		}
		// store next to rank of next character(starting point of next suffix)
		for(int i = 0;i < n;i++) {
			suffix[i].next = i+1<n?suffix[i+1].rank:-1;
		}
		
		Arrays.sort(suffix);
		// Till this part it was sorting using first two characters of suffix
		// Now from length = 4
		/* Assign new ranks to all suffixes. To assign new ranks, we consider the sorted suffixes one by one. 
		 * Assign 0 as new rank to first suffix. For assigning ranks to remaining suffixes, 
		 * we consider rank pair of suffix just before the current suffix. 
		 * If previous rank pair of a suffix is same as previous rank of suffix just before it, then assign it same rank. 
		 * Otherwise assign rank of previous suffix plus one.
		 * 
		 * For eg
		 * Index       Suffix          Rank       
  			5          a               0     [Assign 0 to first]        
  			1          anana           1     (0, 13) is different from previous
  			3          ana             1     (0, 13) is same as previous     
  			0          banana          2     (1, 0) is different from previous      
  			2          nana            3     (13, 0) is different from previous      
  			4          na              3     (13, 0) is same as previous  
  		 * first rank = 0;
  		 * second pair = (0,13) different to prev rank i.e thus ++rank i.e 1
  		 * third pair (0,13 same thus rank = 1
  		 * 4th pair (1,0) different from (0,13) thus rank = ++rank = 2
  		 * 5th (13,0) different from (1,0) = 3 and so on
  		 * 
  		 * if there's no str[i+2] -1
  		 * array according to present rank and next rank
		 */
		
		// temporary array to hold indices of suffixes during process
		int []ind = new int[n];
		
		for(int len = 4;len < n*2;len*=2) {
			int rank = 0, prev = suffix[0].rank;
			//assign first rank = 0
			suffix[0].rank = rank;
			// wherever suffix[0] exited mind its sorted now
			// assign its new index = 0
			ind[suffix[0].index] = 0;
			
			for(int i = 1;i < n;i++) {
				// compare the current pair with earlier pair 
				if(suffix[i].rank == prev && suffix[i].next == suffix[i-1].next) {
					prev = suffix[i].rank;
					suffix[i].rank = rank;
				} else {
					// pair are different
					prev = suffix[i].rank;
					suffix[i].rank = ++rank;
				}
				ind[suffix[i].index] = i;
			}
			// Assign next rank to every suffix
			// take original index of suffix i.e starting character of suffix + add how far counter have moved
			// since it moved in multiple of two next char will at index + length/2
			// check if it exists if not -1 else ind[nextP] which stores temporary rank during process 
			for(int i = 0;i < n;i++) {
				int nextP = suffix[i].index + len/2;
				suffix[i].next = nextP < n?suffix[ind[nextP]].rank:-1;
			}
			Arrays.sort(suffix);
		}
		
		int []res = new int[n];
		for(int i = 0;i < n;i++) {
			res[i] = suffix[i].index;
		}
		//print(s, res);
		//kasai(s, res);
		return res;
	}
	
	void print(String s, int []res) {
		for(int i : res)
			System.out.println(i + " " + s.substring(i));
	}
	
	private int[] kasai(String s, int []sa) {
		int n = sa.length;
		int []lcp = new int[n];
		/* An auxiliary array to store inverse of suffix array 
		 * elements. For example if suffixArr[0] is 5, the 
		 * invSuff[5] would store 0.  This is used to get next 	
		 * suffix string from suffix array.
		 * 
		 */
		int []inv = new int[n];
		for(int i = 0;i < n;i++)
			inv[sa[i]] = i;
		
		// Initialize length of previous LCP 
		int len = 0;
		/* If the current suffix is at n-1, then we don’t 
		 * have next substring to consider. So lcp is not defined for this substring, we put zero. 
		 */
		for(int i = 0;i < n;i++) {
			if(inv[i] == n-1) {
				len = 0;
				continue;
			}
			
			/* j contains index of the next substring to 
	           be considered  to compare with the present 
	           substring, i.e., next string in suffix array */
			int j = sa[inv[i] + 1];
			
			// Directly start matching from k'th index as 
	        // at-least k-1 characters will match 
			while(i + len < n && j + len < n && s.charAt(i+len) == s.charAt(j+len)) {
				len++;
			}
			// lcp for the present suffix. 
			lcp[inv[i]] = len;
			// Deleting the starting character from the string.
			/* other way could be to start from 0th index of both string and match
			 * time complexity would be higher 0(N^2)
			 * since we know lcp[i+1] would be minimum (len-1) where len is lcp[i]
			 * we start from i+len and j+len to make it constant time
			 */
			if(len > 0)
				len--;
		}
		/*for(int i = 0;i < n;i++)
			System.out.println(lcp[i] + ":" + sa[i] + ":" + s.substring(sa[i]));*/
		
		return lcp;
	}

	public int longestRepeatingSubstring(String S) {
		int n = S.length();
		int []sa = suffixarray(S);
		int []lcp = kasai(S, sa);
		int ans = 0;
		for(int i : lcp)
			ans = Math.max(ans, i);
		return ans;
	}
	
	/* starts with 1 character, finds set containing that maxcount = 1
	 * start from beginning, clear set length 2, finds maxcount = 2 then do length 3 
	 * return maxcount
	 */
	public int longestRepeatingSubstring1(String s) {
        Set<String> known = new HashSet<>();
        int maxCount = 0;
        
        int i = 0;
        while(i <  s.length()) {
            int j = i + maxCount + 1;
            if (j > s.length()) return maxCount;
            String x = s.substring(i, j);
            
            if (known.contains(x)) {
                maxCount++;
                known.clear();
                i = 0;
                continue;
            } else {
                known.add(x);
                i++;
            }            
        }
        
        return maxCount;
    }
	
	// store all suffixes
    public int longestRepeatingSubstring2(String S) {
    	// string -> frequency
    	Map<String, Integer> map = new HashMap<>();
    	int n = S.length();
    	for(int i = 0;i < n;i++) {
    		for(int j = i+2;j <= n;j++) {
    			String str = "";
    			if(j > n) {
    				str = S.substring(i);
    			} else {
    				str = S.substring(i, j);
    			}
    			map.put(str, map.getOrDefault(str, 0) + 1);
    		}
    	}
    	
    	int ans = 0, freq = 1;
    	for(String str : map.keySet()) {
    		if(map.get(str) > freq) {
    			ans = str.length();
    			freq = map.get(str);
    		}
    	}
    	return ans;
    }
    
    /*dp[i][j] means end with i, end with j , what's max length of common string.
    abcbc. dp[2][4] = 2 because bc == bc, abc != cbc*/
    
    public int longestRepeatingSubstring3(String S) {
    	int l = S.length();
    	int[][] dp = new int[l+1][l+1];
    	int res = 0;
    	for (int i = 1; i <= l; i++) {
    		for (int j = i + 1; j <= l; j++) {
    			if (S.charAt(i - 1) == S.charAt(j - 1)) {
    				dp[i][j] = dp[i - 1][j - 1] + 1;
    				res = Math.max(dp[i][j],res);
    			}
    		}
    	}
    	return res;
    }
}

public class Main {
	public static void main(String[] args) {
		//String S = "aabcaabdaab";
		String S = "banana";
		System.out.println(new Solution().longestRepeatingSubstring(S));
		System.out.println(new Solution().longestRepeatingSubstring1(S));
		System.out.println(new Solution().longestRepeatingSubstring2(S));
		System.out.println(new Solution().longestRepeatingSubstring3(S));
	}
}