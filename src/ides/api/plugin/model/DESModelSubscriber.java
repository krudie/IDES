package ides.api.plugin.model;

/**
 * Subscriber to messages from a {@link DESModel}.
 * 
 * @author Lenko Grigorov
 */
public interface DESModelSubscriber {
    /**
     * The "saved" (or dirty) status of the model changed (i.e., the model had
     * unsaved modifications and they were saved, or there were no unsaved
     * modifications and now there is an unsaved modification).
     * 
     * @param message the message describing how the "saved" status changed
     */
    public void saveStatusChanged(DESModelMessage message);

    /**
     * The name of the model was modified.
     * 
     * @param message the message for the name change
     */
    public void modelNameChanged(DESModelMessage message);
}
