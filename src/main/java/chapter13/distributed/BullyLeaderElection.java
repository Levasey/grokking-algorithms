package chapter13.distributed;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Алгоритм «зараз» (Bully): при отказе координатора процесс инициирует выборы, опрашивая процессы
 * с большими идентификаторами; если ответа нет, он сам становится лидером. Устойчивый итог —
 * живой процесс с максимальным id.
 * <p>
 * Здесь — детерминированная симуляция обмена сообщениями в одной JVM (без сети): фиксируются
 * шаги для наглядности; результат совпадает с max(живые id).
 */
public final class BullyLeaderElection {

    public record ElectionStep(String kind, int from, int to, String detail) {
    }

    private BullyLeaderElection() {
    }

    /**
     * Запуск выборов от процесса {@code initiator} среди {@code aliveIds}.
     *
     * @return идентификатор нового лидера, если множество живых непусто
     */
    public static OptionalInt run(int initiator, SortedSet<Integer> aliveIds, List<ElectionStep> trace) {
        Objects.requireNonNull(aliveIds, "aliveIds");
        if (trace != null) {
            trace.clear();
        }
        if (aliveIds.isEmpty()) {
            return OptionalInt.empty();
        }
        if (!aliveIds.contains(initiator)) {
            throw new IllegalArgumentException("initiator must be in aliveIds");
        }

        List<Integer> higher = aliveIds.stream()
                .filter(id -> id > initiator)
                .sorted()
                .toList();

        if (higher.isEmpty()) {
            if (trace != null) {
                trace.add(new ElectionStep("coordinator", initiator, initiator,
                        "нет процессов с id выше — остаюсь лидером"));
            }
            return OptionalInt.of(initiator);
        }

        int target = higher.getFirst();
        if (trace != null) {
            trace.add(new ElectionStep("election", initiator, target, "ELECTION"));
            trace.add(new ElectionStep("alive", target, initiator, "OK"));
        }
        return run(target, aliveIds, trace);
    }

    /**
     * Итоговый лидер без трассировки.
     */
    public static OptionalInt leader(SortedSet<Integer> aliveIds) {
        if (aliveIds.isEmpty()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(aliveIds.last());
    }

    /**
     * Удобная обёртка: произвольная коллекция id, без дубликатов; сортировка по возрастанию.
     */
    public static OptionalInt runFromSmallestAlive(List<Integer> aliveIds, List<ElectionStep> trace) {
        Objects.requireNonNull(aliveIds, "aliveIds");
        TreeSet<Integer> set = new TreeSet<>(aliveIds);
        if (set.size() != aliveIds.size()) {
            throw new IllegalArgumentException("aliveIds must not contain duplicates");
        }
        if (set.isEmpty()) {
            return OptionalInt.empty();
        }
        int initiator = set.first();
        return run(initiator, set, trace);
    }

    public static List<ElectionStep> traceElection(int initiator, SortedSet<Integer> aliveIds) {
        List<ElectionStep> steps = new ArrayList<>();
        run(initiator, aliveIds, steps);
        return steps;
    }
}
