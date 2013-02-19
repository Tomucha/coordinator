package cz.clovekvtisni.coordinator.android.workers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class BitmapLoader extends Worker<BitmapLoader.Listener> {

	private final String url;

	public BitmapLoader(String url) {
		this.url = "http://coordinator-test.appspot.com" + url;
	}

	@Override
	protected void doInBackground() {
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(HttpRequest.get(url).stream());
			sendSuccess(bitmap);
		} catch (HttpRequestException e) {
			sendException(e);
		}
	}

	@Override
	public String getId() {
		return url;
	}

	protected void sendException(final Exception e) {
		send(new Runnable() {
			@Override
			public void run() {
				getListener().onException(e);
			}
		});
	}

	protected void sendSuccess(final Bitmap bitmap) {
		send(new Runnable() {
			@Override
			public void run() {
				getListener().onSuccess(bitmap);
			}
		});
	}

	public static interface Listener {
		public void onSuccess(Bitmap bitmap);

		public void onException(Exception e);
	}
}
