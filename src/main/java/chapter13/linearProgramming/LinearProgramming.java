package chapter13.linearProgramming;

import java.util.Objects;

/**
 * Линейное программирование: максимизация {@code c·x} при ограничениях {@code A·x ≤ b}, {@code x ≥ 0}.
 * Слаки добавляются автоматически; симплекс-метод (одна фаза при {@code b ≥ 0}).
 * <p>
 * <b>Учебный объём:</b> при нарушенных предпосылках (например, отрицательные {@code bᵢ} без двухфазного симплекса)
 * или вырожденности или ошибках округления поведение может отличаться от промышленных солверов.
 * Статус {@link Status#INFEASIBLE} зарезервирован; текущая реализация на успешном проходе возвращает только
 * {@link Status#OPTIMAL} или {@link Status#UNBOUNDED}.
 */
public final class LinearProgramming {

    private static final double EPS = 1e-9;

    private LinearProgramming() {
    }

    public enum Status {
        /** Найден оптимум на вершине многогранника допустимых решений */
        OPTIMAL,
        /** Целевая функция не ограничена сверху на допустимом множестве */
        UNBOUNDED,
        /**
         * Недопустимая задача в общем виде (например, пустое допустимое множество). В данной однофазной реализации
         * не возвращается успешным завершением {@link #maximize(double[], double[][], double[])} — значение
         * зафиксировано для расширения API и согласования с полноценными солверами.
         */
        INFEASIBLE
    }

    /**
     * @param status итог симплекса
     * @param x при {@link Status#OPTIMAL} — значения исходных переменных (длина {@code c.length}); при
     *          {@link Status#UNBOUNDED} — {@code null}; при {@link Status#INFEASIBLE} — не используется
     * @param objectiveValue при {@link Status#OPTIMAL} — {@code c·x}; при {@link Status#UNBOUNDED} — {@code Infinity};
     *                       иначе не определён
     * @param pivotCount число выполненных опорных преобразований
     */
    public record Solution(Status status, double[] x, double objectiveValue, int pivotCount) {
        public Solution {
            Objects.requireNonNull(status, "status");
            if (status == Status.OPTIMAL) {
                Objects.requireNonNull(x, "x");
                x = x.clone();
            }
        }

        @Override
        public double[] x() {
            return x == null ? null : x.clone();
        }
    }

