package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Predicate;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.codec.binary.Base64;

import ides.api.core.Hub;

/**
 * I/O related utility functions.
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

    public static int readAvailableSkip(InputStream in, int len) throws IOException {
        return readSkip(in, len, false);
    }

    public static int readSkip(InputStream in, int len) throws IOException {
        return readSkip(in, len, true);
    }

    private static int readSkip(InputStream in, int len, boolean throwOnEOF) throws IOException {
        int count = 0;
        while (count < len) {
            if (in.read() < 0) {
                if (throwOnEOF) {
                    throw new IOException(Hub.string("errorUnexpectedEOF"));
                }
                break;
            }
            ++count;
        }
        return count;
    }

    public static int readAvailableSkipUntil(InputStream in, Predicate<Byte> p) throws IOException {
        return readSkipUntil(in, p, false);
    }

    public static int readSkipUntil(InputStream in, Predicate<Byte> p) throws IOException {
        return readSkipUntil(in, p, true);
    }

    private static int readSkipUntil(InputStream in, Predicate<Byte> p, boolean throwOnEOF) throws IOException {
        int count = 0;
        int b;
        do {
            b = in.read();
            if (b < 0) {
                if (throwOnEOF) {
                    throw new IOException(Hub.string("errorUnexpectedEOF"));
                }
                break;
            }
            ++count;
        } while (!p.test((byte) b));
        return count;
    }

    public static int readAvailableInto(InputStream in, byte[] buf) throws IOException {
        return readInto(in, buf, false);
    }

    public static int readInto(InputStream in, byte[] buf) throws IOException {
        return readInto(in, buf, true);
    }

    private static int readInto(InputStream in, byte[] buf, boolean throwOnEOF) throws IOException {
        int count = 0;
        while (count < buf.length) {
            int b = in.read();
            if (b < 0) {
                if (throwOnEOF) {
                    throw new IOException(Hub.string("errorUnexpectedEOF"));
                }
                break;
            }
            buf[count] = (byte) b;
            ++count;
        }
        return count;
    }

    public static int readIntLE(InputStream in) throws IOException {
        byte[] buf = new byte[4];
        readInto(in, buf);
        return (buf[0] & 0xff) | ((buf[1] & 0xff) << 8) | ((buf[2] & 0xff) << 16) | ((buf[3] & 0xff) << 24);
    }

    public static short readShortLE(InputStream in) throws IOException {
        byte[] buf = new byte[2];
        readInto(in, buf);
        return (short) ((buf[0] & 0xff) | ((buf[1] & 0xff) << 8));
    }

    public static void writeIntLE(OutputStream out, int n) throws IOException {
        byte[] buf = new byte[] { (byte) (n & 0xff), (byte) ((n >> 8) & 0xff), (byte) ((n >> 16) & 0xff),
                (byte) ((n >> 24) & 0xff), };
        out.write(buf);
    }

    public static void writeShortLE(OutputStream out, short n) throws IOException {
        byte[] buf = new byte[] { (byte) (n & 0xff), (byte) ((n >> 8) & 0xff), };
        out.write(buf);
    }
}
