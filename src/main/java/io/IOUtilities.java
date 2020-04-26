package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.codec.binary.Base64;

import ides.api.core.Hub;

/**
 * Some constants to be used in the rest of the program.
 * 
 * @author Lenko Grigorov
 */
public class IOUtilities {

    /**
     * Method for getting a UTF-8 printstream wrapped around a file
     * 
     * @param file the file that needs a printstream wrapped around it
     * @return the printstream pointing to a the file, <code>null</code> if it could
     *         not be created
     */
    public static PrintStream getPrintStream(File file) {
        PrintStream ps = null;
        String errorMsg = "";

        if (file == null) {
            return ps;
        }

        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ioe) {
                    errorMsg = Hub.string("fileUnableToCreate") + file.getPath();
                    throw new RuntimeException();
                }
            }
            if (!file.isFile()) {
                errorMsg = Hub.string("fileNotAFile") + file.getPath();
                throw new RuntimeException();
            }
            if (!file.canWrite()) {
                errorMsg = Hub.string("fileCantWrite") + file.getPath();
                throw new RuntimeException();
            }
            try {
                ps = new PrintStream(file, "UTF-8");
            } catch (FileNotFoundException fnfe) {
                errorMsg = Hub.string("fileNotFound");
                throw new RuntimeException();
            } catch (java.io.UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            Hub.displayAlert(errorMsg);
        }
        return ps;
    }

    public static byte[] decodeBase64(byte[] data) {
        return Base64.decodeBase64(data);
    }

    public static byte[] encodeBase64(byte[] data) {
        return Base64.encodeBase64(data);
    }

    /**
     * Encodes a string so that XML-illegal symbols are properly escaped.
     * 
     * @param s string to encode
     * @return encoded version of the input string
     */
    public static String encodeForXML(String s) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '<') {
                buffer.append("&lt;");
            } else if (c == '>') {
                buffer.append("&gt;");
            } else if (c == '&') {
                buffer.append("&amp;");
            } else if (c == '"') {
                buffer.append("&quot;");
            } else if (c == '\'') {
                buffer.append("&apos;");
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    /**
     * Class used to create file filters for Open/Save/etc. dialog boxes. It filters
     * files depending on their extenstions.
     * 
     * @author Lenko Grigorov
     */
    public static class ExtensionFilter extends FileFilter {
        private String description;

        private String[] extensions;

        /**
         * Construct a new file filter which will select all directories (regardless of
         * name) and all files having an extension equal to the provided extension
         * (regardless of letter case).
         * 
         * @param extension   file extension to be used for filtering (e.g., "txt")
         * @param description description to be used for the filter (e.g., "Text file")
         */
        public ExtensionFilter(String extension, String description) {
            this(new String[] { extension }, description);
        }

        /**
         * Construct a new file filter which will select all directories (regardless of
         * name) and all files having an extension equal to on of the provided
         * extensions (regardless of letter case).
         * 
         * @param extensions  file extensions to be used for filtering (e.g.,
         *                    ["jpg","bmp","png"])
         * @param description description to be used for the filter (e.g., "Image file")
         */
        public ExtensionFilter(String[] extensions, String description) {
            this.extensions = extensions;
            for (int i = 0; i < extensions.length; ++i) {
                extensions[i] = extensions[i].toLowerCase();
            }
            this.description = description;
        }

        /**
         * Returns the description of the file filter (e.g. "Text file").
         * 
         * @return the description of the file filter (e.g. "Text file")
         */
        @Override
        public String getDescription() {
            return description;
        }

        /**
         * Says if the provided file satisfies the constraints of the filter.
         * 
         * @return <code>true</code> if the file is a directory or if the extension of
         *         the file is one of the accepted extensions (regrdless of letter
         *         case), otherwise returns <code>false</code>
         */
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = "";
            if (f.getName().contains(".")) {
                extension = f.getName().substring(f.getName().lastIndexOf('.') + 1);
            }
            for (String accepted : extensions) {
                if (extension.toLowerCase().equals(accepted)) {
                    return true;
                }
            }
            return false;
        }
    }
}
