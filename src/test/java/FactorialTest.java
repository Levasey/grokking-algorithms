import chapter03.recursion.Factorial;

public class FactorialTest {
    public static void main(String[] args) {
        int res = Factorial.factorial(5);
        System.out.println(res == (5 * 4 * 3 * 2 * 1));
    }
}
