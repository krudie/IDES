package ides.api.plugin.io;

import java.io.File;
import java.io.IOException;

import ides.api.plugin.model.DESModel;

/**
 * Access to loading/saving and import/export of models.
 */
public interface IOSubsytem {
    /**
     * Extension for workspace files.
     */
    public static final String WORKSPACE_FILE_EXT = "xws";

    /**
     * Extension for model files.
     */
    public static final String MODEL_FILE_EXT = "xmd";

    /**
     * Load a DES model from a file.
     * 
     * @param file the file with the model.
     * @return the model
     * @throws IOException if there is a problem while loading the model
     * @see FileLoadException
     */
    public DESModel load(File file) throws IOException;

    /**
     * Save a DES model to a file.
     * <p>
     * Note: if the file exists, it will be over-written without any notification.
     * 
     * @param model the model
     * @param file  the file where the model should be saved
     * @throws IOException if there is a problem while saving the model
     */
    public void save(DESModel model, File file) throws IOException;

    /**
     * Import a model. The format of the file is assumed to be given in the
     * description. In order to access the list of supported formats, call
     * {@link IOPluginManager#getImporters()}.
     * 
     * @param src         the file to be imported
     * @param description the description of the file format
     * @return the imported model
     * @throws IOException if there is a problem while importing the model
     * @see IOPluginManager#getImporters()
     * @see ImportExportPlugin#getFileDescription()
     * @see FileLoadException
     */
    public DESModel importFile(File src, String description) throws IOException;

    /**
     * Export a model to the format given in the description. In order to access the
     * list of supported formats, call {@link IOPluginManager#getExporters()}.
     * 
     * @param model       the model to be exported
     * @param dst         the file where the exported model should be saved
     * @param description the description of the file format
     * @throws IOException if there is a problem while exporting the model
     * @see IOPluginManager#getExporters()
     * @see ImportExportPlugin#getFileDescription()
     */
    public void export(DESModel model, File dst, String description) throws IOException;

    /**
     * Determine the file, if any, where the given model is saved.
     * 
     * @param model the model
     * @return the file where the given model is saved; <code>null</code> if the
     *         model has not been saved in a file yet
     */
    public File getFileOfModel(DESModel model);

    /**
     * Specify the file where a model is saved. The file can be <code>null</code>.
     * <p>
     * This method simply sets an annotation. This method does not cause the model
     * to be saved. To save a model, use {@link #save(DESModel, File)}.
     * 
     * @param model the model
     * @param file  the file where the model is saved
     */
    public void setFileOfModel(DESModel model, File file);
}
