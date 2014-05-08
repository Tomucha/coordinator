package cz.clovekvtisni.coordinator.android.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cz.clovekvtisni.coordinator.android.util.Lg;
import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;

import com.fhucho.android.workers.Loader;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import cz.clovekvtisni.coordinator.android.CoordinatorApplication;
import cz.clovekvtisni.coordinator.android.DeployEnvironment;
import cz.clovekvtisni.coordinator.android.util.DiskCache;
import cz.clovekvtisni.coordinator.android.util.DiskCache.BitmapEntry;
import cz.clovekvtisni.coordinator.android.util.Utils;

public class BitmapLoader extends Loader<BitmapLoader.Listener> {
	private static final int CACHE_SIZE = 50 * 1024 * 1024; // 50 MiB
	private static final String CACHE_DIR = "bitmaps";
	private static DiskCache diskCache;

	private final String url;
	private Result result;

	public BitmapLoader(String url) {
		super(Listener.class);
		this.url = DeployEnvironment.SERVER_URL_PREFIX + url;

	}

	private static synchronized DiskCache getOrCreateDiskCache() throws IOException {
		Context appContext = CoordinatorApplication.getAppContext();
		if (diskCache == null) {
			File dir = new File(appContext.getExternalCacheDir(), CACHE_DIR);
			diskCache = DiskCache.open(dir, Utils.getVersionCode(appContext), CACHE_SIZE);
		}
		return diskCache;
	}

	@Override
	protected void doInBackground(boolean reload) {
		InputStream is = null;
		try {
			DiskCache cache = getOrCreateDiskCache();
			BitmapEntry bitmapEntry = cache.getBitmap(url);
			Bitmap bitmap;
			if (bitmapEntry == null) {
                Lg.API_CACHE.i("Loading icon from URL");
				is = HttpRequest.get(url).stream();
				cache.put(url, is);
				bitmap = cache.getBitmap(url).getBitmap();
			} else {
                Lg.API_CACHE.i("Loading icon from cache");
				bitmap = bitmapEntry.getBitmap();
			}
			result = new Result(bitmap);
			getListenerProxy().onSuccess(bitmap);
		} catch (HttpRequestException e) {
			result = new Result(e);
		} catch (IOException e) {
			result = new Result(e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		result.sendToListener();
	}

	@Override
	protected boolean isEquivalentTo(Loader<?> other) {
        /*
        Tady pak zlobi, kdyz maji kategorie stejne ikony.

		if (getClass().equals(other.getClass())) {
			BitmapLoader otherLoader = (BitmapLoader) other;
			if (url.equals(otherLoader.url)) return true;
		}*/
		return false;
	}

	@Override
	protected void onListenerAdded() {
		if (result != null) result.sendToListener();
	}

	public static interface Listener {
		public void onSuccess(Bitmap bitmap);

		public void onException(Exception e);
	}

	private class Result {
		private final Exception exception;
		private final Bitmap bitmap;

		private Result(Exception e) {
			exception = e;
			bitmap = null;
		}

		private Result(Bitmap b) {
			bitmap = b;
			exception = null;
		}

		private void sendToListener() {
			if (bitmap != null) getListenerProxy().onSuccess(bitmap);
			if (exception != null) getListenerProxy().onException(exception);
		}
	}
}
