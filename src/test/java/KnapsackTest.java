import chapter11.dynamicProgramming.Knapsack;
import chapter11.dynamicProgramming.Knapsack.Item;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KnapsackTest {

    @Test
    public void maxValue_emptyItems_isZero() {
        assertEquals(0, Knapsack.maxValue(10, List.of()));
    }

    @Test
    public void maxValue_zeroCapacity_isZero() {
        List<Item> items = List.of(new Item(1, 100));
        assertEquals(0, Knapsack.maxValue(0, items));
    }

    @Test
    public void maxValue_classicExampleFromBook() {
        List<Item> items = List.of(
                new Item(1, 1500),
                new Item(4, 3000),
                new Item(3, 2000),
                new Item(1, 2000)
        );
        assertEquals(4000, Knapsack.maxValue(4, items));
    }

    @Test
    public void maxValueItemIndices_reconstructsOptimalSet() {
        List<Item> items = List.of(
                new Item(1, 1500),
                new Item(4, 3000),
                new Item(3, 2000),
                new Item(1, 2000)
        );
        List<Integer> idx = Knapsack.maxValueItemIndices(4, items);
        int sumW = 0;
        int sumV = 0;
        for (int i : idx) {
            sumW += items.get(i).weight();
            sumV += items.get(i).value();
        }
        assertEquals(4, sumW);
        assertEquals(4000, sumV);
        assertEquals(4000, Knapsack.maxValue(4, items));
    }

    @Test
    public void item_rejectsNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () -> new Item(-1, 0));
    }
}
