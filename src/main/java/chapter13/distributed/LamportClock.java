package chapter13.distributed;

/**
 * Логические часы Лампорта: упорядочивание событий в системе без общих часов.
 * Локальное событие и отправка сообщения увеличивают счётчик; при приёме сообщения время сдвигается
 * до max(своё, удалённое) + 1, чтобы событие приёма было «после» соответствующей отправки.
 * <p>
 * Один экземпляр соответствует одному процессу в модели; синхронизация между потоками одного процесса не встроена.
 */
public final class LamportClock {

    private long time;

    public long time() {
        return time;
    }

    /**
     * Локальное событие на процессе (не связанное с сообщением).
     */
    public long tick() {
        return ++time;
    }

    /**
     * Перед отправкой сообщения вызывается на отправителе; возвращаемое значение кладётся в сообщение.
     */
    public long onSend() {
        return ++time;
    }

    /**
     * Приём сообщения с меткой {@code remoteTimestamp} согласно правилу Лампорта.
     *
     * @param remoteTimestamp метка времени на момент отправки (неотрицательная)
     * @return новое локальное время после обработки приёма
     */
    public long onReceive(long remoteTimestamp) {
        if (remoteTimestamp < 0) {
            throw new IllegalArgumentException("remoteTimestamp must be non-negative");
        }
        time = Math.max(time, remoteTimestamp) + 1;
        return time;
    }

    /**
     * Лексикографическое сравнение меток времени как целых (только полный порядок, не причинность).
     * Для отношения «случилось до» по цепочкам сообщений используйте {@link VectorClock}.
     */
    public static int compareTimestamps(long a, long b) {
        return Long.compare(a, b);
    }
}
