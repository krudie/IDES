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

    /**
     * Create a default exception.
     */
    public NotInCacheException() {
        super();
    }

    /**
     * Create an exception with a message.
     * 
     * @param arg0 the message
     */
    public NotInCacheException(String arg0) {
        super(arg0);
    }

    /**
     * Create an exception with a message and a cause.
     * 
     * @param arg0 the message
     * @param arg1 the cause
     */
    public NotInCacheException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Create an exception with a cause.
     * 
     * @param arg0 the cause
     */
    public NotInCacheException(Throwable arg0) {
        super(arg0);
    }

}
