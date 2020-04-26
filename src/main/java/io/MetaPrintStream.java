package io;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MetaPrintStream extends PrintStream {
    protected PrintStream nested;

    protected String header;

    public MetaPrintStream(PrintStream stream, String header) throws UnsupportedEncodingException {
        super(stream, true, "UTF-8");
        this.header = header;
    }

    public boolean hasOutput() {
        return isOutputHeader;
    }

    private boolean isOutputHeader = false;

    protected void outputHeader() {
        isOutputHeader = true;
        super.print(header);
    }

    @Override
    public PrintStream append(char c) {
        if (!isOutputHeader) {
            outputHeader();
        }
        return super.append(c);
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        if (!isOutputHeader) {
            outputHeader();
        }
        return super.append(csq, start, end);
    }

    @Override
    public PrintStream append(CharSequence csq) {
        if (!isOutputHeader) {
            outputHeader();
        }
        return super.append(csq);
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        if (!isOutputHeader) {
            outputHeader();
        }
        return super.format(l, format, args);
    }

    @Override
    public PrintStream format(String format, Object... args) {
        if (!isOutputHeader) {
            outputHeader();
        }
        return super.format(format, args);
    }

    @Override
    public void print(boolean b) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(b);
    }

    @Override
    public void print(char c) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(c);
    }

    @Override
    public void print(char[] s) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(s);
    }

    @Override
    public void print(double d) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(d);
    }

    @Override
    public void print(float f) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(f);
    }

    @Override
    public void print(int i) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(i);
    }

    @Override
    public void print(long l) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(l);
    }

    @Override
    public void print(Object obj) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(obj);
    }

    @Override
    public void print(String s) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.print(s);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        if (!isOutputHeader) {
            outputHeader();
        }
        return super.printf(l, format, args);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        if (!isOutputHeader) {
            outputHeader();
        }
        return super.printf(format, args);
    }

    @Override
    public void println() {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println();
    }

    @Override
    public void println(boolean x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(char x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(char[] x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(double x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(float x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(int x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(long x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(Object x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void println(String x) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.println(x);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.write(buf, off, len);
    }

    @Override
    public void write(int b) {
        if (!isOutputHeader) {
            outputHeader();
        }
        super.write(b);
    }
}
