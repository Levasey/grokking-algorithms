package chapter10.greedyAlgorithms;

import java.util.*;

/**
 * Жадное приближённое решение задачи о покрытии множества (set cover): на каждом шаге выбирается
 * «станция», покрывающая наибольшее число ещё не покрытых элементов. Оптимум не гарантируется,
 * но на практике даёт приемлемый результат за полиномиальное время.
 */
public final class GreedySetCover {

    private GreedySetCover() {
    }

    /**
     * Возвращает имена выбранных станций в порядке жадного выбора.
     * <p>
     * При равном числе вновь покрываемых элементов выбирается станция с лексикографически
     * меньшим именем — так результат детерминирован.
     *
     * @param stations    карта «имя станции → множество покрываемых элементов» (не изменяется)
     * @param stillNeeded элементы, которые нужно покрыть; копируется внутри метода
     * @param <T>         тип элемента вселенной (должен корректно реализовывать equals/hashCode)
     * @return упорядоченный список имён станций; если полное покрытие невозможно, список содержит
     *         лишь те станции, что успели уменьшить остаток (последняя итерация не добавляет станцию,
     *         если ни одна не покрывает оставшиеся элементы)
     */
    public static <T> List<String> chooseStations(Map<String, Set<T>> stations, Set<T> stillNeeded) {
        Objects.requireNonNull(stations, "stations");
        Objects.requireNonNull(stillNeeded, "stillNeeded");

        Set<T> uncovered = new HashSet<>(stillNeeded);
        List<String> chosen = new ArrayList<>();

        while (!uncovered.isEmpty()) {
            String bestName = null;
            int bestNew = 0;

            for (Map.Entry<String, Set<T>> entry : stations.entrySet()) {
                String name = entry.getKey();
                Set<T> coverage = entry.getValue();
                if (coverage == null || coverage.isEmpty()) {
                    continue;
                }
                int newlyCovered = 0;
                for (T x : coverage) {
                    if (uncovered.contains(x)) {
                        newlyCovered++;
                    }
                }
                if (newlyCovered == 0) {
                    continue;
                }
                if (newlyCovered > bestNew
                        || (newlyCovered == bestNew && name.compareTo(bestName) < 0)) {
                    bestNew = newlyCovered;
                    bestName = name;
                }
            }

            if (bestName == null) {
                break;
            }

            chosen.add(bestName);
            Set<T> cover = stations.get(bestName);
            if (cover != null) {
                uncovered.removeAll(cover);
            }
        }

        return List.copyOf(chosen);
    }
}
