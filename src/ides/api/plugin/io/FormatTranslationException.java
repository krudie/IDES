package ides.api.plugin.io;

import java.io.IOException;

/**
 * Used when there is a problem during the import or export of a file.
 * 
 * @author Lenko Grigorov
 */
public class FormatTranslationException extends IOException {

    private static final long serialVersionUID = -8596085812259995633L;

    public FormatTranslationException() {
        super();
    }

    public FormatTranslationException(String msg) {
        super(msg);
    }

    public FormatTranslationException(Exception e) {
        super(e.getMessage());
        setStackTrace(e.getStackTrace());
    }
}
