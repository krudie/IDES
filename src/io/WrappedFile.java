/**
 * 
 */
package io;
import java.io.File;
import java.io.PrintStream;
/**
 * @author christiansilvano
 *
 */
public class WrappedFile extends File{
	File wrappedFile = null;
	public WrappedFile(File file, int offset1, int offset2)
	{
		super("aa");
		
		//1 - Make wrappedFile be a temporary file, countaining the data between offset1 and offset2
		
	}

}
