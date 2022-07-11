package com.edc.edcportal.core.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 *        Backing Java code for Cache management
 */

public class CacheManager {

    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
    private static HashMap<String, Object> cacheMap = new HashMap<>();

    private CacheManager() {
        super();
    }

    // Get cache by given a key
    @SuppressWarnings("AEM Rules:AEM-15")
    private static synchronized Cache getCache(String key) {
        return (Cache) cacheMap.get(key);
    }

    // If cache object exist
    @SuppressWarnings("AEM Rules:AEM-15")
    private static synchronized boolean hasCache(String key) {
        return cacheMap.containsKey(key);
    }

    // Clear all cache objects
    @SuppressWarnings("AEM Rules:AEM-15")
    public static synchronized void clearAll() {
        cacheMap.clear();
        log.debug("clearAll(): Cache cleared.");
    }

    // Clear cache objects which key start with specified string
    @SuppressWarnings("AEM Rules:AEM-15")
    public static synchronized void clearAll(String type) {
        Iterator<Map.Entry<String, Object>> itor = cacheMap.entrySet().iterator();
        String key;
        ArrayList<String> arr = new ArrayList<>();
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
        log.debug("All cache start with key:{} are cleared", type);
    }

    // Clear cache object by a given key
    @SuppressWarnings("AEM Rules:AEM-15")
    public static synchronized void clearOnly(String key) {
        cacheMap.remove(key);
        log.debug("Cache with key:{} is cleared", key);
    }

    // Add a cache object
    @SuppressWarnings("AEM Rules:AEM-15")
    public static synchronized void putCache(String key, Cache obj) {
        cacheMap.put(key, obj);
        log.debug("Cache with key:{} is added", key);
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
        cache.setTimeOut(liveTime + System.currentTimeMillis());
        cache.setValue(obj);
        log.debug("Adding cache with key:{}, expire in:{} ", key, liveTime);

        cacheMap.put(key, cache);
    }

    // Check if cache expired
    public static boolean cacheExpired(Cache cache) {
        if (null == cache) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long cacheTimeout = cache.getTimeOut();
        return !(cacheTimeout <= 0 || cacheTimeout > currentTime);
    }

    // Get cache size
    public static int getCacheSize() {
        return cacheMap.size();
    }

    // Get cache size for specific cache type
    public static int getCacheSize(String type) {
        int k = 0;
        Iterator<Map.Entry<String, Object>> itor = cacheMap.entrySet().iterator();
        String key;
        while (itor.hasNext()) {
            Map.Entry<String, Object> entry = itor.next();
            key = entry.getKey();
            if (key.indexOf(type) != -1) {
                k++;
            }
        }
        return k;
    }

    // Get all keys from Cache
    public static List<String> getCacheAllkey() {
        List<String> a = new ArrayList<>();
        Iterator<Map.Entry<String, Object>> itor = cacheMap.entrySet().iterator();
        while (itor.hasNext()) {
            Map.Entry<String, Object> entry = itor.next();
            a.add(entry.getKey());
        }
        return a;
    }

    // Get all keys from cache for specific type
    public static List<String> getCacheListkey(String type) {
        List<String> a = new ArrayList<>();
        String key;
        Iterator<Map.Entry<String, Object>> itor = cacheMap.entrySet().iterator();
        while (itor.hasNext()) {
            Map.Entry<String, Object> entry = itor.next();
            key = entry.getKey();
            if (key.indexOf(type) != -1) {
                a.add(key);
            }
        }
        return a;
    }
}
