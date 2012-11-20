package cz.clovekvtisni.coordinator.android.util;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 7/8/11
 * Time: 1:35 PM
 */
public class ValueHolder {
    
	private final LinkedList<Value> values = new LinkedList<Value>();

    private int timeWindow;

	private PruneMode pruneMode;
	
	public static enum PruneMode {
		ON_ADD_ONLY,
		ON_ADD_AND_GET
	}

    /**
     * @param timeWindow casove okno v milisekundach
     */
    public ValueHolder(int timeWindow, PruneMode pruneMode) {
        this.timeWindow = timeWindow;
        this.pruneMode = pruneMode;
        if (timeWindow <= 0) {
            throw new IllegalArgumentException("timeWindow must be greater then 0");
        }
    }

    protected double[] getValues() {
    	if (pruneMode == PruneMode.ON_ADD_AND_GET) {
    		pruneOldValues();
    	}
        double[] values = new double[this.values.size()];
        int i = 0;
        for (Value value : this.values) {
            values[i++] = value.value;
        }
        return values;
    }

    public double getAverageValue() {

        synchronized (values) {
            final double[] values = getValues();
            final int length = values.length;
            if (length == 0)
                return Double.NaN;

            double sum = 0;
            for (int i = 0; i < length; i++) {
                sum += values[i];
            }

            double avg = sum / length;

            double dev = 0;
            for (int i = 0; i < length; i++) {
                double d = avg - values[i];
                dev += d * d;
            }
            dev = Math.sqrt(dev / length);

            sum = 0;
            int c = 0;
            for (int i = 0; i < length; i++) {
                if (Math.abs(avg - values[i]) <= dev) {
                    sum += values[i];
                    c++;
                }
            }

            if (c > 0) {
                avg = sum / c;
            }

            return avg;
        }
    }

    public void reset() {
        synchronized (values) {
            values.clear();
        }
    }

    public void addValue(double value, Date timestamp) {
        synchronized (values) {
            pruneOldValues();
            values.add(new Value(value, timestamp));
        }
    }

    private void pruneOldValues() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -timeWindow);
        final Date maxTime = cal.getTime();
        while (!values.isEmpty() && values.peek().timestamp.before(maxTime)) {
            values.poll();
        }
    }
    
    public double getAverageRelativeDeviation() {
        final double[] values = getValues();
        final int length = values.length;
        if (length == 0) {
            return Double.NaN;
        }
        double avg = getAverageValue();
        double dev = 0;
        for (int i = 0; i < length; i++) {
            double d = (avg - values[i])/avg;
            dev += Math.abs(d);
        }
        return dev / length;
    }
    
    public int getRealTimeWindow() {
    	if (values.isEmpty()) return 0;
    	return (int) (values.getLast().timestamp.getTime() - values.getFirst().timestamp.getTime());
    }

    private static class Value {
        double value;
        Date timestamp;

        private Value(double value, Date timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
