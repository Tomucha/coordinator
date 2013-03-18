package cz.clovekvtisni.coordinator.android.event.map.view;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

import cz.clovekvtisni.coordinator.android.util.Utils;

public class TileCache {
	private static final int MAX_SIZE_BYTES = 1024 * 1024 * 500; // 500 MB
	private static final String DIRECTORY = "tiles";

	private final DiskLruCache diskLruCache;

	public TileCache(Context c) throws IOException {
		File dir = new File(c.getExternalCacheDir(), DIRECTORY);
		dir.mkdir();
		diskLruCache = DiskLruCache.open(dir, Utils.getVersionCode(c), 1, MAX_SIZE_BYTES);
	}

	public Bitmap get(TileId tileId) throws IOException {
		Snapshot snapshot = diskLruCache.get(keyForTile(tileId));
		if (snapshot == null) return null;
		else return BitmapFactory.decodeStream(snapshot.getInputStream(0));
	}

	public void put(TileId tileId, Bitmap bitmap) throws IOException {
		Editor editor = diskLruCache.edit(keyForTile(tileId));
		OutputStream outputStream = editor.newOutputStream(0);
		bitmap.compress(CompressFormat.PNG, 100, outputStream);
		editor.commit();
	}

	private String keyForTile(TileId tileId) {
		return tileId.getOsmZoom() + "_" + tileId.getOsmX() + "_" + tileId.getOsmY();
	}
}
