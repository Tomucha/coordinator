package cz.clovekvtisni.coordinator.android.event.map.view;

import java.util.List;

import android.graphics.Point;
import android.graphics.Rect;

import com.google.common.collect.Lists;


public class Projection {
	private static final double EARTH_RADIUS = 6378137;
	private static final double ONE_LATITUDE_IN_METERS = 111000;

	private final double onePixelInMeters;

	private double zoom; // 1 cm on the screen = <zoom> cm on the map
	private int screenWidth;
	private int screenHeight;
	private LatLon centerLatLon;

	public Projection(int dpi) {
		onePixelInMeters = 1 / (dpi / 2.54 * 100);
	}
	
	public static double oneLatitudeInMeters() {
		return ONE_LATITUDE_IN_METERS;
	}

	public static double oneLongitudeInMeters(double latitude) {
		return Math.PI / 180 * EARTH_RADIUS * Math.cos(Math.toRadians(latitude));
	}
	
	public static Point latLonToOsm(LatLon latLon, int osmZoom) {
		int osmX = (int) Math.floor((latLon.getLon() + 180) / 360 * (1 << osmZoom));
		int osmY = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(latLon.getLat())) + 1
				/ Math.cos(Math.toRadians(latLon.getLat())))
				/ Math.PI)
				/ 2 * (1 << osmZoom));
		return new Point(osmX, osmY);
	}

	public List<ProjectedTile> getTiles() {
		LatLon topLeftLatLon = new LatLon(centerLatLon.getLat() + pixelsToLatitudes(screenHeight)
				/ 2, centerLatLon.getLon() - pixelsToLongitudes(screenWidth) / 2);

		int osmZoom = chooseOsmZoom();

		Point topLeftTileOsm = latLonToOsm(topLeftLatLon, osmZoom);

		LatLon topLeftTileLatLon = osmToLatLon(topLeftTileOsm.x, topLeftTileOsm.y, osmZoom);

		double pxOffsetX = -longitudesToPixels(Math.abs(topLeftTileLatLon.getLon()
				- topLeftLatLon.getLon()));
		double pxOffsetY = -latitudesToPixels(Math.abs(topLeftTileLatLon.getLat()
				- topLeftLatLon.getLat()));

		double tileSizePx = tileSizePx(osmZoom);

		List<ProjectedTile> tiles = Lists.newArrayList();
		for (int yIdx = 0; yIdx < screenHeight / tileSizePx + 2; yIdx++) {
			for (int xIdx = 0; xIdx < screenWidth / tileSizePx + 2; xIdx++) {
				Rect srcRect = new Rect(0, 0, 256, 256);

				Rect dstRect = new Rect();
				dstRect.left = (int) (pxOffsetX + xIdx * tileSizePx);
				dstRect.top = (int) (pxOffsetY + yIdx * tileSizePx);
				dstRect.right = (int) (pxOffsetX + (xIdx + 1) * tileSizePx);
				dstRect.bottom = (int) (pxOffsetY + (yIdx + 1) * tileSizePx);

				TileId tileId = new TileId(topLeftTileOsm.x + xIdx, topLeftTileOsm.y + yIdx,
						osmZoom);
				tiles.add(new ProjectedTile(srcRect, dstRect, tileId));
			}
		}

		return tiles;
	}

	public double getZoom() {
		return zoom;
	}

	public Point latLonToPixels(LatLon latLon) {
		double xDiff = longitudesToPixels(latLon.getLon() - centerLatLon.getLon());
		double yDiff = -latitudesToPixels(latLon.getLat() - centerLatLon.getLat());
		int x = (int) (screenWidth / 2.0 + xDiff);
		int y = (int) (screenHeight / 2.0 + yDiff);
		return new Point(x, y);
	}

	public double mapMetersToPixels(double mapMeters) {
		return mapMeters / zoom / onePixelInMeters;
	}
	
	public double pixelsToLatitudes(double pixels) {
		return pixelsToMapMeters(pixels) / ONE_LATITUDE_IN_METERS;
	}

	public double pixelsToLongitudes(double pixels) {
		return pixelsToMapMeters(pixels) / oneLongitudeInMeters(centerLatLon.getLat());
	}

	public LatLon getCenterLatLon() {
		return centerLatLon;
	}

	public void setCenterLatLon(LatLon centerLatLon) {
		this.centerLatLon = centerLatLon;
	}

	public void setScreenSize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	private int chooseOsmZoom() {
		for (int osmZoom = 0; osmZoom < 18; osmZoom++) {
			double px = tileSizePx(osmZoom);
			if (px < 256) return osmZoom;
		}
		return 18;
	}

	private double latitudesToPixels(double latitudes) {
		return latitudes * ONE_LATITUDE_IN_METERS / zoom / onePixelInMeters;
	}

	private double longitudesToPixels(double longitudes) {
		return longitudes * oneLongitudeInMeters(centerLatLon.getLat()) / zoom / onePixelInMeters;
	}

	private LatLon osmToLatLon(int x, int y, int osmZoom) {
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, osmZoom);
		double lat = Math.toDegrees(Math.atan(Math.sinh(n)));
		double lon = x / Math.pow(2.0, osmZoom) * 360.0 - 180;
		return new LatLon(lat, lon);
	}

	private double pixelsToMapMeters(double pixels) {
		double screenMeters = onePixelInMeters * pixels;
		return screenMeters * zoom;
	}

	private double tileSizePx(int osmZoom) {
		double mapMeters = 40075016.686 * Math.cos(Math.toRadians(centerLatLon.getLat()))
				/ (Math.pow(2, osmZoom));
		double screenMeters = mapMeters / zoom;
		return screenMeters / onePixelInMeters;
	}

	public static class LatLon {
		private final double lat;
		private final double lon;

		public LatLon(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		public double getLat() {
			return lat;
		}

		public double getLon() {
			return lon;
		}
	}

	public static class ProjectedTile {
		private final Rect srcRect, dstRect;
		private final TileId tileId;

		public ProjectedTile(Rect srcRect, Rect dstRect, TileId tileId) {
			this.srcRect = srcRect;
			this.dstRect = dstRect;
			this.tileId = tileId;
		}

		public TileId getTileId() {
			return tileId;
		}

		public Rect getSrcRect() {
			return srcRect;
		}

		public Rect getDstRect() {
			return dstRect;
		}

		public ProjectedTile createCorrespondingTileWithOneLevelLowerZoom() {
			Rect newSrcRect = new Rect();
			newSrcRect.left = 128 * (tileId.getOsmX() % 2);
			newSrcRect.top = 128 * (tileId.getOsmY() % 2);
			newSrcRect.right = newSrcRect.left + 128;
			newSrcRect.bottom = newSrcRect.top + 128;
			TileId newTileId = new TileId(tileId.getOsmX() / 2, tileId.getOsmY() / 2,
					tileId.getOsmZoom() - 1);
			return new ProjectedTile(newSrcRect, dstRect, newTileId);
		}
	}
}
