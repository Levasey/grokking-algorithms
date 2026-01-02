import chapter03.recursion.Fibonacci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FibonacciTest {

    @Test
    public void testFibonacci() {
        assertEquals(5, Fibonacci.fibonacci(5));
    }
}
