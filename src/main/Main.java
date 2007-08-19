package main;

import io.fsa.ver2_1.EPSPlugin;
import io.fsa.ver2_1.FSAFileIOPlugin;
import io.fsa.ver2_1.GrailPlugin;
import io.fsa.ver2_1.GraphExporter;
import io.fsa.ver2_1.LatexPlugin;
import io.fsa.ver2_1.TCTPlugin;




import java.io.IOException;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import operations.fsa.ver2_1.Accessible;
import operations.fsa.ver2_1.Coaccessible;
import operations.fsa.ver2_1.Nonconflicting;
import operations.fsa.ver2_1.Containment;
import operations.fsa.ver2_1.ControlMap;
import operations.fsa.ver2_1.Controllable;
import operations.fsa.ver2_1.LocalModular;
import operations.fsa.ver2_1.Meet;
import operations.fsa.ver2_1.PrefixClosure;
import operations.fsa.ver2_1.Projection;
import operations.fsa.ver2_1.SupCon;
import operations.fsa.ver2_1.SupRed;
import operations.fsa.ver2_1.SynchronousProduct;
import operations.fsa.ver2_1.Trim;

import pluggable.operation.OperationManager;
import presentation.LayoutShell;
import presentation.PresentationManager;
import presentation.fsa.FSAToolset;
import presentation.fsa.FSAGraph;

import model.DESModel;
import model.ModelManager;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;

import services.General;
import services.cache.Cache;
import services.latex.LatexManager;
import ui.MainWindow;
import pluggable.io.*;
//import io.template.ver2_1.TemplateFileIOPlugin;
//import presentation.template.TemplateToolset;
//import model.template.TemplateModel;
//import model.template.ver2_1.TemplateDesign;
/**
 * 
 * @author Lenko Grigorov
 */
public class Main {

	private Main()
	{
	}

	private static void initializePlugins()
	{
		//Input/Output plugins:
		FSAFileIOPlugin.getInstance().initializeFileIO();
		//The template design is disabled for this version of IDES:
		//		TemplateFileIOPlugin.getInstance().initializeFileIO();

		//Import/Export plugins:
		GrailPlugin.getInstance().initializeImportExport();
		TCTPlugin.getInstance().initializeImportExport();
		EPSPlugin.getInstance().initializeImportExport();
		LatexPlugin.getInstance().initializeImportExport();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//set up global exception handler
		// TODO uncomment this line before shipping.  Default exception handler
		// disabled for debugging. -- CLM
//		Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

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
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		Cache.init();
		// TODO: move operation inits to the plugin manager eventually
		///TODO: move the initialization of the plugins to the plugin manager
		initializePlugins();

		//The template design is disabled for this version of IDES:
//		ModelManager.registerModel(TemplateDesign.myDescriptor);
//		PresentationManager.registerToolset(TemplateModel.class, new TemplateToolset());
		ModelManager.registerModel(Automaton.myDescriptor);
		PresentationManager.registerToolset(FSAModel.class, new FSAToolset());
		OperationManager.register(new Meet());
		OperationManager.register(new SynchronousProduct());
		OperationManager.register(new Projection());
		OperationManager.register(new Accessible());
		OperationManager.register(new Coaccessible());
		OperationManager.register(new Trim());
		OperationManager.register(new PrefixClosure());
		OperationManager.register(new Controllable());
		OperationManager.register(new SupCon());
		OperationManager.register(new Containment());
		OperationManager.register(new Nonconflicting());
		OperationManager.register(new ControlMap());
		OperationManager.register(new LocalModular());
		OperationManager.register(new SupRed());

		try {
			if (UIManager.getSystemLookAndFeelClassName() == "com.sun.java.swing.plaf.gtk.GTKLookAndFeel") {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.metal.MetalLookAndFeel");
			} else {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) { }
//		DEBUG: remove eventually
//		for(Object o:UIManager.getLookAndFeelDefaults().keySet())
//		System.out.println(o.toString());

		// show splash screen
		Hub.setMainWindow(new MainWindow());

		//setup stuff that needs the main window
		LatexManager.init(); 

		FSAModel fsa = ModelManager.createModel(FSAModel.class,Hub.string("newModelName"));
		Hub.getWorkspace().addModel(fsa);
		Hub.getWorkspace().setActiveModel(fsa.getName());
		Hub.registerOptionsPane(new GraphExporter.ExportOptionsPane());

		//go live!		
		// TODO make sure that this second call to pack() is necessary (called
		// in the MainWindow constructor as well, and interferes with window sizing
		// code) -- CLM
		//Hub.getMainWindow().pack();
		Hub.getMainWindow().setVisible(true);
	}

	/**
	 * Handles stuff that has to be done before the application terminates.
	 *
	 */
	public static void onExit()
	{
//		This cannot be handled by the io.fsa.ver2_1.handleUnsavedWorkspace
		if(Hub.getWorkspace().isDirty())
			if(!io.CommonActions.handleUnsavedWorkspace())
				return;
		for(Iterator<DESModel> i=Hub.getWorkspace().getModels();i.hasNext();)
		{
			DESModel m=i.next();
			if( m.needsSave() )
				if(!io.CommonActions.handleUnsavedModel(m))
					return;
		}
		//store settings
		Cache.close();
		Hub.getMainWindow().dispose();
		Hub.storePersistentData();
	}

}
