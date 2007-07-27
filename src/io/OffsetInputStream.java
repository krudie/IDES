/**
 * 
 */
package io;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author christiansilvano
 *
 */
public class OffsetInputStream extends FilterInputStream {
	protected int size = 0;
	protected int bytesRead = 0;
	public OffsetInputStream(InputStream is, int offset, int sz)
	{
		super(is);
		size = sz;
		try{
			is.reset();
			is.skip(offset);
		}catch(IOException exception)
		{
			System.out.println("1: " + exception.getMessage());
			
		}
		printInputStream(is);
	}

	public int read()
	{
		if(bytesRead < size)
		{
			bytesRead++;
			try{
				return super.read();
			}catch(IOException exception)
			{
				System.out.println("2: " + exception.getMessage());
				return -1;
			}
		}else
		{
			return -1;
		}
	}
	
	public void printInputStream(InputStream stream)
	{
		String body = "";
		try
		{
			BufferedReader head = new BufferedReader(new InputStreamReader(stream));        	
			String line = head.readLine();	        	
			//Process the file (Add States, Events and Transitions to the model):
			while(line != null)
			{
				body += line;
				body += System.getProperty("line.separator"); 
				line = head.readLine();
			}
			head.close();
		}catch(IOException e)
		{
			System.out.println("Erro: " + e.getMessage());
		}
		System.out.println("Stream content:\n=======\n" + body);
	}
}
