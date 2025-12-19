package chapter04.quicksort;

public class DivideAndConquerDemo {
    public static long divideAndConquer(int[] arr) {
        return recursiveSum(arr, 0, arr.length - 1);
    }
    private static long recursiveSum(int[] arr, int left, int right) {
        if (left > right) {
            return 0;
        }

        if (left == right) {
            return arr[left];
        }
        int mid = (left + right) / 2;
        return recursiveSum(arr, left, mid) + recursiveSum(arr, mid + 1, right);
    }
}
