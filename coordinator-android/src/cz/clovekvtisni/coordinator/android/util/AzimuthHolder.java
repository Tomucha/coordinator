package cz.clovekvtisni.coordinator.android.util;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 7/8/11
 * Time: 6:16 PM
 */
public class AzimuthHolder extends ValueHolder {

    /**
     * @param timeWindow casove okno v milisekundach
     */
    public AzimuthHolder(int timeWindow, PruneMode pruneMode) {
        super(timeWindow, pruneMode);
    }

    @Override
    protected double[] getValues() {
        final double[] values = super.getValues();
        if (values.length > 1) {
            double v1 = values[0] % 360;
            if (v1 < 0) v1 += 360;
            values[0] = v1;
            for (int i = values.length - 1; i > 0; i--) {
                double v = values[i] % 360;
                if (v < 0) v += 360;
                double vl = v - 360;
                double vh = v + 360;

                double d = Math.abs(v - v1);
                double dl = Math.abs(vl - v1);
                double dh = Math.abs(vh - v1);

                if (dl < d) {
                    d = dl;
                    v = vl;
                }

                if (dh < d) {
                    v = vh;
                }

                values[i] = v;
            }
        }
        return values;
    }

    @Override
    public double getAverageValue() {
        double avg = super.getAverageValue() % 360;
        return avg < 0 ? avg + 360 : avg;
    }
}
