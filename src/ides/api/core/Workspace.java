package ides.api.core;

import java.util.Collection;
import java.util.Iterator;

import ides.api.plugin.model.DESModel;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.UIDescriptor;

/**
 * Interface for the main manager (workspace) of DESModels in IDES.
 * 
 * @author Lenko Grigorov
 */
public interface Workspace {

    /**
     * Adds the given DESModel to the set of models in the workspace.
     * 
     * @param model the model to be added
     */
    public void addModel(DESModel model);

    /**
     * Finds a model open in the workspace by name.
     * 
     * @param name the name of the model
     * @return the model if it is among the models open in the workspace;
     *         <code>null</code> otherwise
     */
    public DESModel getModel(String name);

    /**
     * Check if the workspace contains a model with the given name.
     * 
     * @param name the name of the model
     * @return <code>true</code> if the workspace contains a model with the given
     *         name; <code>false</code> otherwise
     */
    public boolean hasModel(String name);

    /**
     * Removes a model from the workspace. If the workspace does not contain a model
     * with the given name, the method does nothing. If the currently active model
     * is removed, the next model in the workspace becomes active, unless it is the
     * last model in the list in which case the previous model in the workspace
     * becomes active.
     * 
     * @param name the name of the model to remove
     */
    public void removeModel(String name);

    /**
     * Returns the name of the model currently active in the workspace.
     * 
     * @return the name of the model currently active in the workspace; or an empty
     *         string if there is no model in the workspace
     */
    public String getActiveModelName();

    /**
     * Returns the model currently active in the workspace.
     * 
     * @return the model currently active in the workspace; or <code>null</code> if
     *         there is no model in the workspace
     */
    public DESModel getActiveModel();

    /**
     * Returns the {@link UIDescriptor} with the UI elements for the currently
     * active model.
     * 
     * @return the {@link UIDescriptor} with the UI elements for the currently
     *         active model; or <code>null</code> if there is no model in the
     *         workspace
     */
    public UIDescriptor getActiveUID();

    /**
     * Sets the active model to the {@link DESModel} with the given name. If the
     * workspace does not contain a model with the given name, the method does
     * nothing. If the name is <code>null</code>, no model becomes active.
     * 
     * @param name the name of the model to become active; or <code>null</code> if
     *             no model should become active
     */
    public void setActiveModel(String name);

    /**
     * Makes the specified presentation active in the user interface. If there is no
     * presentation with the given name, does nothing.
     * 
     * @param name the name of the presentation to be made active
     */
    public void setActivePresentation(String name);

    /**
     * Returns an iterator of all {@link DESModel}s in this workspace.
     * 
     * @return an iterator of all {@link DESModel}s in this workspace
     */
    public Iterator<DESModel> getModels();

    /**
     * Returns if the workspace is dirty (it has been changed since the last save).
     * 
     * @return the dirty flag of the workspace
     */
    public boolean isDirty();

    /**
     * Sets the dirty flag of the workspace (i.e., whether it has been changed since
     * the last save).
     * 
     * @param state the new dirty flag of the workspace
     */
    public void setDirty(boolean state);

    /**
     * Returns the name of the workspace.
     * 
     * @return the name of the workspace
     */
    public String getName();

    /**
     * Check if there are models open in the workspace.
     * 
     * @return <code>true</code> if there is at least one model in the workspace;
     *         <code>false</code> otherwise
     */
    public boolean isEmpty();

    /**
     * Get the number of models in the workspace
     * 
     * @return number of models in the workspace
     */
    public int size();

    /**
     * Returns all models of the given type opened in the workspace.
     * 
     * @param      <T> the type of model
     * @param type the class type of model
     * @return all models of the given type opened in the workspace
     */
    public <T> Collection<T> getModelsOfType(Class<T> type);

    /**
     * Returns the set of presentations used as the UI for the currently active
     * model.
     * 
     * @return the set of presentations used as the UI for the currently active
     *         model
     */
    public Collection<Presentation> getPresentations();

    /**
     * Selects the presentation of the given type from the set of all presentations
     * used as the UI for the currently active model.
     * 
     * @param      <T> the type of presentation to be selected
     * @param type the class type of presentation to be selected
     * @return the subset of presentations of the given type, from all presentations
     *         used as the UI for the currently active model
     */
    public <T> Collection<T> getPresentationsOfType(Class<T> type);

    /**
     * Attaches the given subscriber to this publisher. The given subscriber will
     * receive notifications of changes from this publisher.
     * 
     * @param subscriber subscriber to be attached
     */
    public void addSubscriber(WorkspaceSubscriber subscriber);

    /**
     * Removes the given subscriber to this publisher. The given subscriber will no
     * longer receive notifications of changes from this publisher.
     * 
     * @param subscriber subscriber to be removed
     */
    public void removeSubscriber(WorkspaceSubscriber subscriber);

    /**
     * Sends a notification to subscribers that a repaint is required due to changes
     * to the display options such as Zoom, or toggling show grid, LaTeX rendering,
     * UniformNode size etc.
     */
    public void fireRepaintRequired();

    /**
     * Sends notification to subscribers that the the layout of the workspace is
     * about to change (e.g., a new model is about to become the active model).
     */
    public void fireAboutToRearrangeWorkspace();
}
