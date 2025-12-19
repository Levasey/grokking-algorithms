import chapter01.binarySearch.BinarySearch;

public class BinarySearchTest {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 3, 5, 6, 9};
        int res = BinarySearch.binarySearch(arr, 9);
        System.out.println(res == 4);
        int res2 = BinarySearch.binarySearch(arr, 2);
        System.out.println(res2 == -1);
    }
}
