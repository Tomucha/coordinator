package cz.clovekvtisni.coordinator.android.util.math;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 6/7/11
 * Time: 2:04 PM
 */
public class Polynomial {

    private double[] coefficients;
    private int n;

    public Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
        n = coefficients.length;
    }

    public double evaluate(double x) {
        int i = n - 1;
        double value = coefficients[i];
        for (i--; i >= 0; i--) {
            value += x * coefficients[i];
            x *= x;
        }
        return value;
    }

    public double[] getCoefficients() {
        double[] coefficients = new double[n];
        System.arraycopy(this.coefficients, 0, coefficients, 0, n);
        return coefficients;
    }

    public int getN() {
        return n;
    }

    public double computeStandardDeviation(double[] x, double[] values) {
        double diff = 0;
        int n = x.length;
        if (values.length != n) throw new IllegalArgumentException("counts of x & values must be same");
        for (int i = 0; i < n; i++) {
            double v = values[i] - evaluate(x[i]);
            diff += v*v;
        }
        return Math.sqrt(diff);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            final double c = coefficients[i];
            if (c == 0) continue;
            sb.append(' ');
            if (c > 0) sb.append("+");
            sb.append(c);
            int pow = n - i - 1;
            if (pow > 1) sb.append(" * x^").append(n - i - 1);
            else if (pow == 1) sb.append(" * x");
        }
        return sb.toString();
    }

    public void roundCoefficients(int scale) {
        double pow = Math.pow(10, scale);
        for (int i = 0; i < n; i++) {
            coefficients[i] = Math.round(coefficients[i] * pow) / pow;
        }
    }
}
