package chapter05.hashTable;

import java.util.Objects;

/**
 * Учебная хеш-таблица: коллизии разрешаются связными списками в каждой корзине.
 * При превышении порога загрузки таблица перехешируется — число корзин удваивается.
 */
public class ChainedHashMap<K, V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node<K, V>[] buckets;
    private int size;
    private final float loadFactor;

    public ChainedHashMap(int bucketCount) {
        this(bucketCount, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public ChainedHashMap(int bucketCount, float loadFactor) {
        if (bucketCount <= 0) {
            throw new IllegalArgumentException("bucketCount must be positive");
        }
        if (loadFactor <= 0.0f || loadFactor > 1.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("loadFactor must be in (0, 1]");
        }
        this.loadFactor = loadFactor;
        this.buckets = (Node<K, V>[]) new Node[bucketCount];
    }

    /** Число корзин (для проверки перехеширования в тестах). */
    public int bucketCount() {
        return buckets.length;
    }

    /** Количество пар ключ–значение. */
    public int size() {
        return size;
    }

    private int bucketIndex(K key, int bucketLength) {
        return Math.floorMod(key.hashCode(), bucketLength);
    }

    private int bucketIndex(K key) {
        return bucketIndex(key, buckets.length);
    }

    private void rehash() {
        Node<K, V>[] oldBuckets = buckets;
        int newLength = oldBuckets.length * 2;
        if (newLength < oldBuckets.length) {
            throw new IllegalStateException("bucket array cannot grow further");
        }
        @SuppressWarnings("unchecked")
        Node<K, V>[] newBuckets = (Node<K, V>[]) new Node[newLength];
        buckets = newBuckets;

        for (Node<K, V> bin : oldBuckets) {
            while (bin != null) {
                Node<K, V> next = bin.next;
                int i = bucketIndex(bin.key, newLength);
                bin.next = newBuckets[i];
                newBuckets[i] = bin;
                bin = next;
            }
        }
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "key");
        int i = bucketIndex(key);
        for (Node<K, V> n = buckets[i]; n != null; n = n.next) {
            if (n.key.equals(key)) {
                n.value = value;
                return;
            }
        }
        buckets[i] = new Node<>(key, value, buckets[i]);
        size++;
        if (size > buckets.length * loadFactor) {
            rehash();
        }
    }

    public V get(K key) {
        Objects.requireNonNull(key, "key");
        int i = bucketIndex(key);
        for (Node<K, V> n = buckets[i]; n != null; n = n.next) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        Objects.requireNonNull(key, "key");
        int i = bucketIndex(key);
        for (Node<K, V> n = buckets[i]; n != null; n = n.next) {
            if (n.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
