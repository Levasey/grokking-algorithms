package chapter13.bloomFilter;

import java.util.BitSet;
import java.util.Objects;

/**
 * Фильтр Блума: вероятностная структура «множество», экономящая память за счёт допустимых ложноположительных
 * срабатываний при проверке вхождения. Ложноотрицательных нет: если элемента не было, {@link #mightContain}
 * иногда может вернуть {@code true}, но после {@link #add} всегда {@code true}.
 */
public final class BloomFilter {

    private final BitSet bits;
    private final int bitSize;
    private final int hashFunctionCount;

    /**
     * Подбор размера битовой карты и числа хешей по ожидаемому числу вставок и желаемой вероятности
     * ложноположительного ответа (приближённо, при независимом хешировании).
     *
     * @param expectedInsertions ожидаемое число различных добавляемых элементов
     * @param falsePositiveRate  целевая вероятность ложного «возможно есть» после заполнения (0 &lt; p &lt; 1)
     */
    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions <= 0) {
            throw new IllegalArgumentException("expectedInsertions must be positive");
        }
        if (falsePositiveRate <= 0.0 || falsePositiveRate >= 1.0) {
            throw new IllegalArgumentException("falsePositiveRate must be in (0, 1)");
        }
        this.bitSize = optimalBitSize(expectedInsertions, falsePositiveRate);
        this.hashFunctionCount = optimalHashFunctionCount(expectedInsertions, bitSize);
        this.bits = new BitSet(bitSize);
    }

    /**
     * Явная конфигурация (удобно для тестов и воспроизводимости).
     *
     * @param bitSize             число бит
     * @param hashFunctionCount   число независимых (по смыслу) хеш-функций
     */
    public BloomFilter(int bitSize, int hashFunctionCount) {
        if (bitSize <= 0) {
            throw new IllegalArgumentException("bitSize must be positive");
        }
        if (hashFunctionCount <= 0) {
            throw new IllegalArgumentException("hashFunctionCount must be positive");
        }
        this.bitSize = bitSize;
        this.hashFunctionCount = hashFunctionCount;
        this.bits = new BitSet(bitSize);
    }

    public int bitSize() {
        return bitSize;
    }

    public int hashFunctionCount() {
        return hashFunctionCount;
    }

    /**
     * Добавляет элемент в множество.
     */
    public void add(CharSequence element) {
        Objects.requireNonNull(element, "element");
        for (int i = 0; i < hashFunctionCount; i++) {
            bits.set(indexFor(element, i));
        }
    }

    /**
     * {@code false} — элемента точно не было; {@code true} — элемент мог быть добавлен (или это ложная тревога).
     */
    public boolean mightContain(CharSequence element) {
        Objects.requireNonNull(element, "element");
        for (int i = 0; i < hashFunctionCount; i++) {
            if (!bits.get(indexFor(element, i))) {
                return false;
            }
        }
        return true;
    }

    private int indexFor(CharSequence element, int hashIndex) {
        long h = mixHash(element, hashIndex);
        return (int) Math.floorMod(h, (long) bitSize);
    }

    /**
     * 64-битовое смешивание с разным «солью» по индексу хеша — даёт набор псевдо-независимых позиций.
     */
    private static long mixHash(CharSequence s, int seed) {
        long h = seed * 0x9e3779b97f4a7c15L;
        for (int i = 0; i < s.length(); i++) {
            h = h * 31 + s.charAt(i);
            h ^= (h >>> 33);
            h *= 0xff51afd7ed558ccdL;
        }
        h ^= h >>> 33;
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= h >>> 33;
        return h;
    }

    private static int optimalBitSize(long expectedInsertions, double falsePositiveRate) {
        double ln2Squared = Math.log(2) * Math.log(2);
        double m = -(expectedInsertions * Math.log(falsePositiveRate)) / ln2Squared;
        int bits = (int) Math.ceil(m);
        return Math.max(bits, 1);
    }

    private static int optimalHashFunctionCount(long expectedInsertions, int bitSize) {
        int k = (int) Math.round((double) bitSize / expectedInsertions * Math.log(2));
        return Math.max(k, 1);
    }
}
