package ides.api.plugin.model;

/**
 * A publisher of {@link DESModelMessage}s.
 * 
 * @author Lenko Grigorov
 */
public interface DESModelPublisher {
    /**
     * Attaches the given subscriber to this publisher. The given subscriber will
     * receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void addSubscriber(DESModelSubscriber subscriber);

    /**
     * Removes the given subscriber to this publisher. The given subscriber will no
     * longer receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void removeSubscriber(DESModelSubscriber subscriber);

    /**
     * Returns all current subscribers to this publisher.
     * 
     * @return all current subscribers to this publisher
     */
    public DESModelSubscriber[] getDESModelSubscribers();

}
