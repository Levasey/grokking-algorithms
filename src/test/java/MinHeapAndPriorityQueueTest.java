import chapter13.heap.MinHeap;
import chapter13.heap.PriorityQueue;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapAndPriorityQueueTest {

    @Test
    void minHeap_peekEmpty() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        assertNull(h.peek());
        assertTrue(h.isEmpty());
    }

    @Test
    void minHeap_pollReturnsAscending() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.offer(5);
        h.offer(1);
        h.offer(3);
        assertEquals(1, h.poll());
        assertEquals(3, h.poll());
        assertEquals(5, h.poll());
        assertNull(h.poll());
    }

    @Test
    void minHeap_peekDoesNotRemove() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.offer(2);
        assertEquals(2, h.peek());
        assertEquals(2, h.peek());
        assertEquals(1, h.size());
    }

    @Test
    void minHeap_heapifyConstructor() {
        MinHeap<Integer> h = new MinHeap<>(Comparator.naturalOrder(), List.of(9, 4, 7, 1, 3));
        int[] out = new int[5];
        for (int i = 0; i < 5; i++) {
            out[i] = h.poll();
        }
        assertArrayEquals(new int[] {1, 3, 4, 7, 9}, out);
    }

    @Test
    void minHeap_customComparatorMaxAsTop() {
        MinHeap<Integer> h = new MinHeap<>(Comparator.reverseOrder());
        h.offer(1);
        h.offer(10);
        h.offer(5);
        assertEquals(10, h.poll());
        assertEquals(5, h.poll());
        assertEquals(1, h.poll());
    }

    @Test
    void minHeap_rejectsNullOffer() {
        MinHeap<String> h = MinHeap.naturalOrder();
        assertThrows(NullPointerException.class, () -> h.offer(null));
    }

    @Test
    void priorityQueue_delegatesPollOrder() {
        PriorityQueue<Integer> q = PriorityQueue.naturalOrder();
        q.offer(4);
        q.offer(2);
        q.offer(6);
        assertEquals(2, q.poll());
        assertEquals(4, q.poll());
        assertEquals(6, q.poll());
        assertNull(q.poll());
    }

    @Test
    void priorityQueue_initialCollection() {
        PriorityQueue<Integer> q = new PriorityQueue<>(Comparator.naturalOrder(), List.of(8, 2, 5));
        assertEquals(2, q.poll());
        assertEquals(5, q.poll());
        assertEquals(8, q.poll());
    }
}
