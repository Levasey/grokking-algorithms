package chapter11.dynamicProgramming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Задача о рюкзаке 0/1: каждый предмет можно взять не более одного раза.
 * Таблица dp[i][c] — максимальная ценность из первых {@code i} предметов при вместимости {@code c}.
 */
public final class Knapsack {

    private Knapsack() {
    }

    /**
     * Предмет с положительным или нулевым весом и неотрицательной ценностью.
     */
    public record Item(int weight, int value) {
        public Item {
            if (weight < 0) {
                throw new IllegalArgumentException("weight must be >= 0");
            }
            if (value < 0) {
                throw new IllegalArgumentException("value must be >= 0");
            }
        }
    }

    /**
     * Максимальная суммарная ценность предметов, укладывающихся в рюкзак вместимости {@code capacity}.
     *
     * @param capacity грузоподъёмность (неотрицательная)
     * @param items    предметы (не {@code null}; элементы не {@code null})
     * @return оптимальная ценность
     */
    public static int maxValue(int capacity, List<Item> items) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be >= 0");
        }
        Objects.requireNonNull(items, "items");
        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            Item item = Objects.requireNonNull(items.get(i - 1), "items[" + (i - 1) + "]");
            int w = item.weight();
            int v = item.value();
            for (int c = 0; c <= capacity; c++) {
                int best = dp[i - 1][c];
                if (w <= c) {
                    best = Math.max(best, dp[i - 1][c - w] + v);
                }
                dp[i][c] = best;
            }
        }
        return dp[n][capacity];
    }

    /**
     * Индексы предметов (0-based) в одном из оптимальных наборов; порядок соответствует возрастанию индекса.
     */
    public static List<Integer> maxValueItemIndices(int capacity, List<Item> items) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be >= 0");
        }
        Objects.requireNonNull(items, "items");
        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            Item item = Objects.requireNonNull(items.get(i - 1), "items[" + (i - 1) + "]");
            int w = item.weight();
            int v = item.value();
            for (int c = 0; c <= capacity; c++) {
                int best = dp[i - 1][c];
                if (w <= c) {
                    best = Math.max(best, dp[i - 1][c - w] + v);
                }
                dp[i][c] = best;
            }
        }
        List<Integer> picked = new ArrayList<>();
        int c = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][c] != dp[i - 1][c]) {
                int w = items.get(i - 1).weight();
                picked.add(i - 1);
                c -= w;
            }
        }
        Collections.reverse(picked);
        return List.copyOf(picked);
    }
}
