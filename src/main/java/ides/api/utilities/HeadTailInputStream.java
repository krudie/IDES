package ides.api.utilities;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper for {@link InputStream}s which allows the prefixing of the stream
 * with a header and the appending of a tail to the stream. To the reader of the
 * stream, it appears as if the stream contains the header, the content of the
 * original stream and the tail.
 * <p>
 * For example, if one wants to add the tags "&lt;b&gt;" and "&lt;/b&gt;" around
 * the content of the stream <code>input</code>, it is sufficient to say: <br>
 * <code>input=new HeadTailInputStream(input,"&lt;b&gt;".getBytes(),"&lt;/b&gt;".getBytes());</code>
 * 
 * @author Lenko Grigorov
 */
public class HeadTailInputStream extends FilterInputStream {

    /**
     * The header to be available before the content of the original stream.
     */
    protected byte[] head;

    /**
     * The tail to be available after the content of the original stream.
     */
    protected byte[] tail;

    /**
     * bytes left to read from the head
     */
    private int headLeft;

    /**
     * bytes left to read from the tail
     */
    private int tailLeft;

    /**
     * the original stream has hit EOF
     */
    private boolean hitEOF = false;

    /**
     * Wrap an {@link InputStream} so that readers read a custom header before the
     * original content and a custom tail after the original content.
     * 
     * @param in   the stream to be wrapped
     * @param head the custom header to be available before the original content
     * @param tail the custom tail to be available after the original content
     */
    public HeadTailInputStream(InputStream in, byte[] head, byte[] tail) {
        super(in);
        this.head = head;
        this.tail = tail;
        headLeft = head.length;
        tailLeft = tail.length;
    }

    @Override
    public int available() throws IOException {
        if (!hitEOF) {
            return headLeft + super.available();
        } else {
            return tailLeft;
        }
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

    @Override
    public long skip(long n) throws IOException {
        long skipped = 0;
        while (n - skipped > 0) {
            if (read() < 0) // hit the end of stream
            {
                break;
            } else {
                ++skipped;
            }
        }
        return skipped;
    }

    @Override
    public int read() throws IOException {
        if (headLeft > 0) // still reading header
        {
            headLeft--;
            return head[head.length - headLeft - 1];
        } else if (!hitEOF) // header finished, reading input stream
        {
            int r = super.read();
            if (r < 0) // input stream finished
            {
                hitEOF = true;
                tailLeft--;
                return tail[0];
            } else {
                return r;
            }
        } else if (tailLeft > 0) // still reading tail
        {
            tailLeft--;
            return tail[tail.length - tailLeft - 1];
        } else
        // tail finished
        {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = 0;
        while (bytesRead < len) {
            int r = read();
            if (r < 0) {
                break;
            }
            b[off + bytesRead] = (byte) r;
            bytesRead++;
        }
        if (bytesRead == 0) {
            return -1;
        }
        return bytesRead;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
}
