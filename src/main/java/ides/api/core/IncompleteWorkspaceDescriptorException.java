package ides.api.core;

import java.util.Collection;
import java.util.HashSet;

import ides.api.plugin.model.DESModel;
import main.WorkspaceBackend;
import main.WorkspaceDescriptor;

/**
 * The data needed to construct a {@link WorkspaceDescriptor} is incomplete.
 * 
 * @author Lenko Grigorov
 */
public class IncompleteWorkspaceDescriptorException extends Exception {
    private static final long serialVersionUID = 3991559737853629043L;

    /**
     * A list of the models which prevented the creation of the workspace descriptor
     * (new models which have never been saved).
     * 
     * @see WorkspaceBackend#getDescriptor()
     */
    protected Collection<DESModel> neverSavedModels = new HashSet<DESModel>();

    /**
     * Creates an expection with an empty list of models.
     */
    public IncompleteWorkspaceDescriptorException() {
    }

    /**
     * Creates an exception which contains a list of the models which prevented the
     * creation of the workspace descriptor.
     * 
     * @param models the list of models which prevented the creation of the
     *               workspace descriptor
     */
    public IncompleteWorkspaceDescriptorException(Collection<DESModel> models) {
        neverSavedModels.addAll(models);
    }

    /**
     * Returns a list of the models which prevented the creation of the workspace
     * descriptor (e.g., new models in the workspace which have never been saved and
     * their file location is unavailable).
     * 
     * @return a list of the models which prevented the creation of the workspace
     *         descriptor
     */
    public Collection<DESModel> getNeverSavedModels() {
        return neverSavedModels;
    }
}
