/**
 * 
 */
package ie.fsa.ver2_1;

import java.io.File;

/**
 * @author christiansilvano
 *
 */
public class PNGPlugin {
	/**
	 * Registers itself to the IOPluginManager
	 *
	 */
	{
		
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
		
	}
	
	/**
	 * Import a file from a different format to the IDES file system
	 * @param importFile - the source file
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
		return null;
	}
	
	/**
	 * 
	 */
	public String getExportExtension()
	{
		return null;
	}
}
