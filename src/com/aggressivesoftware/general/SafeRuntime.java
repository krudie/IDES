/*
 * Created on Dec 15, 2004
 */
package com.aggressivesoftware.general;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class provides safe access to Runtime.getRuntime().exec(cmd);
 * This is used to activate other external applications.
 * Naive use of Runtime can cause the system to hang. 
 * 
 * @author Michael Wood
 */
public final class SafeRuntime 
{
    /**
     * Execute the given command array, and print the status and progress to the terminal window.
     * This methods provides handlers for the error and input streams, thereby avoiding the hanging problem.
     * 
     * @param	cmd		The command array to be executed.
     */
    public static void execWithFeedback(String cmd[])
    {
        try
        {            	            
            System.out.print("Executing: ");
            for (int i=0; i<cmd.length; i++) { System.out.print(cmd[i] + " "); }
            System.out.println("\n");
            
            Process process = Runtime.getRuntime().exec(cmd);

            StreamHandler error_stream = new StreamHandler(process.getErrorStream(),true);            
            StreamHandler output_stream = new StreamHandler(process.getInputStream(),true);
                
            error_stream.start();
            output_stream.start();
                                    
            System.out.println("\nExit Value: " + process.waitFor());        
        } 
		catch (Exception e) {}
    }
    
    /**
     * Execute the given command array, without printing anything to the terminal window.
     * This methods provides handlers for the error and input streams, thereby avoiding the hanging problem.
     * 
     * @param	cmd		The command array to be executed.
     */
    public static void execWithoutFeedback(String[] cmd)
    {
		try 
		{ 
			Process process = Runtime.getRuntime().exec(cmd); 
			
            StreamHandler error_stream = new StreamHandler(process.getErrorStream(),false);            
            StreamHandler output_stream = new StreamHandler(process.getInputStream(),false);
                
            error_stream.start();
            output_stream.start();
		}
		catch (Exception e) {}
    }
}

/**
 * @author Michael Wood
 * 
 * This class is used to dump a given InputStream either to the terminal window, or to nowhere.
 * It runs in a seperate thread.
 */
class StreamHandler extends Thread
{
	/**
	 * The InputStream to be handled.
	 */
    InputStream input_stream = null;

	/**
	 * Whether or not the stream should be dumped to the terminal window.
	 */
    boolean use_system_out = false;
    
    /**
     * Construct this StreamHandler to handle the given InputStream.
     * 
     * @param	input_stream	The InputStream to be handled.
     * @param	use_system_out	Whether or not the stream should be dumped to the terminal window.
     */
    StreamHandler(InputStream input_stream, boolean use_system_out) 
	{ 
    	this.input_stream = input_stream; 
    	this.use_system_out = use_system_out;
    }
    
    /**
     * Dump the input_stream to the terminal window or to nowhere.
     */
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(input_stream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) { if (use_system_out) { System.out.println(line); } }    
        } 
        catch (Exception e) { }
    }
}