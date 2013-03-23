package cz.clovekvtisni.coordinator.android.event.map.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.os.Handler;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import cz.clovekvtisni.coordinator.android.event.map.PreloadTool;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.domain.EventLocation;

public class NetworkTileLoader {
	private static final int THREADS = 2;

	private final BlockingDeque<TileId> requestedTiles = new LinkedBlockingDeque<TileId>();
	private final TileCache cache;
	private final ExecutorService executor;
	private final Handler handler;
	private RemainingTilesListener remainingTilesListener;
	private TileLoadedListener tileLoadedListener;

	public NetworkTileLoader(TileCache cache, Handler handler) {
		this.cache = cache;
		this.executor = Executors.newFixedThreadPool(THREADS);
		this.handler = handler;
		startUp();
	}

	public void preloadTiles(final EventLocation[] eventLocations) {
		new Thread() {
			public void run() {
				try {
					Set<TileId> tiles = PreloadTool.tilesThatShouldBePreloaded(eventLocations,
							cache);
					System.out.println(tiles.size());
					for (TileId tileId : tiles) {
						if (!requestedTiles.contains(tileId)) requestedTiles.addLast(tileId);
					}
				} catch (IOException e) {
					throw new AssertionError();
				}
			};
		}.start();
	}

	public void requestTile(TileId tileId) {
		requestedTiles.remove(tileId);
		requestedTiles.addFirst(tileId);
	}

	public void setTileLoadedListener(TileLoadedListener listener) {
		if (this.tileLoadedListener != null) throw new IllegalStateException();
		this.tileLoadedListener = listener;
	}

	public void removeTileLoadedListener(TileLoadedListener listener) {
		if (this.tileLoadedListener != listener) throw new IllegalStateException();
		this.tileLoadedListener = null;
	}
	
	public void setRemainingTilesListener(RemainingTilesListener listener) {
		if (this.remainingTilesListener != null) throw new IllegalStateException();
		this.remainingTilesListener = listener;
	}
	
	public void removeRemainingTilesListener(RemainingTilesListener listener) {
		if (this.remainingTilesListener != listener) throw new IllegalStateException();
		this.remainingTilesListener = null;
	}

	public void shutDown() {
		executor.shutdownNow();
	}

	public void startUp() {
		for (int i = 0; i < THREADS; i++) {
			executor.submit(new Worker());
		}
	}

	private class Worker implements Runnable {
		private void download(TileId tileId) {
			InputStream is = null;
			try {
				is = HttpRequest.get(tileId.getUrl()).buffer();
				cache.put(tileId, is);
				Lg.MAP.d("Downloaded tile " + tileId + ", " + requestedTiles.size()
						+ " more in the queue.");
				returnBitmapOrNull(cache.get(tileId), tileId);
			} catch (HttpRequestException e) {
				e.printStackTrace();
				returnBitmapOrNull(null, tileId);
			} catch (IOException e) {
				e.printStackTrace();
				returnBitmapOrNull(null, tileId);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}

		@Override
		public void run() {
			while (true) {
				if (executor.isShutdown()) break;
				TileId tileId;
				try {
					tileId = requestedTiles.takeFirst();
				} catch (InterruptedException e) {
					continue;
				}
				download(tileId);
			}
		}

		private void returnBitmapOrNull(final Bitmap bitmap, final TileId tileId) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (tileLoadedListener != null) tileLoadedListener.onTileLoaded(tileId, bitmap);
					
					int remaining = requestedTiles.size();
					if (remainingTilesListener != null) remainingTilesListener.onRemainingTilesChanged(remaining);
				}
			});
		}
	}
	
	public static interface RemainingTilesListener {
		public void onRemainingTilesChanged(int remainingTiles);
	}
}
