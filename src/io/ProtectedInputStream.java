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
public class ProtectedInputStream extends FilterInputStream
{
	long length = -1;

	long counter = 0;

	/**
	 * @param f
	 * @param off1
	 * @param s
	 */
	public ProtectedInputStream(InputStream stream, long offset, long length)
			throws IOException
	{
		super(stream);
		stream.skip(offset);
		this.length = length;
	}

	@Override
	public int read() throws IOException
	{
		if (counter < length)
		{
			counter++;
			return super.read();
		}
		else
		{
			return -1;
		}
	}

	@Override
	public long skip(long n) throws IOException
	{
		long skipped = 0;
		while (skipped < n)
		{
			int r = read();
			if (r < 0)
			{
				break;
			}
			skipped++;
		}
		return skipped;
	}

	public void mark()
	{
	}

	@Override
	public void reset()
	{
	}

	@Override
	public boolean markSupported()
	{
		return false;
	}

	public int available() throws IOException
	{
		return Math.min(super.available(), (int)(length - counter));
	}

	public void close()
	{

	}
}
