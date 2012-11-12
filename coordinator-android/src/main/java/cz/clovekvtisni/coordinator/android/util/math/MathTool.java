package cz.clovekvtisni.coordinator.android.util.math;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 6/7/11
 * Time: 2:03 PM
 */
public class MathTool {

    public static Polynomial solveQuadraticRegression(double[] x, double[] y) {
        int n = y.length;
        if (y.length != n) throw new IllegalArgumentException("different count x and y");

        double sumX = 0, sumX2 = 0, sumX3 = 0, sumX4 = 0;
        for (double v : x) {
            double vv = v;
            sumX += vv;
            vv *= v;
            sumX2 += vv;
            vv *= v;
            sumX3 += vv;
            vv *= v;
            sumX4 += vv;
        }
        Matrix m = new Matrix(new double[][] {
                {sumX4, sumX3, sumX2},
                {sumX3, sumX2, sumX},
                {sumX2, sumX, n}
        });

        double sumY = 0, sumYX = 0, sumYX2 = 0;
        for (int i = 0; i < n; i++) {
            sumY += y[i];
            double yx = y[i] * x[i];
            sumYX += yx;
            sumYX2 += yx * x[i];
        }

        Matrix mInv = m.getInverseMatrix();
        Matrix solution = mInv.multiply(new Matrix(new double[][]{
                {sumYX2}, {sumYX}, {sumY}
        }));
        double[][] data = solution.getData();
        return new Polynomial(new double[]{data[0][0], data[1][0], data[2][0]});
    }

    public static Polynomial solveLinearRegression(double[] x, double[] y) {
        int n = y.length;
        if (y.length != n) throw new IllegalArgumentException("different count x and y");

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumX2 += x[i] * x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
        }

        double denominator = n * sumX2 - sumX * sumX;

        return new Polynomial(new double[]{
                (n * sumXY  - sumX * sumY) / denominator,
                (sumX2 * sumY - sumX * sumXY) / denominator
        });
    }

}
