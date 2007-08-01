/**
 * 
 */
package ie.fsa.ver2_1;

import io.IOUtilities;
import io.ctct.CTCTException;
import io.ctct.LL_CTCT_Command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.pietschy.command.file.ExtensionFileFilter;

import main.Hub;
import model.fsa.FSAModel;
import pluggable.io.IOCoordinator;
import pluggable.io.IOPluginManager;
import pluggable.io.ImportExportPlugin;

/**
 * @author christiansilvano
 *
 */
public class TCTPlugin implements ImportExportPlugin{
	
	
	private String description = IOUtilities.TCT_DESCRIPTOR;
	private String ext = IOUtilities.TCT_FILE_EXT;
	
	public String getExportExtension(){
		return ext;
	}
	//Singleton instance:
	private static TCTPlugin instance = null;
	private TCTPlugin()
	{
		this.initializeImportExport();
	}
	
	
	public static TCTPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new TCTPlugin();
		}
		return instance;
	}
	
	
	/**
	 * Registers itself to the IOPluginManager
	 *
	 */
	public void initializeImportExport()
	{
		IOPluginManager.getInstance().registerExport(this, IOUtilities.TCT_DESCRIPTOR, IOUtilities.FSA_DESCRIPTOR);
		IOPluginManager.getInstance().registerImport(this, IOUtilities.TCT_DESCRIPTOR, IOUtilities.FSA_DESCRIPTOR);
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
		FSAModel a= null;
		try{
			a = (FSAModel)IOCoordinator.getInstance().load(src);
		}catch(IOException e){
			Hub.displayAlert(e.getMessage());
		}
    	
		try
		{
	    	LL_CTCT_Command.GiddesToCTCT(dst.getAbsolutePath(),a,LL_CTCT_Command.em);
	    	LL_CTCT_Command.em.saveGlobalMap(new File(dst.getParentFile().getAbsolutePath()+File.separator+"global.map"));
		}
		catch (CTCTException fileException)
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
		//This code does not work. It will result in errors if used.
    	try
    	{
    		FSAModel a=LL_CTCT_Command.CTCTtoGiddes(src.getAbsolutePath(),src.getName().substring(0,src.getName().lastIndexOf(".")));
//    		presentation.fsa.FSAGraph g=new presentation.fsa.FSAGraph(a);
    		//saving the imported model to dst
    		IOCoordinator.getInstance().save(a, dst);
       		//Add the new layout to the workspace
//    		Hub.getWorkspace().addModel(a);
//			Hub.getWorkspace().setActiveModel(a.getName());
    	}catch(CTCTException e)
    	{
    		e.printStackTrace();
    		Hub.displayAlert(Hub.string("cantParseImport")+src);
    	}
    	catch(RuntimeException e)
    	{
    		e.printStackTrace();
    		Hub.displayAlert(Hub.string("cantParseImport")+src);
    	}

	}
	
	
	/**
	 * Return a human readable description of the plugin
	 */
	public String getDescription()
	{
		return description;
	}
	
}
