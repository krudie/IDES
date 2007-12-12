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

		/**
		 * Registers itself to the IOPluginManager
		 *
		 */
		public void initializeImportExport();
		/**
		 * Unregisters itself from the IOPluginManager
		 *
		 */
		public void unload();
		
		/**
		 * Exports a file to a different format
		 * @param src - the source file
		 * @param dst - the destination
		 */
		public void exportFile(File src, File dst) throws FormatTranslationException;
		
		/**
		 * Import a file from a different format to the IDES file system
		 * @param importFile - the source file
		 * @return
		 */
		public void importFile(File src, File dst) throws FormatTranslationException;
		
		
		/**
		 * Return a human readable description of the plugin
		 */
		public String getDescription();
		
		/**
		 * Return a string with the file extension of the exported files. eg.: "png"
		 * if the plugin exports to png.
		 */
		public String getFileExtension();
}
