/**
 * 
 */
package io;
import java.io.PrintStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author christiansilvano
 *
 */
public class WrappedPrintStream extends PrintStream{ 
  	 public WrappedPrintStream(OutputStream o) throws UnsupportedEncodingException
  	 {	
  		 super(o,true,"UTF-8");
  	 }
  	 
  	 public void close(){};
  	 public void closeWrappedPrintStream()
  	 {
  			 super.close();	 
  	 }
   }
