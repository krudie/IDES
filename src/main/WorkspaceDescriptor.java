package main;

import java.io.File;
import java.util.Vector;

/**
 * @author Lenko Grigorov
 */
public class WorkspaceDescriptor {
    public static final String FILE_ID = "file";

    public static final String CHILD_ID = "child";

    protected Vector<String[]> models = new Vector<String[]>();

    protected int selectedModel = 0;

    protected File file = null;

    public WorkspaceDescriptor(File file) {
        this.file = file;
    }

    public void insertModel(String model, int idx) {
        if (idx > models.size()) {
            idx = 0;
        }
        models.insertElementAt(new String[] { FILE_ID, model }, idx);
    }

    public void insertModel(String parent, String model, int idx) {
        if (idx > models.size()) {
            idx = 0;
        }
        models.insertElementAt(new String[] { CHILD_ID, parent, model }, idx);
    }

    public Vector<String[]> getModels() {
        return models;
    }

    public void setSelectedModel(int idx) {
        selectedModel = idx;
    }

    public int getSelectedModel() {
        return selectedModel;
    }

    public File getFile() {
        return file;
    }
}
