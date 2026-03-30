package chapter13.heap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Бинарная мин-куча на массиве: родитель с индексом {@code i} — {@code (i - 1) / 2},
 * дети — {@code 2i + 1} и {@code 2i + 2}. Минимум всегда в корне (индекс 0).
 * <p>
 * Сложность: вставка и извлечение минимума — O(log n); просмотр минимума — O(1).
 */
public final class MinHeap<E> {

    private final List<E> data;
    private final Comparator<? super E> comparator;

    /**
     * Пустая куча с заданным порядком «меньше = выше приоритет».
     */
    public MinHeap(Comparator<? super E> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
        this.data = new ArrayList<>();
    }

    /**
     * Куча из элементов за O(n) через просейку снизу вверх ({@link #heapify}).
     */
    public MinHeap(Comparator<? super E> comparator, Collection<? extends E> initial) {
        this(comparator);
        data.addAll(initial);
        heapify();
    }

    /**
     * Пустая куча с естественным порядком {@link Comparable}.
     */
    public static <E extends Comparable<? super E>> MinHeap<E> naturalOrder() {
        return new MinHeap<>(Comparator.naturalOrder());
    }

    public void offer(E element) {
        Objects.requireNonNull(element, "element");
        data.add(element);
        siftUp(data.size() - 1);
    }

    /**
     * Минимальный элемент без удаления; {@code null}, если куча пуста.
     */
    public E peek() {
        return data.isEmpty() ? null : data.getFirst();
    }

    /**
     * Удаляет и возвращает минимум; {@code null}, если куча пуста.
     */
    public E poll() {
        if (data.isEmpty()) {
            return null;
        }
        E root = data.getFirst();
        E last = data.removeLast();
        if (!data.isEmpty()) {
            data.set(0, last);
            siftDown(0);
        }
        return root;
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    private void heapify() {
        for (int i = parent(data.size() - 1); i >= 0; i--) {
            siftDown(i);
        }
    }

    private void siftUp(int i) {
        while (i > 0) {
            int p = parent(i);
            if (!less(i, p)) {
                break;
            }
            swap(i, p);
            i = p;
        }
    }

    private void siftDown(int i) {
        int n = data.size();
        while (true) {
            int smallest = i;
            int l = left(i);
            int r = right(i);
            if (l < n && less(l, smallest)) {
                smallest = l;
            }
            if (r < n && less(r, smallest)) {
                smallest = r;
            }
            if (smallest == i) {
                break;
            }
            swap(i, smallest);
            i = smallest;
        }
    }

    private static int parent(int i) {
        return (i - 1) >>> 1;
    }

    private static int left(int i) {
        return (i << 1) + 1;
    }

    private static int right(int i) {
        return (i << 1) + 2;
    }

    private boolean less(int i, int j) {
        return comparator.compare(data.get(i), data.get(j)) < 0;
    }

    private void swap(int i, int j) {
        Collections.swap(data, i, j);
    }
}
