/**
 * 
 */
package io.fsa.ver2_1;

import io.IOUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;


import main.Hub;


import pluggable.io.IOCoordinator;
import pluggable.io.IOPluginManager;
import pluggable.io.ImportExportPlugin;
import presentation.fsa.FSAGraph;
import services.latex.LatexManager;

/**
 * @author christiansilvano
 *
 */
public class LatexPlugin implements ImportExportPlugin{

	private String description = IOUtilities.LATEX_DESCRIPTOR;
	private String ext = IOUtilities.LATEX_FILE_EXT;
//	Singleton instance:
	private static LatexPlugin instance = null;
	private LatexPlugin()
	{
		this.initializeImportExport();
	}


	public static LatexPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new LatexPlugin();
		}
		return instance;
	}
	/**
	 * Registers itself to the IOPluginManager
	 *
	 */
	public void initializeImportExport()
	{
		IOPluginManager.getInstance().registerExport(this, IOUtilities.EPS_DESCRIPTOR, IOUtilities.FSA_DESCRIPTOR);
	}
	/**
	 * Unregisters itself from the IOPluginManager
	 *
	 */
	public void unload()
	{

	}

	/**
	 * Exports a file to a different format
	 * @param src - the source file
	 * @param dst - the destination
	 */
	public void exportFile(File src, File dst)
	{
		if(!LatexManager.isLatexEnabled())
		{
			Hub.displayAlert(Hub.string("enableLatex4Export"));
			return;
		}
		// Modified: June 16, 2006
		// Modifier: Sarah-Jane Whittaker
		//Comment by Christian: Why don't make the "GraphExporter" return an OutputStream
		//instead of a String?
		try
		{
			FSAGraph graphModel = (FSAGraph)Hub.getWorkspace().getActiveLayoutShell();
			PrintStream ps = new PrintStream(dst);
			GraphExporter.createPSTricksFileContents(graphModel, ps);
			ps.close();
		}catch(IOException e)
		{
			Hub.displayAlert(Hub.string("problemLatexExport")+dst.getPath());
		}
//		if (fileContents == null)
//		{
//			return;
//		}
//
//		try
//		{
//			latexWriter = new FileWriter(dst);
//			latexWriter.write(fileContents);
//			latexWriter.close();
//		}
//		catch (IOException fileException)
//		{
//			Hub.displayAlert(Hub.string("problemLatexExport")+dst.getPath());
//		}
	}

	/**
	 * Import a file from a different format to the IDES file system
	 * @param importFile - the source file
	 * @return
	 */
	public File importFile(File importFile)
	{
		return null;
	}


	/**
	 * Return a human readable description of the plugin
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	 */
	public String getExportExtension()
	{
		return ext;
	}

	/**
	 * Import a file from a different format to the IDES file system
	 * @param importFile - the source file
	 * @return
	 */
	public void importFile(File src, File dst)
	{

	}
}
