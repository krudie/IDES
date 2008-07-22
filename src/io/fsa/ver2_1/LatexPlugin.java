/**
 * 
 */
package io.fsa.ver2_1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import main.Hub;
import model.fsa.FSAModel;
import pluggable.io.FormatTranslationException;
import pluggable.io.IOCoordinator;
import pluggable.io.IOPluginManager;
import pluggable.io.ImportExportPlugin;
import presentation.fsa.FSAGraph;
import services.latex.LatexManager;

/**
 * @author christiansilvano
 */
public class LatexPlugin implements ImportExportPlugin
{

	private String description = "LaTeX (PSTricks)";

	private String ext = "tex";

	/**
	 * Registers itself to the IOPluginManager
	 */
	public void initializeImportExport()
	{
		IOPluginManager.getInstance().registerExport(this, FSAModel.class);
	}

	/**
	 * Unregisters itself from the IOPluginManager
	 */
	public void unload()
	{

	}

	/**
	 * Exports a file to a different format
	 * 
	 * @param src
	 *            - the source file
	 * @param dst
	 *            - the destination
	 */
	public void exportFile(File src, File dst)
			throws FormatTranslationException
	{
		if (!LatexManager.isLatexEnabled())
		{
			Hub.displayAlert(Hub.string("enableLatex4Export"));
			return;
		}
		// Modified: June 16, 2006
		// Modifier: Sarah-Jane Whittaker
		// Comment by Christian: Why don't make the "GraphExporter" return an
		// OutputStream
		// instead of a String?
		PrintStream ps = null;
		try
		{
			FSAModel a = (FSAModel)IOCoordinator.getInstance().load(src);

			FSAGraph graphModel = GraphExportHelper.wrapRecomputeShift(a);

			ps = new PrintStream(dst);
			GraphExporter.createPSTricksFileContents(graphModel, ps);
			ps.close();
		}
		catch (IOException e)
		{
			throw new FormatTranslationException(e);
		}
		finally
		{
			if (ps != null)
			{
				ps.close();
			}
		}
	}

	/**
	 * Import a file from a different format to the IDES file system
	 * 
	 * @param importFile
	 *            - the source file
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
	public String getFileExtension()
	{
		return ext;
	}

	/**
	 * Import a file from a different format to the IDES file system
	 * 
	 * @param importFile
	 *            - the source file
	 * @return
	 */
	public void importFile(File src, File dst)
	{

	}
}
