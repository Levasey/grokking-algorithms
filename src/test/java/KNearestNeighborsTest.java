import chapter12.kNearestNeighbors.KNearestNeighbors;
import chapter12.kNearestNeighbors.KNearestNeighbors.LabeledPoint;
import chapter12.kNearestNeighbors.KNearestNeighbors.RegressionPoint;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KNearestNeighborsTest {

    @Test
    public void squaredDistance_samePoint_isZero() {
        double[] v = {1, 2, 3};
        assertEquals(0.0, KNearestNeighbors.squaredDistance(v, v));
    }

    @Test
    public void squaredDistance_1d() {
        assertEquals(25.0, KNearestNeighbors.squaredDistance(new double[]{2}, new double[]{7}));
    }

    @Test
    public void classify_k1_nearestLabel() {
        List<LabeledPoint> data = List.of(
                new LabeledPoint(new double[]{0, 0}, "A"),
                new LabeledPoint(new double[]{10, 10}, "B")
        );
        assertEquals("A", KNearestNeighbors.classify(new double[]{1, 0}, data, 1));
        assertEquals("B", KNearestNeighbors.classify(new double[]{9, 10}, data, 1));
    }

    @Test
    public void classify_k3_majorityVotes() {
        List<LabeledPoint> data = List.of(
                new LabeledPoint(new double[]{0, 0}, "cat"),
                new LabeledPoint(new double[]{0, 1}, "cat"),
                new LabeledPoint(new double[]{5, 5}, "dog"),
                new LabeledPoint(new double[]{6, 6}, "dog")
        );
        assertEquals("cat", KNearestNeighbors.classify(new double[]{0, 0.5}, data, 3));
    }

    @Test
    public void classify_voteTie_prefersCloserNeighborOrder() {
        List<LabeledPoint> data = List.of(
                new LabeledPoint(new double[]{0, 0}, "A"),
                new LabeledPoint(new double[]{1.2, 0}, "B"),
                new LabeledPoint(new double[]{0, 10}, "A")
        );
        assertEquals("B", KNearestNeighbors.classify(new double[]{1, 0}, data, 2));
    }

    @Test
    public void predictAverage_k2_isMeanOfTwoClosestTargets() {
        List<RegressionPoint> data = List.of(
                new RegressionPoint(new double[]{0}, 10),
                new RegressionPoint(new double[]{2}, 20),
                new RegressionPoint(new double[]{10}, 100)
        );
        double pred = KNearestNeighbors.predictAverage(new double[]{1}, data, 2);
        assertEquals(15.0, pred, 1e-9);
    }

    @Test
    public void classify_rejectsEmptyTraining() {
        assertThrows(IllegalArgumentException.class,
                () -> KNearestNeighbors.classify(new double[]{1}, List.of(), 1));
    }

    @Test
    public void classify_rejectsBadK() {
        List<LabeledPoint> one = List.of(new LabeledPoint(new double[]{0}, "a"));
        assertThrows(IllegalArgumentException.class, () -> KNearestNeighbors.classify(new double[]{0}, one, 0));
        assertThrows(IllegalArgumentException.class, () -> KNearestNeighbors.classify(new double[]{0}, one, 2));
    }
}
