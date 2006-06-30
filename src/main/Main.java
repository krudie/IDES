package main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import services.cache.Cache;
import services.latex.LatexManager;
import ui.MainWindow;

public class Main {
	
	private Main()
	{
	}

	
	/**
	 * Handles stuff that has to be done before the application terminates.
	 *
	 */
	public static void onExit()
	{
		//store settings
		Hub.storePersistentData();
		Cache.close();
		Hub.getMainWindow().dispose();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//set up global exception handler
		//GlobalExceptionHandler geh=new GlobalExceptionHandler();
		
		//Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
		
		//load resource with strings used in the program
		try
		{
			Hub.addResouceBundle(ResourceBundle.getBundle("strings"));
		}catch(MissingResourceException e)
		{
			javax.swing.JOptionPane.showMessageDialog(null, "Cannot load the file with the text messages used in the program.\n" +
					"The file \"strings.properties\" has to be available at startup.");
			System.exit(2);
		}
		
		//load settings
		try
		{
			Hub.loadPersistentData();
		} catch(IOException e)
		{
			Hub.displayAlert(Hub.string("cantLoadSettings"));
			System.exit(3);
		}
		
		//setup other stuff
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		
		Cache.init();

		try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) { }
//DEBUG: remove eventually
//	    for(Object o:UIManager.getLookAndFeelDefaults().keySet())
//	    	System.out.println(o.toString());
	    
		// TODO load UISettings and workspace in a thread
		// show splash screen
		Hub.setMainWindow(new MainWindow());

		//setup stuff that needs the main window
		LatexManager.init(); //TODO revamp the whole commands stuff 
		
		//go live!
		Hub.getMainWindow().setVisible(true);
	}
}
