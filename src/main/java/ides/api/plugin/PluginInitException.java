package ides.api.plugin;

/**
 * Plugins should use this exception when initialization fails.
 * 
 * @author Lenko Grigorov
 */
public class PluginInitException extends Exception {
    private static final long serialVersionUID = -1925207920485858643L;

    /**
     * Create a default exception.
     */
    public PluginInitException() {
    }

    /**
     * Create an exception with a message.
     * 
     * @param arg0 the message
     */
    public PluginInitException(String arg0) {
        super(arg0);
    }

    /**
     * Create an exception with a cause.
     * 
     * @param arg0 the cause
     */
    public PluginInitException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Create an exception with a message and a cause.
     * 
     * @param arg0 the message
     * @param arg1 the cause
     */
    public PluginInitException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
