/**
 * 
 */
package io.fsa.ver2_1;

import io.ParsingToolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import main.Annotable;
import main.Hub;
import model.DESModel;
import model.fsa.FSAModel;
import pluggable.io.FileIOPlugin;
import pluggable.io.IOPluginManager;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author christiansilvano
 *
 */
public class FSAFileIOPlugin implements FileIOPlugin{
	
	public Set<String> getMetaTags()
	{
		Set<String> returnSet = new HashSet<String>();
		returnSet.add("layout");
		return returnSet;
	}
	
	public String getIOTypeDescriptor()
	{
		return "FSA";
	}
	
	public static final String LAST_PATH_SETTING_NAME="lastUsedPath";
	//Singleton instance:
	private static FSAFileIOPlugin instance = null;
	private FSAFileIOPlugin()
	{
		this.initializeFileIO();
	}
	
	
	public static FSAFileIOPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new FSAFileIOPlugin();
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
		
		//correct way:
//		IOPluginManager.getInstance().registerDataLoader(this ,"FSA");
//		IOPluginManager.getInstance().registerDataSaver(this, "FSA");
//		IOPluginManager.getInstance().registerMetaSaver(this, "FSA", "layout");
//		IOPluginManager.getInstance().registerMetaLoader(this, "FSA", "layout");
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
		//this "plugin" does not use "fileDir"
	     FSAModel a = null;
	        if(!f.canRead())
	        {
	        	Hub.displayAlert(Hub.string("fileCantRead")+f.getPath());
	        	return (DESModel)a;
	        }
	        String errors="";
	        try
	        {
	        	BufferedReader head=new BufferedReader(new FileReader(f));
	        	head.readLine();
	        	String line=head.readLine();
	        	head.close();
	        	if(line.trim().startsWith("<automaton"))
	        	{
	            	AutomatonParser20 ap = new AutomatonParser20();
	                a = ap.parse(f);
	                errors=ap.getParsingErrors();
	        	}
	        	else
	        	{
	            	AutomatonParser ap = new AutomatonParser();
	                a = ap.parse(f);
	                errors=ap.getParsingErrors();
	        	}
	        }catch(Exception e)
	        {
	        	a=null;
	        	errors+=e.getMessage();
	        }
	        if(!"".equals(errors))
	        {
	        	Hub.displayAlert(Hub.string("errorsParsingXMLFileL1")+f.getPath()+
	        			"\n"+Hub.string("errorsParsingXMLFileL2"));
	        }
	        if(a!=null)
	        {
	        	a.setName(ParsingToolbox.removeFileType(f.getName()));
	        	a.setAnnotation(Annotable.FILE,f);
	        }
	        Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,f.getParent());
	        return (DESModel)a;
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
