/**
 * 
 */
package io.fsa.ver2_1;

import java.io.File;
import java.io.IOException;

import main.Hub;
import model.fsa.FSAModel;
import pluggable.io.FormatTranslationException;
import pluggable.io.IOCoordinator;
import pluggable.io.IOPluginManager;
import pluggable.io.ImportExportPlugin;
import presentation.PresentationManager;
import presentation.fsa.FSAGraph;
import services.latex.LatexManager;
import services.latex.LatexRenderException;

/**
 * @author christiansilvano
 */
public class EPSPlugin implements ImportExportPlugin
{
	private String description = "EPS";

	private String ext = "eps";

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
	 * @param src -
	 *            the source file
	 * @param dst -
	 *            the destination
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
		// FSAModel model = (FSAModel)IOCoordinator.getInstance().load(src);
		try
		{
			FSAModel a = (FSAModel)IOCoordinator.getInstance().load(src);
			FSAGraph graphModel = (FSAGraph)PresentationManager
					.getToolset(FSAModel.class).wrapModel(a);
			String fileContents = GraphExporter
					.createEPSFileContents(graphModel);
			if (fileContents == null)
			{
				throw new FormatTranslationException(Hub
						.string("internalError"));
			}
			LatexManager.getRenderer().latex2EPS(fileContents, dst);
		}
		catch (IOException e)
		{
			throw new FormatTranslationException(e);
		}
		catch (LatexRenderException e)
		{
			throw new FormatTranslationException(e);
		}
	}

	/**
	 * Import a file from a different format to the IDES file system
	 * 
	 * @param importFile -
	 *            the source file
	 * @return
	 */
	public void importFile(File src, File dst)
	{

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
}
