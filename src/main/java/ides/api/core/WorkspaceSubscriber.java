/**
 * 
 */
package ides.api.core;

/**
 * Implemented by classes that wish to receive change notifications from the
 * {@link Workspace}.
 * 
 * @author Helen Bretzke
 */
public interface WorkspaceSubscriber {

    /**
     * Notifies this subscriber that a model collection change (a DES model is
     * created or opened (added) or closed (removed), etc.) has occurred in the
     * workspace.
     * 
     * @param message details of the change notification
     */
    public void modelCollectionChanged(WorkspaceMessage message);

    /**
     * Notifies this subscriber that a change requiring a repaint has occurred in
     * the workspace.
     */
    public void repaintRequired();

    /**
     * Notifies this subscriber that a new model has become the active model in the
     * workspace.
     * 
     * @param message details of the change notification
     */
    public void modelSwitched(WorkspaceMessage message);

    /**
     * Notifies this subscriber that the layout of the workspace is about to change
     * (e.g., a new model is about to become the active model).
     */
    public void aboutToRearrangeWorkspace();
}
