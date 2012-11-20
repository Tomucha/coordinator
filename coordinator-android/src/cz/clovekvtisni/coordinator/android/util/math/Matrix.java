package cz.clovekvtisni.coordinator.android.util.math;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 6/7/11
 * Time: 12:23 PM
 */
public class Matrix {

    private double[][] data;

    private int rows;
    private int cols;

    public Matrix(double[][] data) {
        this(data, true);
    }

    protected Matrix(double[][] data, boolean copyData) {
        rows = data.length;
        if (rows < 1) throw new IllegalArgumentException("matrix must contain at least one row");
        cols = data[0].length;
        if (cols < 1) throw new IllegalArgumentException("matrix must contain at least one column");

        if (copyData) {
            this.data = new double[rows][cols];
            for (int y = 0; y < rows; y++) {
                if (cols != data[y].length) throw new IllegalArgumentException((y+1) + "th row has different columns count then 1th row");
                System.arraycopy(data[y], 0, this.data[y], 0, cols);
            }
        }
        else {
            for (int y = 1; y < rows; y++) {
                if (cols != data[y].length) throw new IllegalArgumentException((y+1) + "th row has different columns count then 1th row");
            }
            this.data = data;
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double[][] getData() {
        double[][] data = new double[rows][cols];
        for (int y = 0; y < rows; y++) {
            System.arraycopy(this.data[y], 0, data[y], 0, cols);
        }
        return data;
    }

    public Matrix getInverseMatrix() {

        if (rows > 3 || cols > 3) throw new UnsupportedOperationException();

        double[][] mInv = new double[cols][rows];
        double det = getDeterminant();

        for(int y = 0; y < cols; y++) {
            for (int x = 0; x < rows; x++) {
                mInv[y][x] = ((x + y) % 2 == 0 ? 1 : -1) * getSubMatrix(x, y).getDeterminant() / det;
            }
        }

        return new Matrix(mInv, false);
    }

    public Matrix getSubMatrix(int ry, int rx) {
        double[][] subMatrix = new double[rows - 1][cols -1];
        int y = 0;
        for (int my = 0; my < rows; my++) {
            if (my == ry) continue;
            int x = 0;
            for (int mx = 0; mx < cols; mx++) {
                if (mx == rx) continue;
                subMatrix[y][x] = data[my][mx];
                x++;
            }
            y++;
        }
        return new Matrix(subMatrix, false);
    }

    public double getDeterminant() {
        if (rows != cols || rows > 3) throw new UnsupportedOperationException();
        if (rows == 1) return data[0][0];
        if (rows == 2) return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        return
                data[0][0] * data[1][1] * data[2][2] +
                data[0][2] * data[1][0] * data[2][1] +
                data[0][1] * data[1][2] * data[2][0] -
                data[0][2] * data[1][1] * data[2][0] -
                data[0][0] * data[1][2] * data[2][1] -
                data[0][1] * data[1][0] * data[2][2];
    }

    public Matrix multiply(Matrix m2) {
        int rows1 = rows;
        int cols1 = cols;

        int rows2 = m2.getRows();
        int cols2 = m2.getCols();

        if (cols1 != rows2)
            throw new IllegalArgumentException("matrix columns count isn't equal m2 rows count");

        double[][] m = new double[rows1][cols2];

        for (int y = 0; y < rows1; y++) {
            for (int x = 0; x < cols2; x++) {
                double v = 0;
                for (int i = 0; i < cols1; i++)
                    v += data[y][i] * m2.data[i][x];
                m[y][x] = v;
            }
        }

        return new Matrix(m, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matrix)) return false;

        Matrix matrix = (Matrix) o;

        if (cols != matrix.cols) return false;
        if (rows != matrix.rows) return false;

        return Arrays.deepEquals(data, matrix.data);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix{");
        for (int i = 0; i < rows; i++) {
            if (i > 0) sb.append(", ");
            sb.append("{");
            for (int j = 0; j < cols; j++) {
                if (j > 0) sb.append(", ");
                sb.append(data[i][j]);
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }


}
