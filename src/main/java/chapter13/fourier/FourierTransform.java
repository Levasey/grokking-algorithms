package chapter13.fourier;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Дискретное преобразование Фурье (ДПФ) и быстрое преобразование Фурье (БПФ, Cooley–Tukey, основание 2).
 * <p>
 * Прямое преобразование: {@code X[k] = Σ_n x[n]·exp(-2πi·k·n/N)}.<br>
 * Обратное: {@code x[n] = (1/N)·Σ_k X[k]·exp(+2πi·k·n/N)}.
 */
public final class FourierTransform {

    private FourierTransform() {
    }

    public record Complex(double re, double im) {
        public Complex {
            if (Double.isNaN(re) || Double.isNaN(im)) {
                throw new IllegalArgumentException("Complex components must not be NaN");
            }
        }

        public double magnitude() {
            return Math.hypot(re, im);
        }
    }

    /**
     * ДПФ вещественного сигнала; длина произвольная, сложность O(N²).
     */
    public static Complex[] dft(double[] signal) {
        Objects.requireNonNull(signal, "signal");
        if (signal.length == 0) {
            throw new IllegalArgumentException("signal must be non-empty");
        }
        int n = signal.length;
        Complex[] out = new Complex[n];
        double twoPiOverN = 2.0 * Math.PI / n;
        for (int k = 0; k < n; k++) {
            double sumRe = 0;
            double sumIm = 0;
            for (int t = 0; t < n; t++) {
                double angle = -twoPiOverN * k * t;
                double c = Math.cos(angle);
                double s = Math.sin(angle);
                double x = signal[t];
                sumRe += x * c;
                sumIm += x * s;
            }
            out[k] = new Complex(sumRe, sumIm);
        }
        return out;
    }

    /**
     * Обратное ДПФ; длина спектра произвольная.
     */
    public static Complex[] inverseDft(Complex[] spectrum) {
        Objects.requireNonNull(spectrum, "spectrum");
        if (spectrum.length == 0) {
            throw new IllegalArgumentException("spectrum must be non-empty");
        }
        int n = spectrum.length;
        Complex[] out = new Complex[n];
        double twoPiOverN = 2.0 * Math.PI / n;
        double invN = 1.0 / n;
        for (int t = 0; t < n; t++) {
            double sumRe = 0;
            double sumIm = 0;
            for (int k = 0; k < n; k++) {
                double angle = twoPiOverN * k * t;
                double c = Math.cos(angle);
                double s = Math.sin(angle);
                Complex sk = spectrum[k];
                sumRe += sk.re * c - sk.im * s;
                sumIm += sk.re * s + sk.im * c;
            }
            out[t] = new Complex(sumRe * invN, sumIm * invN);
        }
        return out;
    }

    /**
     * БПФ вещественного сигнала; {@code signal.length} должна быть степенью двойки (&gt;= 1).
     */
    public static Complex[] fft(double[] signal) {
        Objects.requireNonNull(signal, "signal");
        if (signal.length == 0) {
            throw new IllegalArgumentException("signal must be non-empty");
        }
        requirePowerOfTwo(signal.length);
        Complex[] a = new Complex[signal.length];
        for (int i = 0; i < signal.length; i++) {
            a[i] = new Complex(signal[i], 0);
        }
        fftInPlace(a, false);
        return a;
    }

    /**
     * Обратное БПФ; длина спектра — степень двойки.
     */
    public static Complex[] inverseFft(Complex[] spectrum) {
        Objects.requireNonNull(spectrum, "spectrum");
        if (spectrum.length == 0) {
            throw new IllegalArgumentException("spectrum must be non-empty");
        }
        requirePowerOfTwo(spectrum.length);
        Complex[] a = Arrays.copyOf(spectrum, spectrum.length);
        fftInPlace(a, true);
        return a;
    }

    /**
     * ДПФ с параллельным вычислением по частотам {@code k}; даёт тот же результат, что {@link #dft(double[])}.
     */
    public static Complex[] parallelDft(double[] signal) {
        Objects.requireNonNull(signal, "signal");
        if (signal.length == 0) {
            throw new IllegalArgumentException("signal must be non-empty");
        }
        int n = signal.length;
        double twoPiOverN = 2.0 * Math.PI / n;
        Complex[] out = new Complex[n];
        IntStream.range(0, n).parallel().forEach(k -> {
            double sumRe = 0;
            double sumIm = 0;
            for (int t = 0; t < n; t++) {
                double angle = -twoPiOverN * k * t;
                double c = Math.cos(angle);
                double s = Math.sin(angle);
                double x = signal[t];
                sumRe += x * c;
                sumIm += x * s;
            }
            out[k] = new Complex(sumRe, sumIm);
        });
        return out;
    }

