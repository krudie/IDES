/**
 * 
 */
package model.fsa;

import java.util.ArrayList;

/**
 * 
 * 
 * @author Helen Bretzke
 */
public class FSAPublisherAdaptor implements FSAPublisher {

	private ArrayList<FSASubscriber> subscribers;
		
	public FSAPublisherAdaptor() {
		super();
		subscribers = new ArrayList<FSASubscriber>();
	}

	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(FSASubscriber subscriber) {
		subscribers.add(subscriber);		
	}
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(FSASubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	public void fireFSAStructureChanged(FSAMessage message){
		for(FSASubscriber s : subscribers)
		{
			s.fsaStructureChanged(message);
		}
	}	
	
	public void fireFSAEventSetChanged(FSAMessage message) {
		for(FSASubscriber s : subscribers)
		{
			s.fsaEventSetChanged(message);
		}			
	}
	
	public void fireFSASaved() {
		for(FSASubscriber s : subscribers) {
			s.fsaSaved();
		}	
	}
}
