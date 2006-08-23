package model.fsa;

import java.util.ListIterator;

import model.DESElement;

public interface FSAState extends DESElement {

	/**
	 * adds a transition that originates from the state.
	 * @param t the transition to be removed
	 */
	public abstract void addSourceTransition(FSATransition t);

	/**
	 * removes a transition that originates from the state. 
	 * @param t the transition to be removed
	 */
	public abstract void removeSourceTransition(FSATransition t);

	/**
	 * returns an iterator for the transitions originating from this state.
	 * @return a source transition iterator
	 */
	public abstract ListIterator<FSATransition> getSourceTransitionsListIterator();
	
	/**
	 * adds a transition that ends in this state.
	 * @param t the transition to be added.
	 */
	public abstract void addTargetTransition(FSATransition t);

	/**
	 * removes a transition that ends in this state.
	 * @param t the transition to be removed.
	 */
	public abstract void removeTargetTransition(FSATransition t);

	/**
	 * @return an iterator for the transitions ending in this state
	 */
	public abstract ListIterator<FSATransition> getTargetTransitionListIterator();
	
	/**
	 * @return true iff this is an initial state
	 */	
	public boolean isInitial();

	/**
	 * @return true iff this is marked (final) state
	 */
	public boolean isMarked();
	
	/**
	 * sets the "initial" property of the state 
	 * @param b true if state to become initial, false otherwise
	 */
	public void setInitial(boolean b);
	
	/**
	 * sets the "marked" property of the state
	 * @param b true if state to become marked, false otherwise
	 */
	public void setMarked(boolean b);
}