    /**
     * Обратное ДПФ; параллельно по индексу времени {@code t}.
     */
    public static Complex[] parallelInverseDft(Complex[] spectrum) {
        Objects.requireNonNull(spectrum, "spectrum");
        if (spectrum.length == 0) {
            throw new IllegalArgumentException("spectrum must be non-empty");
        }
        int n = spectrum.length;
        double twoPiOverN = 2.0 * Math.PI / n;
        double invN = 1.0 / n;
        Complex[] out = new Complex[n];
        IntStream.range(0, n).parallel().forEach(t -> {
            double sumRe = 0;
            double sumIm = 0;
            for (int k = 0; k < n; k++) {
                double angle = twoPiOverN * k * t;
                double c = Math.cos(angle);
                double s = Math.sin(angle);
                Complex sk = spectrum[k];
                sumRe += sk.re * c - sk.im * s;
                sumIm += sk.re * s + sk.im * c;
            }
            out[t] = new Complex(sumRe * invN, sumIm * invN);
        });
        return out;
    }

    /**
     * БПФ; на каждом этапе бабочки независимые блоки длины {@code len} обрабатываются параллельно.
     */
    public static Complex[] parallelFft(double[] signal) {
        Objects.requireNonNull(signal, "signal");
        if (signal.length == 0) {
            throw new IllegalArgumentException("signal must be non-empty");
        }
        requirePowerOfTwo(signal.length);
        Complex[] a = new Complex[signal.length];
        for (int i = 0; i < signal.length; i++) {
            a[i] = new Complex(signal[i], 0);
        }
        parallelFftInPlace(a, false);
        return a;
    }

    /**
     * Обратное БПФ (параллельные этапы бабочек).
     */
    public static Complex[] parallelInverseFft(Complex[] spectrum) {
        Objects.requireNonNull(spectrum, "spectrum");
        if (spectrum.length == 0) {
            throw new IllegalArgumentException("spectrum must be non-empty");
        }
        requirePowerOfTwo(spectrum.length);
        Complex[] a = Arrays.copyOf(spectrum, spectrum.length);
        parallelFftInPlace(a, true);
        return a;
    }

    private static void requirePowerOfTwo(int n) {
        if (n <= 0 || (n & (n - 1)) != 0) {
            throw new IllegalArgumentException("length must be a positive power of two");
        }
    }

    private static void fftInPlace(Complex[] a, boolean inverse) {
        int n = a.length;
        bitReversePermute(a);

        int sign = inverse ? 1 : -1;
        for (int len = 2; len <= n; len <<= 1) {
            double ang = sign * 2.0 * Math.PI / len;
            double wlenRe = Math.cos(ang);
            double wlenIm = Math.sin(ang);
            for (int i = 0; i < n; i += len) {
                butterflyBlock(a, len, i, wlenRe, wlenIm);
            }
        }

        if (inverse) {
            scaleInverse(a, n);
        }
    }

    private static void parallelFftInPlace(Complex[] a, boolean inverse) {
        int n = a.length;
        bitReversePermute(a);

        int sign = inverse ? 1 : -1;
        for (int len = 2; len <= n; len <<= 1) {
            final int stageLen = len;
            double ang = sign * 2.0 * Math.PI / stageLen;
            double wlenRe = Math.cos(ang);
            double wlenIm = Math.sin(ang);
            int step = stageLen;
            int numBlocks = n / step;
            IntStream.range(0, numBlocks).parallel().forEach(block -> {
                int i = block * step;
                butterflyBlock(a, stageLen, i, wlenRe, wlenIm);
            });
        }

        if (inverse) {
            IntStream.range(0, n).parallel().forEach(i -> {
                Complex z = a[i];
                a[i] = new Complex(z.re / n, z.im / n);
            });
        }
    }

    private static void butterflyBlock(Complex[] a, int len, int i, double wlenRe, double wlenIm) {
        double wRe = 1.0;
        double wIm = 0.0;
        int half = len >>> 1;
        for (int j = 0; j < half; j++) {
            int i1 = i + j;
            int i2 = i1 + half;
            Complex u = a[i1];
            Complex v = a[i2];
            double vr = v.re * wRe - v.im * wIm;
            double vi = v.re * wIm + v.im * wRe;
            a[i1] = new Complex(u.re + vr, u.im + vi);
            a[i2] = new Complex(u.re - vr, u.im - vi);
            double nwRe = wRe * wlenRe - wIm * wlenIm;
            double nwIm = wRe * wlenIm + wIm * wlenRe;
            wRe = nwRe;
            wIm = nwIm;
        }
    }

    private static void scaleInverse(Complex[] a, int n) {
        double invN = 1.0 / n;
        for (int i = 0; i < n; i++) {
            Complex z = a[i];
            a[i] = new Complex(z.re * invN, z.im * invN);
        }
    }

    private static void bitReversePermute(Complex[] a) {
        int n = a.length;
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >>> 1;
            for (; (j & bit) != 0; bit >>>= 1) {
                j ^= bit;
            }
            j ^= bit;
            if (i < j) {
                Complex tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
        }
    }
}
