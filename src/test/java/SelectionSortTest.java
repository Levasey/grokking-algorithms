import chapter02.selectionSort.SelectionSort;

import java.util.Arrays;

public class SelectionSortTest {
    public static void main(String[] args) {
        int[] arr = new int[]{5, 4, 3, 2, 1};
        SelectionSort.selectionSort(arr);
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.equals(arr, new int[]{1, 2, 3, 4, 5}));
    }
}
