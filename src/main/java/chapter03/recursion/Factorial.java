package chapter03.recursion;

public class Factorial {
    /**
     * Recursive factorial for non-negative integers. For {@code n < 0} the mathematical extension
     * is not covered here; this method rejects such input to avoid non-terminating recursion.
     *
     * @param n must be {@code >= 0}
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public static int factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, got: " + n);
        }
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }
}
