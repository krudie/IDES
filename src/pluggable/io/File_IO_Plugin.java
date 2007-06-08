/**
 * 
 */
package pluggable.io;
import model.DESModel;
import java.io.File;

/**
 * @author christiansilvano
 *
 */
public interface File_IO_Plugin {
	
	//use a IOIE_PluginManager as parameter
	public void initializeFileIO();
	
	//save its data in fileStream
	public void saveData(File fileStream, DESModel model, String FileDirectory);
	
	//load data from fileStream
	public DESModel loadData(File fileStream, String fileDir);
	
	//load metadata from fileStream
	void loadMeta(File fileStream);
	
	//save metaData to the dataStream according to model.
	public void saveMeta(File dataStream, DESModel model);	
	void unload();
	

}
