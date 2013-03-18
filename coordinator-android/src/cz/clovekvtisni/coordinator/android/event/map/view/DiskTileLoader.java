package cz.clovekvtisni.coordinator.android.event.map.view;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;

import com.google.common.collect.Sets;


public class DiskTileLoader implements TileLoadedListener {
	private final TileCache cache;
	private final ExecutorService executor;
	private final Handler handler;
	private final NetworkTileLoader networkTileLoader;
	private final TileLoadedListener listener;
	private final Set<TileId> tileIds = Sets.newHashSet();

	public DiskTileLoader(TileCache cache, TileLoadedListener listener, Handler handler) {
		this.cache = cache;
		this.executor = Executors.newFixedThreadPool(1);
		this.listener = listener;
		this.handler = handler;
		this.networkTileLoader = new NetworkTileLoader(cache, this, handler);
	}

	public void onTileLoaded(final TileId tileId, final Bitmap bitmap) {
		tileIds.remove(tileId);
		listener.onTileLoaded(tileId, bitmap);
	}

	public void requestTile(TileId tileId) {
		if (tileIds.contains(tileId)) return;

		tileIds.add(tileId);
		executor.execute(new Worker(tileId));
	}

	public void shutDown() {
		executor.shutdownNow();
		networkTileLoader.shutDown();
	}

	private class Worker implements Runnable {
		private final TileId tileId;

		public Worker(TileId tileId) {
			this.tileId = tileId;
		}

		@Override
		public void run() {
			try {
				Bitmap b = cache.get(tileId);
				if (b == null) requestDownload();
				else returnBitmapOrNull(b);
			} catch (IOException e) {
				e.printStackTrace();
				returnBitmapOrNull(null);
			}
		}

		private void requestDownload() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					networkTileLoader.requestTile(tileId);
				}
			});
		}

		private void returnBitmapOrNull(final Bitmap bitmap) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					onTileLoaded(tileId, bitmap);
				}
			});
		}
	}
}
