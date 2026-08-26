package ides.api.plugin.io;

import ides.api.plugin.model.DESModel;

/**
 * Used when a file cannot be loaded because the version of the format is not
 * supported.
 * 
 * @author Lenko Grigorov
 */
public class UnsupportedVersionException extends FileLoadException {
    private static final long serialVersionUID = -2729508909271561710L;

    /**
     * Create a default exception.
     */
    public UnsupportedVersionException() {
    }

    /**
     * Create an exception with a message.
     * 
     * @param msg the message
     */
    public UnsupportedVersionException(String msg) {
        super(msg);
    }

    /**
     * Creates an exception which will give access to the partially-loaded model at
     * the time of the exception.
     * 
     * @param msg          message
     * @param partialModel the partially-loaded model at the time of the exception
     */
    public UnsupportedVersionException(String msg, DESModel partialModel) {
        super(msg, partialModel);
    }

}
