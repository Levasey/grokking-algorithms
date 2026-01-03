package chapter04.quicksort;

import java.util.List;
import java.util.ArrayList;

public class QuickSort2 {
    public static List<Integer> run(List<Integer> numbers) {
        List<Integer> sorted = new ArrayList<>(numbers);
        sort(sorted, 0, sorted.size() - 1, true);
        return sorted;
    }

    public static List<Integer> run(List<Integer> numbers, String direction) {
        List<Integer> sorted = new ArrayList<>(numbers);

        if (direction.equals("asc")) {
            sort(sorted, 0, sorted.size() - 1, true);
        } else if (direction.equals("desc")) {
            sort(sorted, 0, sorted.size() - 1, false);
        } else {
            throw new IllegalArgumentException("Direction must be 'asc' or 'desc'");
        }

        return sorted;
    }

    private static int partition(List<Integer> numbers, int left, int right, boolean ascending) {
        int mid = left + (right - left) / 2;
        int pivot = numbers.get(mid);
        swap(numbers, mid, right);
        int storeIndex = left;

        if (ascending) {
            for (int i = left; i < right; i++) {
                if (numbers.get(i) < pivot) {
                    swap(numbers, i, storeIndex);
                    storeIndex++;
                }
            }
        } else {
            for (int i = left; i < right; i++) {
                if (numbers.get(i) > pivot) {
                    swap(numbers, i, storeIndex);
                    storeIndex++;
                }
            }
        }

        swap(numbers, storeIndex, right);
        return storeIndex;
    }

    private static void sort(List<Integer> numbers, int left, int right, boolean ascending) {
        if (left < right) {
            int pivotIndex = partition(numbers, left, right, ascending);
            sort(numbers, left, pivotIndex - 1, ascending);
            sort(numbers, pivotIndex + 1, right, ascending);
        }
    }

    private static void swap(List<Integer> numbers, int i, int j) {
        int temp = numbers.get(i);
        numbers.set(i, numbers.get(j));
        numbers.set(j, temp);
    }
}
