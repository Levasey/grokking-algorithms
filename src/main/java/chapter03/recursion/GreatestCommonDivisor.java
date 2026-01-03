package chapter03.recursion;

public class GreatestCommonDivisor {
    public static int gcd(int a, int b) {
        if (b == 0) return Math.abs(a);
        return gcd(b, a % b);
    }

    public static int gsd1(int a, int b) {
             if (a == 0) {
                 return b;
             }

             if (b == 0) {
                 return a;
             }

             if (a == b) {
                 return a;
             }

             if (a > b) {
                 return gsd1(a - b, b);
             }

             return gsd1(a, b - a);
    }
}
