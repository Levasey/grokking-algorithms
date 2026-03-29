import chapter03.recursion.Fibonacci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FibonacciTest {

    @Test
    public void testFibonacci() {
        assertEquals(5, Fibonacci.fibonacci(5));
    }

    @Test
    public void iterativeMatchesNaiveForSmallN() {
        for (int n = 0; n <= 20; n++) {
            assertEquals(Fibonacci.fibonacci(n), Fibonacci.fibonacciIterative(n), "n=" + n);
        }
    }

    @Test
    public void fibonacciIterative_handles50() {
        assertEquals(12_586_269_025L, Fibonacci.fibonacciIterative(50));
    }
}
