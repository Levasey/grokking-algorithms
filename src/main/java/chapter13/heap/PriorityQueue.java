package chapter13.heap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * Приоритетная очередь: элемент с наименьшим значением по {@link Comparator} извлекается первым
 * (для {@link Comparable} через {@link #naturalOrder()} — минимальный ключ первым).
 * Реализована бинарной мин-кучей {@link MinHeap}.
 * <p>
 * Имя совпадает с {@link java.util.PriorityQueue}, но тип другой; для API коллекций JDK используйте класс из {@code java.util}.
 * Не потокобезопасна.
 */
public final class PriorityQueue<E> {

    private final MinHeap<E> heap;

    public PriorityQueue(Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        this.heap = new MinHeap<>(comparator);
    }

    public PriorityQueue(Comparator<? super E> comparator, Collection<? extends E> initial) {
        Objects.requireNonNull(comparator, "comparator");
        this.heap = new MinHeap<>(comparator, initial);
    }

    public static <E extends Comparable<? super E>> PriorityQueue<E> naturalOrder() {
        return new PriorityQueue<>(Comparator.naturalOrder());
    }

    public void offer(E element) {
        heap.offer(element);
    }

    public E peek() {
        return heap.peek();
    }

    public E poll() {
        return heap.poll();
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
}
