package cz.clovekvtisni.coordinator.android.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {
	public static int getVersionCode(Context c) {
		try {
			return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
