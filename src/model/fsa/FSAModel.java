package model.fsa;

import java.util.ListIterator;


public interface FSAModel {

	/**
	 * @return the ID of the automaton
	 */
	public abstract String getID();
	
	/**
	 * @param id the ID of the automaton
	 */
	public abstract void setID(String id);
	
	/**
	 * @return the name of the automaton
	 */
	public abstract String getName();

	/**
	 * @param name the name of the automaton
	 */
	public abstract void setName(String name);

	/**
	 * @param s a state that needs to be added.
	 */
	public abstract void add(FSAState s);

	/**
	 * @return the number of states in the automaton
	 */
	public abstract int getStateCount();

	/**
	 * @return the number of transitions in the automaton
	 */
	public abstract int getTransitionCount();

	/**
	 * @return the number of events in the automaton
	 */	
	public abstract int getEventCount();
	
	/**
	 * removes the state from the automaton and all transitions leading to 
	 * the state and originating from the state
	 * @param s the state to be removed
	 */
	public abstract void remove(FSAState s);

	/**
	 * @return a custom list iterator for the states
	 */
	public abstract ListIterator<FSAState> getStateIterator();

	/**
	 * searches for the state with the given id.
	 * @param id the id of the state
	 * @return the state, null if it doesn't exist
	 */
	public abstract FSAState getState(long id);

	/**
	 * Adds a transition the the automaton and adds the transition to
	 * the list of sources and targets in the source and target state of the
	 * transition.
	 * @param t the transition to be added to the state
	 */
	public abstract void add(FSATransition t);

	/**
	 * Removes a transition from the automaton. Removes the transition from the 
	 * list of sourcetransitions and the list of target transitions in the 
	 * right states.
	 * @param t the transition to be removed
	 */
	public abstract void remove(FSATransition t);

	/**
	 * searches for the transition with the given id.
	 * @param id the id of the transition.
	 * @return the transition, null if the transition is not in the automaton.
	 */
	public abstract FSATransition getTransition(long id);

	/**
	 * @return a custom list iterator for the transitions.
	 */
	public abstract ListIterator<FSATransition> getTransitionIterator();

	/**
	 * Adds an event to the aotumaton.
	 * @param e the event that shall be added to the automaton.
	 */
	public abstract void add(FSAEvent e);

	/**
	 * Removes an event from the automaton.
	 * @param e the event to be removed
	 */
	public abstract void remove(FSAEvent e);

	/**
	 * @return a custom list iterator for the events.
	 */
	public abstract ListIterator<FSAEvent> getEventIterator();

	/**
	 * searches for the event with the given event id.
	 * @param id the id of the event
	 * @return the event, null if it doesn't exist
	 */
	public abstract FSAEvent getEvent(long id);

}