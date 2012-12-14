package cz.clovekvtisni.coordinator.android.event.map;

import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.view.View;
import cz.clovekvtisni.coordinator.android.event.map.Projection.LatLon;
import cz.clovekvtisni.coordinator.android.event.map.Projection.ProjectedTile;

public class OsmMapView extends View implements TileLoadedListener {

	private static final int TILE_BITMAP_BYTES = 256 * 256 * 4;
	private static final Paint BITMAP_PAINT = new Paint(Paint.FILTER_BITMAP_FLAG);

	private DiskTileLoader tileLoader;
	private LruCache<TileId, Bitmap> bitmapCache;
	private Projection projection;

	public OsmMapView(Context context) {
		super(context);

		initCache();
		initProjection();
		initTileLoader();
		setOnTouchListener(new TouchListener(projection));
	}
	
	@Override
	public void onTileLoaded(TileId tileId, Bitmap bitmap) {
		if (bitmap == null) return;
		bitmapCache.put(tileId, bitmap);
		invalidate();
	}

	private void initCache() {
		int memClass = ((ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 2;
		bitmapCache = new LruCache<TileId, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(TileId key, Bitmap bitmap) {
				return TILE_BITMAP_BYTES;
			}
		};
	}

	private void initTileLoader() {
		DiskTileCache cache;
		try {
			cache = new DiskTileCache(getContext());
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError();
		}
		tileLoader = new DiskTileLoader(cache, this, new Handler());
	}

	private void initProjection() {
		projection = new Projection(getResources().getDisplayMetrics().densityDpi);
		projection.setCenterLatLon(new LatLon(50.083333, 14.416667));
		projection.setZoom(10000);
	}

	public void onDestroy() {
		tileLoader.shutDown();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (ProjectedTile tile : projection.getTiles()) {
			Bitmap bitmap = bitmapCache.get(tile.getTileId());
			if (bitmap == null) {
				tileLoader.requestTile(tile.getTileId());
			} else {
				canvas.drawBitmap(bitmap, tile.getSrcRect(), tile.getDstRect(), BITMAP_PAINT);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		projection.setScreenSize(w, h);
	}
}
