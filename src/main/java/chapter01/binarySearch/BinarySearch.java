package chapter01.binarySearch;

public class BinarySearch {
    public static  int binarySearch(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int guess = arr[mid];
            if (guess == key) {
                return mid;
            } else if (guess < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}
