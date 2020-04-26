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

    public UnsupportedVersionException() {
    }

    public UnsupportedVersionException(String msg) {
        super(msg);
    }

    public UnsupportedVersionException(String msg, DESModel partialModel) {
        super(msg, partialModel);
    }

}
