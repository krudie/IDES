package ides.api.plugin.io;

import java.io.IOException;

/**
 * Used when there is a problem during the import or export of a file.
 * 
 * @author Lenko Grigorov
 */
public class FormatTranslationException extends IOException {

    private static final long serialVersionUID = -8596085812259995633L;

    /**
     * Create a default exception.
     */
    public FormatTranslationException() {
        super();
    }

    /**
     * Create an exception with a message.
     * 
     * @param msg the message
     */
    public FormatTranslationException(String msg) {
        super(msg);
    }

    /**
     * Create an exception with a cause.
     * 
     * @param e the cause
     */
    public FormatTranslationException(Exception e) {
        super(e.getMessage());
        setStackTrace(e.getStackTrace());
    }
}
