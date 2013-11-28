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
import cz.clovekvtisni.coordinator.android.util.Lg;
import cz.clovekvtisni.coordinator.android.util.Utils;

public class OsmMapView extends View implements TouchHelper.OnMapTapListener, TileLoadedListener {

	private static final int TILE_BITMAP_BYTES = 256 * 256 * 4;
	private static final Paint BITMAP_PAINT = new Paint(Paint.FILTER_BITMAP_FLAG);

	private DiskTileLoader diskTileLoader;
	private List<MapOverlay> overlays = Lists.newArrayList();
	private static LruCache<TileId, Bitmap> bitmapCache;
	private Projection projection;

    private OsmMapEventsListener osmMapEventsListener;

	public OsmMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initCache();
		initProjection();
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
		diskTileLoader.shutDown();
	}

	@Override
	public void onSingleTap(float x, float y) {
		double nearestDp = Integer.MAX_VALUE;
		MapOverlay nearestOverlay = null;
		for (MapOverlay overlay : overlays) {
            if (overlay.getLatLon() != null) {
			    Point p = projection.latLonToPixels(overlay.getLatLon());
			    int distancePx = (int) Math.sqrt(Math.pow((x - p.x), 2) + Math.pow((y - p.y), 2));
			    int distanceDp = (int) Utils.pxToDp(getResources(), distancePx);
			    if (distanceDp < 40 && distanceDp < nearestDp) {
				    nearestOverlay = overlay;
				    nearestDp = distanceDp;
			    }
            } else {
                // null LatLon in overlay, probably my position
            }
		}

		if (nearestOverlay != null) nearestOverlay.onTap();
	}

    @Override
    public void onLongTap(float x, float y) {
        double latitude = projection.pixelsToLatitudes((getHeight()/2)-y) + projection.getCenterLatLon().getLat();
        double longitude = projection.pixelsToLongitudes(x-getWidth()/2) + projection.getCenterLatLon().getLon();
        Lg.MAP.i("Long touch at "+latitude+" x "+longitude);
        if (osmMapEventsListener != null) {
            osmMapEventsListener.onLongTap(latitude, longitude);
        }
    }

    public void setCenter(LatLon center) {
		projection.setCenterLatLon(center);
		invalidate();
	}

    public LatLon getCenter() {
        return projection.getCenterLatLon();
    }
	
	public void setNetTileLoader(NetworkTileLoader netTileLoader) {
		TileCache cache;
		try {
			cache = TileCache.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError();
		}
		
		diskTileLoader = new DiskTileLoader(cache, this, netTileLoader, new Handler());
	}

	public void setZoom(double zoom) {
		projection.setZoom(zoom);
		invalidate();
	}

    public double getZoom() {
        return projection.getZoom();
    }

	private void initCache() {
        if (bitmapCache == null) {
            int memClass = ((ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            int cacheSize = 1024 * 1024 * memClass / 3;
            bitmapCache = new LruCache<TileId, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(TileId key, Bitmap bitmap) {
                    return TILE_BITMAP_BYTES;
                }
            };
        }
	}

	private void initProjection() {
		projection = new Projection(getResources().getDisplayMetrics().densityDpi);
        // default, will be overriden by my parent
		projection.setCenterLatLon(new LatLon(50.083333, 14.416667));
		projection.setZoom(100000);
	}

	private void drawOverlays(Canvas canvas) {
		for (MapOverlay overlay : overlays) {
            if (overlay.getLatLon() != null) {
			    Point p = projection.latLonToPixels(overlay.getLatLon());
			    overlay.onDraw(canvas, p.x, p.y, projection.mapMetersToPixels(1));
            } else {
                Lg.MAP.w("Null LatLon in overlay: "+overlay);
            }
		}
	}

	private void drawTiles(Canvas canvas) {
		for (ProjectedTile tile : projection.getTiles()) {
			Bitmap bitmap = bitmapCache.get(tile.getTileId());
			if (bitmap == null) {
				diskTileLoader.requestTile(tile.getTileId());

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


    public interface OsmMapEventsListener {
        void onLongTap(double latitude, double longitude);
    }

    public OsmMapEventsListener getOsmMapEventsListener() {
        return osmMapEventsListener;
    }

    public void setOsmMapEventsListener(OsmMapEventsListener osmMapEventsListener) {
        this.osmMapEventsListener = osmMapEventsListener;
    }

}
