package main;

import io.fsa.ver1.CommonTasks;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import operations.fsa.ver1.Accessible;
import operations.fsa.ver1.Coaccessible;
import operations.fsa.ver1.Controllable;
import operations.fsa.ver1.Meet;
import operations.fsa.ver1.PrefixClosure;
import operations.fsa.ver1.Projection;
import operations.fsa.ver1.SupCon;
import operations.fsa.ver1.SynchronousProduct;
import operations.fsa.ver1.Trim;

import pluggable.operation.OperationManager;
import presentation.fsa.GraphExporter;
import presentation.fsa.FSAGraph;

import model.fsa.ver1.Automaton;

import services.General;
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
		if(Hub.getWorkspace().isDirty())
			if(!CommonTasks.handleUnsavedWorkspace())
				return;
		for(Iterator<FSAGraph> i=Hub.getWorkspace().getGraphModels();i.hasNext();)
		{
			FSAGraph gm=i.next();
			if(gm.isDirty())
				if(!CommonTasks.handleUnsavedModel(gm))
					return;
		}
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
		// TODO: move operation inits to the plugin manager eventually
		OperationManager.register(new Meet());
		OperationManager.register(new SynchronousProduct());
		OperationManager.register(new Projection());
		OperationManager.register(new Accessible());
		OperationManager.register(new Coaccessible());
		OperationManager.register(new Trim());
		OperationManager.register(new PrefixClosure());
		OperationManager.register(new Controllable());
		OperationManager.register(new SupCon());

		try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) { }
//DEBUG: remove eventually
//	    for(Object o:UIManager.getLookAndFeelDefaults().keySet())
//	    	System.out.println(o.toString());
	    
		// show splash screen
		Hub.setMainWindow(new MainWindow());

		//setup stuff that needs the main window
		LatexManager.init(); 
		
		Automaton fsa = new Automaton(Hub.string("newAutomatonName"));
		Hub.getWorkspace().addFSAModel(fsa);
		Hub.getWorkspace().setActiveModel(fsa.getName());
		Hub.registerOptionsPane(new GraphExporter.ExportOptionsPane());

		//go live!		
		Hub.getMainWindow().pack();
		Hub.getMainWindow().setVisible(true);
	}
}
