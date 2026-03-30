import chapter13.linearProgramming.LinearProgramming;
import chapter13.linearProgramming.LinearProgramming.Solution;
import chapter13.linearProgramming.LinearProgramming.Status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinearProgrammingTest {

    private static void assertApprox(double expected, double actual, double tol) {
        assertEquals(expected, actual, tol);
    }

    @Test
    public void maximize_classicThreeConstraints_optimalInteriorVertex() {
        double[] c = {3, 2};
        double[][] a = {
                {1, 0},
                {2, 1},
                {2, 3}
        };
        double[] b = {4, 12, 18};

        Solution s = LinearProgramming.maximize(c, a, b);
        assertSame(Status.OPTIMAL, s.status());
        assertApprox(56.0 / 3.0, s.objectiveValue(), 1e-6);
        assertTrue(LinearProgramming.isFeasible(a, b, s.x(), 1e-5));
        assertApprox(56.0 / 3.0, LinearProgramming.objectiveAt(c, s.x()), 1e-6);
        assertApprox(4.0, s.x()[0], 1e-5);
        assertApprox(10.0 / 3.0, s.x()[1], 1e-5);
    }

    @Test
    public void maximize_simplexUnbounded_returnsUnbounded() {
        double[] c = {1, 1};
        double[][] a = {{1, -1}};
        double[] b = {0};

        Solution s = LinearProgramming.maximize(c, a, b);
        assertSame(Status.UNBOUNDED, s.status());
        assertNull(s.x());
    }

    @Test
    public void minimize_smallProblem_matchesNegatedMax() {
        double[] c = {2, 1};
        double[][] a = {{1, 1}};
        double[] b = {1};

        Solution s = LinearProgramming.minimize(c, a, b);
        assertSame(Status.OPTIMAL, s.status());
        assertApprox(0.0, s.objectiveValue(), 1e-6);
        assertTrue(LinearProgramming.isFeasible(a, b, s.x(), 1e-5));
    }

    @Test
    public void maximize_rejectsNegativeRhs() {
        double[] c = {1};
        double[][] a = {{1}};
        double[] b = {-1};
        assertThrows(IllegalArgumentException.class, () -> LinearProgramming.maximize(c, a, b));
    }

    @Test
    public void maximize_rejectsDimensionMismatch() {
        double[] c = {1, 2};
        double[][] a = {{1}};
        double[] b = {1};
        assertThrows(IllegalArgumentException.class, () -> LinearProgramming.maximize(c, a, b));
    }

    @Test
    public void objectiveAt_manualDotProduct() {
        assertEquals(10.0, LinearProgramming.objectiveAt(new double[]{2, 3}, new double[]{2, 2}), 1e-12);
    }
}
