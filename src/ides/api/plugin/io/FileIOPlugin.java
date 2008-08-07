/**
 * 
 */
package ides.api.plugin.io;

import ides.api.plugin.model.DESModel;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Set;

/**
 * Interface for plugins which save/load {@link DESModel}s or which save/load
 * meta-data sections in {@link DESModel} files.
 * <p>
 * For meta-data sections, refer to external documentation.
 * 
 * @author christiansilvano
 * @author Lenko Grigorov
 */
public interface FileIOPlugin
{
	/**
	 * Return the string describing the type of model handled by the plugin.
	 * This string is matched against the type in IDES model files.
	 * <p>
	 * For example, the type of Finite-State Machines is "FSA".
	 * 
	 * @return the string describing the type of model handled by the plugin
	 */
	public String getIOTypeDescriptor();

	/**
	 * Returns the set of meta-data "tags" handled by the plugin. These tags are
	 * matched against the meta-data tags in IDES model files.
	 * <p>
	 * For example, the tag for layout meta-data in FSA files is "layout".
	 * 
	 * @return the set of meta-data "tags" handled by the plugin
	 */
	public Set<String> getMetaTags();

	/**
	 * Save the description of the DES model into the provided stream. It is
	 * highly recommended that the data conforms to the XML format.
	 * <p>
	 * The provided directory pointer can be used if the plugin wishes to work
	 * with additional files in the process of saving.
	 * 
	 * @param stream
	 *            stream where to write the data
	 * @param model
	 *            DES model to save
	 * @param fileDirectory
	 *            directory where the model will be saved
	 * @throws FileSaveException
	 *             when there is a problem saving the data
	 */
	public void saveData(PrintStream stream, DESModel model, File fileDirectory)
			throws FileSaveException;

	/**
	 * Load a DES model from the provided stream.
	 * <p>
	 * The provided directory pointer can be used if the plugin wishes to work
	 * with additional files in the process of loading.
	 * 
	 * @param stream
	 *            stream with the model data
	 * @param fileDirectory
	 *            directory from where the model is loaded
	 * @return the loaded model
	 * @throws FileLoadException
	 *             when there is a problem loading the model
	 */
	public DESModel loadData(InputStream stream, File fileDirectory)
			throws FileLoadException;

	/**
	 * Loads meta-data for a given DES model from a meta-data section.
	 * 
	 * @param stream
	 *            stream from where meta-data should be read
	 * @param model
	 *            the DES model for which the meta-data is to be loaded
	 * @param tag
	 *            the "tag" of the meta-data section to be loaded
	 * @throws FileLoadException
	 *             when there is a problem loading the meta-data
	 */
	public void loadMeta(InputStream stream, DESModel model, String tag)
			throws FileLoadException;

	/**
	 * Save the meta-data section with the given "tag" for a given DES model. It
	 * is highly recommended that the data conforms to the XML format.
	 * 
	 * @param stream
	 *            stream where to write the meta-data
	 * @param model
	 *            the DES model for which the meta-data is to be saved
	 * @param tag
	 *            the "tag" of the meta-data section to be saved
	 * @throws FileSaveException
	 *             when there is a problem saving the mata-data
	 */
	public void saveMeta(PrintStream stream, DESModel model, String tag)
			throws FileSaveException;

}
