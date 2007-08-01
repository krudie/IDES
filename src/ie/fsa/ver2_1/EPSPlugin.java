/**
 * 
 */
package ie.fsa.ver2_1;

import io.IOUtilities;
import pluggable.io.IOCoordinator;
import model.fsa.FSAModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Hub;

import org.pietschy.command.file.ExtensionFileFilter;

import pluggable.io.IOPluginManager;

import pluggable.io.ImportExportPlugin;
import presentation.fsa.GraphExporter;
import services.latex.LatexManager;
import services.latex.LatexRenderException;
/**
 * @author christiansilvano
 *
 */
public class EPSPlugin implements ImportExportPlugin{
	private String description = IOUtilities.EPS_DESCRIPTOR;
	private String ext = IOUtilities.EPS_FILE_EXT;
//	Singleton instance:
	private static EPSPlugin instance = null;
	private EPSPlugin()
	{
		this.initializeImportExport();
	}
	
	
	public static EPSPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new EPSPlugin();
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
		if(Hub.getWorkspace().getActiveModel()==null)
			return;
		if(!LatexManager.isLatexEnabled())
		{
			Hub.displayAlert(Hub.string("enableLatex4Export"));
			return;
		}
		// Modified: June 16, 2006
		// Modifier: Sarah-Jane Whittaker
		//		FSAModel model = (FSAModel)IOCoordinator.getInstance().load(src);
		String fileContents = GraphExporter.createEPSFileContents();
		//		System.out.println(fileContents);
		FileWriter latexWriter = null;
		
		if (fileContents == null)
		{
			return;
		}
		
		try
		{
			latexWriter = new FileWriter(dst);
			latexWriter.write(fileContents);
			latexWriter.close();
		}
		catch (IOException fileException)
		{
			Hub.displayAlert(Hub.string("problemLatexExport")+dst.getPath());
		}

	}
	
	/**
	 * Import a file from a different format to the IDES file system
	 * @param importFile - the source file
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
	public String getExportExtension()
	{
		return ext;
	}
}
