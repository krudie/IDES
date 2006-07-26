/**
 * 
 */
package observer;

import java.util.ArrayList;

/**
 * @author helen
 *
 */
public class FSMPublisher {

	private ArrayList<FSMSubscriber> subscribers;
		
	public FSMPublisher() {
		super();
		subscribers = new ArrayList<FSMSubscriber>();
	}

	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(FSMSubscriber subscriber) {
		subscribers.add(subscriber);		
	}
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(FSMSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	public void fireFSMStructureChanged(FSMMessage message){
		for(FSMSubscriber s : subscribers)
		{
			s.fsmStructureChanged(message);
		}
	}
	
	public void fireFSMEventSetChanged(FSMMessage message){
		for(FSMSubscriber s : subscribers)
		{
			s.fsmEventSetChanged(message);
		}			
	}
	
}
