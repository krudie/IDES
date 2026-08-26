package ides.api.plugin.io;

import java.io.IOException;

/**
 * Used when there is a problem saving the data in a file.
 * 
 * @author Lenko Grigorov
 */
public class FileSaveException extends IOException {

    private static final long serialVersionUID = 1678695993655527352L;

    /**
     * Create a default exception.
     */
    public FileSaveException() {
    }

    /**
     * Create an exception with a message.
     * 
     * @param arg0 the message
     */
    public FileSaveException(String arg0) {
        super(arg0);
    }

    /**
     * Create an exception with a cause.
     * 
     * @param arg0 the cause
     */
    public FileSaveException(Throwable arg0) {
        super();
        initCause(arg0);
    }

    /**
     * Create an exception with a message and a cause.
     * 
     * @param arg0 the message
     * @param arg1 the cause
     */
    public FileSaveException(String arg0, Throwable arg1) {
        super(arg0);
        initCause(arg1);
    }

}
