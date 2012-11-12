package cz.clovekvtisni.coordinator.android.util;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.graphics.Bitmap;

/**
 * Jednoducha cache, ke kazde polozce lze ulozit time-to-live ve vterinach.
 * 
 * Polozky se ukladaji jako {@link SoftReference}, takze je mozne, ze budou kdykoliv ustreleny.
 * 
 * Nehodi se k ukladani {@link Bitmap} nebo podobnych objektu, ktere vyzaduji poctivy uklid nejakym destruktorem.
 * 
 */
@SuppressWarnings("serial")
public class MicroCache<K, V> implements Serializable {

	private transient Map<K, MicroCacheEntry<V>> contentMap = null;
    private int defaultTtlSec = 600;
	
	/**
	 * @param key klic
	 * @param value hodnota
     * @param entryTtl platnost ve vterinach
	 */
	public synchronized void put(K key, V value, int entryTtlSec) {
		ensureMap();
		contentMap.put(key, new MicroCacheEntry<V>(value, entryTtlSec));
	}

    public void put(K key, V value) {
        put(key, value, defaultTtlSec);
    }

    public void setDefaultTtlSec(int defaultTtlSec) {
        this.defaultTtlSec = defaultTtlSec;
    }

    public synchronized void remove(K key) {
		ensureMap();
		contentMap.remove(key);
	}
	
	/**
	 * Returns an object stored under specified key, or null, if cache doesnt contain such object, or it's entry has expired.
	 * 
	 * @param key
	 * @return
	 */
	public synchronized V get(K key) {
		ensureMap();
		MicroCacheEntry<V> entry = contentMap.get(key);
		if (entry == null) {
			return null;
		}
		if (!entry.isAlive()) {
			contentMap.remove(key);
			return null;
		}
		return entry.getValue();
	}

	/**
	 * Searches whole cache for dead entries.
	 */
	private synchronized void clearMicroCache() {
		ensureMap();
		Set<K> keys = new HashSet<K>();
		Set<K> keyz = contentMap.keySet();
		keys.addAll(keyz);
		for (K key : keys) {
			MicroCacheEntry<V> entry = contentMap.get(key);
			if (!entry.isAlive()) {
				contentMap.remove(key);
			}
		}
	}
	
	private void ensureMap() {
		if (contentMap == null) {
			contentMap = new HashMap<K, MicroCacheEntry<V>>();
		}
	}

    public synchronized void clear() {
        contentMap = null;
    }

    /**
	 * Cache entry object.
	 * @author Tomas Zverina
	 */
	private static class MicroCacheEntry<V> {
		
		private SoftReference<V> value = null;
		private int ttlSec = 0;
		private long createdTimestamp = 0;
		
		/**
		 * @param value hodnota
		 * @param ttlSec platnost ve vterinach
		 */
		MicroCacheEntry(V value, int ttlSec) {
			super();
			this.value = new SoftReference<V>(value);
			this.ttlSec = ttlSec * 1000;
			createdTimestamp = System.currentTimeMillis();
		}
		
		V getValue() {
			return value.get();
		}
		
		boolean isAlive() {
			return (
				((System.currentTimeMillis()-createdTimestamp) < ttlSec)
				&& (value.get() != null)
			);
		}
		
	}
}