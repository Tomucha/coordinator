package cz.clovekvtisni.coordinator.android.event.map.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import cz.clovekvtisni.coordinator.android.CoordinatorApplication;
import cz.clovekvtisni.coordinator.android.util.DiskCache;
import cz.clovekvtisni.coordinator.android.util.DiskCache.Snapshot;

public class TileCache {
	private static final String CACHE_DIR = "tiles";

	private static TileCache instance;

	private final DiskCache diskCache;

	public static synchronized TileCache getInstance() throws IOException {
		if (instance == null) instance = new TileCache();
		return instance;
	}

	private TileCache() throws IOException {
		Context c = CoordinatorApplication.getAppContext();
		diskCache = DiskCache.newInstance(new File(c.getExternalCacheDir(), CACHE_DIR));
	}

	public Bitmap get(TileId tileId) throws IOException {
		Snapshot snapshot = diskCache.get(tileKey(tileId));
		if (snapshot == null) return null;
		else return snapshot.getBitmap();
	}
	
	public void put(TileId tileId, InputStream is) throws IOException {
		diskCache.put(tileKey(tileId), is);
	}

	private String tileKey(TileId tileId) {
		return tileId.getOsmZoom() + "_" + tileId.getOsmX() + "_" + tileId.getOsmY();
	}
}
