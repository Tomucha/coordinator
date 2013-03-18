package cz.clovekvtisni.coordinator.android.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DiskCache {

	private static final String FILENAME_ANNOTATIONS = "annotations";
	private static final String FILENAME_VALUE = "value";

	private static final List<File> usedDirs = new ArrayList<File>();

	private final File dir;
	private final File tmpDir;
	private int nextTmpFileName = 0;

	private DiskCache(File dir) throws IOException {
		this.dir = dir;
		this.tmpDir = new File(dir, "tmp");

		FileUtils.forceMkdir(dir);
		FileUtils.forceMkdir(tmpDir);
		
		clean();
	}

	public static synchronized DiskCache newInstance(File dir) throws IOException {
		if (usedDirs.contains(dir)) {
			throw new RuntimeException("Cache dir " + dir.getAbsolutePath() + " was used before.");
		}

		usedDirs.add(dir);

		return new DiskCache(dir);
	}
	
	private synchronized void clean() throws IOException {
		for(File file:tmpDir.listFiles()) {
			System.out.println(file.getAbsolutePath());
			FileUtils.forceDelete(file);
		}
	}

	public synchronized Snapshot get(String key) throws IOException {
		File entryDir = new File(dir, toInternalKey(key));
		if (!entryDir.exists()) return null;

		File versionDir = new File(entryDir, String.valueOf(maxEntryVersion(entryDir)));
		File valueFile = new File(versionDir, FILENAME_VALUE);
		File annotationsFile = new File(versionDir, FILENAME_ANNOTATIONS);
		if (!valueFile.exists() || !annotationsFile.exists()) return null;

		FileInputStream fis = new FileInputStream(valueFile);
		return new Snapshot(fis, readAnnotations(annotationsFile));
	}

	public synchronized CacheOutputStream openStream(String key) throws IOException {
		return openStream(key, new HashMap<String, Serializable>());
	}

	public synchronized CacheOutputStream openStream(String key,
			Map<String, ? extends Serializable> annotations) throws IOException {
		nextTmpFileName++;
		File dir = new File(tmpDir, String.valueOf(nextTmpFileName));
		FileUtils.forceMkdir(dir);
		return new CacheOutputStream(dir, key, annotations);
	}

	public void put(String key, InputStream is) throws IOException {
		put(key, is, new HashMap<String, Serializable>());
	}

	public void put(String key, InputStream is, Map<String, Serializable> annotations)
			throws IOException {
		CacheOutputStream os = null;
		try {
			os = openStream(key, annotations);
			IOUtils.copy(is, os);
		} finally {
			if (os != null) os.close();
		}
	}

	public void put(String key, String value) throws IOException {
		put(key, value, new HashMap<String, Serializable>());
	}

	public void put(String key, String value, Map<String, ? extends Serializable> annotations)
			throws IOException {
		CacheOutputStream os = null;
		try {
			os = openStream(key, annotations);
			os.write(value.getBytes());
		} finally {
			if (os != null) os.close();
		}

	}

	private synchronized void publish(String key, File unpublished) throws IOException {
		File entryDir = new File(dir, toInternalKey(key));
		if (!entryDir.exists()) FileUtils.forceMkdir(entryDir);

		String version = String.valueOf(maxEntryVersion(entryDir) + 1);
		deleteAllVersions(entryDir);
		File versionDir = new File(entryDir, version);

		FileUtils.moveDirectory(unpublished, versionDir);
	}

	private int maxEntryVersion(File entryDir) {
		String[] names = entryDir.list();
		int maxVersion = 0;
		for (String name : names) {
			int version = Integer.parseInt(name);
			if (version > maxVersion) maxVersion = version;
		}
		return maxVersion;
	}

	private void deleteAllVersions(File entryDir) {
		File[] files = entryDir.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	private Map<String, Serializable> readAnnotations(File file) throws IOException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			@SuppressWarnings("unchecked")
			Map<String, Serializable> annotations = (Map<String, Serializable>) ois.readObject();
			return annotations;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(ois);
		}
	}

	private void saveAnnotations(Map<String, ? extends Serializable> annotations, File file)
			throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(annotations);
		} finally {
			IOUtils.closeQuietly(oos);
		}
	}

	private String toInternalKey(String key) {
		return Utils.md5(key);
	}

	private class CacheOutputStream extends FilterOutputStream {

		private final File dir;
		private final Map<String,? extends  Serializable> annotations;
		private final String key;
		private boolean failed = false;

		public CacheOutputStream(File dir, String key, Map<String, ? extends Serializable> annotations)
				throws FileNotFoundException {
			super(new BufferedOutputStream(new FileOutputStream(new File(dir, FILENAME_VALUE))));
			this.dir = dir;
			this.key = key;
			this.annotations = annotations;
		}

		@Override
		public void close() throws IOException {
			super.close();
			saveAnnotations(annotations, new File(dir, FILENAME_ANNOTATIONS));

			if (!failed) publish(key, dir);
		}

		@Override
		public void flush() throws IOException {
			try {
				super.flush();
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}

		@Override
		public void write(int oneByte) throws IOException {
			try {
				super.write(oneByte);
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}

		@Override
		public void write(byte[] buffer) throws IOException {
			try {
				super.write(buffer);
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}

		@Override
		public void write(byte[] buffer, int offset, int length) throws IOException {
			try {
				super.write(buffer, offset, length);
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}
	}

	public static class Snapshot {
		private final InputStream is;
		private final Map<String, Serializable> annotations;

		public Snapshot(InputStream is, Map<String, Serializable> annotations) {
			this.is = is;
			this.annotations = annotations;
		}

		public Bitmap getBitmap() throws IOException {
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				return bitmap;
			} finally {
				is.close();
			}

		}

		public String getString() throws IOException {
			try {
				String string = IOUtils.toString(is, Charsets.UTF_8);
				return string;
			} finally {
				is.close();
			}
		}

		public InputStream getInputStream() {
			return is;
		}

		public Map<String, Serializable> getAnnotations() {
			return annotations;
		}

		public void close() throws IOException {
			is.close();
		}
	}
}
