/**
 * 
 */
package io.fsa.ver2_1;

import java.io.File;
import java.io.IOException;

import ides.api.core.Hub;
import ides.api.latex.LatexRenderException;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.io.FormatTranslationException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.ImportExportPlugin;
import io.IOCoordinator;
import presentation.fsa.FSAGraph;

/**
 * @author christiansilvano
 */
public class EPSPlugin implements ImportExportPlugin {
    private String description = "EPS";

    private String ext = "eps";

    /**
     * Registers itself to the IOPluginManager
     */
    public void initialize() {
        IOPluginManager.instance().registerExport(this, FSAModel.class);
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
        if (!Hub.getLatexManager().isLatexEnabled()) {
            Hub.displayAlert(Hub.string("enableLatex4Export"));
            return;
        }
        // Modified: June 16, 2006
        // Modifier: Sarah-Jane Whittaker
        // FSAModel model = (FSAModel)IOCoordinator.getInstance().load(src);
        try {
            FSAModel a = (FSAModel) IOCoordinator.getInstance().load(src);

            FSAGraph graphModel = GraphExportHelper.wrapRecomputeShift(a);

            String fileContents = GraphExporter.createEPSFileContents(graphModel);
            if (fileContents == null) {
                throw new FormatTranslationException(Hub.string("internalError"));
            }
            Hub.getLatexManager().getRenderer().latex2EPS(fileContents, dst);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        } catch (LatexRenderException e) {
            throw new FormatTranslationException(e);
        }
    }

    /**
     * Import a file from a different format to the IDES file system
     * 
     * @param src the source file
     * @param dst the destination file
     */
    public void importFile(File src, File dst) {

    }

    /**
     * Return a human readable description of the plugin
     */
    public String getFileDescription() {
        return description;
    }

    /**
     * 
     */
    public String getFileExtension() {
        return ext;
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
        return "EPS export";
    }

    public String getVersion() {
        return Hub.string("IDES_VER");
    }
}
