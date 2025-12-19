import chapter04.quicksort.QuickSort;

import java.util.Arrays;

public class QuickSortTest {
    public static void main(String[] args) {
        int[] array = {10, 7, 8, 9, 1, 5};
        QuickSort.quickSort(array);
        System.out.println(Arrays.equals(array, new int[]{1, 5, 7, 8, 9, 10}));
    }
}
