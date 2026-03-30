package chapter13.hyperLogLog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HyperLogLogTest {

    @Test
    void empty_estimateZero() {
        HyperLogLog hll = new HyperLogLog(12);
        assertEquals(0, hll.estimateCardinality());
    }

    @Test
    void singleDistinct_estimateAboutOne() {
        HyperLogLog hll = new HyperLogLog(12);
        hll.add("only-one");
        long n = hll.estimateCardinality();
        assertTrue(n >= 1 && n <= 5, "expected ~1, got " + n);
    }

    @Test
    void duplicates_doNotInflateCardinality() {
        HyperLogLog hll = new HyperLogLog(12);
        for (int i = 0; i < 10_000; i++) {
            hll.add("same");
        }
        long n = hll.estimateCardinality();
        assertTrue(n >= 1 && n <= 5, "expected ~1 with duplicates, got " + n);
    }

    @Test
    void manyDistinct_withinTypicalError() {
        HyperLogLog hll = new HyperLogLog(14);
        int trueCardinality = 50_000;
        for (int i = 0; i < trueCardinality; i++) {
            hll.add("key-" + i);
        }
        long est = hll.estimateCardinality();
        double relError = Math.abs(est - trueCardinality) / (double) trueCardinality;
        assertTrue(relError < 0.12, "expected relative error < 12%, got " + est + " (~" + (relError * 100) + "%)");
    }

    @Test
    void invalidPrecision_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(3));
        assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(19));
    }
}
