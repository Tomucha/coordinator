package cz.clovekvtisni.coordinator.android.api;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;

import com.google.gson.JsonElement;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Snapshot;

import cz.clovekvtisni.coordinator.android.other.CoordinatorApplication;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.Utils;
import cz.clovekvtisni.coordinator.api.response.ApiResponseData;

public class Cache {
	public static final Cache INSTANCE = new Cache();

	private static final int MAX_SIZE_BYTES = 50 * 1000 * 1000; // 50 MB
	private static final String DIRECTORY = "api";

	private final DiskLruCache diskLruCache;

	private Cache() {
		try {
			Context c = CoordinatorApplication.getAppContext();
			File dir = new File(c.getExternalCacheDir(), DIRECTORY);
			diskLruCache = DiskLruCache.open(dir, Utils.getVersionCode(c), 2, MAX_SIZE_BYTES);
		} catch (IOException e) {
			String msg = "Opening cache failed";
			Lg.API_CACHE.e(msg, e);
			throw new AssertionError(msg);
		}
	}

	public <RP> Item<RP> get(String key, Class<? extends RP> type) {
		String logMsgPrefix = "Get " + key + ": ";
		Snapshot snapshot = null;
		try {
			snapshot = diskLruCache.get(key);
			if (snapshot == null) {
				Lg.API_CACHE.d(logMsgPrefix + "doesn't exist, returning null.");
				return null;
			}

			Item<RP> item = new Item<RP>(snapshot, type);
			Lg.API_CACHE.d(logMsgPrefix + "success.");
			return item;
		} catch (IOException e) {
			Lg.API_CACHE.w(logMsgPrefix + "exception.", e);
			return null;
		} finally {
			if (snapshot != null) snapshot.close();
		}
	}

	public void put(String key, ApiResponseData data) {
		byte[] bytes = ApiUtils.GSON.toJsonTree(data).toString().getBytes();

		DiskLruCache.Editor editor = null;
		try {
			editor = diskLruCache.edit(key);
			if (editor == null) return;

			writeValue(bytes, editor);
			writeTime(editor);

			editor.commit();

			Lg.API_CACHE.d("Put " + key + ": success.");
		} catch (IOException e) {
			try {
				if (editor != null) editor.abort();
			} catch (IOException ignored) {
			}

			Lg.API_CACHE.w("Put " + key + ": exception.", e);
		}
	}

	private void writeValue(byte[] bytes, DiskLruCache.Editor editor) throws IOException {
		OutputStream os = null;
		try {
			os = editor.newOutputStream(0);
			os.write(bytes);
		} finally {
			if (os != null) os.close();
		}
	}

	private void writeTime(DiskLruCache.Editor editor) throws IOException {
		OutputStream os = null;
		try {
			os = editor.newOutputStream(1);
			long time = System.currentTimeMillis();
			os.write(String.valueOf(time).getBytes());
		} finally {
			if (os != null) os.close();
		}
	}

	public static class Item<RP> {
		private final long time;
		private final RP value;

		private Item(Snapshot snapshot, Class<? extends RP> type) throws IOException {
			time = Long.valueOf(snapshot.getString(1));

			JsonElement json = ApiUtils.PARSER.parse(snapshot.getString(0));
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
