package model.fsa;

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
public class FSAModel {

	private LinkedList<FSAObserver> observers;
	
	public FSAModel() {
		observers = new LinkedList<FSAObserver>();
	}
	
	/**
	 * Notifies (calls update on) all of my observers.
	 */	
	public void notifyAllObservers() {
		Iterator iter = observers.iterator();		
		while(iter.hasNext()){
			((FSAObserver)iter.next()).update();
		}		
	}
	
	/**
	 * Notifies (calls update on) all of my observers except the given one (assuming that it 
	 * was the one that sent the notification.)
	 */ 
	public void notifyAllBut(FSAObserver observer) {
		Iterator iter = observers.iterator();
		FSAObserver current;
		while(iter.hasNext()){
			current = (FSAObserver)iter.next();
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
	public void attach(FSAObserver observer) {
		observers.add(observer);		
	}
	
	/**
	 * Detaches the given observer to this model object.
	 * This observer will no longer receive notifications of changes to this model.
	 * 
	 * @param observer
	 */	
	public void detach(FSAObserver observer) {
		observers.remove(observer);
	}
}
