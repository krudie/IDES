package ides.api.plugin.model;

/**
 * Interface for {@link DESModel}s which, in turn, contain other
 * {@link DESModel}s as child models. An example would be a hierarchical system.
 * 
 * @author Lenko Grigorov
 */
public interface ParentModel extends DESModel {
    /**
     * Returns the ID of a child model which can be used to obtain the child model.
     * 
     * @param child a child model of the parent model
     * @return the ID of the child model
     * @throws IllegalArgumentException when the model in the argument is not a
     *                                  child of this model
     * @see #getChildModel(String)
     */
    public String getChildModelId(DESModel child) throws IllegalArgumentException;

    /**
     * Returns the child model identified by the given ID string.
     * 
     * @param id the ID of the child model
     * @return the child model identified by the given ID string; or
     *         <code>null</code> if this parent model does not contain a child model
     *         with the given ID
     */
    public DESModel getChildModel(String id);
}