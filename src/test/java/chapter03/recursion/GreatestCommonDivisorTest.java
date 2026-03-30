package chapter03.recursion;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreatestCommonDivisorTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            48 | 18 | 6
            1071 | 462 | 21
            17 | 13 | 1
            1 | 1 | 1
            0 | 5 | 5
            5 | 0 | 5
            0 | 0 | 0
            12 | 12 | 12
            100 | 25 | 25
            -12 | 18 | 6
            12 | -18 | 6
            -12 | -18 | 6
            """)
    void gcd_euclidean_matchesExpected(int a, int b, int expected) {
        assertEquals(expected, GreatestCommonDivisor.gcd(a, b));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            48 | 18 | 6
            1071 | 462 | 21
            17 | 13 | 1
            1 | 1 | 1
            0 | 5 | 5
            5 | 0 | 5
            0 | 0 | 0
            12 | 12 | 12
            100 | 25 | 25
            """)
    void gcd_subtractionVariant_matchesEuclideanForNonNegative(int a, int b, int expected) {
        assertEquals(expected, GreatestCommonDivisor.gsd1(a, b));
        assertEquals(expected, GreatestCommonDivisor.gsd1(b, a));
    }
}
