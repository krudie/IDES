/**
 * 
 */
package io;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author christiansilvano
 */
public class WrappedPrintStream extends PrintStream
{
	public WrappedPrintStream(OutputStream o)
			throws UnsupportedEncodingException
	{
		super(o, true);
	}

	@Override
	public void close()
	{
	};

	public void closeWrappedPrintStream()
	{
		super.close();
	}
}
