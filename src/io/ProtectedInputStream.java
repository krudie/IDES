/**
 * 
 */
package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author christiansilvano
 *
 */
public class ProtectedInputStream extends FileInputStream {
	long size = -1;
	long counter = 0;
	/**
	 * 
	 * @param f
	 * @param off1
	 * @param s
	 */
	public ProtectedInputStream(File f, long off1, long s) throws FileNotFoundException
	{
		super(f);
		try{
			size = s;
			super.skip(off1);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public int read() throws IOException
	{
		if(counter < size)
		{
			counter++;
			int r = (char)super.read();
			return r;
		}else{
			return -1;
		}
	}

	public int read(byte[] b,int off,int len) throws IOException
	{
		if(counter < size)
		{
			counter++;
			return super.read(b, off, len);
		}else{
			return -1;
		}
	}

	public int read(byte[] b) throws IOException
	{
		if(counter < size)
		{
			counter++;
			return super.read(b);
		}else{
			return -1;
		}
	}

	public long skip(long n) throws IOException
	{
		long skipped = 0;
		if(counter == size)
		{
			return -1;
		}
		while(counter < size | skipped < n)
		{
			read();
			counter++;
			skipped++;
		}
		return skipped;
	}

	public void mark(){}
	public void reset(){}
	public boolean markSupported()
	{
		return false;
	}
}
