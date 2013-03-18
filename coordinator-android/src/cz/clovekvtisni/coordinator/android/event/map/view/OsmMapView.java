package cz.clovekvtisni.coordinator.android.event.map.view;

import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.View;

import com.google.common.collect.Lists;

import cz.clovekvtisni.coordinator.android.event.map.MapOverlay;
import cz.clovekvtisni.coordinator.android.event.map.view.Projection.LatLon;
import cz.clovekvtisni.coordinator.android.event.map.view.Projection.ProjectedTile;
import cz.clovekvtisni.coordinator.android.event.map.view.TouchHelper.OnSingleTapListener;
import cz.clovekvtisni.coordinator.android.util.Utils;

public class OsmMapView extends View implements OnSingleTapListener, TileLoadedListener {

	private static final int TILE_BITMAP_BYTES = 256 * 256 * 4;
	private static final Paint BITMAP_PAINT = new Paint(Paint.FILTER_BITMAP_FLAG);

	private DiskTileLoader tileLoader;
	private List<MapOverlay> overlays = Lists.newArrayList();
	private LruCache<TileId, Bitmap> bitmapCache;
	private Projection projection;

	public OsmMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initCache();
		initProjection();
		initTileLoader();
		setOnTouchListener(new TouchHelper(context, projection, this));
	}

	public List<MapOverlay> getOverlays() {
		return overlays;
	}

	@Override
	public void onTileLoaded(TileId tileId, Bitmap bitmap) {
		if (bitmap == null) return;
		bitmapCache.put(tileId, bitmap);
		invalidate();
	}

	public void onDestroy() {
		tileLoader.shutDown();
	}

	@Override
	public void onSingleTap(float x, float y) {
		double nearestDp = Integer.MAX_VALUE;
		MapOverlay nearestOverlay = null;
		for (MapOverlay overlay : overlays) {
			Point p = projection.latLonToPixels(overlay.getLatLon());
			int distancePx = (int) Math.sqrt(Math.pow((x - p.x), 2) + Math.pow((y - p.y), 2));
			int distanceDp = (int) Utils.pxToDp(getResources(), distancePx);
			if (distanceDp < 40 && distanceDp < nearestDp) {
				nearestOverlay = overlay;
				nearestDp = distanceDp;
			}
		}

		if (nearestOverlay != null) nearestOverlay.onTap();
	}

	public void setCenter(LatLon center) {
		projection.setCenterLatLon(center);
		invalidate();
	}

	public void setZoom(double zoom) {
		projection.setZoom(zoom);
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
		TileCache cache;
		try {
			cache = new TileCache(getContext());
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError();
		}
		tileLoader = new DiskTileLoader(cache, this, new Handler());
	}

	private void initProjection() {
		projection = new Projection(getResources().getDisplayMetrics().densityDpi);
		projection.setCenterLatLon(new LatLon(50.083333, 14.416667));
		projection.setZoom(100000);
	}

	private void drawOverlays(Canvas canvas) {
		for (MapOverlay overlay : overlays) {
			Point p = projection.latLonToPixels(overlay.getLatLon());
			overlay.onDraw(canvas, p.x, p.y, projection.mapMetersToPixels(1));
		}
	}

	private void drawTiles(Canvas canvas) {
		for (ProjectedTile tile : projection.getTiles()) {
			Bitmap bitmap = bitmapCache.get(tile.getTileId());
			if (bitmap == null) {
				tileLoader.requestTile(tile.getTileId());

				ProjectedTile parentTile = tile.createCorrespondingTileWithOneLevelLowerZoom();
				bitmap = bitmapCache.get(parentTile.getTileId());
				tile = parentTile;
			}

			if (bitmap != null) {
				canvas.drawBitmap(bitmap, tile.getSrcRect(), tile.getDstRect(), BITMAP_PAINT);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawTiles(canvas);
		drawOverlays(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		projection.setScreenSize(w, h);
	}
}
