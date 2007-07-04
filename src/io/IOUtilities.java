package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.codec.binary.Base64;

import main.Hub;

/**
 * Some constants to be used in the rest of the program.
 * @author Lenko Grigorov
 */
public class IOUtilities {
	/**
	 * Descriptors for DES file types
	 */
	public static final String GRAIL_DESCRIPTOR="GRAIL";
	public static final String TCT_DESCRIPTOR="TCT";
	public static final String EPS_DESCRIPTOR="EPS";
	
	
	
	/**
	 * Descriptor for DES models
	 */
	public static final String FSA_DESCRIPTOR="FSA";
	public static final String TEMPLATE_DESIGN_DESCRIPTOR="TemplateDesign";
	
	/**
	 * Extension for workspace files.
	 */
	public static final String WORKSPACE_FILE_EXT="xws";
	/**
	 * Extension for model files.
	 */
	public static final String MODEL_FILE_EXT="xmd";
	/**
	 * Extension for LaTeX files.
	 */
	public static final String LATEX_FILE_EXT="tex";
	/**
	 * Extension for EPS files.
	 */
	public static final String EPS_FILE_EXT="eps";
	/**
	 * Extension for Grail+ files.
	 */
	public static final String FM_FILE_EXT="fm";
	/**
	 * Extension for TCT files.
	 */
	public static final String TCT_FILE_EXT="des";
	
	/**
     * Method for getting a UTF-8 printstream wrapped around a file
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
    
    /**
     * Encodes a string so that XML-illegal symbols are properly escaped.
     * @param s string to encode
     * @return encoded version of the input string
     */
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
