/**
 * 
 */
package observer;

import java.util.ArrayList;

/**
 * @author helen
 *
 */
public class FSAPublisher {

	private ArrayList<FSASubscriber> subscribers;
		
	public FSAPublisher() {
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
	
	public void fireFSMStructureChanged(FSAMessage message){
		for(FSASubscriber s : subscribers)
		{
			s.fsmStructureChanged(message);
		}
	}	
	
	public void fireFSMEventSetChanged(FSAMessage message){
		for(FSASubscriber s : subscribers)
		{
			s.fsmEventSetChanged(message);
		}			
	}	
}
