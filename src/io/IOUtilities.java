package io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.QCodec;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import main.Hub;

/**
 * Some constants to be used in the rest of the program.
 * @author Lenko Grigorov
 */
public class IOUtilities {
	/**
	 * Extension for workspace files.
	 */
	public static final String WORKSPACE_FILE_EXT="xws";
	/**
	 * Extension for model files.
	 */
	public static final String MODEL_FILE_EXT="xmd";
	
	/**
     * Method for getting a printstream wrapped around a file
     * @param file the file that needs a printstream wrapped around it
     * @return the printstream pointing to a the file, <code>null</code> if it could not be created
     */
    public static PrintStream getPrintStream(File file){
        PrintStream ps = null;
        String errorMsg="";

    	if(file==null)
        	return ps;
        
        try
        {
	        if(!file.exists())
	        {
	        	try{
	        		file.createNewFile();
	        	}
	        	catch(IOException ioe)
	        	{
	        		errorMsg=Hub.string("fileUnableToCreate")+file.getPath();
	        		throw new RuntimeException();
	        	}
	        }
	        if(!file.isFile())
	        {
	        	errorMsg=Hub.string("fileNotAFile")+file.getPath();
        		throw new RuntimeException();
	        }
	        if(!file.canWrite())
	        {
	        	errorMsg=Hub.string("fileCantWrite")+file.getPath();
	        	throw new RuntimeException();
	        }
	        try{
	            ps = new PrintStream(file,"UTF-8");	            
	        }
	        catch(FileNotFoundException fnfe){
	        	errorMsg=Hub.string("fileNotFound");
	        	throw new RuntimeException();	        	
	        }
	        catch(java.io.UnsupportedEncodingException e)
	        {
	        	throw new RuntimeException(e);
	        }
        }catch(RuntimeException e)
        {
        	Hub.displayAlert(errorMsg);
        }
        return ps;
    }
    
    public static byte[] decodeBase64(byte[] data)
    {
    	return Base64.decodeBase64(data);
    }
    
    public static byte[] encodeBase64(byte[] data)
    {	
    	return Base64.encodeBase64(data);
    }
    
    public static String encodeForXML(String s)
    {
    	   StringBuffer buffer = new StringBuffer();
    	   for(int i = 0;i < s.length();i++)
    	   {
    	      char c = s.charAt(i);
    	      if(c == '<')
    	         buffer.append("&lt;");
    	      else if(c == '>')
    	         buffer.append("&gt;");
    	      else if(c == '&')
    	         buffer.append("&amp;");
    	      else if(c == '"')
    	         buffer.append("&quot;");
    	      else if(c == '\'')
    	         buffer.append("&apos;");
    	      else
    	         buffer.append(c);
    	   }
    	   return buffer.toString();
    }
}
