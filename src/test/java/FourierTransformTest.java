import chapter13.fourier.FourierTransform;
import chapter13.fourier.FourierTransform.Complex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FourierTransformTest {

    private static void assertComplexEquals(Complex expected, Complex actual, double eps) {
        assertEquals(expected.re(), actual.re(), eps, "re");
        assertEquals(expected.im(), actual.im(), eps, "im");
    }

    @Test
    public void dft_constant_signal_dc_only() {
        double[] x = {1, 1, 1, 1};
        Complex[] xk = FourierTransform.dft(x);
        assertEquals(4, xk.length);
        assertComplexEquals(new Complex(4, 0), xk[0], 1e-9);
        assertComplexEquals(new Complex(0, 0), xk[1], 1e-9);
        assertComplexEquals(new Complex(0, 0), xk[2], 1e-9);
        assertComplexEquals(new Complex(0, 0), xk[3], 1e-9);
    }

    @Test
    public void fft_matches_dft_powerOfTwo() {
        double[] x = {0, 1, 2, 3, 2, 1, 0, -1};
        Complex[] dft = FourierTransform.dft(x);
        Complex[] fft = FourierTransform.fft(x);
        for (int k = 0; k < x.length; k++) {
            assertComplexEquals(dft[k], fft[k], 1e-9);
        }
    }

    @Test
    public void inverseDft_roundTrip_realSignal() {
        double[] x = {1, -0.5, 0.25, 0};
        Complex[] X = FourierTransform.dft(x);
        Complex[] y = FourierTransform.inverseDft(X);
        for (int n = 0; n < x.length; n++) {
            assertEquals(x[n], y[n].re(), 1e-9);
            assertEquals(0.0, y[n].im(), 1e-9);
        }
    }

    @Test
    public void inverseFft_roundTrip() {
        double[] x = {1, 0, 1, 0};
        Complex[] X = FourierTransform.fft(x);
        Complex[] y = FourierTransform.inverseFft(X);
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
}
