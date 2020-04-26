package main;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import ides.api.core.Hub;
import ides.api.core.IncompleteWorkspaceDescriptorException;
import ides.api.core.Workspace;
import ides.api.core.WorkspaceMessage;
import ides.api.core.WorkspaceSubscriber;
import ides.api.latex.LatexPresentation;
import ides.api.plugin.io.FileLoadException;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelMessage;
import ides.api.plugin.model.DESModelSubscriber;
import ides.api.plugin.model.ParentModel;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.Toolset;
import ides.api.plugin.presentation.ToolsetManager;
import ides.api.plugin.presentation.UIDescriptor;
import ides.api.utilities.GeneralUtils;
import io.IOCoordinator;
import io.ParsingToolbox;
import util.AnnotationKeys;

/**
 * The main manager of the open DESModels.
 * 
 * @author Lenko Grigorov
 */
public class WorkspaceBackend implements DESModelSubscriber, Workspace {

    /**
     * needed for special handling of first (automatic) add and first
     * (user-initated) add
     */
    private long countAdd = 0;

    /**
     * dirty bit
     */
    private boolean dirty = false;

    /**
     * Name of the workspace, derived from the file name where the workspace is
     * saved.
     */
    private String name;

    /**
     * The file where the workspace is saved (if at all).
     */
    private File myFile = null;

    /**
     * index of the currently active DESModel
     */
    private int activeModelIdx;

    /**
     * The {@link UIDescriptor} for the currently active model.
     */
    protected UIDescriptor activeUID;

    // TODO A model of global events set (alphabet) and all local alphabets
    // private FSAEventsModel eventsModel;

    /**
     * List of the models opened in the workspace.
     */
    private Vector<DESModel> systems;

    /**
     * pointer to instance
     */
    static WorkspaceBackend me;

    /**
     * A list of the presentations forming the UI for the currently active
     * {@link DESModel}.
     */
    protected LinkedList<Presentation> activePresentations = new LinkedList<Presentation>();

