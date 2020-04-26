/**
 * 
 */
package ides.api.plugin.model;

import ides.api.core.Annotable;
import presentation.fsa.FSAGraph;

/**
 * The interface for all models which IDES will handle.
 * 
 * @author Lenko Grigorov
 */
public interface DESModel extends Annotable, DESModelPublisher {
    /**
     * Sets the display name of the model.
     * 
     * @param name new display name for the model
     */
    public void setName(String name);

    /**
     * Returns the display name of the model.
     * 
     * @return the display name of the model
     */
    public abstract String getName();

    /**
     * Returns the model type.
     * 
     * @return the model type
     */
    public DESModelType getModelType();

    /**
     * Notifies the model that some associated metadata has been changed.
     */
    public void metadataChanged();

    /**
     * Notifies the model that it has been saved.
     */
    public void modelSaved();

    /**
     * Returns <code>true</code> if the information about the model needs to be
     * saved; <code>false</code> otherwise. This method should also consider changes
     * to the associated metadata (see {@link #metadataChanged()}). E.g., if the
     * user moves a node of a {@link FSAGraph}, this method will return
     * <code>true</code> since the updated position of the node has to be saved.
     * 
     * @return <code>true</code> if the information maintained by the model needs to
     *         be saved; <code>false</code> otherwise
     */
    public boolean needsSave();

    /**
     * Returns the parent model, if any, of the model.
     * <p>
     * The parent model of a model is the model which is responsible for the
     * maintenance of the child model, e.g., keeping track of changes, saving and
     * loading, etc. If a model has a parent model, IDES will treat the child model
     * as a temporary "view" into the parent model (e.g., it will not ask to save
     * the model on closing, it will not allow renaming of the model, etc.)
     * 
     * @return the parent model of the model; <code>null</code> if there is no
     *         parent
     */
    public ParentModel getParentModel();

    /**
     * Sets the parent model of the model.
     * 
     * @param model the parent model for the model
     * @see #getParentModel()
     */
    public void setParentModel(ParentModel model);

    /**
     * Obtains the set of events in the model. If there are no events in the model,
     * returns an empty set.
     * 
     * @return the set of events in the model.
     */
    public DESEventSet getEventSet();
}
