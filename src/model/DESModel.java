package model;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * State participant of the Observer design pattern.
 * Provides data and operations for notifying all observers of changes
 * to the data.
 * Data models requiring multiple concurrent views should extend this class.
 * 
 * @author helen bretzke
 *
 */
public class DESModel {

	private LinkedList<DESObserver> observers;
	
	public DESModel() {
		observers = new LinkedList<DESObserver>();
	}
	
	/**
	 * Notifies (calls update on) all of my observers.
	 */	
	public void notifyAllObservers() {
		Iterator iter = observers.iterator();		
		while(iter.hasNext()){
			((DESObserver)iter.next()).update();
		}		
	}
	
	/**
	 * Notifies (calls update on) all of my observers except the given one (assuming that it 
	 * was the one that sent the notification.)
	 */ 
	public void notifyAllBut(DESObserver observer) {
		Iterator iter = observers.iterator();
		DESObserver current;
		while(iter.hasNext()){
			current = (DESObserver)iter.next();
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
	public void attach(DESObserver observer) {
		observers.add(observer);		
	}
	
	/**
	 * Detaches the given observer to this model object.
	 * This observer will no longer receive notifications of changes to this model.
	 * 
	 * @param observer
	 */	
	public void detach(DESObserver observer) {
		observers.remove(observer);
	}
}
