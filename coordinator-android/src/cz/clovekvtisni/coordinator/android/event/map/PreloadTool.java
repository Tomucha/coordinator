package cz.clovekvtisni.coordinator.android.event.map;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.graphics.Point;
import cz.clovekvtisni.coordinator.android.event.map.view.Projection;
import cz.clovekvtisni.coordinator.android.event.map.view.Projection.LatLon;
import cz.clovekvtisni.coordinator.android.event.map.view.TileCache;
import cz.clovekvtisni.coordinator.android.event.map.view.TileId;
import cz.clovekvtisni.coordinator.domain.EventLocation;

public class PreloadTool {

	private static final int DEFAULT_RADIUS_METERS = 2000;
	private static final int MIN_OSM_ZOOM = 10;
	private static final int MAX_OSM_ZOOM = 18;

	public static Set<TileId> tilesThatShouldBePreloaded(EventLocation[] eventLocations,
			TileCache cache) throws IOException {
		Set<TileId> tiles = new HashSet<TileId>();
		for (EventLocation loc : eventLocations) {
			long radius = loc.getRadius() == null ? DEFAULT_RADIUS_METERS : loc.getRadius();
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			double latDiff = radius / Projection.oneLatitudeInMeters();
			double lonDiff = radius / Projection.oneLongitudeInMeters(lat);

			for (int osmZoom = MIN_OSM_ZOOM; osmZoom <= MAX_OSM_ZOOM; osmZoom++) {
				Point corner1 = Projection.latLonToOsm(new LatLon(lat + latDiff, lon + lonDiff),
						osmZoom);
				Point corner2 = Projection.latLonToOsm(new LatLon(lat - latDiff, lon - lonDiff),
						osmZoom);

				int minOsmX = corner1.x;
				int maxOsmX = corner2.x;
				if (minOsmX > maxOsmX) {
					int tmp = minOsmX;
					minOsmX = maxOsmX;
					maxOsmX = tmp;
				}

				int minOsmY = corner1.y;
				int maxOsmY = corner2.y;
				if (minOsmY > maxOsmY) {
					int tmp = minOsmY;
					minOsmY = maxOsmY;
					maxOsmY = tmp;
				}

				for (int osmX = minOsmX; osmX <= maxOsmX; osmX++) {
					for (int osmY = minOsmY; osmY <= maxOsmY; osmY++) {
						tiles.add(new TileId(osmX, osmY, osmZoom));
					}
				}
			}
		}
		
		removeCachedTiles(tiles, cache);

		return tiles;
	}

	private static void removeCachedTiles(Set<TileId> tiles, TileCache cache) throws IOException {
		for (Iterator<TileId> i = tiles.iterator(); i.hasNext();) {
			TileId tileId = i.next();
			if (cache.contains(tileId)) i.remove();
		}
	}
}
