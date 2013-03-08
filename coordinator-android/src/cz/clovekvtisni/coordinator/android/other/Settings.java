package cz.clovekvtisni.coordinator.android.other;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cz.clovekvtisni.coordinator.android.CoordinatorApplication;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Settings {
	private static final String KEY_AUTH_KEY = "authKey";
	
	private static SharedPreferences getPrefs() {
		return PreferenceManager.getDefaultSharedPreferences(CoordinatorApplication.getAppContext());
	}
	
	public static String getAuthKey() {
		return getPrefs().getString(KEY_AUTH_KEY, null);
	}
	
	public static void setAuthKey(String authKey) {
		Editor editor = getPrefs().edit();
		editor.putString(KEY_AUTH_KEY, authKey);
		SharedPrefsCompat.apply(editor);
	}
	
	private static class SharedPrefsCompat {
		private static Method applyMethod;

		static {
			try {
				Class<SharedPreferences.Editor> cls = SharedPreferences.Editor.class;
				applyMethod = cls.getMethod("apply");
			} catch (NoSuchMethodException unused) {
			}
		}

		private static void apply(SharedPreferences.Editor editor) {
			if (applyMethod != null) {
				try {
					applyMethod.invoke(editor);
					return;
				} catch (InvocationTargetException unused) {
				} catch (IllegalAccessException unused) {
				}
			}
			editor.commit();
		}
	}
}
