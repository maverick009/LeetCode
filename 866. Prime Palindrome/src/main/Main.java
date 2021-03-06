package main;
// https://leetcode.com/problems/prime-palindrome/discuss/212297/Why-even-number-of-digit-prime-palindromes-are-not-possible
// https://leetcode.com/problems/prime-palindrome/discuss/146798/All-Even-Digits-Palindrome-are-Divisible-by-11

/* All even digits palindrome are divisible by 11
 * A number is divisible by 11 if sum(even digits) - sum(odd digits) is divisible by 11.
 * only 11 is prime thus base condition
 */
class Solution {
	public int primePalindrome(int N) {
		if (8 <= N && N <= 11) 
			return 11;
		/* range of number is 1 to 10^8
		 * we need to check only odd digit numbers
		 * get a number like 129, reverse 921 substring(1) -> 21
		 * add 12921 check if this is prime
		 */
		for (int x = 1; x < 100000; x++) {
			String s = Integer.toString(x), r = new StringBuilder(s).reverse().toString().substring(1);
			int y = Integer.parseInt(s + r);
			if (y >= N && isPrime(y)) return y;
		}
		return -1;
	}

	public Boolean isPrime(int x) {
		if (x < 2 || x % 2 == 0) return x == 2;
		for (int i = 3; i * i <= x; i += 2)
			if (x % i == 0) return false;
		return true;
	}
}

public class Main {
	public static void main(String[] args) {
		int N = 13;
		System.out.println(new Solution().primePalindrome(N));
	}
}