    /**
     * Get the instance of the workspace.
     * 
     * @return the singleton instance of the workspace
     */
    public static WorkspaceBackend instance() {
        if (me == null) {
            me = new WorkspaceBackend();
        }
        return me;
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Initializes the workspace.
     */
    protected WorkspaceBackend() {
        activeModelIdx = -1;
        systems = new Vector<DESModel>();
        name = Hub.string("newModelName");
        // eventsModel = new EventsModel();
    }

    /**
     * Adds the given DESModel to the set of models in the workspace.
     * 
     * @param model the model to be added
     */
    public void addModel(DESModel model) {
        // Remove initial Untitled model if it is empty
        if (countAdd == 1 && getActiveModel() != null && !getActiveModel().needsSave()
                && getActiveModel().getAnnotation(AnnotationKeys.FILE) == null) {
            removeModel(getActiveModel().getName());
        }

        if (getModel(model.getName()) == model) {
            return;
        }

        if (getModel(model.getName()) != null) {
            int i = 1;
            while (getModel(model.getName() + " (" + i + ")") != null) {
                ++i;
            }
            model.setName(model.getName() + " (" + i + ")");
        }

        model.addSubscriber(this);

        Cursor cursor = Hub.getMainWindow().getCursor();
        Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        systems.add(model);
        fireModelCollectionChanged(new WorkspaceMessage(model.getName(), WorkspaceMessage.ADD));
        if (countAdd != 0) {
            dirty = true;
        }
        Hub.getMainWindow().setCursor(cursor);
        countAdd++;
    }

    /**
     * Returns the index in the list of open models of the model with the given
     * name.
     * 
     * @param name name of the model
     * @return the index of the model in the list of open models if the name matches
     *         an open model; -1 otherwise
     */
    protected int getModelIndex(String name) {
        for (int i = 0; i < systems.size(); ++i) {
            if (systems.elementAt(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds a model open in the workspace by name.
     * 
     * @param name the name of the model
     * @return the model if it is among the models open in the workspace;
     *         <code>null</code> otherwise
     */
    public DESModel getModel(String name) {
        int idx = getModelIndex(name);
        if (idx < 0) {
            return null;
        }
        return systems.elementAt(idx);
    }

    /**
     * Check if the workspace contains a model with the given name.
     * 
     * @param name the name of the model
     * @return <code>true</code> if the workspace contains a model with the given
     *         name; <code>false</code> otherwise
     */
    public boolean hasModel(String name) {
        return getModel(name) != null;
    }

    /**
     * Removes a model from the workspace. If the workspace does not contain a model
     * with the given name, the method does nothing. If the currently active model
     * is removed, the next model in the workspace becomes active, unless it is the
     * last model in the list in which case the previous model in the workspace
     * becomes active.
     * 
     * @param name the name of the model to remove
     */
    public void removeModel(String name) {
        DESModel m = this.getModel(name);

        if (m == null) {
            return;
        }

        if (m.needsSave() && m.getParentModel() == null) {
            if (!io.CommonFileActions.handleUnsavedModel(m)) {
                return;
            }
        }

        for (Iterator<DESModel> i = getModels(); i.hasNext();) {
            DESModel cm = i.next();
            if (cm.getParentModel() == m) {
                removeModel(cm.getName());
            }
        }

        m.removeSubscriber(this);

        int idx = getModelIndex(name);
        if (activeModelIdx == idx) {
            if (activeModelIdx + 1 < systems.size()) {
                setActiveModel(systems.elementAt(activeModelIdx + 1).getName());
            } else if (activeModelIdx - 1 >= 0) {
                setActiveModel(systems.elementAt(activeModelIdx - 1).getName());
            } else {
                setActiveModel(null);
            }
        }

        DESModel model = systems.get(idx);

        systems.removeElementAt(idx);

        if (activeModelIdx > idx) {
            activeModelIdx--;
        }

        fireModelCollectionChanged(new WorkspaceMessage(model.getName(), WorkspaceMessage.REMOVE));

        dirty = true;
    }

    // public FSAEventsModel getEventsModel() {
    // // TODO Auto-generated method stub
    // return null;
    // }

    /**
     * Returns the name of the model currently active in the workspace.
     * 
     * @return the name of the model currently active in the workspace; or an empty
     *         string if there is no model in the workspace
     */
    public String getActiveModelName() {
        if (activeModelIdx < 0) {
            return "";
        }
        return systems.elementAt(activeModelIdx).getName();
    }

    /**
     * Returns the model currently active in the workspace.
     * 
     * @return the model currently active in the workspace; or <code>null</code> if
     *         there is no model in the workspace
     */
    public DESModel getActiveModel() {
        if (activeModelIdx < 0) {
            return null;
        }
        return systems.elementAt(activeModelIdx);
    }

    /**
     * Returns the {@link UIDescriptor} with the UI elements for the currently
     * active model.
     * 
     * @return the {@link UIDescriptor} with the UI elements for the currently
     *         active model; or <code>null</code> if there is no model in the
     *         workspace
     */
    public UIDescriptor getActiveUID() {
        if (activeModelIdx < 0) {
            return null;
        }
        return activeUID;
    }

    /**
     * Dispose of all presentations currently in the workspace.
     */
    protected void releasePresentations() {
        for (Presentation p : activePresentations) {
            if (p instanceof LatexPresentation) {
                Hub.getLatexManager().removeLatexPresentation((LatexPresentation) p);
            }
            p.release();
        }
        if (activeUID != null) {
            if (activeUID.getStatusBar() instanceof LatexPresentation) {
                Hub.getLatexManager().removeLatexPresentation((LatexPresentation) activeUID.getStatusBar());
            }
            activeUID.getStatusBar().release();
        }
        activeUID = null;
        activePresentations = new LinkedList<Presentation>();
    }

    /**
     * Sets the active model to the {@link DESModel} with the given name. If the
     * workspace does not contain a model with the given name, the method does
     * nothing. If the name is <code>null</code>, no model becomes active.
     * 
     * @param name the name of the model to become active; or <code>null</code> if
     *             no model should become active
     */
    public void setActiveModel(String name) {
        if (name != null && getModelIndex(name) < 0) {
            return;
        }
        fireAboutToRearrangeWorkspace();
        releasePresentations();
        if (name == null) {
            activeModelIdx = -1;
        } else {
            activeModelIdx = getModelIndex(name);

            Toolset ts = ToolsetManager.instance()
                    .getToolset(systems.elementAt(activeModelIdx).getModelType().getMainPerspective());

            activeUID = ts.getUIElements(systems.elementAt(activeModelIdx));

            activePresentations = new LinkedList<Presentation>();
            Presentation[] ps = activeUID.getMainPanePresentations();

            for (int i = 0; i < ps.length; ++i) {
                activePresentations.add(ps[i]);
            }

            ps = activeUID.getRightPanePresentations();
            for (int i = 0; i < ps.length; ++i) {
                activePresentations.add(ps[i]);
            }
            for (Presentation p : activePresentations) {
                if (p instanceof LatexPresentation) {
                    Hub.getLatexManager().addLatexPresentation((LatexPresentation) p);
                }
            }
            if (activeUID.getStatusBar() instanceof LatexPresentation) {
                Hub.getLatexManager().addLatexPresentation((LatexPresentation) activeUID.getStatusBar());
            }
        }

        fireModelSwitched(new WorkspaceMessage(Hub.getWorkspace().getActiveModelName(), WorkspaceMessage.MODIFY));
    }

    /**
     * Returns an iterator of all {@link DESModel}s in this workspace.
     * 
     * @return an iterator of all {@link DESModel}s in this workspace
     */
    public Iterator<DESModel> getModels() {
        ArrayList<DESModel> g = new ArrayList<DESModel>();
        Iterator<DESModel> iter = systems.iterator();
        while (iter.hasNext()) {
            g.add(iter.next());
        }
        return g.iterator();
    }

    /**
     * Returns if the workspace is dirty (it has been changed since the last save).
     * 
     * @return the dirty flag of the workspace
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Sets the dirty flag of the workspace (i.e., whether it has been changed since
     * the last save).
     * 
     * @param state the new dirty flag of the workspace
     */
    public void setDirty(boolean state) {
        dirty = state;
    }

    /**
     * Returns the name of the workspace.
     * 
     * @return the name of the workspace
     */
    public String getName() {
        return name;
    }

    /**
     * Check if there are models open in the workspace.
     * 
     * @return <code>true</code> if there is at least one model in the workspace;
     *         <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return systems.isEmpty();
    }

    /**
     * Replaces the current workspace with the workspace given in the descriptor.
     * 
     * @param wd the descriptor of the replacement workspace
     */
    public void replaceWorkspace(WorkspaceDescriptor wd) {
        // deactivate all models
        setActiveModel(null);

        Iterator<DESModel> iter = getModels();
        while (iter.hasNext()) {
            removeModel(iter.next().getName());
            iter = getModels();
        }

        myFile = wd.getFile();
        name = myFile.getName();
        Hub.getMainWindow().setTitle(Hub.string("IDES_SHORT_NAME") + " " + Hub.string("IDES_VER") + ": " + name);
        Vector<String[]> files = wd.getModels();
        DESModel[] loadedModels = new DESModel[files.size()];
        int idx = wd.getSelectedModel();
        for (int i = 0; i < files.size(); ++i) {
            DESModel model = null;
            String[] info = files.elementAt(i);
            if (!WorkspaceDescriptor.FILE_ID.equals(info[0])) {
                continue;
            }
            File file = new File(info[1]);
            try {
                model = IOCoordinator.getInstance().load(file);
            } catch (IOException e) {
                if (e instanceof FileLoadException && ((FileLoadException) e).getPartialModel() != null) {
                    model = ((FileLoadException) e).getPartialModel();
                    Hub.displayAlert(Hub.string("errorsParsingXMLFileL1") + file.getName() + "\n"
                            + GeneralUtils.truncateMessage(e.getMessage()) + "\n"
                            + Hub.string("errorsParsingXMLFileL2"));
                } else {
                    Hub.displayAlert(Hub.string("errorsParsingXMLFileL1") + file.getName() + "\n"
                            + GeneralUtils.truncateMessage(e.getMessage()) + "\n" + Hub.string("errorsParsingXMLfail"));
                }
            }
            if (model != null) {
                model.setName(ParsingToolbox.removeFileType(file.getName()));
                model.setAnnotation(AnnotationKeys.FILE, file);
                loadedModels[i] = model;
            }
        }
        boolean failLoadingChildren = false;
        for (int i = 0; i < files.size(); ++i) {
            String[] info = files.elementAt(i);
            if (!WorkspaceDescriptor.CHILD_ID.equals(info[0])) {
                continue;
            }
            DESModel parent = null;
            for (DESModel m : loadedModels) {
                if (m != null && m.getName().equals(info[1])) {
                    parent = m;
                    break;
                }
            }
            if (parent == null || !(parent instanceof ParentModel)) {
                failLoadingChildren = true;
                continue;
            }
            DESModel model = ((ParentModel) parent).getChildModel(info[2]);
            if (model != null) {
                loadedModels[i] = model;
            } else {
                failLoadingChildren = true;
            }
        }
        if (failLoadingChildren) {
            Hub.displayAlert(Hub.string("cantLoadAllChildren1") + "\n" + Hub.string("cantLoadAllChildren2"));
        }
        String selectedModel = null;
        for (int i = 0; i < loadedModels.length; ++i) {
            DESModel m = loadedModels[i];
            if (m != null) {
                addModel(m);
                if (selectedModel == null || i <= idx) {
                    selectedModel = m.getName();
                }
            }
        }
        fireModelCollectionChanged(new WorkspaceMessage("", WorkspaceMessage.MODIFY));

        setActiveModel(selectedModel);
        setDirty(false);
    }

    /**
     * Returns a descriptor of the current workspace.
     * 
     * @return descriptor of the current workspace
     * @throws IncompleteWorkspaceDescriptorException when the descriptor can't be
     *                                                created due to new unsaved
     *                                                models
     */
    public WorkspaceDescriptor getDescriptor() throws IncompleteWorkspaceDescriptorException {
        // Christian: If activated, the following code will ignore aptemptives
        // to save the workspace,
        // when the only model being showed is the "default" model which is
        // opened when IDES is loaded
        // for the first time *and* this model was never modified.
        // if(countAdd==1 && getActiveLayoutShell()!=null &&
        // !getActiveModel().needsSave())
        // {
        // return null;
        // }
        WorkspaceDescriptor wd = new WorkspaceDescriptor(myFile);
        Vector<DESModel> unsavedModels = new Vector<DESModel>();
        Iterator<DESModel> i = getModels();
        while (i.hasNext()) {
            DESModel a = i.next();
            if (!a.hasAnnotation(AnnotationKeys.FILE) && a.getParentModel() == null) {
                unsavedModels.add(a);
            }
        }

        if (!unsavedModels.isEmpty()) {
            throw new IncompleteWorkspaceDescriptorException(unsavedModels);
        }

        for (int counter = 0; counter < systems.size(); ++counter) {
            DESModel a = systems.elementAt(counter);
            if (a.getParentModel() == null) {
                wd.insertModel(((File) a.getAnnotation(AnnotationKeys.FILE)).getName(), counter);
            } else {
                wd.insertModel(a.getParentModel().getName(), a.getParentModel().getChildModelId(a), counter);
            }
            if (a.getName().equals(getActiveModelName())) {
                wd.setSelectedModel(counter);
            }
        }
        return wd;
    }

    /**
     * Sets the file for the current workspace. This file will be used when creating
     * descriptors of the workspace.
     * 
     * @param f the new file for the workspace
     * @see #getDescriptor()
     */
    public void setFile(File f) {
        myFile = f;
        name = myFile.getName();
    }

    /**
     * Get the number of models in the workspace
     * 
     * @return number of models in the workspace
     */
    public int size() {
        return systems.size();
    }

    public <T> Collection<T> getModelsOfType(Class<T> type) {
        Vector<T> models = new Vector<T>();
        for (DESModel m : systems) {
            Class<?>[] ifaces = m.getModelType().getModelPerspectives();
            for (int i = 0; i < ifaces.length; ++i) {
                if (ifaces[i].equals(type)) {
                    models.add(type.cast(m));
                    break;
                }
            }
        }
        return models;
    }

    /**
     * Returns the set of presentations used as the UI for the currently active
     * model.
     * 
     * @return the set of presentations used as the UI for the currently active
     *         model
     */
    public Collection<Presentation> getPresentations() {
        return activePresentations;
    }

    /**
     * Selects the presentation of the given type from the set of all presentations
     * used as the UI for the currently active model.
     * 
     * @param      <T> the type of presentation to be selected
     * @param type the class type of presentation to be selected
     * @return the subset of presentations of the given type, from all presentations
     *         used as the UI for the currently active model
     */
    public <T> Collection<T> getPresentationsOfType(Class<T> type) {
        Vector<T> ps = new Vector<T>();
        for (Presentation p : activePresentations) {
            if (type.isInstance(p)) {
                ps.add(type.cast(p));
            }
        }
        return ps;
    }

    public void saveStatusChanged(DESModelMessage message) {
    }

    public void modelNameChanged(DESModelMessage message) {
        setDirty(true);
        DESModel model = message.getSource();
        DESModel duplicate = null;
        for (DESModel m : systems) {
            if (m != model && m.getName().equals(model.getName())) {
                duplicate = m;
                break;
            }
        }
        if (duplicate != null) {
            int i = 1;
            while (getModel(model.getName() + " (" + i + ")") != null) {
                ++i;
            }
            duplicate.setName(model.getName() + " (" + i + ")");
        }
    }

    /**
     * List of subscribers to be notified of change events
     */
    private ArrayList<WorkspaceSubscriber> subscribers = new ArrayList<WorkspaceSubscriber>();

    /**
     * Attaches the given subscriber to this publisher. The given subscriber will
     * receive notifications of changes from this publisher.
     * 
     * @param subscriber subscriber to be attached
     */
    public void addSubscriber(WorkspaceSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * Removes the given subscriber to this publisher. The given subscriber will no
     * longer receive notifications of changes from this publisher.
     * 
     * @param subscriber subscriber to be removed
     */
    public void removeSubscriber(WorkspaceSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * Sends a notification to subscribers that a repaint is required due to changes
     * to the display options such as Zoom, or toggling show grid, LaTeX rendering,
     * UniformNode size etc.
     */
    public void fireRepaintRequired() {
        for (Presentation p : activePresentations) {
            p.forceRepaint();
        }
        for (WorkspaceSubscriber s : subscribers.toArray(new WorkspaceSubscriber[] {})) {
            s.repaintRequired();
        }
    }

    /**
     * Sends notification to subscribers when a DES model is created or opened
     * (added), closed (removed) etc.
     * 
     * @param message
     */
    protected void fireModelCollectionChanged(WorkspaceMessage message) {
        for (WorkspaceSubscriber s : subscribers.toArray(new WorkspaceSubscriber[] {})) {
            s.modelCollectionChanged(message);
        }
    }

    /**
     * Sends notification to subscribers that a new model has become the active
     * model in the workspace.
     * 
     * @param message
     */
    protected void fireModelSwitched(WorkspaceMessage message) {
        for (WorkspaceSubscriber s : subscribers.toArray(new WorkspaceSubscriber[] {})) {
            s.modelSwitched(message);
        }
    }

    /**
     * Sends notification to subscribers that the the layout of the workspace is
     * about to change (e.g., a new model is about to become the active model).
     */
    public void fireAboutToRearrangeWorkspace() {
        for (WorkspaceSubscriber s : subscribers.toArray(new WorkspaceSubscriber[] {})) {
            s.aboutToRearrangeWorkspace();
        }
    }

    public void setActivePresentation(String name) {
        for (Presentation p : getActiveUID().getMainPanePresentations()) {
            if (p.getName().equals(name)) {
                HubBackend.getTabbedWindow().activateMainTab(name);
                return;
            }
        }
        for (Presentation p : getActiveUID().getRightPanePresentations()) {
            if (p.getName().equals(name)) {
                HubBackend.getTabbedWindow().activateRightTab(name);
                return;
            }
        }
        for (Presentation p : getActiveUID().getLeftPanePresentations()) {
            if (p.getName().equals(name)) {
                HubBackend.getTabbedWindow().activateLeftTab(name);
                return;
            }
        }
    }
}