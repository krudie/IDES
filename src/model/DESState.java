package model;

import java.util.ListIterator;

public interface DESState {

	/**
	 * adds a transition that originates from the state.
	 * @param t the transition to be removed
	 */
	public abstract void addSourceTransition(DESTransition t);

	/**
	 * removes a transition that originates from the state. 
	 * @param t the transition to be removed
	 */
	public abstract void removeSourceTransition(DESTransition t);

	/**
	 * returns an iterator for the transitions originating from this state.
	 * @return a source transition iterator
	 */
	public abstract ListIterator<DESTransition> getSourceTransitionsListIterator();
	
	/**
	 * adds a transition that ends in this state.
	 * @param t the transition to be added.
	 */
	public abstract void addTargetTransition(DESTransition t);

	/**
	 * removes a transition that ends in this state.
	 * @param t the transition to be removed.
	 */
	public abstract void removeTargetTransition(DESTransition t);

	/**
	 * @return an iterator for the transitions ending in this state
	 */
	public abstract ListIterator<DESTransition> getTargetTransitionListIterator();

	/**
	 * @return the id of this state.
	 */
	public abstract int getId();

	/**
	 * @param id the id of this state
	 */
	public abstract void setId(int id);

}