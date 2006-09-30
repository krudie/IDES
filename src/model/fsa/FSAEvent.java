package model.fsa;

import model.DESElement;

/**
 * Defines an event in a finite state automaton.
 * 
 * @author Helen Bretzke
 */
public interface FSAEvent extends DESElement {
	
	/**
     * Returns the symbol that represents this event in the (local?) alphabet.
     *      
     * @return the symbol that represents this event
     */
	public abstract String getSymbol();
	
	/**
	 * Sets the symbol for this event to <code>symbol</code>.
	 * 
	 * @param the symbol to set
	 */
	public abstract void setSymbol(String symbol);
	
	/**
	 * Returns true iff this event's controllable property
	 * is set to true. 
	 * 
	 * @return true iff this event is controllable
	 */
	public abstract boolean isControllable();
	
	/**
	 * Sets this event's controllable property to <code>b</code>.
	 *  
	 * @param b the value to set
	 */
	public abstract void setControllable(boolean b);
	
	/**
	 * Returns true iff this event's observable property
	 * is set to true. 
	 * 
	 * @return true iff this event is observable
	 */
	public abstract boolean isObservable();
	
	/**
	 * Sets this event's observable property to <code>b</code>.
	 *  
	 * @param b the value to set
	 */
	public abstract void setObservable(boolean b);
	
	/**
	 * Returns true iff <code>o</code> is of type FSAEvent
	 * and has the same properties as this FSAEvent. 
	 * 
	 * @param o another object
	 * @return true iff <code>o</code> is of type FSAEvent
	 * 	and has the same properties as this FSAEvent.
	 */
	public abstract boolean equals(Object o);

}