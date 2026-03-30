package chapter13.fourier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FourierTransformTest {

    private static void assertComplexEquals(FourierTransform.Complex expected, FourierTransform.Complex actual, double eps) {
        assertEquals(expected.re(), actual.re(), eps, "re");
        assertEquals(expected.im(), actual.im(), eps, "im");
    }

    @Test
    public void dft_constant_signal_dc_only() {
        double[] x = {1, 1, 1, 1};
        FourierTransform.Complex[] xk = FourierTransform.dft(x);
        assertEquals(4, xk.length);
        assertComplexEquals(new FourierTransform.Complex(4, 0), xk[0], 1e-9);
        assertComplexEquals(new FourierTransform.Complex(0, 0), xk[1], 1e-9);
        assertComplexEquals(new FourierTransform.Complex(0, 0), xk[2], 1e-9);
        assertComplexEquals(new FourierTransform.Complex(0, 0), xk[3], 1e-9);
    }

    @Test
    public void fft_matches_dft_powerOfTwo() {
        double[] x = {0, 1, 2, 3, 2, 1, 0, -1};
        FourierTransform.Complex[] dft = FourierTransform.dft(x);
        FourierTransform.Complex[] fft = FourierTransform.fft(x);
        for (int k = 0; k < x.length; k++) {
            assertComplexEquals(dft[k], fft[k], 1e-9);
        }
    }

    @Test
    public void inverseDft_roundTrip_realSignal() {
        double[] x = {1, -0.5, 0.25, 0};
        FourierTransform.Complex[] X = FourierTransform.dft(x);
        FourierTransform.Complex[] y = FourierTransform.inverseDft(X);
        for (int n = 0; n < x.length; n++) {
            assertEquals(x[n], y[n].re(), 1e-9);
            assertEquals(0.0, y[n].im(), 1e-9);
        }
    }

    @Test
    public void inverseFft_roundTrip() {
        double[] x = {1, 0, 1, 0};
        FourierTransform.Complex[] X = FourierTransform.fft(x);
        FourierTransform.Complex[] y = FourierTransform.inverseFft(X);
        for (int n = 0; n < x.length; n++) {
            assertEquals(x[n], y[n].re(), 1e-9);
            assertEquals(0.0, y[n].im(), 1e-9);
        }
    }

    @Test
    public void fft_rejectsNonPowerOfTwo() {
        assertThrows(IllegalArgumentException.class, () -> FourierTransform.fft(new double[]{1, 2, 3}));
    }

    @Test
    public void dft_rejectsNull() {
        assertThrows(NullPointerException.class, () -> FourierTransform.dft(null));
    }

    @Test
    public void dft_rejectsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> FourierTransform.dft(new double[0]));
    }

    @Test
    public void parallelDft_matchesSequential() {
        double[] x = {0, 1, 2, 3, 2, 1, 0, -1};
        FourierTransform.Complex[] seq = FourierTransform.dft(x);
        FourierTransform.Complex[] par = FourierTransform.parallelDft(x);
        for (int k = 0; k < x.length; k++) {
            assertComplexEquals(seq[k], par[k], 1e-9);
        }
    }

    @Test
    public void parallelInverseDft_matchesSequential() {
        double[] x = {1, -0.5, 0.25, 0};
        FourierTransform.Complex[] X = FourierTransform.dft(x);
        FourierTransform.Complex[] seq = FourierTransform.inverseDft(X);
        FourierTransform.Complex[] par = FourierTransform.parallelInverseDft(X);
        for (int n = 0; n < x.length; n++) {
            assertComplexEquals(seq[n], par[n], 1e-9);
        }
    }

    @Test
    public void parallelFft_matchesFft() {
        double[] x = {0, 1, 2, 3, 2, 1, 0, -1};
        FourierTransform.Complex[] fft = FourierTransform.fft(x);
        FourierTransform.Complex[] pfft = FourierTransform.parallelFft(x);
        for (int k = 0; k < x.length; k++) {
            assertComplexEquals(fft[k], pfft[k], 1e-9);
        }
    }

    @Test
    public void parallelInverseFft_roundTrip() {
        double[] x = {1, 0, 1, 0};
        FourierTransform.Complex[] X = FourierTransform.parallelFft(x);
        FourierTransform.Complex[] y = FourierTransform.parallelInverseFft(X);
        for (int n = 0; n < x.length; n++) {
            assertEquals(x[n], y[n].re(), 1e-9);
            assertEquals(0.0, y[n].im(), 1e-9);
        }
    }
}
