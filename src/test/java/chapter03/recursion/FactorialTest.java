package chapter03.recursion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FactorialTest {

    @Test
    public void testFactorial() {
        assertEquals(120, Factorial.factorial(5));
    }
}
