package cz.clovekvtisni.coordinator.android.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * Jedoduchy profiler - begin("tag") end("tag")
 * 
 * 
 * Tomucha a Zoidberg.
 * 
 */
public class MaProfiler {

    private static ThreadLocal<Stack> stacks = new ThreadLocal<Stack>();
    private static boolean enabledGlobal = false;
    private static boolean disabledByError = false;
    private static NumberFormat nfInt = new DecimalFormat("###########0");
    private static String space = "            ";

    private static final Object lock = new Object();

    private static List<Stack> allStacks = new ArrayList<MaProfiler.Stack>();

	//Tomas
    public static void enable() {
        enabledGlobal = true;
    }
	//Tomas
	public static void disable() {
        enabledGlobal = false;
    }

    private static void initializeIfNeeded() {
        synchronized (lock) {
            if (enabledGlobal && stack() == null) {
                enabledForThread();
            }
        }
    }

	public static void ensureEnabled() {
    	if (! enabled()) {
    		enabledForThread();
    	}
    }

    private static void enabledForThread() {
        enabledGlobal = true;
        String name = Thread.currentThread().getName();
        assertState(stack() == null, "Profiler was already enabled for the thread "+name);
        Stack stack = new Stack(name);
        stacks.set(stack);
        allStacks.add(stack);
    }

    private static Stack stack() {
        return stacks.get();
    }

    private static boolean enabled() {
        return enabledGlobal && !disabledByError && stack() != null;
    }

    public static boolean begin(String tag) {
        synchronized (lock) {
            initializeIfNeeded();
            if (enabled()) {
                stack().begin(tag);
                return true;
            }
            return false;
        }
    }

    public static void end(String tag) {
        synchronized (lock) {
            if (enabled()) {
                Stack stack = stack();
                stack.end(tag);
            }
        }
    }

    public static void print() {
        StringBuffer b = new StringBuffer();
        print(b);
        System.out.println(b);
    }

    public static void log(String tag) {
        StringBuffer b = new StringBuffer();
        print(b);
        String[] lines = b.toString().split("\r?\n");
        for (String line : lines) {
            Log.d(tag, line);
        }
    }

    public static void print(StringBuffer b) {
        synchronized (lock) {
            if (enabled()) {
                Map<String,PointcutStatsHolder> allPointcutStats = new HashMap<String, PointcutStatsHolder>();
                List<Pointcut> allPointcuts = new ArrayList<Pointcut>();
                for (Stack s : allStacks) {
                    allPointcuts.addAll(s.pointcuts.values());
                }
                Collections.sort(allPointcuts);
                for (Pointcut p : allPointcuts) {
                    if (! allPointcutStats.containsKey(p.id)) {
                        allPointcutStats.put(p.id, new PointcutStatsHolder(p));
                    } else {
                        allPointcutStats.get(p.id).add(p);
                    }
                }
                List<PointcutStatsHolder> sortedPointcuts = new ArrayList<PointcutStatsHolder>(allPointcutStats.values());
                Collections.sort(sortedPointcuts);

                for (PointcutStatsHolder psh : sortedPointcuts) {
                    psh.print(b);
                    b.append("\n");
                }
            }
        }
    }

    public static void reset() {
        synchronized (lock) {
            stacks = new ThreadLocal<Stack>();
            disabledByError = false;
            allStacks.clear();
        }
    }

    private static class Stack {

        private Map<String, Pointcut> pointcuts = new HashMap<String, Pointcut>(20);
        private LinkedList<Pointcut> stack = new LinkedList<Pointcut>();
        private String threadName = null;

        private Stack(String threadName) {
            this.threadName = threadName;
        }

        private Pointcut top() {
            if (stack.isEmpty()) return null;
            return stack.getLast();
        }

        private void begin(String clazz) {
            assertState(clazz!=null, "Pointcut class should not be empty");
            // you can begin whatever you want
            Pointcut current = top();
            Pointcut newTop = getPointcut(current, clazz);
            assertState(!newTop.opened(), "Opening already opened pointcut, that really should'n happen: "+clazz);
            newTop.begin();
            stack.addLast(newTop);
        }

        private void end(String clazz) {
            assertState(clazz!=null, "Pointcut class should not be empty");
            Pointcut current = top();
            assertState(current!=null, "No pointcut is opened, maybe wrong order of 'begin', 'end' and 'enableForThread'");
            if (current != null) {
                assertState(current.clazz != null,  current + " with null clazz");
                assertState(current.clazz.equals(clazz), "Closing pointcut "+clazz+" but opened is "+current.clazz);
                current.end();
            }
            assertState(stack.removeLast().equals(current), "Closed pointcut is not the top one, bug in MaProfiler: "+current);
        }

