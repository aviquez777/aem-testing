package com.edc.edcweb.core.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Backing Java code for Cache management
 */

public class CacheManager{

	private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
	private static HashMap<String, Object> cacheMap = new HashMap<String, Object>();


	private CacheManager() {
		super();
	}

	// Get cache by given a key
	private synchronized static Cache getCache(String key) {
		return (Cache) cacheMap.get(key);
	}

	// If cache object exist 
	private synchronized static boolean hasCache(String key) {
		return cacheMap.containsKey(key);
	}

	// Clear all cache objects
	public synchronized static void clearAll() {
		cacheMap.clear();
		log.debug("clearAll(): Cache cleared.");
	}

	// Clear cache objects which key start with specified string  
	public synchronized static void clearAll(String type) {
		Iterator<Map.Entry<String, Object>>  itor = cacheMap.entrySet().iterator();
		String key;
		ArrayList<String> arr = new ArrayList<String>();
		try {
			while (itor.hasNext()) {
				Map.Entry<String, Object> entry = itor.next();
				key = entry.getKey();
				if (key.startsWith(type)) {
					arr.add(key);
				}
			}
			for (int k = 0; k < arr.size(); k++) {
				clearOnly(arr.get(k));
			}
			
			log.debug("All cache start with key:{} are cleared",type);
			
		} catch (NoSuchElementException | IllegalStateException | IndexOutOfBoundsException e) {
            log.debug("getCacheSize Exception error {}", e);
            log.error("error ", e);
		}
	}

	// Clear cache object by a given key
	public synchronized static void clearOnly(String key) {
		cacheMap.remove(key);
		log.debug("Cache with key:{} is cleared",key);
	}

	// Add a cache object
	public synchronized static void putCache(String key, Cache obj) {
		cacheMap.put(key, obj);
		log.debug("Cache with key:{} is added",key);
	}

	// Get a key
	public static Cache getCacheInfo(String key) {

		Cache cache = null;
		if (hasCache(key)) {
			cache = getCache(key);
			if (cacheExpired(cache)) {
				clearOnly(key);
				cache = null;
			}
		} 
		return cache;
	}

	// Add a cache with detail information, 
	public static void putCacheInfo(String key, Object obj, long liveTime) {
		Cache cache = new Cache();
		cache.setKey(key);
		if(liveTime<0){
			//never expire
			cache.setTimeOut(-1);
		}
		else {
			cache.setTimeOut(liveTime + System.currentTimeMillis());
		}
		cache.setValue(obj);
		log.debug("Adding cache with key:{}, expire in:{} ",key, liveTime);
		
		cacheMap.put(key, cache);
	}

	// Check if cache expired
	public static boolean cacheExpired(Cache cache) {
		if (null == cache) { 
			return false;
		}
		long currentTime = System.currentTimeMillis(); 
		long cacheTimeout = cache.getTimeOut(); 
		if(cacheTimeout <= 0 || cacheTimeout > currentTime) { 
			return false;
		} else { 
			return true;
		}
	}

	//Get cache size
	public static int getCacheSize() {
		return cacheMap.size();
	}

	// Get cache size for specific cache type
	public static int getCacheSize(String type) {
		int k = 0;
		Iterator<Map.Entry<String, Object>> itor = cacheMap.entrySet().iterator();
		String key;
		try {
			while (itor.hasNext()) {
				Map.Entry<String, Object> entry = itor.next();
				key = entry.getKey();
				if (key.indexOf(type) != -1) { 
					k++;
				}
			}
		} catch (NoSuchElementException | IllegalStateException e) {
            log.debug("getCacheSize Exception error {}", e);
            log.error("error ", e);
		}

		return k;
	}

	//Get all keys from Cache
	public static ArrayList<String> getCacheAllkey() {
		ArrayList<String> a = new ArrayList<String>();
		try {
			Iterator <Map.Entry<String, Object>> itor = cacheMap.entrySet().iterator();
			while (itor.hasNext()) {
				Map.Entry<String, Object> entry = itor.next();
				a.add((String) entry.getKey());
			}
		} catch (NoSuchElementException | IllegalStateException e) {
            log.debug("getCacheSize Exception error {}", e);
            log.error("error ", e);
		}
		
		return a;
	}

	// Get all keys from cache for specific type
	public static ArrayList<String> getCacheListkey(String type) {
		ArrayList<String> a = new ArrayList<String>();
		String key;
		try {
			Iterator<Map.Entry<String, Object>> itor = cacheMap.entrySet().iterator();
			while (itor.hasNext()) {
				Map.Entry<String, Object> entry = itor.next();
				key = entry.getKey();
				if (key.indexOf(type) != -1) {
					a.add(key);
				}
			}
		} catch (NoSuchElementException | IllegalStateException e) {
            log.debug("getCacheSize Exception error {}", e);
            log.error("error ", e);
		} 
		return a;
	}
}
