package chapter08.balancedTrees;

import java.util.Objects;

/**
 * AVL-дерево — самобалансирующееся бинарное дерево поиска из главы 8 второго издания книги:
 * высоты левого и правого поддерева любой вершины отличаются не более чем на 1.
 * При нарушении выполняются повороты (одиночные или двойные).
 *
 * @param <K> тип ключей, сравниваемых естественным порядком
 */
public class AvlTree<K extends Comparable<? super K>> {

    private Node<K> root;
    private int size;

    private static final class Node<K> {
        K key;
        Node<K> left;
        Node<K> right;
        /** Высота поддерева: лист = 0. */
        int height;

        Node(K key) {
            this.key = key;
            this.height = 0;
        }
    }

    public int size() {
        return size;
    }

    /** Высота дерева (-1 для пустого, 0 для одного узла). */
    public int height() {
        return height(root);
    }

    public boolean contains(K key) {
        Objects.requireNonNull(key, "key");
        return find(root, key) != null;
    }

    /**
     * Вставка ключа. Повторная вставка того же ключа не меняет дерево.
     */
    public void insert(K key) {
        Objects.requireNonNull(key, "key");
        root = insert(root, key);
    }

    private Node<K> find(Node<K> node, K key) {
        while (node != null) {
            int c = key.compareTo(node.key);
            if (c == 0) {
                return node;
            }
            node = c < 0 ? node.left : node.right;
        }
        return null;
    }

    private static <K> int height(Node<K> n) {
        return n == null ? -1 : n.height;
    }

    private static <K> void refreshHeight(Node<K> n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    /** balance = h(left) − h(right), для AVL должно быть в [-1, 1]. */
    private static <K> int balanceFactor(Node<K> n) {
        return height(n.left) - height(n.right);
    }

    private Node<K> rotateRight(Node<K> y) {
        Node<K> x = y.left;
        Node<K> t2 = x.right;
        x.right = y;
        y.left = t2;
        refreshHeight(y);
        refreshHeight(x);
        return x;
    }

    private Node<K> rotateLeft(Node<K> x) {
        Node<K> y = x.right;
        Node<K> t2 = y.left;
        y.left = x;
        x.right = t2;
        refreshHeight(x);
        refreshHeight(y);
        return y;
    }

    private Node<K> rebalance(Node<K> node) {
        refreshHeight(node);
        int bf = balanceFactor(node);

        if (bf > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        if (bf < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private Node<K> insert(Node<K> node, K key) {
        if (node == null) {
            size++;
            return new Node<>(key);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key);
        } else if (cmp > 0) {
            node.right = insert(node.right, key);
        } else {
            return node;
        }
        return rebalance(node);
    }

    /**
     * Проверка инвариантов BST и AVL (для тестов и отладки).
     *
     * @throws IllegalStateException если дерево некорректно
     */
    public void validate() {
        validateNode(root, null, null);
    }

    private void validateNode(Node<K> node, K min, K max) {
        if (node == null) {
            return;
        }
        if (min != null && node.key.compareTo(min) <= 0) {
            throw new IllegalStateException("BST violated: " + node.key + " <= " + min);
        }
        if (max != null && node.key.compareTo(max) >= 0) {
            throw new IllegalStateException("BST violated: " + node.key + " >= " + max);
        }
        int bf = balanceFactor(node);
        if (bf < -1 || bf > 1) {
            throw new IllegalStateException("AVL violated at " + node.key + ", balance=" + bf);
        }
        int lh = height(node.left);
        int rh = height(node.right);
        if (node.height != 1 + Math.max(lh, rh)) {
            throw new IllegalStateException("Height field wrong at " + node.key);
        }
        validateNode(node.left, min, node.key);
        validateNode(node.right, node.key, max);
    }
}
