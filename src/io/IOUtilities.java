package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import main.Hub;

/**
 * Some constants to be used in the rest of the program.
 * @author Lenko Grigorov
 */
public class IOUtilities {
	/**
	 * Extension for workspace files.
	 */
	public static final String WORKSPACE_FILE_EXT="iws";
	/**
	 * Extension for model files.
	 */
	public static final String MODEL_FILE_EXT="iml";
	
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
	            ps = new PrintStream(file);	            
	        }
	        catch(FileNotFoundException fnfe){
	        	errorMsg=Hub.string("fileNotFound");
	        	throw new RuntimeException();	        	
	        }
        }catch(RuntimeException e)
        {
        	Hub.displayAlert(errorMsg);
        }
        return ps;
    }
}
