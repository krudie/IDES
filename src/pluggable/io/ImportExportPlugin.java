/**
 * 
 */
package pluggable.io;
import java.io.File;

/**
 * @author christiansilvano
 *
 */
public interface ImportExportPlugin {

		//subscribe itself to the IOIE_PluginManager
		public void initializeIE();
		
		//reads a IDES model and return a model for another plataform 
		public File exportModel(File src);
		//reads src and return a File with a IDES model
		public File importModel(File src);
		
	
}
