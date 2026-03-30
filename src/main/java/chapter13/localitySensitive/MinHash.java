package chapter13.localitySensitive;

import java.util.Collection;
import java.util.Objects;
import java.util.SplittableRandom;

/**
 * MinHash: компактная подпись множества, такая что доля совпадающих компонент двух подписей
 * приближает коэффициент Жаккара похожести множеств. Используется как основа для LSH.
 */
public final class MinHash {

    private final int length;
    private final long[] factorsA;
    private final long[] addB;

    /**
     * @param hashFunctionCount число функций (длина подписи)
     * @param seed              воспроизводимость коэффициентов
     */
    public MinHash(int hashFunctionCount, long seed) {
        if (hashFunctionCount <= 0) {
            throw new IllegalArgumentException("hashFunctionCount must be positive");
        }
        this.length = hashFunctionCount;
        this.factorsA = new long[hashFunctionCount];
        this.addB = new long[hashFunctionCount];
        SplittableRandom rnd = new SplittableRandom(seed);
        for (int i = 0; i < hashFunctionCount; i++) {
            factorsA[i] = rnd.nextLong() | 1L;
            addB[i] = rnd.nextLong();
        }
    }

    public int length() {
        return length;
    }

    /**
     * Подпись множества признаков (каждый признак — уже закодированный {@code long}, например хеш шингла).
     *
     * @throws IllegalArgumentException если множество пустое
     */
    public long[] signature(Collection<Long> features) {
        Objects.requireNonNull(features, "features");
        if (features.isEmpty()) {
            throw new IllegalArgumentException("features must be non-empty for MinHash");
        }
        long[] sig = new long[length];
        for (int i = 0; i < length; i++) {
            long a = factorsA[i];
            long b = addB[i];
            long min = Long.MAX_VALUE;
            for (long x : features) {
                long v = a * x + b;
                if (Long.compareUnsigned(v, min) < 0) {
                    min = v;
                }
            }
            sig[i] = min;
        }
        return sig;
    }
}
