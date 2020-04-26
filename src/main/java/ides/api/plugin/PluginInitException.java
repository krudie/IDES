package ides.api.plugin;

/**
 * Plugins should use this exception when initialization fails.
 * 
 * @author Lenko Grigorov
 */
public class PluginInitException extends Exception {
    private static final long serialVersionUID = -1925207920485858643L;

    public PluginInitException() {
    }

    public PluginInitException(String arg0) {
        super(arg0);
    }

    public PluginInitException(Throwable arg0) {
        super(arg0);
    }

    public PluginInitException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
