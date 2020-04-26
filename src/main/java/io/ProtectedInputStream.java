/**
 * 
 */
package io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Lenko Grigorov
 * @author christiansilvano
 */
public class ProtectedInputStream extends FilterInputStream {
    long length = -1;

    long counter = 0;

    public ProtectedInputStream(InputStream stream, long offset, long length) throws IOException {
        super(stream);
        stream.skip(offset);
        this.length = length;
    }

    @Override
    public int read() throws IOException {
        if (counter < length) {
            counter++;
            return super.read();
        } else {
            return -1;
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int count = 0;
        int r = read();
        if (r < 0) {
            return -1;
        }
        while (r >= 0 && count < len) {
            b[off + count] = (byte) r;
            try {
                r = read();
            } catch (IOException e) {
                r = -1;
            }
            count++;
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = 0;
        while (skipped < n) {
            int r = read();
            if (r < 0) {
                break;
            }
            skipped++;
        }
        return skipped;
    }

    public void mark() {
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    public int available() throws IOException {
        return Math.min(super.available(), (int) (length - counter));
    }

    public void close() {

    }
}
