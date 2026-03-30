package chapter04.quicksort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuickSort2Test {

    static Stream<Arguments> ascendingCases() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(10, 7, 8, 9, 1, 5),
                        Arrays.asList(1, 5, 7, 8, 9, 10)),
                Arguments.of(
                        Arrays.asList(3, 3, 3),
                        Arrays.asList(3, 3, 3)),
                Arguments.of(
                        List.of(42),
                        List.of(42)),
                Arguments.of(
                        Collections.<Integer>emptyList(),
                        Collections.<Integer>emptyList()),
                Arguments.of(
                        Arrays.asList(5, 4, 3, 2, 1),
                        Arrays.asList(1, 2, 3, 4, 5)),
                Arguments.of(
                        Arrays.asList(2, 8, 2, 1),
                        Arrays.asList(1, 2, 2, 8)));
    }

    @ParameterizedTest
    @MethodSource("ascendingCases")
    void run_defaultIsAscending(List<Integer> input, List<Integer> expected) {
        assertEquals(expected, QuickSort2.run(input));
    }

    @ParameterizedTest
    @MethodSource("ascendingCases")
    void run_explicitAsc_matchesDefault(List<Integer> input, List<Integer> expected) {
        assertEquals(expected, QuickSort2.run(input, "asc"));
    }

    @Test
    void run_descending() {
        assertEquals(Collections.emptyList(), QuickSort2.run(Collections.emptyList(), "desc"));
        List<Integer> input = Arrays.asList(10, 7, 8, 9, 1, 5);
        assertEquals(Arrays.asList(10, 9, 8, 7, 5, 1), QuickSort2.run(input, "desc"));
    }

    @Test
    void run_invalidDirection_throws() {
        List<Integer> input = List.of(1, 2);
        assertThrows(IllegalArgumentException.class, () -> QuickSort2.run(input, "up"));
        assertEquals("Direction must be 'asc' or 'desc'",
                assertThrows(IllegalArgumentException.class, () -> QuickSort2.run(input, "")).getMessage());
    }

    @Test
    void run_doesNotMutateOriginalList() {
        List<Integer> original = new ArrayList<>(Arrays.asList(2, 1));
        List<Integer> copy = new ArrayList<>(original);
        QuickSort2.run(original);
        assertEquals(copy, original);
    }
}
