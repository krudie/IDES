/**
 * 
 */
package io.template.ver2_1;

import model.template.TemplateModel;
import io.ParsingToolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import main.Annotable;
import main.Hub;
import model.DESModel;
import pluggable.io.FileIOPlugin;
import pluggable.io.IOPluginManager;
import presentation.template.TemplateGraph;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author christiansilvano
 *
 */
public class TemplateFileIOPlugin implements FileIOPlugin{
	
	public Set<String> getMetaTags()
	{
		Set<String> returnSet = new HashSet<String>();
		returnSet.add("layout");
		return returnSet;
	}
	
	public String getIOTypeDescriptor()
	{
		return "TemplateDesign";
	}
	
	public static final String LAST_PATH_SETTING_NAME="lastUsedPath";
	//Singleton instance:
	private static TemplateFileIOPlugin instance = null;
	private TemplateFileIOPlugin()
	{
		this.initializeFileIO();
	}
	
	
	public static TemplateFileIOPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new TemplateFileIOPlugin();
		}
		return instance;
	}
	
	/**
	 * Subscribes itself to the IOIE_PluginManager informing whether this object
	 * is a "metaSaver", "dataSaver", "metaLoader" or "dataLoader".
	 */
	public void initializeFileIO()
	{
		//The FSA model is capable of saving metaData
		IOPluginManager.getInstance().registerDataLoader(this);
		IOPluginManager.getInstance().registerDataSaver(this);
		IOPluginManager.getInstance().registerMetaSaver(this);
		IOPluginManager.getInstance().registerMetaLoader(this);
	}
	
	
	/**
	 * Saves its data in <code>file</code> according to a <code>model</code>.
	 * @param file the file to save the data in.
	 * @param model the model to be saved in the file.
	 * @param fileDirectory path to the file, so auxiliar files can be created.
	 */
	public boolean saveData(File file, DESModel model, File fileDirectory)
	{
		return false;
	}
	
	/**
	 * Loads data from the file.
	 * @param file
	 * @param fileDir
	 * @return
	 */
	public DESModel loadData(File f, File fileDir)
	{	  
		//load file
		//go fsatoolset.wrap()
		//templatetoolset.wrap()
		
	      TemplateGraph graph = null;
	      TemplateModel model = null;
	        if(!f.canRead())
	        {
	        	Hub.displayAlert(Hub.string("fileCantRead")+f.getPath());
	        	return (DESModel)model;
	        }
	        String errors="";
	        try
	        {	
	        	model = TemplateParser.parse(f);
	        }catch(Exception e)
	        {
	        	graph=null;
	        	errors+=e.getMessage();
//	        	e.printStackTrace();
	        }
	        
	        if(!"".equals(errors))
	        {
	        	Hub.displayAlert(Hub.string("errorsParsingXMLFileL1")+f.getPath()+
	        			"\n"+Hub.string("errorsParsingXMLFileL2"));
	        }
	        if(graph!=null)
	        {
	        	graph.setAnnotation(Annotable.FILE,f);
	        }
	        Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,f.getParent());

	        return (DESModel)model;
	}
	
	/**
	 * Loads metadata from the file
	 * @param file
	 */
	public void loadMeta(File file)
	{
		
	}
	
	/**
	 * Save metaData to the file, according to model.
	 * @param file
	 * @param model
	 */
	public boolean saveMeta(File file, DESModel model)
	{
		return false;
	}
	
	/**
	 * Unsubscribe itself from the IOIE_PluginManager
	 *
	 */
	public void unload()
	{
		
	}
}
