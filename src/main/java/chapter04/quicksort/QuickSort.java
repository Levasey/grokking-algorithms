package chapter04.quicksort;

public class QuickSort {
    public static void quickSort(int[] arr) {
        int low = 0;
        int high = arr.length - 1;
        sort(arr, low, high);
    }

    public static void sort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = patrtition(arr, low, high);
            sort(arr, low, pivot - 1);
            sort(arr, pivot + 1, high);
        }
    }

    private static int patrtition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
