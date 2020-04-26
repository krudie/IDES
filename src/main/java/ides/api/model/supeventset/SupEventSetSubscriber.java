package ides.api.model.supeventset;

/**
 * A subscriber to messages sent by {@link SupervisoryEventSet} models.
 * 
 * @author Valerie Sugarman
 */
public interface SupEventSetSubscriber {

    /**
     * The event set changed.
     * 
     * @param message description of the modification.
     */
    public abstract void supEventSetChanged(SupEventSetMessage message);
}
