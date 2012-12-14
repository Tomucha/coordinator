package cz.clovekvtisni.coordinator.android.event.map;

import android.graphics.Bitmap;

public interface TileLoadedListener {
	public void onTileLoaded(TileId tileId, Bitmap bitmap);
}
