package fsa.model;

import java.util.ListIterator;

public interface State {

	/**
	 * adds a transition that originates from the state.
	 * @param t the transition to be removed
	 */
	public abstract void addSourceTransition(Transition t);

	/**
	 * removes a transition that originates from the state. 
	 * @param t the transition to be removed
	 */
	public abstract void removeSourceTransition(Transition t);

	/**
	 * returns an iterator for the transitions originating from this state.
	 * @return a source transition iterator
	 */
	public abstract ListIterator<Transition> getSourceTransitionsListIterator();
	
	/**
	 * adds a transition that ends in this state.
	 * @param t the transition to be added.
	 */
	public abstract void addTargetTransition(Transition t);

	/**
	 * removes a transition that ends in this state.
	 * @param t the transition to be removed.
	 */
	public abstract void removeTargetTransition(Transition t);

	/**
	 * @return an iterator for the transitions ending in this state
	 */
	public abstract ListIterator<Transition> getTargetTransitionListIterator();

	/**
	 * @return the id of this state.
	 */
	public abstract int getId();

	/**
	 * @param id the id of this state
	 */
	public abstract void setId(int id);

}