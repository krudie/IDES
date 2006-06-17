package services.cache;

import java.util.Properties;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class Cache {

	private Cache()
	{
	}
	
	public Object clone()
	{
	    throw new RuntimeException("Cloning of "+this.getClass().toString()+" not supported."); 
	}

	private static GeneralCacheAdministrator cache=null;
	
	public static void init()
	{
		System.setProperty("org.apache.commons.logging.simplelog.log.com.opensymphony.oscache", "error");
		Properties cacheConfig=new Properties();
		cacheConfig.put("cache.memory","true");
		cacheConfig.put("cache.capacity","1");
		cacheConfig.put("cache.unlimited.disk","true");
		cacheConfig.put("cache.persistence.class","com.opensymphony.oscache.plugins.diskpersistence.HashDiskPersistenceListener");
		cacheConfig.put("cache.path","cache");
		cache=new com.opensymphony.oscache.general.GeneralCacheAdministrator(cacheConfig);
	}

	
	public static void put(String key, Object value)
	{
		cache.putInCache(key,value);
	}
	
	public static Object get(String key) throws NotInCacheException
	{
		try
		{
			return cache.getFromCache(key,CacheEntry.INDEFINITE_EXPIRY);
		}catch(NeedsRefreshException e)
		{
			cache.cancelUpdate(key);
			throw new NotInCacheException("Can't find object for the key: "+key);
		}
	}
	
	public static void close()
	{
	}

}
