package chapter13.parallel;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Идеи главы про параллелизм: разделить работу на независимые части (map), затем объединить результаты (reduce).
 * Здесь — простые примеры на общем пуле форков {@link java.util.concurrent.ForkJoinPool#commonPool()}.
 * <p>
 * Для {@link #mapReduce}: порядок применения {@code reducer} к частичным результатам не гарантирован; операция должна быть
 * ассоциативной (и желательно коммутативной для минимизации численного дрейфа у вещественных типов).
 */
public final class ParallelMapReduce {

    private ParallelMapReduce() {
    }

    /**
     * Сумма элементов массива; для больших массивов может быть быстрее последовательного цикла за счёт параллельного стрима.
     */
    public static double parallelSum(double[] values) {
        Objects.requireNonNull(values, "values");
        return java.util.Arrays.stream(values).parallel().sum();
    }

    /**
     * Параллельное отображение индекса в значение и свёрка ассоциативной операцией (аналог map + reduce).
     *
     * @param n            верхняя граница полуинтервала [0, n)
     * @param mapper       f(i)
     * @param identity     нейтральный элемент для {@code reducer}
     * @param reducer      ассоциативная операция (как при параллельном reduce порядок аргументов может отличаться)
     * @param <T>          тип аккумулятора
     */
    public static <T> T mapReduce(int n, Function<Integer, T> mapper, T identity, BinaryOperator<T> reducer) {
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(reducer, "reducer");
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        return IntStream.range(0, n).parallel().mapToObj(mapper::apply).reduce(identity, reducer);
    }
}
