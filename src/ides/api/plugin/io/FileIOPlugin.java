/**
 * 
 */
package ides.api.plugin.io;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Set;

import ides.api.plugin.model.DESModel;

/**
 * Interface for plugins which save/load {@link DESModel}s or which save/load
 * meta-data sections in {@link DESModel} files.
 * <p>
 * For meta-data sections, refer to external documentation.
 * 
 * @author christiansilvano
 * @author Lenko Grigorov
 */
public interface FileIOPlugin {
    /**
     * Return the string describing the type of model handled by the plugin. This
     * string is matched against the type in IDES model files.
     * <p>
     * For example, the type of Finite-State Machines is "FSA".
     * <p>
     * This method is relevant only to data-handling plugins. Meta-data plugins do
     * not need to implement this method.
     * 
     * @return the string describing the type of model handled by the plugin
     */
    public String getIOTypeDescriptor();

    /**
     * Returns the version of the format in which the model data is saved.
     * <p>
     * This method is relevant only to plugins which save model data.
     * 
     * @return the version of the format in which the model data is saved
     */
    public String getSaveDataVersion();

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
     * Returns the version of the format in which the meta-data section with the
     * given "tag" is saved.
     * <p>
     * This method is relevant only to plugins which save model meta-data.
     * 
     * @param tag the "tag" for which version information is requested
     * @return the version of the format in which the meta-data section with the
     *         given "tag" is saved; if the "tag" is not supported by this plugin,
     *         the return value is unspecified
     */
    public String getSaveMetaVersion(String tag);

    /**
     * Save the description of the DES model into the provided stream. It is highly
     * recommended that the data conforms to the XML format.
     * <p>
     * The provided file name can be used if the plugin needs to work with
     * additional files in the directory or needs the name of the save file. The
     * file name is only for information purposes. All data <b>must</b> be written
     * to the provided stream.
     * 
     * @param stream   stream where to write the data (will be encoded into UTF-8)
     * @param model    DES model to save
     * @param fileName absolute path to the file where the model will be saved
     * @throws FileSaveException when there is a problem saving the data
     */
    public void saveData(PrintStream stream, DESModel model, String fileName) throws FileSaveException;

    /**
     * Load a DES model from the provided stream.
     * <p>
     * The provided file name can be used if the plugin needs to work with
     * additional files in the directory or needs the name of the loaded file. The
     * file name is only for information purposes. All data <b>must</b> be read from
     * the provided stream.
     * 
     * @param version  the version of the format of the model data
     * @param stream   UTF-8 encoded stream with the model data
     * @param fileName absolute path to the file from where the model is loaded
     * @return the loaded model
     * @throws FileLoadException when there is a problem loading the model
     */
    public DESModel loadData(String version, InputStream stream, String fileName) throws FileLoadException;

    /**
     * Loads meta-data for a given DES model from a meta-data section.
     * 
     * @param version the version of the format of the meta-data section
     * @param stream  UTF-8 encoded stream from where meta-data should be read
     * @param model   the DES model for which the meta-data is to be loaded
     * @param tag     the "tag" of the meta-data section to be loaded
     * @throws FileLoadException when there is a problem loading the meta-data
     */
    public void loadMeta(String version, InputStream stream, DESModel model, String tag) throws FileLoadException;

    /**
     * Save the meta-data section with the given "tag" for a given DES model. It is
     * highly recommended that the data conforms to the XML format.
     * 
     * @param stream stream where to write the meta-data (will be encoded into
     *               UTF-8)
     * @param model  the DES model for which the meta-data is to be saved
     * @param tag    the "tag" of the meta-data section to be saved
     * @throws FileSaveException when there is a problem saving the mata-data
     */
    public void saveMeta(PrintStream stream, DESModel model, String tag) throws FileSaveException;

}
