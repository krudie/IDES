package ides.api.cache;

/**
 * Interface for classes that provide access to a disk-based cache. The main is
 * intended to be the caching of rendered LaTeX labels so that they don't have
 * to be re-rendered all the time.
 * 
 * @author Lenko Grigorov
 */
public interface Cache {
    /**
     * Put a {@link java.io.Serializable} object into the cache.
     * 
     * @param key   the key that will be used for retrieval
     * @param value the object to be stored in the cache
     * @see #get(String)
     */
    public void put(String key, Object value);

    /**
     * Retrieve an object from the cache.
     * 
     * @param key the key used to retrieve the object
     * @return the object retrieved
     * @throws NotInCacheException when the object is not found in the cache
     * @see #put(String, Object)
     */
    public Object get(String key) throws NotInCacheException;

}
