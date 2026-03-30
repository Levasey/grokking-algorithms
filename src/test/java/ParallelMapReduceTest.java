import chapter13.parallel.ParallelMapReduce;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParallelMapReduceTest {

    @Test
    public void parallelSum_empty_isZero() {
        assertEquals(0.0, ParallelMapReduce.parallelSum(new double[0]), 0.0);
    }

    @Test
    public void parallelSum_matchesExpected() {
        double[] v = {1, 2, 3, 4, 5};
        assertEquals(15.0, ParallelMapReduce.parallelSum(v), 1e-12);
    }

    @Test
    public void mapReduce_sumOfSquares() {
        Integer r = ParallelMapReduce.mapReduce(
                5,
                i -> i * i,
                0,
                Integer::sum);
        assertEquals(0 + 1 + 4 + 9 + 16, r);
    }

    @Test
    public void mapReduce_rejectsNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> ParallelMapReduce.mapReduce(-1, i -> i, 0, Integer::sum));
    }
}
