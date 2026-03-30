package chapter13.linearRegression;

import java.util.Arrays;
import java.util.Objects;

/**
 * Линейная регрессия: модель {@code y ≈ w₀ + w₁·x₁ + … + wₙ·xₙ}.
 * Обучение — пакетный градиентный спуск по среднеквадратичной ошибке (MSE).
 */
public final class LinearRegression {

    private LinearRegression() {
    }

    /**
     * Веса после обучения: {@code weights[0]} — свободный член (bias), {@code weights[j]} — коэффициент при {@code x[j-1]}.
     *
     * @param iterations число выполненных шагов спуска
     */
    public record FitResult(double[] weights, int iterations) {
        public FitResult {
            Objects.requireNonNull(weights, "weights");
            if (iterations < 0) {
                throw new IllegalArgumentException("iterations must be non-negative");
            }
        }
    }

    /**
     * Обучает модель на выборке.
     *
     * @param x             {@code x[i]} — вектор признаков i-го объекта (без добавленной единицы для bias)
     * @param y             целевые значения, длина совпадает с числом строк {@code x}
     * @param learningRate  шаг обучения α
     * @param maxIterations максимум итераций градиентного спуска (&gt; 0)
     * @param epsilon       если после шага max изменение любого веса &lt; epsilon, спуск останавливается (ранняя сходимость)
     */
    public static FitResult train(double[][] x, double[] y, double learningRate, int maxIterations, double epsilon) {
        Objects.requireNonNull(x, "x");
        Objects.requireNonNull(y, "y");
        if (x.length == 0) {
            throw new IllegalArgumentException("x must be non-empty");
        }
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y must have the same number of rows");
        }
        if (learningRate <= 0 || Double.isNaN(learningRate)) {
            throw new IllegalArgumentException("learningRate must be positive");
        }
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("maxIterations must be positive");
        }
        if (epsilon < 0 || Double.isNaN(epsilon)) {
            throw new IllegalArgumentException("epsilon must be non-negative");
        }

        int m = x.length;
        int n = x[0].length;
        for (int i = 1; i < m; i++) {
            if (x[i].length != n) {
                throw new IllegalArgumentException("all feature rows must have the same length");
            }
        }

        int dim = n + 1;
        double[] w = new double[dim];
        double[] grad = new double[dim];
        double[] pred = new double[m];

        int iter = 0;
        for (; iter < maxIterations; iter++) {
            for (int i = 0; i < m; i++) {
                pred[i] = w[0];
                double[] row = x[i];
                for (int j = 0; j < n; j++) {
                    pred[i] += w[j + 1] * row[j];
                }
            }

            grad[0] = 0;
            for (int j = 0; j < n; j++) {
                grad[j + 1] = 0;
            }
            for (int i = 0; i < m; i++) {
                double err = pred[i] - y[i];
                grad[0] += err;
                double[] row = x[i];
                for (int j = 0; j < n; j++) {
                    grad[j + 1] += err * row[j];
                }
            }
            double invM = 1.0 / m;
            for (int j = 0; j < dim; j++) {
                grad[j] *= invM;
            }

            double maxDelta = 0;
            for (int j = 0; j < dim; j++) {
                double step = learningRate * grad[j];
                w[j] -= step;
                maxDelta = Math.max(maxDelta, Math.abs(step));
            }

            if (maxDelta < epsilon) {
                iter++;
                break;
            }
        }

        return new FitResult(Arrays.copyOf(w, w.length), iter);
    }

    /**
     * Предсказание по обученным весам и вектору признаков (той же размерности, что у строк {@code x} при обучении).
     */
    public static double predict(double[] weights, double[] features) {
        Objects.requireNonNull(weights, "weights");
        Objects.requireNonNull(features, "features");
        if (weights.length != features.length + 1) {
            throw new IllegalArgumentException("weights length must be features.length + 1 (bias + coefficients)");
        }
        double s = weights[0];
        for (int j = 0; j < features.length; j++) {
            s += weights[j + 1] * features[j];
        }
        return s;
    }
}
