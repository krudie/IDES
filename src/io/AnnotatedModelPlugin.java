package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import ides.api.core.Hub;
import ides.api.plugin.io.FileIOPlugin;
import ides.api.plugin.io.FileLoadException;
import ides.api.plugin.io.FileSaveException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.UnsupportedVersionException;
import ides.api.plugin.model.DESModel;
import ides.api.utilities.GeneralUtils;
import util.AnnotationKeys;

/**
 * Plugin to store and load meta-data with user annotations for models. This
 * plugin is generic; works with all models.
 * 
 * @author Lenko Grigorov
 */
public class AnnotatedModelPlugin implements FileIOPlugin {

    protected final static String META_TAG = "annotation";

    protected final static String VERSION = "3";

    public String getIOTypeDescriptor() {
        return null;
    }

    public Set<String> getMetaTags() {
        Set<String> tags = new HashSet<String>();
        tags.add(META_TAG);
        return tags;
    }

    public String getSaveDataVersion() {
        return null;
    }

    public String getSaveMetaVersion(String tag) {
        if (META_TAG.equals(tag)) {
            return VERSION;
        }
        return "";
    }

    public DESModel loadData(String version, InputStream stream, String fileName) throws FileLoadException {
        return null;
    }

    public void loadMeta(String version, InputStream stream, DESModel model, String tag) throws FileLoadException {
        if (!tag.equals(META_TAG)) {
            throw new FileLoadException(Hub.string("ioUnsupportedMetaTag") + " [" + tag + "]");
        }
        if (!VERSION.equals(version)) {
            throw new UnsupportedVersionException(Hub.string("errorUnsupportedVersion"));
        }
        StringBuffer text = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            int r = in.read();
            while (r >= 0) {
                text.append((char) r);
                r = in.read();
            }
            in.close();
        } catch (UnsupportedEncodingException e) {
            throw new FileLoadException(e.getMessage());
        } catch (IOException e) {
            throw new FileLoadException(e.getMessage());
        }
        model.setAnnotation(AnnotationKeys.TEXT_ANNOTATION, GeneralUtils.XMLTextUnescape(text.toString()));
    }

    public void saveData(PrintStream stream, DESModel model, String fileName) throws FileSaveException {
    }

    public void saveMeta(PrintStream stream, DESModel model, String tag) throws FileSaveException {
        if (!META_TAG.equals(tag)) {
            throw new FileSaveException(Hub.string("ioUnsupportedMetaTag") + " [" + tag + "]");
        }
        if (!model.hasAnnotation(AnnotationKeys.TEXT_ANNOTATION)) {
            return;
        }
        stream.print(GeneralUtils.XMLTextEscape((String) model.getAnnotation(AnnotationKeys.TEXT_ANNOTATION)));
    }

    public void initialize() {
        IOPluginManager.instance().registerMetaLoader(this, META_TAG);
        IOPluginManager.instance().registerMetaSaver(this);
    }

    public void unload() {
    }

}
