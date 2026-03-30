import chapter13.bloomFilter.BloomFilter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BloomFilterTest {

    @Test
    void mightContain_falseWhenEmpty() {
        BloomFilter f = new BloomFilter(100, 0.01);
        assertFalse(f.mightContain("missing"));
    }

    @Test
    void mightContain_trueAfterAdd() {
        BloomFilter f = new BloomFilter(100, 0.01);
        f.add("alpha");
        assertTrue(f.mightContain("alpha"));
    }

    @Test
    void noFalseNegatives_onAddedDistinctStrings() {
        BloomFilter f = new BloomFilter(500, 0.01);
        for (int i = 0; i < 200; i++) {
            f.add("key-" + i);
        }
        for (int i = 0; i < 200; i++) {
            assertTrue(f.mightContain("key-" + i), "expected no false negative for key-" + i);
        }
    }

    @Test
    void invalidExpectedInsertions() {
        assertThrows(IllegalArgumentException.class, () -> new BloomFilter(0, 0.01));
    }

    @Test
    void invalidFalsePositiveRate() {
        assertThrows(IllegalArgumentException.class, () -> new BloomFilter(10, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new BloomFilter(10, 1.0));
    }

    @Test
    void explicitConstructor_rejectsNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> new BloomFilter(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new BloomFilter(64, 0));
    }
}
