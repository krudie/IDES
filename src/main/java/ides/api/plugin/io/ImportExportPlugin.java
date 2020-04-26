/**
 * 
 */
package ides.api.plugin.io;

import java.io.File;

import ides.api.plugin.model.DESModel;

/**
 * Interface for plugins which import/export {@link DESModel}s.
 * 
 * @author christiansilvano
 * @author Lenko Grigrov
 */
public interface ImportExportPlugin {
    /**
     * Export a {@link DESModel} into a different format. The model is provided as
     * an IDES file. It is OK to overwrite the destination as the user's permission
     * has been requested.
     * <p>
     * The persistence of the source file after the method call should not be
     * assumed.
     * 
     * @param src a pointer to the IDES file with the DES model to export
     * @param dst a pointer to the file where output should be written
     * @throws FormatTranslationException when there is a problem exporting the
     *                                    model
     */
    public void exportFile(File src, File dst) throws FormatTranslationException;

    /**
     * Import a DES model from a different format. The DES model has to be saved in
     * an IDES file. It is OK to overwrite the destination as the user's permission
     * has been requested.
     * <p>
     * The persistence of the destination file after the method call should not be
     * assumed.
     * 
     * @param src a pointer to the file from where the model has to be read
     * @param dst a pointer to a file where to output the IDES model
     * @throws FormatTranslationException when there is a problem importing the
     *                                    model
     */
    public void importFile(File src, File dst) throws FormatTranslationException;

    /**
     * Return a human readable description of the file format supported by the
     * plugin.
     * 
     * @return a human readable description of the file format supported by the
     *         plugin
     */
    public String getFileDescription();

    /**
     * Return a string with the file extension of the foreign model format. E.g.,
     * "jpg" if the plugin exports to JPEG images.
     * 
     * @return a string with the file extension of the foreign model format
     */
    public String getFileExtension();
}
