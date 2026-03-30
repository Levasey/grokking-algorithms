package chapter10.greedyAlgorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static GreedySetCover.chooseStations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreedySetCoverTest {

    @Test
    @DisplayName("Классический пример из главы: штаты и радиостанции")
    void bookExampleStatesAndStations() {
        Map<String, Set<String>> stations = new LinkedHashMap<>();
        stations.put("kone", Set.of("id", "nv", "ut"));
        stations.put("ktwo", Set.of("wa", "id", "mt"));
        stations.put("kthree", Set.of("or", "nv", "ca"));
        stations.put("kfour", Set.of("nv", "ut"));
        stations.put("kfive", Set.of("ca", "az"));

        Set<String> needed = Set.of("mt", "wa", "or", "id", "nv", "ut", "ca", "az");

        List<String> picked = chooseStations(stations, needed);

        Set<String> covered = new HashSet<>();
        for (String name : picked) {
            covered.addAll(stations.get(name));
        }
        for (String state : needed) {
            assertTrue(covered.contains(state), "state " + state + " must be covered");
        }
        assertEquals(4, picked.size());
        assertEquals(List.of("kone", "kfive", "ktwo", "kthree"), picked);
    }

    @Test
    @DisplayName("Одна станция покрывает всё")
    void singleStationCoversAll() {
        Map<String, Set<String>> stations = Map.of("only", Set.of("a", "b", "c"));
        assertEquals(List.of("only"), chooseStations(stations, Set.of("a", "b", "c")));
    }

    @Test
    @DisplayName("Пустая вселенная даёт пустой набор станций")
    void emptyUniverse() {
        Map<String, Set<String>> stations = Map.of("s", Set.of("x"));
        assertTrue(chooseStations(stations, Set.of()).isEmpty());
    }

    @Test
    @DisplayName("Невозможное покрытие: останавливаемся без добавления бессмысленных станций")
    void cannotCoverAll() {
        Map<String, Set<String>> stations = Map.of(
                "a", Set.of("1"),
                "b", Set.of("2")
        );
        List<String> picked = chooseStations(stations, Set.of("1", "2", "missing"));
        assertEquals(List.of("a", "b"), picked);
    }
}
