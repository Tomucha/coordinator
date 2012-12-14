package cz.clovekvtisni.coordinator.android.event.map;

import com.google.common.base.Objects;

public class TileId {
	private final int osmX, osmY, osmZoom;

	public TileId(int osmX, int osmY, int osmZoom) {
		this.osmX = osmX;
		this.osmY = osmY;
		this.osmZoom = osmZoom;
	}

	public String cacheKey() {
		return osmZoom + "." + osmX + "." + osmY;
	}

	public String getUrl() {
		return "http://a.tile.openstreetmap.org/" + osmZoom + "/" + osmX + "/" + osmY + ".png";
	}

	public int getOsmX() {
		return osmX;
	}

	public int getOsmY() {
		return osmY;
	}

	public int getOsmZoom() {
		return osmZoom;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;

		if (obj instanceof TileId) {
			final TileId other = (TileId) obj;
			return osmX == other.osmX && osmY == other.osmY && osmZoom == other.osmZoom;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(osmX, osmY, osmZoom);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(osmZoom).addValue(osmX).addValue(osmY)
				.toString();
	}
}
