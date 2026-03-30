package chapter13.distributed;

import java.util.Arrays;
import java.util.Objects;

/**
 * Векторные часы: для каждого процесса ведётся счётчик событий; при обмене сообщениями векторы
 * сливаются по покомпонентному max, затем инкрементируется компонента получателя.
 * Позволяет отличить причинный порядок от конкурирующих (параллельных) событий.
 */
public final class VectorClock {

    public enum CausalRelation {
        /** Событие A предшествует B (A строго «раньше» по причинности). */
        BEFORE,
        /** Событие A следует за B. */
        AFTER,
        /** Одинаковые векторы (одно и то же состояние логических часов). */
        EQUAL,
        /** Несравнимо по причинности — конкурирующие события. */
        CONCURRENT
    }

    private final int processIndex;
    private final int[] vector;

    /**
     * @param processCount число процессов (длина вектора)
     * @param ownerIndex    индекс владельца этого экземпляра {@code [0, processCount)}
     */
    public VectorClock(int processCount, int ownerIndex) {
        if (processCount <= 0) {
            throw new IllegalArgumentException("processCount must be positive");
        }
        if (ownerIndex < 0 || ownerIndex >= processCount) {
            throw new IllegalArgumentException("ownerIndex out of range");
        }
        this.processIndex = ownerIndex;
        this.vector = new int[processCount];
    }

    public int processIndex() {
        return processIndex;
    }

    public int[] snapshot() {
        return Arrays.copyOf(vector, vector.length);
    }

    /**
     * Локальное событие на владельце часов.
     */
    public void tick() {
        vector[processIndex]++;
    }

    /**
     * Вектор для отправки в сообщении (копия текущего состояния; уже отражает время перед отправкой
     * после вызова {@link #tick()} или предыдущего {@link #onSend()}).
     */
    public int[] onSend() {
        tick();
        return snapshot();
    }

    /**
     * Приём сообщения с вектором отправителя (копия не изменяется снаружи).
     */
    public void onReceive(int[] remote) {
        Objects.requireNonNull(remote, "remote");
        if (remote.length != vector.length) {
            throw new IllegalArgumentException("remote vector length mismatch");
        }
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.max(vector[i], remote[i]);
        }
        vector[processIndex]++;
    }

    /**
     * Причинное отношение вектора {@code a} к вектору {@code b} в смысле «событие с часами a
     * предшествует событию с часами b».
     */
    public static CausalRelation relation(int[] a, int[] b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (a.length != b.length) {
            throw new IllegalArgumentException("vector length mismatch");
        }
        boolean aLeB = true;
        boolean bLeA = true;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > b[i]) {
                aLeB = false;
            }
            if (b[i] > a[i]) {
                bLeA = false;
            }
        }
        if (aLeB && bLeA) {
            return CausalRelation.EQUAL;
        }
        if (aLeB) {
            return CausalRelation.BEFORE;
        }
        if (bLeA) {
            return CausalRelation.AFTER;
        }
        return CausalRelation.CONCURRENT;
    }
}
