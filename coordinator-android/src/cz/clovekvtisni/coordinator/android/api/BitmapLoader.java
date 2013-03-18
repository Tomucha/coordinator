package cz.clovekvtisni.coordinator.android.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;

import com.fhucho.android.workers.Loader;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import cz.clovekvtisni.coordinator.android.CoordinatorApplication;
import cz.clovekvtisni.coordinator.android.DeployEnvironment;
import cz.clovekvtisni.coordinator.android.util.DiskCache;

public class BitmapLoader extends Loader<BitmapLoader.Listener> {
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
			diskCache = DiskCache.newInstance(dir);
		}
		return diskCache;
	}

	@Override
	protected void doInBackground() {
		InputStream is = null;
		try {
			DiskCache cache = getOrCreateDiskCache();
			if (cache.get(url) == null) {
				is = HttpRequest.get(url).stream();
				cache.put(url, is);
			}
			Bitmap bitmap = cache.get(url).getBitmap();
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
		if (getClass().equals(other.getClass())) {
			BitmapLoader otherLoader = (BitmapLoader) other;
			if (url.equals(otherLoader.url)) return true;
		}
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