        private Pointcut getPointcut(Pointcut current, String clazz) {
            String id = Pointcut.generateId(current, clazz);
            Pointcut inMap = pointcuts.get(id);
            if (inMap == null) {
                inMap = new Pointcut(current, clazz);
                pointcuts.put(id, inMap);
            }
            return inMap;
        }


//        public void print(StringBuffer b) {
//            try {
//                List<Pointcut> all = new ArrayList<Pointcut>(pointcuts.values());
//                Collections.sort(all);
//                b.append("MaProfiler result for thread "+threadName+":");
//                for (Pointcut pointcut : all) {
//                    b.append("\n");
//                    pointcut.print(b);
//                }
//            } catch (Exception e ){
//                System.err.println("Profiler threw an exception: "+e);
//                e.printStackTrace();
//                disabledByError = true;
//            }
//        }
    }

    private static class PointcutStatsHolder implements Comparable<PointcutStatsHolder> {
    	private String id;
    	private long overallTime = 0;
        private long max = 0;
        private long min = Long.MAX_VALUE;
        private long overallCalls = 0;

        public PointcutStatsHolder(Pointcut pointcut) {
        	this.id = pointcut.id;
        	this.max = pointcut.max;
        	this.min = pointcut.min;
        	this.overallCalls = pointcut.getOverallCalls();
        	this.overallTime = pointcut.getOverallTime();
        }

        public void add(Pointcut pointcut) {
        	if (! id.equals(pointcut.id)) {
        		throw new IllegalStateException("ID's cannot be different " + id + " vs " + pointcut.id);
        	}
        	if (min > pointcut.min) {
        		min = pointcut.min;
        	}
        	if (max < pointcut.max) {
        		max = pointcut.max;
        	}
        	overallTime += pointcut.getOverallTime();
        	overallCalls += pointcut.getOverallCalls();
        }

        public void print(StringBuffer b) {
            b.append(id);
            b.append("\n\t");
            long avg = getAvg();
            b.append(format(overallTime));
            b.append(" ms | ");
            b.append(format(overallCalls));
            b.append(" calls | ");
            b.append(format(avg));
            b.append(" ms/call | ");
            b.append(format(max));
            b.append(" max | ");
            b.append(format(min));
            b.append(" min");
        }

		private Long getAvg() {
			return overallTime / overallCalls;
		}

		@Override
		public int compareTo(PointcutStatsHolder arg0) {
			return getAvg().compareTo(arg0.getAvg());
		}


    }

    private static class Pointcut implements Comparable<Pointcut> {

        private String clazz = null;
        private String id = null;
        private long overallTime = 0;
        private long max = 0;
        private long min = Long.MAX_VALUE;
        private long overallCalls = 0;
        private long callStarted = 0;

        private Pointcut(Pointcut parent, String clazz) {
            this.clazz = clazz;
            this.id = generateId(parent, clazz);
        }

        private void begin() {
            assertState(callStarted == 0, "Pointcut monitor was already started: "+clazz);
            callStarted = System.currentTimeMillis();
        }

        private void end() {
            assertState(callStarted != 0, "Pointcut monitor was not started: "+clazz);
            overallCalls++;
            long duration = System.currentTimeMillis()-callStarted;
            overallTime+=duration;
            max = Math.max(max, duration);
            min = Math.min(min, duration);
            callStarted = 0;
        }

        private static String generateId(Pointcut parent, String clazz) {
            assertState(clazz != null && clazz.trim().length()>0, "Pointcut class cannot be empty");
            assertState(!clazz.contains("."), "Pointcut class cannot contain '.'");
            if (parent == null) return clazz;
            return parent.id+"."+clazz;
        }

        public boolean opened() {
            return callStarted > 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pointcut pointcut = (Pointcut) o;
            if (id != null ? !id.equals(pointcut.id) : pointcut.id != null) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
		public int compareTo(Pointcut o) {
            return id.compareTo(o.id);
        }

        public long getOverallTime() {
        	if (opened()) {
        		return overallTime + System.currentTimeMillis() - callStarted;
        	} else {
        		return overallTime;
        	}
        }

        public long getOverallCalls() {
        	if (opened()) {
        		return overallCalls + 1;
        	} else {
        		return overallCalls;
        	}
        }

    }

    private static void assertState(boolean state, String message) {
        if (!state) {
            disabledByError = true;
            try {
                throw new IllegalStateException("MaProfiler assertation error: "+message);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected static String format(long val) {
        int e = 0;
        while (val > 99999) {
            e+=3;
            val = val / 1000;
        }
        String res = nfInt.format(val)+(e > 0 ? "e"+e : "");
        int len = res.length();
        return space.substring(0, space.length()-len)+res;
    }

     

}
