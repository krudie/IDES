/**
 * 
 */
package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ides.api.core.Hub;
import ides.api.plugin.io.FileIOPlugin;
import ides.api.plugin.io.FileLoadException;
import ides.api.plugin.io.FileSaveException;
import ides.api.plugin.io.FormatTranslationException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.IOSubsytem;
import ides.api.plugin.io.ImportExportPlugin;
import ides.api.plugin.model.DESModel;
import io.fsa.ver2_1.AutomatonParser20;
import util.AnnotationKeys;

/**
 * @author christiansilvano
 */
public final class IOCoordinator implements IOSubsytem {
    // Singleton instance:
    private static IOCoordinator instance = null;

    private IOCoordinator() {
    }

    public static IOCoordinator getInstance() {
        if (instance == null) {
            instance = new IOCoordinator();
        }
        return instance;
    }

    public File getFileOfModel(DESModel model) {
        Object o = model.getAnnotation(AnnotationKeys.FILE);
        if (o instanceof File) {
            return (File) o;
        }
        return null;
    }

    public void setFileOfModel(DESModel model, File file) {
        model.setAnnotation(AnnotationKeys.FILE, file);
    }

    public void save(DESModel model, File file) throws IOException {
        // Get the plugin capable of saving a model of the type "type"
        // Currently there must be just one data saver for a model type.
        FileIOPlugin dataSaver = IOPluginManager.instance().getDataSaver(model.getModelType().getMainPerspective());
        if (dataSaver == null) {
            throw new FileSaveException(Hub.string("errorCannotSaveType") + model.getModelType().getDescription());
        }

        // Read the dataType and version from the plugin modelDescriptor
        String type = dataSaver.getIOTypeDescriptor();
        String version = dataSaver.getSaveDataVersion();

        // Get all the plugins capable of saving the metaTags for ""type""
        // There can be several different meta savers for a specific data type.
        Set<FileIOPlugin> metaSavers = IOPluginManager.instance()
                .getMetaSavers(model.getModelType().getMainPerspective());
        if (metaSavers == null) {
            metaSavers = new HashSet<FileIOPlugin>();
        }
        Iterator<FileIOPlugin> metaIt = metaSavers.iterator();

        // Open ""file"" and start writing the header of the IDES file format
        WrappedPrintStream ps = null;
        ps = new WrappedPrintStream(IOUtilities.getPrintStream(file), "UTF-8");
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<model version=\"" + version + "\" type=\"" + type + "\" id=\"" + model.getName() + "\">");
        ps.println("<data>");

