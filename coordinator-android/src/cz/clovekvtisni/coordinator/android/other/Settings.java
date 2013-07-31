package cz.clovekvtisni.coordinator.android.other;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import cz.clovekvtisni.coordinator.android.CoordinatorApplication;

public class Settings {
	private static final String KEY_AUTH_KEY = "authKey";
	private static final String KEY_GCM_REGISTRATION_ID = "gcmRegId";
	private static final String KEY_PREFIX_PRELOADED = "preloaded";
    private static final String KEY_PREFIX_MAP_SETTINGS = "map-settings";

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

    public static void setMapSettings(long eventId, float latitude, float longitude, float zoom) {
        setFloat(KEY_PREFIX_MAP_SETTINGS+eventId+"latitude", latitude);
        setFloat(KEY_PREFIX_MAP_SETTINGS+eventId+"longitude", longitude);
        setFloat(KEY_PREFIX_MAP_SETTINGS+eventId+"zoom", zoom);
    }

    public static boolean hasMapSettings(long eventId) {
        return getPrefs().getFloat(KEY_PREFIX_MAP_SETTINGS+eventId+"latitude", 0) != 0;
    }

    public static float getMapSettingsLatitude(long eventId) {
        return getPrefs().getFloat(KEY_PREFIX_MAP_SETTINGS+eventId+"latitude", 0);
    }

    public static float getMapSettingsLongitude(long eventId) {
        return getPrefs().getFloat(KEY_PREFIX_MAP_SETTINGS+eventId+"longitude", 0);
    }

    public static float getMapSettingsZoom(long eventId) {
        return getPrefs().getFloat(KEY_PREFIX_MAP_SETTINGS+eventId+"zoom", 0);
    }

    public static String getGcmRegistrationId() {
		return getString(KEY_GCM_REGISTRATION_ID);
	}

	public static void setGcmRegistrationId(String regId) {
		setString(KEY_GCM_REGISTRATION_ID, regId);
	}
	
	public static boolean isEventMapPreloaded(long eventId) {
		return getPrefs().getBoolean(KEY_PREFIX_PRELOADED + eventId, false);
	}
	
	public static void setEventMapPreloaded(long eventId) {
		setBoolean(KEY_PREFIX_PRELOADED + eventId, true);
	}

	private static String getString(String key) {
		return getPrefs().getString(key, null);
	}

	private static void setString(String key, String value) {
		Editor editor = getPrefs().edit();
		editor.putString(key, value);
		SharedPrefsCompat.apply(editor);
	}

    private static void setFloat(String key, float value) {
        Editor editor = getPrefs().edit();
        editor.putFloat(key, value);
        SharedPrefsCompat.apply(editor);
    }

    private static void setInt(String key, int value) {
        Editor editor = getPrefs().edit();
        editor.putInt(key, value);
        SharedPrefsCompat.apply(editor);
    }

    private static void setBoolean(String key, boolean value) {
		Editor editor = getPrefs().edit();
		editor.putBoolean(key, value);
		SharedPrefsCompat.apply(editor);
	}

    public static void clear() {
        getPrefs().edit().clear().commit();
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
