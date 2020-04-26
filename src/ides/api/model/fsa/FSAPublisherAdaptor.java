/**
 * 
 */
package ides.api.model.fsa;

import java.util.ArrayList;

/**
 * Implementation of the {@link FSAPublisher} interface.
 * 
 * @author Helen Bretzke
 */
public abstract class FSAPublisherAdaptor implements FSAPublisher {

    private ArrayList<FSASubscriber> subscribers;

    /**
     * Initialize the adaptor.
     */
    public FSAPublisherAdaptor() {
        super();
        subscribers = new ArrayList<FSASubscriber>();
    }

    /**
     * Attaches the given subscriber to this publisher. The given subscriber will
     * receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void addSubscriber(FSASubscriber subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * Removes the given subscriber to this publisher. The given subscriber will no
     * longer receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void removeSubscriber(FSASubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * Returns all current subscribers to this publisher.
     * 
     * @return all current subscribers to this publisher
     */
    public FSASubscriber[] getFSASubscribers() {
        return subscribers.toArray(new FSASubscriber[] {});
    }

    public void fireFSAStructureChanged(FSAMessage message) {
        for (FSASubscriber s : getFSASubscribers()) {
            // sets the dirty flag
            s.fsaStructureChanged(message);
        }
    }

    public void fireFSAEventSetChanged(FSAMessage message) {
        for (FSASubscriber s : getFSASubscribers()) {
            // sets the dirty flag
            s.fsaEventSetChanged(message);
        }
    }

}
