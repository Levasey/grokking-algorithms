import chapter01.binarySearch.BinarySearch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BinarySearchTest {

    @Test
    void testBinarySearch_Found() {
        int[] arr = {1, 3, 5, 7, 9, 11, 13, 15};

        assertEquals(0, BinarySearch.binarySearch(arr, 1));   // первый элемент
        assertEquals(3, BinarySearch.binarySearch(arr, 7));   // средний элемент
        assertEquals(7, BinarySearch.binarySearch(arr, 15));  // последний элемент
        assertEquals(2, BinarySearch.binarySearch(arr, 5));   // произвольный элемент
    }

    @Test
    void testBinarySearch_NotFound() {
        int[] arr = {1, 3, 5, 7, 9, 11, 13, 15};

        assertEquals(-1, BinarySearch.binarySearch(arr, 0));   // меньше минимального
        assertEquals(-1, BinarySearch.binarySearch(arr, 8));   // между элементами
        assertEquals(-1, BinarySearch.binarySearch(arr, 20));  // больше максимального
    }

    @Test
    void testBinarySearch_EmptyArray() {
        int[] arr = {};
        assertEquals(-1, BinarySearch.binarySearch(arr, 5));
    }

    @Test
    void testBinarySearch_SingleElement() {
        int[] arr = {5};

        assertEquals(0, BinarySearch.binarySearch(arr, 5));   // найден
        assertEquals(-1, BinarySearch.binarySearch(arr, 3));  // не найден
    }

    @Test
    void testBinarySearch_DuplicateElements() {
        // Бинарный поиск в массиве с дубликатами (возвращает любой из индексов)
        int[] arr = {1, 2, 2, 2, 3, 4, 5};

        int result = BinarySearch.binarySearch(arr, 2);
        assertTrue(result >= 1 && result <= 3); // должен вернуть индекс 1, 2 или 3
    }
}
