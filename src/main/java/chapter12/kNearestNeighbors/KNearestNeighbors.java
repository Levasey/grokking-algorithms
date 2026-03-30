package chapter12.kNearestNeighbors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Алгоритм k ближайших соседей (KNN): классификация по голосованию среди k ближайших точек
 * и регрессия как среднее целевых значений у k ближайших. Расстояние — евклидово.
 */
public final class KNearestNeighbors {

    private KNearestNeighbors() {
    }

    /**
     * Обучающая точка с дискретной меткой класса.
     *
     * @param features признаки (одинаковая размерность у всех точек в задаче)
     * @param label    метка класса
     */
    public record LabeledPoint(double[] features, String label) {
        public LabeledPoint {
            Objects.requireNonNull(features, "features");
            Objects.requireNonNull(label, "label");
            if (features.length == 0) {
                throw new IllegalArgumentException("features must be non-empty");
            }
            features = features.clone();
        }

        @Override
        public double[] features() {
            return features.clone();
        }
    }

    /**
     * Обучающая точка для регрессии с вещественной целью.
     *
     * @param features признаки
     * @param target   целевое значение
     */
    public record RegressionPoint(double[] features, double target) {
        public RegressionPoint {
            Objects.requireNonNull(features, "features");
            if (features.length == 0) {
                throw new IllegalArgumentException("features must be non-empty");
            }
            features = features.clone();
        }

        @Override
        public double[] features() {
            return features.clone();
        }
    }

    /**
     * Предсказание метки класса: среди {@code k} ближайших к {@code query} точек — большинство голосов.
     * При ничьей выбирается метка ближайшего соседа среди участников ничьей.
     *
     * @param query    вектор признаков (та же длина, что у обучающих точек)
     * @param training обучающая выборка (не {@code null}; элементы не {@code null})
     * @param k        число соседей (положительное, не больше размера выборки)
     * @return предсказанная метка
     */
    public static String classify(double[] query, List<LabeledPoint> training, int k) {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(training, "training");
        if (training.isEmpty()) {
            throw new IllegalArgumentException("training must be non-empty");
        }
        if (k <= 0 || k > training.size()) {
            throw new IllegalArgumentException("k must be in [1, training.size()]");
        }
        int dim = query.length;
        for (int i = 0; i < training.size(); i++) {
            LabeledPoint p = Objects.requireNonNull(training.get(i), "training[" + i + "]");
            if (p.features().length != dim) {
                throw new IllegalArgumentException("feature dimension mismatch");
            }
        }

        List<LabeledPoint> nearest = kNearestLabeled(query, training, k);

        Map<String, Integer> votes = new HashMap<>();
        for (LabeledPoint p : nearest) {
            votes.merge(p.label(), 1, Integer::sum);
        }
        int maxCount = votes.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        List<String> tied = votes.entrySet().stream()
                .filter(e -> e.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .toList();

        if (tied.size() == 1) {
            return tied.getFirst();
        }
        var tieSet = new HashSet<>(tied);
        for (LabeledPoint p : nearest) {
            if (tieSet.contains(p.label())) {
                return p.label();
            }
        }
        return tied.getFirst();
    }

    /**
     * Регрессия: среднее целевых значений у {@code k} ближайших точек.
     *
     * @param query    вектор признаков
     * @param training обучающая выборка
     * @param k        число соседей
     * @return среднее по целям k соседей
     */
    public static double predictAverage(double[] query, List<RegressionPoint> training, int k) {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(training, "training");
        if (training.isEmpty()) {
            throw new IllegalArgumentException("training must be non-empty");
        }
        if (k <= 0 || k > training.size()) {
            throw new IllegalArgumentException("k must be in [1, training.size()]");
        }
        int dim = query.length;
        for (int i = 0; i < training.size(); i++) {
            RegressionPoint p = Objects.requireNonNull(training.get(i), "training[" + i + "]");
            if (p.features().length != dim) {
                throw new IllegalArgumentException("feature dimension mismatch");
            }
        }

        List<RegressionPoint> nearest = kNearestRegression(query, training, k);
        double sum = 0;
        for (RegressionPoint p : nearest) {
            sum += p.target();
        }
        return sum / k;
    }

    /**
     * Квадрат евклидова расстояния между векторами одинаковой длины (без промежуточного {@code sqrt}).
     */
    public static double squaredDistance(double[] a, double[] b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (a.length != b.length) {
            throw new IllegalArgumentException("vectors must have same length");
        }
        double s = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            s += d * d;
        }
        return s;
    }

    private static List<LabeledPoint> kNearestLabeled(double[] query, List<LabeledPoint> training, int k) {
        record Item(LabeledPoint point, double dist2) {
        }
        List<Item> ranked = new ArrayList<>(training.size());
        for (LabeledPoint p : training) {
            ranked.add(new Item(p, squaredDistance(query, p.features())));
        }
        ranked.sort(Comparator.comparingDouble(Item::dist2));
        List<LabeledPoint> out = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            out.add(ranked.get(i).point());
        }
        return out;
    }

    private static List<RegressionPoint> kNearestRegression(double[] query, List<RegressionPoint> training, int k) {
        record Item(RegressionPoint point, double dist2) {
        }
        List<Item> ranked = new ArrayList<>(training.size());
        for (RegressionPoint p : training) {
            ranked.add(new Item(p, squaredDistance(query, p.features())));
        }
        ranked.sort(Comparator.comparingDouble(Item::dist2));
        List<RegressionPoint> out = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            out.add(ranked.get(i).point());
        }
        return out;
    }
}
