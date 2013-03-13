package cz.clovekvtisni.coordinator.android.other;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cz.clovekvtisni.coordinator.android.CoordinatorApplication;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Settings {
	private static final String KEY_AUTH_KEY = "authKey";
	private static final String KEY_GCM_REGISTRATION_ID = "gcmRegId";

	private static SharedPreferences getPrefs() {
		return PreferenceManager
				.getDefaultSharedPreferences(CoordinatorApplication.getAppContext());
	}

	public static String getAuthKey() {
		return getString(KEY_AUTH_KEY);
	}

	public static void setAuthKey(String authKey) {
		setString(KEY_AUTH_KEY, authKey);
	}

	public static String getGcmRegistrationId() {
		return getString(KEY_GCM_REGISTRATION_ID);
	}

	public static void setGcmRegistrationId(String regId) {
		setString(KEY_GCM_REGISTRATION_ID, regId);
	}

	private static String getString(String key) {
		return getPrefs().getString(key, null);
	}

	private static void setString(String key, String value) {
		Editor editor = getPrefs().edit();
		editor.putString(key, value);
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
