package cz.clovekvtisni.coordinator.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;
import cz.clovekvtisni.coordinator.android.api.DeployEnvironment;

/**
 * This class contains a set of common Android utilities for logging and debuging.
 * 
 * 
 * @author tomucha
 *
 */
public class CommonTool {

	private static final String TAG = "Coordinator";

	private static DeployEnvironment stylBeeEnvironment;

	public static DeployEnvironment getEnvironment() {
		if (stylBeeEnvironment == null) {
			stylBeeEnvironment = DeployEnvironment.getTestingEnvironment();
		}
		return stylBeeEnvironment;
	}

	public static String getTAG() {
		return TAG;
	}

	public static void logD(String s) {
		if (getEnvironment().getLogLevel() <= Log.DEBUG)
			Log.d(TAG, s);
	}

	public static void logD(String subTag, String s) {
		if (getEnvironment().getLogLevel() <= Log.DEBUG)
			Log.d(TAG + "." + subTag, s);
	}

	public static void logW(String s) {
		if (getEnvironment().getLogLevel() <= Log.WARN)
			Log.w(TAG, s);
	}

	public static void logW(String subTag, String s) {
		if (getEnvironment().getLogLevel() <= Log.WARN)
			Log.w(TAG + "." + subTag, s);
	}

	public static void logE(String s, Throwable e) {
		if (getEnvironment().getLogLevel() <= Log.ERROR)
			Log.e(TAG, s, e);
	}

	public static void logE(String s) {
		if (getEnvironment().getLogLevel() <= Log.ERROR)
			Log.e(TAG, s);
	}

	public static void logE(String subTag, String s, Throwable e) {
		if (getEnvironment().getLogLevel() <= Log.ERROR)
			Log.e(TAG + "." + subTag, s, e);
	}

	public static void logI(String s) {
		if (getEnvironment().getLogLevel() <= Log.INFO)
			Log.i(TAG, s);
	}

	public static void logI(String subTag, String s) {
		if (getEnvironment().getLogLevel() <= Log.INFO)
			Log.i(TAG + "." + subTag, s);
	}

	public static void showToast(final Context context, final String text) {
		logI("Toast: "+text);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
				toast.show();
			}
		};
		if ((context instanceof Activity) && !((Activity) context).isFinishing()) {
			((Activity) context).runOnUiThread(runnable);
		} else {
			runnable.run();
		}
	}

	public static void showToast(Context context, int msg) {
		showToast(context, context.getString(msg));
	}

	public static String getVersion(Context	c) {
		try {
			return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "unknown";
		}
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			//
		}
	}

	public static int getStringIdentifier(Context context, String name) {
		return context.getResources().getIdentifier(name, "string", context.getPackageName());
	}

	public static int getDrawableIdentifier(Context context, String name) {
		return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
	}

	public static void shareText(Activity a, CharSequence title, CharSequence text, int requestId) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        a.startActivityForResult(Intent.createChooser(shareIntent, title), requestId);		
	}

}
