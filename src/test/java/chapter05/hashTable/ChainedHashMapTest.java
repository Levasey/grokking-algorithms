package chapter05.hashTable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChainedHashMapTest {

    @Test
    void putAndGet() {
        ChainedHashMap<String, Integer> map = new ChainedHashMap<>(8);
        map.put("apple", 67);
        map.put("milk", 49);
        assertEquals(67, map.get("apple"));
        assertEquals(49, map.get("milk"));
        assertNull(map.get("_missing"));
    }

    @Test
    void updateExistingKey() {
        ChainedHashMap<String, String> map = new ChainedHashMap<>(4);
        map.put("a", "first");
        map.put("a", "second");
        assertEquals("second", map.get("a"));
    }

    @Test
    void collisionsStillResolve() {
        // малое число корзин повышает шанс коллизий по модулю
        ChainedHashMap<Integer, String> map = new ChainedHashMap<>(2);
        for (int i = 0; i < 20; i++) {
            map.put(i, "v" + i);
        }
        for (int i = 0; i < 20; i++) {
            assertEquals("v" + i, map.get(i));
        }
    }

    @Test
    void containsKey() {
        ChainedHashMap<String, Object> map = new ChainedHashMap<>(16);
        map.put("k", null);
        assertTrue(map.containsKey("k"));
        assertFalse(map.containsKey("other"));
    }

    @Test
    void rehashExpandsBucketsAndPreservesEntries() {
        ChainedHashMap<Integer, String> map = new ChainedHashMap<>(4, 0.75f);
        assertEquals(4, map.bucketCount());
        for (int i = 0; i < 32; i++) {
            map.put(i, "v" + i);
        }
        assertTrue(map.bucketCount() > 4);
        assertEquals(32, map.size());
        for (int i = 0; i < 32; i++) {
            assertEquals("v" + i, map.get(i));
        }
    }

    @Test
    void updateExistingKeyDoesNotGrowSizeOrForceRehashAlone() {
        ChainedHashMap<String, Integer> map = new ChainedHashMap<>(2, 0.75f);
        map.put("x", 1);
        map.put("x", 2);
        assertEquals(1, map.size());
        assertEquals(2, map.get("x"));
    }
}
