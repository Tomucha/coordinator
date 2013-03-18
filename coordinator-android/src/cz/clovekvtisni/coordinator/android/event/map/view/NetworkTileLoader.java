package cz.clovekvtisni.coordinator.android.event.map.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.os.Handler;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class NetworkTileLoader {
	private final TileCache cache;
	private final ExecutorService executor;
	private final Handler handler;
	private final TileLoadedListener listener;

	public NetworkTileLoader(TileCache cache, TileLoadedListener listener, Handler handler) {
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
			InputStream is = null;
			try {
				is = HttpRequest.get(tileId.getUrl()).buffer();
				cache.put(tileId, is);
				returnBitmapOrNull(cache.get(tileId));
			} catch (HttpRequestException e) {
				e.printStackTrace();
				returnBitmapOrNull(null);
			} catch (IOException e) {
				e.printStackTrace();
				returnBitmapOrNull(null);
			} finally {
				IOUtils.closeQuietly(is);
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
