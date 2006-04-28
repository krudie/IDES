package model.fsa;

import java.util.ListIterator;

public interface FSAState {

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
	 * @return the id of this state.
	 */
	public abstract int getId();

	/**
	 * @param id the id of this state
	 */
	public abstract void setId(int id);	
}