package cz.clovekvtisni.coordinator.android.event.map.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.os.Handler;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import cz.clovekvtisni.coordinator.android.util.Lg;

public class NetworkTileLoader {
	private static final int THREADS = 3;

	private final LinkedList<TileId> requestedTiles = new LinkedList<TileId>();
	private final TileCache cache;
	private final ExecutorService executor;
	private final Handler handler;
	private TileLoadedListener listener;

	public NetworkTileLoader(TileCache cache, Handler handler) {
		this.cache = cache;
		this.executor = Executors.newFixedThreadPool(THREADS);
		this.handler = handler;
		startUp();
	}

	public void requestTile(TileId tileId) {
        synchronized (requestedTiles) {
            Lg.MAP.i("Requested tile: "+tileId);
		    requestedTiles.remove(tileId);
		    requestedTiles.addFirst(tileId);
            requestedTiles.notify();
        }
	}

	public void setListener(TileLoadedListener listener) {
		if (this.listener != null) throw new IllegalStateException();
		this.listener = listener;
	}

	public void removeListener(TileLoadedListener listener) {
		if (this.listener != listener) throw new IllegalStateException();
		this.listener = null;
	}

	public void shutDown() {
		executor.shutdownNow();
	}

	public void startUp() {
		for (int i = 0; i < THREADS; i++) {
            Lg.MAP.i("Creating new Worker");
			executor.submit(new Worker());
		}
	}

	private class Worker implements Runnable {
		private void download(TileId tileId) {
			InputStream is = null;
			try {
				is = HttpRequest.get(tileId.getUrl()).buffer();
				cache.put(tileId, is);
				Lg.MAP.i("Downloaded tile, " + requestedTiles.size() + " remaining.");
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
				if (executor.isShutdown()) {
                    Lg.MAP.i("Ending download thread");
                    break;
                }
				TileId tileId = null;
				try {
                    try {
                        synchronized (requestedTiles) {
                           if (!requestedTiles.isEmpty()) {
                              tileId = requestedTiles.removeFirst();
                           }
                        }
                    } catch (NoSuchElementException e) {
                        // ok, ok, we get it, no such element
                    }
                    if (tileId == null) {
                        synchronized (requestedTiles) {
                            Lg.MAP.i("Waiting for tile: "+tileId);
                            requestedTiles.wait();
                        }
                    }
				} catch (InterruptedException e) {
					continue;
				}
                if (tileId != null) {
				    download(tileId);
                }
			}
		}

		private void returnBitmapOrNull(final Bitmap bitmap, final TileId tileId) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (listener != null) listener.onTileLoaded(tileId, bitmap);
				}
			});
		}
	}
}
