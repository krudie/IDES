package ides.api.plugin.io;

import java.io.IOException;

/**
 * Used when there is a problem saving the data in a file.
 * 
 * @author Lenko Grigorov
 */
public class FileSaveException extends IOException {

    private static final long serialVersionUID = 1678695993655527352L;

    public FileSaveException() {
    }

    public FileSaveException(String arg0) {
        super(arg0);
    }

    public FileSaveException(Throwable arg0) {
        super();
        initCause(arg0);
    }

    public FileSaveException(String arg0, Throwable arg1) {
        super(arg0);
        initCause(arg1);
    }

}