        // Make the dataSaver plugin save the data information on the
        // file (protect the original content)
        dataSaver.saveData(ps, model, file.getAbsolutePath());
        // The data information is stored:
        ps.println("</data>");
        // 3 - Make the metaSavers one by one save the meta information on the
        // file
        while (metaIt.hasNext()) {
            FileIOPlugin plugin = metaIt.next();
            Iterator<String> tags = plugin.getMetaTags().iterator();
            while (tags.hasNext()) {
                String tag = tags.next();
                MetaPrintStream metaps = new MetaPrintStream(ps,
                        "<meta tag=\"" + tag + "\" version=\"" + plugin.getSaveMetaVersion(tag) + "\">");
                plugin.saveMeta(metaps, model, tag);
                if (metaps.hasOutput()) {
                    // the metaps stream might be closed by the plugin, so use
                    // ps
                    ps.println("</meta>");
                }
            }
        }
        ps.println("</model>");
        ps.closeWrappedPrintStream();
    }

    // Get the "type" of the model in file and ask the plugin that manage this
    // kind of "type" to load the DES.
    public DESModel load(File file) throws IOException {
        // try to deal with files from IDES ver 2.0
        if (isFileVer20(file)) {
            AutomatonParser20 parser20 = new AutomatonParser20();
            DESModel model = parser20.parse(file);
            if (model == null || !"".equals(parser20.getParsingErrors())) {
                throw new FileLoadException(parser20.getParsingErrors(), model);
            }
            model.modelSaved();
            return model;
        }

        // set when FileLoadException is encountered in loadData or loadMeta
        String errorMsg = "";
        boolean errorEncountered = false;

        DESModel returnModel = null;

        // Create a FileInputStream with "file"
        FileInputStream fis = new FileInputStream(file);
        FileChannel fch = fis.getChannel();

        try {
            TagRecovery recovery = new TagRecovery();
            recovery.parse(fis);

            if (recovery.dataType == null) {
                throw new FileLoadException(Hub.string("xmlParsingDefNotFound"));
            }

            // LOADING DATA TO THE MODEL
            FileIOPlugin plugin = IOPluginManager.instance().getDataLoader(recovery.getDataType());
            if (plugin == null) {
                throw new FileLoadException(Hub.string("pluginNotFoundFile"));
            }

            fch.position(recovery.dataOffset);
            InputStream dataStream = new ProtectedInputStream(fis, 0, recovery.getDataLength());
            try {
                returnModel = plugin.loadData(recovery.getDataVersion(), dataStream, file.getAbsolutePath());
            } catch (FileLoadException e) {
                if (e.getPartialModel() == null) {
                    throw e;
                } else {
                    errorEncountered = true;
                    errorMsg += e.getMessage();
                    returnModel = e.getPartialModel();
                }
            }

            // LOADING METADATA TO THE MODEL:
            for (String tag : recovery.getTags()) {
                FileIOPlugin metaPlugin = IOPluginManager.instance().getMetaLoaders(recovery.dataType, tag);
                if (metaPlugin == null) {
                    errorEncountered = true;
                    errorMsg += Hub.string("pluginNotFoundMeta") + " [" + tag + "]\n";
                } else {
                    try {
                        fch.position(recovery.getTagOffset(tag));
                        // Get a stream containing the metaInformation
                        InputStream metaStream = new ProtectedInputStream(fis, 0, recovery.getTagLength(tag));
                        metaPlugin.loadMeta(recovery.getTagVersion(tag), metaStream, returnModel, tag);
                    } catch (FileLoadException e) {
                        errorEncountered = true;
                        errorMsg += e.getMessage();
                    }
                }
            }
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }
        if (returnModel != null) {
            returnModel.setName(ParsingToolbox.removeFileType(file.getName()));
            returnModel.setAnnotation(AnnotationKeys.FILE, file);
            returnModel.modelSaved();
        }
        if (errorEncountered) {
            throw new FileLoadException(errorMsg, returnModel);
        }
        return returnModel;
    }

    public DESModel importFile(File src, String description) throws IOException {
        ImportExportPlugin plugin = IOPluginManager.instance().getImporter(description);
        if (plugin == null) {
            throw new FormatTranslationException(Hub.string("pluginNotFoundFile"));
        }
        DESModel model = null;
        File dst = File.createTempFile("IDESimport", IOSubsytem.MODEL_FILE_EXT);
        try {
            plugin.importFile(src, dst);
            model = load(dst);
            model.removeAnnotation(AnnotationKeys.FILE);
            model.setName(ParsingToolbox.removeFileType(src.getName()));
            model.modelSaved();
        } catch (FileLoadException e) {
            if (e.getPartialModel() != null) {
                e.getPartialModel().removeAnnotation(AnnotationKeys.FILE);
                e.getPartialModel().setName(ParsingToolbox.removeFileType(src.getName()));
                e.getPartialModel().modelSaved();
            }
            throw e;
        } finally {
            dst.delete();
        }
        return model;
    }

    public void export(DESModel model, File dst, String description) throws IOException {
        if (model == null) {
            throw new FormatTranslationException(Hub.string("internalError"));
        }
        Set<ImportExportPlugin> plugins = IOPluginManager.instance()
                .getExporters(model.getModelType().getMainPerspective());
        if (plugins == null) {
            plugins = new HashSet<ImportExportPlugin>();
        }
        ImportExportPlugin plugin = null;
        for (ImportExportPlugin p : plugins) {
            if (p.getFileDescription().equals(description)) {
                plugin = p;
                break;
            }
        }
        if (plugin == null) {
            throw new FormatTranslationException(Hub.string("pluginNotFoundFile"));
        }
        File src = File.createTempFile("IDESexport", IOSubsytem.MODEL_FILE_EXT);
        try {
            save(model, src);
            plugin.exportFile(src, dst);
        } finally {
            src.delete();
        }
    }

    protected boolean isFileVer20(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        in.readLine();
        String line = in.readLine();
        in.close();
        if (line != null && line.startsWith("<automaton>")) {
            return true;
        }
        return false;
    }
}
