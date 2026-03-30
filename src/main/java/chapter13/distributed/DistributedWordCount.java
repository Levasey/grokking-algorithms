package chapter13.distributed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Учебная симуляция MapReduce подсчёта слов на «узлах» (один JVM): map выполняется на сегментах
 * входного списка строк, shuffle группирует по ключу, reduce суммирует частоты.
 * Иллюстрирует идею распределённого конвейера без фреймворков вроде Hadoop.
 */
public final class DistributedWordCount {

    private static final Pattern WORD = Pattern.compile("[\\p{IsAlphabetic}\\p{IsDigit}]+");

    public record MapOutput(String word, int count) {
    }

    public record ReduceShard(String word, int total) implements Comparable<ReduceShard> {
        @Override
        public int compareTo(ReduceShard o) {
            int c = word.compareTo(o.word);
            if (c != 0) {
                return c;
            }
            return Integer.compare(total, o.total);
        }
    }

    private DistributedWordCount() {
    }

    /**
     * Подсчёт слов с разбиением входа на {@code nodeCount} частей (последние части могут быть короче).
     *
     * @param lines     строки документов (каждая как отдельный фрагмент текста)
     * @param nodeCount число виртуальных узлов (положительное)
     * @return отсортированный по слову список итогов после reduce
     */
    public static List<ReduceShard> count(List<String> lines, int nodeCount) {
        Objects.requireNonNull(lines, "lines");
        if (nodeCount <= 0) {
            throw new IllegalArgumentException("nodeCount must be positive");
        }
        if (lines.isEmpty()) {
            return List.of();
        }

        List<List<MapOutput>> mapResults = mapPhase(lines, nodeCount);
        Map<String, List<Integer>> groups = shuffle(mapResults);
        return reduce(groups);
    }

    static List<String> tokenizeLine(String line) {
        String lower = line.toLowerCase(Locale.ROOT);
        List<String> tokens = new ArrayList<>();
        Matcher m = WORD.matcher(lower);
        while (m.find()) {
            tokens.add(m.group());
        }
        return tokens;
    }

    private static List<List<MapOutput>> mapPhase(List<String> lines, int nodeCount) {
        int n = lines.size();
        int base = n / nodeCount;
        int extra = n % nodeCount;
        List<List<MapOutput>> all = new ArrayList<>();
        int start = 0;
        for (int p = 0; p < nodeCount; p++) {
            int len = base + (p < extra ? 1 : 0);
            int end = start + len;
            List<MapOutput> local = new ArrayList<>();
            for (int i = start; i < end && i < n; i++) {
                for (String w : tokenizeLine(lines.get(i))) {
                    local.add(new MapOutput(w, 1));
                }
            }
            if (!local.isEmpty()) {
                all.add(local);
            }
            start = end;
        }
        return all;
    }

    private static Map<String, List<Integer>> shuffle(List<List<MapOutput>> mapResults) {
        Map<String, List<Integer>> buckets = new HashMap<>();
        for (List<MapOutput> shard : mapResults) {
            for (MapOutput mo : shard) {
                buckets.computeIfAbsent(mo.word, k -> new ArrayList<>()).add(mo.count);
            }
        }
        return buckets;
    }

    private static List<ReduceShard> reduce(Map<String, List<Integer>> groups) {
        List<ReduceShard> out = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> e : groups.entrySet()) {
            int sum = 0;
            for (int c : e.getValue()) {
                sum += c;
            }
            out.add(new ReduceShard(e.getKey(), sum));
        }
        out.sort(ReduceShard::compareTo);
        return out;
    }
}
