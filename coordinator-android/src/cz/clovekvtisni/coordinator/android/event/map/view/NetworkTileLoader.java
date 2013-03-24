package cz.clovekvtisni.coordinator.android.event.map.view;

import android.graphics.Bitmap;
import android.os.Handler;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import cz.clovekvtisni.coordinator.android.event.map.PreloadTool;
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.domain.EventLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkTileLoader {
    private static final int THREADS = 3;

    private final LinkedList<TileId> requestedTiles = new LinkedList<TileId>();
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
                    Lg.MAP.i("Computing tiles to preload");
                    Set<TileId> tiles = PreloadTool.tilesThatShouldBePreloaded(eventLocations, cache);
                    Lg.MAP.i("Will preload: " + tiles.size());
                    synchronized (requestedTiles) {
                        for (TileId tileId : tiles) {
                            if (!requestedTiles.contains(tileId)) requestedTiles.addLast(tileId);
                        }
                        if (requestedTiles.isEmpty() && remainingTilesListener != null) {
                            remainingTilesListener.onRemainingTilesChanged(0);
                        } else {
                            requestedTiles.notify();
                        }
                    }
                } catch (IOException e) {
                    throw new AssertionError();
                }
            }

            ;
        }.start();
    }

    public void requestTile(TileId tileId) {
        synchronized (requestedTiles) {
            Lg.MAP.i("Requested tile: " + tileId);
            requestedTiles.remove(tileId);
            requestedTiles.addFirst(tileId);
            requestedTiles.notify();
        }
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
                Lg.MAP.i("Downloaded tile " + tileId + ", " + requestedTiles.size() + " more in the queue.");
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
                            Lg.MAP.i("Waiting for tile: " + tileId);
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
