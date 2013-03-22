package cz.clovekvtisni.coordinator.android.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.google.gson.JsonElement;

import cz.clovekvtisni.coordinator.android.CoordinatorApplication;
import cz.clovekvtisni.coordinator.android.util.DiskCache;
import cz.clovekvtisni.coordinator.android.util.DiskCache.StringEntry;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.Utils;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

public class ApiCache {
	private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
	private static final String CACHE_DIR = "api";
	private static final String KEY_TIME = "time";

	private static ApiCache instance;

	private final DiskCache diskCache;

	public static synchronized ApiCache getInstance() {
		if (instance == null) instance = new ApiCache();
		return instance;
	}

	private ApiCache() {
		Context c = CoordinatorApplication.getAppContext();
        try {
            diskCache = DiskCache.open(new File(c.getExternalCacheDir(), CACHE_DIR),
                    Utils.getVersionCode(c), CACHE_SIZE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

	public <RP> Item<RP> get(String key, Class<? extends RP> type) {
		String logMsgPrefix = "Get " + key + ": ";
        StringEntry stringEntry = null;
        try {
            stringEntry = diskCache.getString(key);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        if (stringEntry == null) {
			Lg.API_CACHE.d(logMsgPrefix + "doesn't exist.");
			return null;
		} else {
			Item<RP> item = new Item<RP>(stringEntry, type);
			Lg.API_CACHE.d(logMsgPrefix + "success.");
			return item;
		}
	}

	public void put(String key, ApiResponseData data) {
		Lg.API_CACHE.d("Put " + key + ".");

		String value = ApiUtils.GSON.toJsonTree(data).toString();

		Map<String, Long> annotations = new HashMap<String, Long>();
		annotations.put(KEY_TIME, System.currentTimeMillis());
        try {
            diskCache.put(key, value, annotations);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

	public static class Item<RP> {
		private final long time;
		private final RP value;

		private Item(StringEntry stringEntry, Class<? extends RP> type) {
			time = (Long) stringEntry.getMetadata().get(KEY_TIME);

			JsonElement json = ApiUtils.PARSER.parse(stringEntry.getString());
			value = ApiUtils.GSON.fromJson(json, type);
		}

		public long getTime() {
			return time;
		}

		public RP getValue() {
			return value;
		}
	}
}
