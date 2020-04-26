package ides.api.cache;

/**
 * Exception used by {@link Cache} to announce an object was not found in the
 * cache.
 * 
 * @author Lenko Grigorov
 */
public class NotInCacheException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7567544231199177891L;

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
