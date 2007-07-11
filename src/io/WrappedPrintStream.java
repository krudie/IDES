/**
 * 
 */
package io;
import java.io.PrintStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author christiansilvano
 *
 */
public class WrappedPrintStream extends PrintStream{ 
  	 public WrappedPrintStream(OutputStream o)
  	 {	 
  		 super(o);
  	 }
  	 
  	 public void close(){};
  	 public void closeWrappedPrintStream()
  	 {
  			 super.close();	 
  	 }
   }
