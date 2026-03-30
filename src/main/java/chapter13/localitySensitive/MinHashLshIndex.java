package chapter13.localitySensitive;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Индекс на основе MinHash и LSH (banding): документы с высокой похожестью Жаккара по множествам признаков
 * с большой вероятностью попадают в общие корзины хотя бы в одной полосе, что даёт компактный список кандидатов
 * вместо полного перебора.
 */
public final class MinHashLshIndex {

    private final MinHash minHash;
    private final int bands;
    private final int rowsPerBand;
    private final Map<Long, Set<String>> buckets = new HashMap<>();
    private final Map<String, long[]> signatures = new HashMap<>();

    /**
     * @param hashFunctionCount общее число мин-хешей (длина подписи); должно делиться на {@code bands}
     * @param bands             число полос LSH; в каждой полосе {@code hashFunctionCount / bands} строк подписи
     * @param seed              параметр {@link MinHash}
     */
    public MinHashLshIndex(int hashFunctionCount, int bands, long seed) {
        if (hashFunctionCount <= 0 || bands <= 0) {
            throw new IllegalArgumentException("hashFunctionCount and bands must be positive");
        }
        if (hashFunctionCount % bands != 0) {
            throw new IllegalArgumentException("hashFunctionCount must be divisible by bands");
        }
        this.minHash = new MinHash(hashFunctionCount, seed);
        this.bands = bands;
        this.rowsPerBand = hashFunctionCount / bands;
    }

    public int hashFunctionCount() {
        return minHash.length();
    }

    public int bands() {
        return bands;
    }

    public int rowsPerBand() {
        return rowsPerBand;
    }

    /**
     * Индексирует документ по множеству признаков.
     */
    public void add(String documentId, Collection<Long> features) {
        Objects.requireNonNull(documentId, "documentId");
        Objects.requireNonNull(features, "features");
        long[] sig = minHash.signature(features);
        signatures.put(documentId, sig);
        for (int b = 0; b < bands; b++) {
            long key = bandKey(b, sig, b * rowsPerBand);
            buckets.computeIfAbsent(key, k -> new HashSet<>()).add(documentId);
        }
    }

    /**
     * Кандидаты на высокую похожесть для уже проиндексированного документа (включает сам {@code documentId}).
     */
    public Set<String> candidateNeighbors(String documentId) {
        long[] sig = signatures.get(documentId);
        if (sig == null) {
            throw new IllegalArgumentException("unknown documentId: " + documentId);
        }
        return candidatesForSignature(sig);
    }

    /**
     * Кандидаты для произвольного множества признаков (например, новый запрос без добавления в индекс).
     */
    public Set<String> queryCandidates(Collection<Long> features) {
        Objects.requireNonNull(features, "features");
        long[] sig = minHash.signature(features);
        return candidatesForSignature(sig);
    }

    /**
     * Приближённая оценка Жаккара между двумя проиндексированными документами по совпадению компонент подписи.
     */
    public double estimatedJaccard(String documentIdA, String documentIdB) {
        long[] sa = signatures.get(documentIdA);
        long[] sb = signatures.get(documentIdB);
        if (sa == null || sb == null) {
            throw new IllegalArgumentException("both documents must be indexed");
        }
        int same = 0;
        for (int i = 0; i < sa.length; i++) {
            if (sa[i] == sb[i]) {
                same++;
            }
        }
        return (double) same / sa.length;
    }

    private Set<String> candidatesForSignature(long[] sig) {
        Set<String> out = new HashSet<>();
        for (int b = 0; b < bands; b++) {
            long key = bandKey(b, sig, b * rowsPerBand);
            Set<String> ids = buckets.get(key);
            if (ids != null) {
                out.addAll(ids);
            }
        }
        return out;
    }

    private long bandKey(int bandIndex, long[] sig, int from) {
        long h = 0x9e3779b97f4a7c15L + bandIndex;
        for (int i = 0; i < rowsPerBand; i++) {
            h = h * 31 + sig[from + i];
        }
        return h;
    }
}
