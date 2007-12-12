/**
 * 
 */
package pluggable.io;
import model.DESModel;


import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;
import java.util.Set;
 
/**
 * @author christiansilvano
 *
 */
//TODO Change the File in the saving methods by InputStreams
public interface FileIOPlugin {
	
	/**
	 * Subscribes itself to the IOIE_PluginManager informing whether this object
	 * is a "metaSaver", "dataSaver", "metaLoader" or "dataLoader".
	 */
	public void initializeFileIO();
	
	public String getIOTypeDescriptor();
	
	/**
	 * Returns a list of meta data "tags" for a given data "type"
	 * @param type, a string with the data "type" e.g.: "FSA", "TemplateDesign"
	 * @return a set of "tags"
	 */
	public Set<String> getMetaTags(String type);
	
	/**
	 * Saves its data in <code>file</code> according to a <code>model</code>.
	 * @param file the file to save the data in.
	 * @param model the model to be saved in the file.
	 * @param fileDirectory path to the file, so auxiliar files can be created.
	 */
	public boolean saveData(PrintStream stream, DESModel model, File fileDirectory);
	
	/**
	 * Loads data from the file.
	 * @param file
	 * @param fileDir
	 * @return
	 */
	public DESModel loadData(InputStream stream, File fileDir) throws FileLoadException;
	
	/**
	 * Loads metadata from the file
	 * @param file
	 */
	void loadMeta(InputStream stream, DESModel model) throws FileLoadException;
	
	/**
	 * Save metaData to the file according to model.
	 * @param file
	 * @param model
	 */
	public boolean saveMeta(PrintStream stream, DESModel model, String type, String tag);	
	
	/**
	 * Unsubscribe itself from the IOIE_PluginManager
	 *
	 */
	void unload();
	

}
