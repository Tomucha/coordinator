package cz.clovekvtisni.coordinator.android.event.map;

import android.graphics.Canvas;
import cz.clovekvtisni.coordinator.android.event.map.view.Projection.LatLon;

public abstract class MapOverlay {
	private LatLon latLon;

	public MapOverlay() {
	}

	public MapOverlay(LatLon latLon) {
		this.latLon = latLon;
	}

	public LatLon getLatLon() {
		return latLon;
	}
	
	public void onTap() {
	}

	public void setLatLon(LatLon latLon) {
		this.latLon = latLon;
	}

	public abstract void onDraw(Canvas canvas, int x, int y, double oneMapMeterInPixels);

}
