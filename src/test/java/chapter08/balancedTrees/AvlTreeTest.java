package chapter08.balancedTrees;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvlTreeTest {

    @Test
    @DisplayName("Поиск и размер после вставок")
    void containsAndSize() {
        AvlTree<Integer> t = new AvlTree<>();
        for (int x : new int[] {10, 5, 15, 3, 7}) {
            t.insert(x);
        }
        assertEquals(5, t.size());
        assertTrue(t.contains(7));
        assertFalse(t.contains(99));
        assertDoesNotThrow(t::validate);
    }

    @Test
    @DisplayName("Дубликаты не увеличивают размер")
    void duplicateIgnored() {
        AvlTree<String> t = new AvlTree<>();
        t.insert("a");
        t.insert("a");
        assertEquals(1, t.size());
        assertDoesNotThrow(t::validate);
    }

    @Test
    @DisplayName("Последовательная вставка сохраняет логарифмическую высоту")
    void sequentialInsertsStayBalanced() {
        AvlTree<Integer> t = new AvlTree<>();
        for (int i = 0; i < 100; i++) {
            t.insert(i);
        }
        assertEquals(100, t.size());
        // для AVL: h <= 1.44 * log2(n+2) - 0.328; при n=100 достаточно h < 12
        assertTrue(t.height() < 12, "height=" + t.height());
        assertDoesNotThrow(t::validate);
    }

    @Test
    @DisplayName("Поворот LL: вставки 3,2,1")
    void leftLeftCase() {
        AvlTree<Integer> t = new AvlTree<>();
        t.insert(3);
        t.insert(2);
        t.insert(1);
        assertDoesNotThrow(t::validate);
        assertTrue(t.contains(1) && t.contains(2) && t.contains(3));
    }

    @Test
    @DisplayName("Поворот RR: вставки 1,2,3")
    void rightRightCase() {
        AvlTree<Integer> t = new AvlTree<>();
        t.insert(1);
        t.insert(2);
        t.insert(3);
        assertDoesNotThrow(t::validate);
    }

    @Test
    @DisplayName("Двойной LR: вставки 3,1,2")
    void leftRightCase() {
        AvlTree<Integer> t = new AvlTree<>();
        t.insert(3);
        t.insert(1);
        t.insert(2);
        assertDoesNotThrow(t::validate);
    }

    @Test
    @DisplayName("Двойной RL: вставки 1,3,2")
    void rightLeftCase() {
        AvlTree<Integer> t = new AvlTree<>();
        t.insert(1);
        t.insert(3);
        t.insert(2);
        assertDoesNotThrow(t::validate);
    }

    @Test
    @DisplayName("null ключ запрещён")
    void nullKey() {
        AvlTree<Integer> t = new AvlTree<>();
        assertThrows(NullPointerException.class, () -> t.insert(null));
        assertThrows(NullPointerException.class, () -> t.contains(null));
    }

    @Test
    @DisplayName("Пустое дерево")
    void empty() {
        AvlTree<Integer> t = new AvlTree<>();
        assertEquals(0, t.size());
        assertEquals(-1, t.height());
        assertDoesNotThrow(t::validate);
    }
}
