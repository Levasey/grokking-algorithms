import chapter03.recursion.Factorial;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FactorialTest {

    @Test
    public void testFactorial() {
        assertEquals(15, Factorial.factorial(5));
    }
}
