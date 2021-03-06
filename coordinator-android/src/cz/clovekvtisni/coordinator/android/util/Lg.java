package cz.clovekvtisni.coordinator.android.util;

import android.util.Log;

public class Lg {
	public static final Lg APP = new Lg("Coordinator");
	public static final Lg API = new Lg(APP, "Api");
	public static final Lg API_CACHE = new Lg(API, "Cache");
	public static final Lg API_LOADER = new Lg(API, "Loader");
	public static final Lg GCM = new Lg(APP, "GCM");
	public static final Lg LOCATION = new Lg(APP, "Location");
	public static final Lg MAP = new Lg(APP, "Map");
	private static final String INDENT = "    ";

	private final String tag;

	private Lg(String tag) {
		this.tag = tag;
	}

	private Lg(Lg parentLg, String tag) {
		this.tag = parentLg.tag + "." + tag;
	}
	
	public void v(String msg) {
		Log.v(tag, msg);
	}

	public void d(String msg) {
		Log.d(tag, msg);
	}

	public void dd(String msg) {
		d(INDENT + msg);
	}

	public void i(String msg) {
		Log.i(tag, msg);
	}
	
	public void w(String msg) {
		Log.w(tag, msg);
	}

	public void w(String msg, Throwable tr) {
		Log.w(tag, msg, tr);
	}

    public void e(String msg) {
        Log.e(tag, msg);
    }

	public void e(String msg, Throwable tr) {
		Log.e(tag, msg, tr);
	}

}
