package chapter03.recursion;

/**
 * Наивная рекурсия из книги — экспоненциальное время; для больших n — {@link #fibonacciIterative(int)}.
 * {@code long} переполняется примерно при n &gt; 92.
 */
public class Fibonacci {

    public static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static long fibonacciIterative(int n) {
        if (n <= 1) return n;
        long a = 0;
        long b = 1;
        for (int i = 2; i <= n; i++) {
            long c = a + b;
            a = b;
            b = c;
        }
        return b;
    }
}
