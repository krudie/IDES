package model;

import java.util.Iterator;
import java.util.LinkedList;



/**
 * State participant of the Observer design pattern.
 * Registers and notifies all observers of changes to shared data.
 * FSA data models requiring multiple concurrent views should extend this class.
 * 
 * @author helen bretzke
 *
 */
public class Publisher {

	private LinkedList<Subscriber> subscribers;
	
	public Publisher() {
		subscribers = new LinkedList<Subscriber>();
	}
	
	/**
	 * Notifies (calls update on) all of my observers.
	 */	
	public void notifyAllSubscribers() {
		Iterator iter = subscribers.iterator();		
		while(iter.hasNext()){
			((Subscriber)iter.next()).update();
		}		
	}
	
	/**
	 * Notifies (calls update on) all of my observers except the given one (assuming that it 
	 * was the one that sent the notification.)
	 */ 
	public void notifyAllBut(Subscriber observer) {
		Iterator iter = subscribers.iterator();
		Subscriber current;
		while(iter.hasNext()){
			current = (Subscriber)iter.next();
			if(current != observer){
				current.update();
			}
		}		
	}

	/**
	 * Attaches the given observer to this model object.
	 * This observer will receive notifications of changes to this model.
	 * 
	 * @param observer
	 */
	public void attach(Subscriber observer) {
		subscribers.add(observer);		
	}
	
	/**
	 * Detaches the given observer to this model object.
	 * This observer will no longer receive notifications of changes to this model.
	 * 
	 * @param observer
	 */	
	public void detach(Subscriber observer) {
		subscribers.remove(observer);
	}
}
