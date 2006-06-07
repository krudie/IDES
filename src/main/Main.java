package main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import ui.MainWindow;

public class Main {
	
	/**
	 * Handles stuff that has to be done before the application terminates.
	 *
	 */
	public static void onExit()
	{
		//store settings
		Hub.storePersistentData();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//load resource with strings used in the program
		try
		{
			Hub.addResouceBundle(ResourceBundle.getBundle("strings"));
		}catch(MissingResourceException e)
		{
			javax.swing.JOptionPane.showMessageDialog(null, "Cannot load the file with the text messages used in the program.\n" +
					"The file \"strings.properties\" has to be available at startup.");
			System.exit(1);
		}
		
		//load settings
		try
		{
			Hub.loadPersistentData();
		} catch(IOException e)
		{
			javax.swing.JOptionPane.showMessageDialog(null, Hub.string("cantLoadSettings"));
			System.exit(2);
		}
		
		// TODO load UISettings and workspace in a thread
		// show splash screen
		new MainWindow().setVisible(true);		
	}
}