    /**
     * Максимизирует {@code Σ cⱼ xⱼ} при {@code Σ Aᵢⱼ xⱼ ≤ bᵢ}, {@code xⱼ ≥ 0}.
     *
     * @param c коэффициенты цели, длина n
     * @param A матрица ограничений m×n
     * @param b правые части, длина m; все {@code bᵢ ≥ 0} (иначе см. двухфазный симплекс — здесь не реализован)
     * @throws IllegalArgumentException при несогласованных размерах или отрицательном {@code bᵢ}
     */
    public static Solution maximize(double[] c, double[][] A, double[] b) {
        Objects.requireNonNull(c, "c");
        Objects.requireNonNull(A, "A");
        Objects.requireNonNull(b, "b");
        if (c.length == 0) {
            throw new IllegalArgumentException("c must be non-empty");
        }
        int n = c.length;
        if (A.length != b.length) {
            throw new IllegalArgumentException("A row count must equal b.length");
        }
        if (b.length == 0) {
            throw new IllegalArgumentException("b must be non-empty");
        }
        for (int i = 0; i < aRows(A); i++) {
            if (A[i].length != n) {
                throw new IllegalArgumentException("each A row must have length c.length");
            }
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i] + EPS < 0 || Double.isNaN(b[i])) {
                throw new IllegalArgumentException("all b[i] must be non-negative (b[" + i + "]=" + b[i] + ")");
            }
        }

        int m = b.length;
        int slack = m;
        int cols = n + slack + 1;
        int objRow = m;
        double[][] t = new double[m + 1][cols];

        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, t[i], 0, n);
            t[i][n + i] = 1.0;
            t[i][cols - 1] = b[i];
        }
        for (int j = 0; j < n; j++) {
            t[objRow][j] = -c[j];
        }

        int[] basis = new int[m];
        for (int i = 0; i < m; i++) {
            basis[i] = n + i;
        }

        int pivots = 0;
        while (true) {
            int entering = -1;
            double minObj = 0;
            for (int j = 0; j < n + slack; j++) {
                double v = t[objRow][j];
                if (v < -EPS && (entering < 0 || v < minObj)) {
                    minObj = v;
                    entering = j;
                }
            }
            if (entering < 0) {
                break;
            }

            int leaving = -1;
            double minRatio = Double.POSITIVE_INFINITY;
            for (int i = 0; i < m; i++) {
                double aij = t[i][entering];
                if (aij > EPS) {
                    double ratio = t[i][cols - 1] / aij;
                    if (ratio < minRatio - EPS) {
                        minRatio = ratio;
                        leaving = i;
                    } else if (Math.abs(ratio - minRatio) <= EPS && basis[i] < basis[leaving]) {
                        leaving = i;
                    }
                }
            }
            if (leaving < 0) {
                return new Solution(Status.UNBOUNDED, null, Double.POSITIVE_INFINITY, pivots);
            }

            pivot(t, leaving, entering, objRow, cols);
            basis[leaving] = entering;
            pivots++;
        }

        double[] x = new double[n];
        for (int j = 0; j < n; j++) {
            int row = basicRow(t, m, objRow, j);
            x[j] = row < 0 ? 0.0 : t[row][cols - 1];
        }
        double z = 0.0;
        for (int j = 0; j < n; j++) {
            z += c[j] * x[j];
        }
        return new Solution(Status.OPTIMAL, x, z, pivots);
    }

    /**
     * Сводит к {@link #maximize}: минимизирует {@code c·x} ⇔ максимизирует {@code (-c)·x}.
     */
    public static Solution minimize(double[] c, double[][] A, double[] b) {
        Objects.requireNonNull(c, "c");
        double[] neg = new double[c.length];
        for (int j = 0; j < c.length; j++) {
            neg[j] = -c[j];
        }
        Solution s = maximize(neg, A, b);
        if (s.status() != Status.OPTIMAL) {
            return s;
        }
        return new Solution(Status.OPTIMAL, s.x(), -s.objectiveValue(), s.pivotCount());
    }

    private static int aRows(double[][] A) {
        return A.length;
    }

    private static void pivot(double[][] t, int leaving, int entering, int objRow, int cols) {
        double piv = t[leaving][entering];
        for (int j = 0; j < cols; j++) {
            t[leaving][j] /= piv;
        }
        for (int i = 0; i < t.length; i++) {
            if (i == leaving) {
                continue;
            }
            double f = t[i][entering];
            if (Math.abs(f) < EPS) {
                continue;
            }
            for (int j = 0; j < cols; j++) {
                t[i][j] -= f * t[leaving][j];
            }
        }
    }

    /**
     * Номер строки ограничения (0 … m-1), где столбец {@code col} — базисный: единица в этой строке,
     * нули в остальных строках ограничений и ноль в строке цели.
     */
    private static int basicRow(double[][] t, int m, int objRow, int col) {
        if (Math.abs(t[objRow][col]) > EPS) {
            return -1;
        }
        int row = -1;
        for (int i = 0; i < m; i++) {
            if (Math.abs(t[i][col] - 1.0) > EPS) {
                continue;
            }
            boolean ok = true;
            for (int k = 0; k < m; k++) {
                if (k != i && Math.abs(t[k][col]) > EPS) {
                    ok = false;
                    break;
                }
            }
            if (!ok) {
                continue;
            }
            if (row >= 0) {
                return -1;
            }
            row = i;
        }
        return row;
    }

    /**
     * Утилита для проверки: вычисляет {@code c·x} заново от переданного {@code x}.
     */
    public static double objectiveAt(double[] c, double[] x) {
        Objects.requireNonNull(c, "c");
        Objects.requireNonNull(x, "x");
        if (c.length != x.length) {
            throw new IllegalArgumentException("c and x must have the same length");
        }
        double s = 0.0;
        for (int j = 0; j < c.length; j++) {
            s += c[j] * x[j];
        }
        return s;
    }

    /**
     * Проверяет допустимость: {@code A·x ≤ b} и {@code x ≥ 0} с заданным допуском {@code tolerance}
     * по нарушениям ограничений и отрицательным компонентам {@code x} (симплекс внутри использует порядка {@code 1e-9}).
     */
    public static boolean isFeasible(double[][] A, double[] b, double[] x, double tolerance) {
        Objects.requireNonNull(A, "A");
        Objects.requireNonNull(b, "b");
        Objects.requireNonNull(x, "x");
        for (double v : x) {
            if (v < -tolerance) {
                return false;
            }
        }
        int m = A.length;
        if (b.length != m) {
            throw new IllegalArgumentException("A row count must equal b.length");
        }
        int n = x.length;
        for (int i = 0; i < m; i++) {
            if (A[i].length != n) {
                throw new IllegalArgumentException("A row width must equal x.length");
            }
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            if (sum > b[i] + tolerance) {
                return false;
            }
        }
        return true;
    }
}
