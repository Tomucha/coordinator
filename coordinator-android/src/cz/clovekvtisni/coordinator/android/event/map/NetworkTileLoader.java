package cz.clovekvtisni.coordinator.android.event.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class NetworkTileLoader {
	private final DiskTileCache cache;
	private final ExecutorService executor;
	private final Handler handler;
	private final TileLoadedListener listener;

	public NetworkTileLoader(DiskTileCache cache, TileLoadedListener listener, Handler handler) {
		this.cache = cache;
		this.executor = Executors.newFixedThreadPool(2);
		this.listener = listener;
		this.handler = handler;
	}

	public void requestTile(TileId tileId) {
		executor.execute(new Worker(tileId));
	}

	public void shutDown() {
		executor.shutdownNow();
	}

	private class Worker implements Runnable {
		private final TileId tileId;

		public Worker(TileId tileId) {
			this.tileId = tileId;
		}

		@Override
		public void run() {
			try {
				InputStream stream = HttpRequest.get(tileId.getUrl()).stream();
				Bitmap bitmap = BitmapFactory.decodeStream(stream);
				if (bitmap == null) {
					returnBitmapOrNull(null);
				} else {
					cache.put(tileId, bitmap);
					returnBitmapOrNull(bitmap);
				}
			} catch (HttpRequestException e) {
				e.printStackTrace();
				returnBitmapOrNull(null);
			} catch (IOException e) {
				e.printStackTrace();
				returnBitmapOrNull(null);
			}
		}

		private void returnBitmapOrNull(final Bitmap bitmap) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.onTileLoaded(tileId, bitmap);
				}
			});
		}
	}
}
