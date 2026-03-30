package chapter04.quicksort;

/**
 * Быстрая сортировка (Lomuto): перед разбиением опорный элемент — медиана трёх ({@code low}, середина, {@code high}),
 * чтобы типичный случай уже отсортированного входа не деградировал к квадратичному времени.
 */
public class QuickSort {
    public static void quickSort(int[] arr) {
        int low = 0;
        int high = arr.length - 1;
        sort(arr, low, high);
    }

    public static void sort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = partition(arr, low, high);
            sort(arr, low, pivot - 1);
            sort(arr, pivot + 1, high);
        }
    }

    private static void medianOfThreePivotToEnd(int[] arr, int low, int high) {
        int mid = low + (high - low) / 2;
        if (arr[low] > arr[mid]) {
            swap(arr, low, mid);
        }
        if (arr[low] > arr[high]) {
            swap(arr, low, high);
        }
        if (arr[mid] > arr[high]) {
            swap(arr, mid, high);
        }
        swap(arr, mid, high);
    }

    private static int partition(int[] arr, int low, int high) {
        medianOfThreePivotToEnd(arr, low, high);
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
