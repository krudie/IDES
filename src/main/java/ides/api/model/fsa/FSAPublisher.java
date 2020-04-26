package ides.api.model.fsa;

/**
 * A publisher of FSA messages.
 * 
 * @author Lenko Grigorov
 */
public interface FSAPublisher {

    /**
     * Attaches the given subscriber to this publisher. The given subscriber will
     * receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void addSubscriber(FSASubscriber subscriber);

    /**
     * Removes the given subscriber to this publisher. The given subscriber will no
     * longer receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void removeSubscriber(FSASubscriber subscriber);

    /**
     * Returns all current subscribers to this publisher.
     * 
     * @return all current subscribers to this publisher
     */
    public FSASubscriber[] getFSASubscribers();

    /**
     * Triggers a notification to all subscribers that the structure of the FSA
     * model has changed.
     * 
     * @param message message with additional info about the change
     * @see FSAMessage
     */
    public void fireFSAStructureChanged(FSAMessage message);

    /**
     * Triggers a notification to all subscribers that the event set of the FSA
     * model has changed.
     * 
     * @param message message with additional info about the change
     * @see FSAMessage
     */
    public void fireFSAEventSetChanged(FSAMessage message);

    // /**
    // * Triggers a notification to all subscribers that the
    // * FSA model was saved to disk.
    // */
    // public void fireFSASaved();

    // public FSAModel getFSAModel();
}
