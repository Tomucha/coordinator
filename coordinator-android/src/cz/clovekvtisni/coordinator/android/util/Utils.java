package cz.clovekvtisni.coordinator.android.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Utils {
	public static int dpToPx(Resources res, int dp) {
		DisplayMetrics metrics = res.getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
	}
	
	public static float pxToDp(Resources res, int px) {
	    return px / res.getDisplayMetrics().density;
	}
	
	public static int getVersionCode(Context c) {
		try {
			return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
