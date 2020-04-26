package ides.api.model.supeventset;

/**
 * A publisher of SupervisoryEventSet messages.
 * 
 * @author Valerie Sugarman
 */
public interface SupEventSetPublisher {

    /**
     * Attaches the given subscriber to this publisher. The given subscriber will
     * receive notifications of changes from this publisher.
     * 
     * @param subscriber the subscriber to be attached
     */
    public abstract void addSubscriber(SupEventSetSubscriber subscriber);

    /**
     * Triggers a notification to all subscribers that the event set has changed.
     * 
     * @param message message with additional information about the change.
     */
    public abstract void fireSupEventSetChanged(SupEventSetMessage message);

    /**
     * Returns all current subscribers to this publisher.
     * 
     * @return all current subscribers to this publisher.
     */
    public abstract SupEventSetSubscriber[] getSupEventSetSubscribers();

    /**
     * Removes the given subscriber to this publisher. The given subscriber will no
     * longer receive notifications of changes from this publisher.
     * 
     * @param subscriber the subscriber to be removed
     */
    public abstract void removeSubscriber(SupEventSetSubscriber subscriber);
}
