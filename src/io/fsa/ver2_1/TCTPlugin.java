/**
 * 
 */
package io.fsa.ver2_1;

import java.io.File;
import java.io.IOException;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.io.FormatTranslationException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.ImportExportPlugin;
import io.IOCoordinator;
import io.ctct.CTCTException;
import io.ctct.LL_CTCT_Command;

/**
 * @author christiansilvano
 */
public class TCTPlugin implements ImportExportPlugin {

    private String description = "TCT (experimental)";

    private String ext = "des";

    public String getFileExtension() {
        return ext;
    }

    /**
     * Registers itself to the IOPluginManager
     */
    public void initialize() {
        IOPluginManager.instance().registerExport(this, FSAModel.class);
        IOPluginManager.instance().registerImport(this);
    }

    /**
     * Unregisters itself from the IOPluginManager
     */
    public void unload() {
    }

    /**
     * Exports a file to a different format
     * 
     * @param src - the source file
     * @param dst - the destination
     */
    public void exportFile(File src, File dst) throws FormatTranslationException {
        FSAModel a = null;
        try {
            a = (FSAModel) IOCoordinator.getInstance().load(src);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        }

        try {
            LL_CTCT_Command.GiddesToCTCT(dst.getAbsolutePath(), a, LL_CTCT_Command.em);
            LL_CTCT_Command.em
                    .saveGlobalMap(new File(dst.getParentFile().getAbsolutePath() + File.separator + "global.map"));
        } catch (CTCTException e) {
            throw new FormatTranslationException(e);
        }
    }

    /**
     * Import a file from a different format to the IDES file system
     * 
     * @param src the source file
     * @param dst the destination file
     */
    public void importFile(File src, File dst) throws FormatTranslationException {
        try {
            FSAModel a = LL_CTCT_Command.CTCTtoGiddes(src.getAbsolutePath(),
                    src.getName().substring(0, src.getName().lastIndexOf(".")));
            // Save the imported model to <code>dst</code>
            IOCoordinator.getInstance().save(a, dst);
        } catch (CTCTException e) {
            throw new FormatTranslationException(e);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        }
    }

    /**
     * Return a human readable description of the plugin
     */
    public String getFileDescription() {
        return description;
    }

    public String getCredits() {
        return Hub.string("DEVELOPERS");
    }

    public String getDescription() {
        return "part of IDES";
    }

    public String getLicense() {
        return "same as IDES";
    }

    public String getName() {
        return "TCT import and export";
    }

    public String getVersion() {
        return Hub.string("IDES_VER");
    }
}
