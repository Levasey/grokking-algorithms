package chapter05.hashTable;

/**
 * Пример из главы 5: быстрая проверка, голосовал ли человек уже (O(1) в среднем).
 */
public class VoterRegistry {

    private final ChainedHashMap<String, Boolean> voted;

    public VoterRegistry(int bucketCount) {
        this.voted = new ChainedHashMap<>(bucketCount);
    }

    /**
     * @return {@code true}, если имя зарегистрировано впервые (допущен к «голосу»);
     *         {@code false}, если такое имя уже было.
     */
    public boolean tryRegister(String name) {
        if (voted.containsKey(name)) {
            return false;
        }
        voted.put(name, Boolean.TRUE);
        return true;
    }
}
