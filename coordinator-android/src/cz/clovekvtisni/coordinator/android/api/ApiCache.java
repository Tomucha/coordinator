package cz.clovekvtisni.coordinator.android.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.google.gson.JsonElement;

import cz.clovekvtisni.coordinator.android.CoordinatorApplication;
import cz.clovekvtisni.coordinator.android.util.DiskCache;
import cz.clovekvtisni.coordinator.android.util.DiskCache.Snapshot;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

public class ApiCache {
	private static final String CACHE_DIR = "api";
	private static final String KEY_TIME = "time";
	
	private static ApiCache instance;
	
	private final DiskCache diskCache;

	public static synchronized ApiCache getInstance() throws IOException {
		if (instance == null) instance = new ApiCache();
		return instance;
	}

	private ApiCache() throws IOException {
		Context c = CoordinatorApplication.getAppContext();
		diskCache = DiskCache.newInstance(new File(c.getExternalCacheDir(), CACHE_DIR));
	}

	public <RP> Item<RP> get(String key, Class<? extends RP> type) throws IOException {
		String logMsgPrefix = "Get " + key + ": ";
		Snapshot snapshot = diskCache.get(key); // muze byt null
		if (snapshot == null) {
			Lg.API_CACHE.d(logMsgPrefix + "doesn't exist.");
			return null;
		} else {
			Item<RP> item = new Item<RP>(snapshot, type);
			Lg.API_CACHE.d(logMsgPrefix + "success.");
			return item;
		}
	}

	public void put(String key, ApiResponseData data) throws IOException {
		Lg.API_CACHE.d("Put " + key + ".");

		String value = ApiUtils.GSON.toJsonTree(data).toString();

		Map<String, Long> annotations = new HashMap<String, Long>();
		annotations.put(KEY_TIME, System.currentTimeMillis());
		diskCache.put(key, value, annotations);
	}

	public static class Item<RP> {
		private final long time;
		private final RP value;

		private Item(Snapshot snapshot, Class<? extends RP> type) throws IOException {
			time = (Long) snapshot.getAnnotations().get(KEY_TIME);

			JsonElement json = ApiUtils.PARSER.parse(snapshot.getString());
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
