package model.fsa;

import java.util.ListIterator;

import model.DESElement;

/**
 * The interface defining a representation of a state in a
 * finite state automaton. 
 * 
 * @author Helen Bretzke
 */
public interface FSAState extends DESElement {

	/**
	 * Adds a transition that originates from this state. 
	 * 
	 * @param t the transition to be added
	 */
	public abstract void addSourceTransition(FSATransition t);

	/**
	 * Removes a transition that originates from this state. 
	 *  
	 * @param t the transition to be removed
	 */
	public abstract void removeSourceTransition(FSATransition t);

	/**
	 * Returns an iterator for the transitions originating from this state.
	 * 
	 * @return a source transition iterator
	 */
	public abstract ListIterator<FSATransition> getSourceTransitionsListIterator();
	
	/**
	 * Adds a transition that ends in this state. 
	 * 
	 * @param t the transition to be added.
	 */
	public abstract void addTargetTransition(FSATransition t);

	/**
	 * Removes a transition that ends in this state. 
	 * 
	 * @param t the transition to be removed.
	 */
	public abstract void removeTargetTransition(FSATransition t);

	/**
	 * Returns an iterator for the transitions ending in this state. 
	 * 
	 * @return an iterator for the transitions ending in this state
	 */
	public abstract ListIterator<FSATransition> getTargetTransitionListIterator();
	
	/**
	 * Returns true iff this is an initial state. 
	 * 
	 * @return true iff this is an initial state
	 */	
	public boolean isInitial();

	/**
	 * Returns true iff this is a marked (final) state. 
	 * 
	 * @return true iff this is marked (final) state
	 */
	public boolean isMarked();
	
	/**
	 * Sets the "initial" property of the state.
	 * 
	 * @param b true if state to become initial, false otherwise
	 */
	public void setInitial(boolean b);
	
	/**
	 * Sets the "marked" property of the state. 
	 * 
	 * @param b true if state to become marked, false otherwise
	 */
	public void setMarked(boolean b);
}