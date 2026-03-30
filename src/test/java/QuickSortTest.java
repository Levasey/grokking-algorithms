import chapter04.quicksort.QuickSort;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuickSortTest {
    @Test
    public void testQuickSort() {
        int[] array = {10, 7, 8, 9, 1, 5};
        int[] expected = {1, 5, 7, 8, 9, 10};
        QuickSort.quickSort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void quickSort_alreadySorted() {
        int[] sorted = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] copy = sorted.clone();
        QuickSort.quickSort(copy);
        assertArrayEquals(sorted, copy);
    }
}
