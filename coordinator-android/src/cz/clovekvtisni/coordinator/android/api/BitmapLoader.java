package cz.clovekvtisni.coordinator.android.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fhucho.android.workers.Loader;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class BitmapLoader extends Loader<BitmapLoader.Listener> {

	private final int density;
	private final String url;

	private Result result;

	public BitmapLoader(String url, int density) {
		super(Listener.class);
		this.density = density;
		this.url = "http://coordinator-test.appspot.com" + url;
	}

	@Override
	protected void doInBackground() {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inDensity = 160;
			options.inTargetDensity = density;
			Bitmap bitmap = BitmapFactory
					.decodeStream(HttpRequest.get(url).stream(), null, options);
			result = new Result(bitmap);
			getListenerProxy().onSuccess(bitmap);
		} catch (HttpRequestException e) {
			result = new Result(e);
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
