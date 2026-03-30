import chapter13.linearRegression.LinearRegression;
import chapter13.linearRegression.LinearRegression.FitResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinearRegressionTest {

    @Test
    public void train_perfectLine_oneFeature_recoversSlopeAndIntercept() {
        double[][] x = {
                {0}, {1}, {2}
        };
        double[] y    = {2, 5, 8};

        FitResult fit = LinearRegression.train(x, y, 0.15, 50_000, 1e-12);

        double[] w = fit.weights();
        assertEquals(2, w.length);
        assertEquals(2.0, w[0], 1e-6);
        assertEquals(3.0, w[1], 1e-6);
        assertTrue(fit.iterations() < 50_000);
    }

    @Test
    public void train_twoFeatures_plane() {
        double[][] x = {
                {0, 0},
                {1, 0},
                {0, 1},
                {1, 1},
                {2, 1}
        };
        double[] y = {1, 2, 3, 4, 5};

        FitResult fit = LinearRegression.train(x, y, 0.08, 100_000, 1e-10);
        double[] w = fit.weights();

        assertEquals(1.0, w[0], 5e-4);
        assertEquals(1.0, w[1], 5e-4);
        assertEquals(2.0, w[2], 5e-4);
    }

    @Test
    public void predict_matchesManualDotProduct() {
        double[] w = {10, -0.5, 2};
        assertEquals(10.0, LinearRegression.predict(w, new double[]{0, 0}), 1e-12);
        assertEquals(11.5, LinearRegression.predict(w, new double[]{1, 1}), 1e-12);
    }

    @Test
    public void train_rejectsDimensionMismatch() {
        double[][] x = {{1}, {2}};
        double[] y   = {1};
        assertThrows(IllegalArgumentException.class,
                () -> LinearRegression.train(x, y, 0.1, 100, 1e-9));
    }

    @Test
    public void train_rejectsEmptyX() {
        assertThrows(IllegalArgumentException.class,
                () -> LinearRegression.train(new double[0][], new double[0], 0.1, 100, 1e-9));
    }

    @Test
    public void predict_rejectsBadWeightLength() {
        assertThrows(IllegalArgumentException.class,
                () -> LinearRegression.predict(new double[]{1, 2}, new double[]{1, 2, 3}));
    }

    @Test
    public void trainParallel_matchesTrain_oneFeature() {
        double[][] x = {{0}, {1}, {2}};
        double[] y = {2, 5, 8};
        FitResult seq = LinearRegression.train(x, y, 0.15, 50_000, 1e-12);
        FitResult par = LinearRegression.trainParallel(x, y, 0.15, 50_000, 1e-12);
        assertEquals(seq.iterations(), par.iterations());
        assertArrayEquals(seq.weights(), par.weights(), 1e-9);
    }

    @Test
    public void trainParallel_matchesTrain_twoFeatures() {
        double[][] x = {
                {0, 0},
                {1, 0},
                {0, 1},
                {1, 1},
                {2, 1}
        };
        double[] y = {1, 2, 3, 4, 5};
        FitResult seq = LinearRegression.train(x, y, 0.08, 100_000, 1e-10);
        FitResult par = LinearRegression.trainParallel(x, y, 0.08, 100_000, 1e-10);
        assertEquals(seq.iterations(), par.iterations());
        assertArrayEquals(seq.weights(), par.weights(), 1e-9);
    }
}
