package services.cache;

import services.latex.Renderer;

/**
 *
 * Exception used by {@link Cache} to announce an object was not found in the cache.
 *
 * @author Lenko Grigorov
 */
public class NotInCacheException extends Exception {

	public NotInCacheException() {
		super();
	}

	public NotInCacheException(String arg0) {
		super(arg0);
	}

	public NotInCacheException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NotInCacheException(Throwable arg0) {
		super(arg0);
	}

}
