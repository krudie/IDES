/**
 * 
 */
package observer;

import java.util.ArrayList;

/**
 * @author helen bretzke
 *
 */
public class FSMGraphPublisher {

	private ArrayList<FSMGraphSubscriber> subscribers;
	
	public FSMGraphPublisher()
	{
		subscribers = new ArrayList<FSMGraphSubscriber>(); 
	}
	
	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(FSMGraphSubscriber subscriber) {
		subscribers.add(subscriber);		
	}
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(FSMGraphSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	/**
	 * Notifies all subscribers that there has been a change to an element of 
	 * the graph publisher.
	 * 
	 * @param message
	 */
	public void fireFSMGraphChanged(FSMGraphMessage message)
	{
		for(FSMGraphSubscriber s : subscribers)
		{
			s.fsmGraphChanged(message);
		}		
	}

	/**
	 * Notifies all subscribers that there has been a change to the elements  
	 * currently selected in the graph publisher.
	 * 
	 * @param message
	 */
	public void fireFSMGraphSelectionChanged(FSMGraphMessage message)
	{
		for(FSMGraphSubscriber s : subscribers)
		{
			s.fsmGraphSelectionChanged(message);
		}
		
	}
		
}
