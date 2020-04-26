package services.cache;

import java.io.File;
import java.util.Properties;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

import ides.api.cache.Cache;
import ides.api.cache.NotInCacheException;
import ides.api.latex.LatexRenderException;
import ides.api.latex.LatexUtils;
import presentation.fsa.GraphLabel;

/**
 * This class provides access to a disk-based cache. Its main use is intended to
 * be the caching of rendered LaTeX labels so that they don't have to be
 * re-rendered all the time.
 * 
 * @author Lenko Grigorov
 */
public class CacheBackend implements Cache {

    private CacheBackend() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * This determines when files will be purged from the cache. If an item in the
     * cache has not been used for longer than this period of time (in seconds), it
     * will be deleted when the cache is closed.
     * 
     * @see #close()
     */
    private static final int CACHE_EXPIRY = 2592000;

    /**
     * The directory where the cache files will be kept.
     */
    private static final String CACHE_DIR = "cache";

    /**
     * The cache implementation.
     */
    private static GeneralCacheAdministrator cache = null;

    /**
     * Instance for the non-static methods.
     */
    private static CacheBackend me = null;

    /**
     * Initialize the cache.
     */
    public static void init() {
        System.setProperty("org.apache.commons.logging.simplelog.log.com.opensymphony.oscache", "error");
        Properties cacheConfig = new Properties();
        cacheConfig.put("cache.memory", "true");
        cacheConfig.put("cache.capacity", "1");
        cacheConfig.put("cache.unlimited.disk", "true");
        cacheConfig.put("cache.persistence.class",
                "com.opensymphony.oscache.plugins.diskpersistence.HashDiskPersistenceListener");
        cacheConfig.put("cache.path", CACHE_DIR);
        cache = new com.opensymphony.oscache.general.GeneralCacheAdministrator(cacheConfig);
        // insert empty LaTeX label
        try {
            cache.putInCache(GraphLabel.class.getName(), LatexUtils.labelStringToImageBytes(""));
        } catch (LatexRenderException e) {
            throw new RuntimeException(e);
        }
    }

    public static CacheBackend instance() {
        if (me == null) {
            me = new CacheBackend();
        }
        return me;
    }

    /**
     * Put a {@link java.io.Serializable} object into the cache.
     * 
     * @param key   the key that will be used for retrieval
     * @param value the object to be stored in the cache
     * @see #get(String)
     */
    public void put(String key, Object value) {
        cache.putInCache(key, value);
    }

    /**
     * Retrieve an object from the cache.
     * 
     * @param key the key used to retrieve the object
     * @return the object retrieved
     * @throws NotInCacheException when the object is not found in the cache
     * @see #put(String, Object)
     */
    public Object get(String key) throws NotInCacheException {
        try {
            Object value = cache.getFromCache(key, CacheEntry.INDEFINITE_EXPIRY);
            // refresh
            cache.putInCache(key, value);
            return value;
        } catch (NeedsRefreshException e) {
            cache.cancelUpdate(key);
            throw new NotInCacheException("Can't find object for the key: " + key);
        }
    }

    /**
     * Close and finalize the cache.
     * <p>
     * After a call to this method, the cache should not be used unless
     * {@link #init()} is called again.
     * 
     * @see #init()
     */
    public static void close() {
        File[] cachedFiles = new File(CACHE_DIR + File.separator + "application").listFiles();
        for (int i = 0; i < cachedFiles.length; ++i) {
            if ((System.currentTimeMillis() - cachedFiles[i].lastModified()) / 1000L > CACHE_EXPIRY) {
                cachedFiles[i].delete();
            }
        }
    }

}
