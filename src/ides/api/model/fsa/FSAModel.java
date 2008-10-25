package ides.api.model.fsa;

// import io.fsa.ver2_1.SubElement;

import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.DESModel;

import java.util.ListIterator;

public interface FSAModel extends DESModel, FSAPublisher
{

	/**
	 * Assembles a new state which can be added to the model. The state is not
	 * automatically added to the model.
	 * 
	 * @return a new state
	 */
	public abstract FSAState assembleState();

	/**
	 * Assembles a new event which can be added to the model. The event is not
	 * automatically added to the model.
	 * 
	 * @param symbol
	 *            the name for the event
	 * @return a new event with the given name
	 */
	public abstract FSAEvent assembleEvent(String symbol);

	/**
	 * Assembles a new transition which can be added to the model. The
	 * transition is not automatically added to the model.
	 * 
	 * @param source
	 *            the id of the source state of the transition
	 * @param target
	 *            the id of the target state of the transition
	 * @param event
	 *            the id of the event of the transition
	 * @return a new transition with the given properties
	 * @throws IllegalArgumentException
	 *             when the given source, target or event are not part of the
	 *             model
	 */
	public abstract FSATransition assembleTransition(long source, long target,
			long event);

	/**
	 * @param s
	 *            a state that needs to be added.
	 */
	public abstract void add(FSAState s);

	/**
	 * @return the number of states in the automaton
	 */
	public abstract long getStateCount();

	/**
	 * @return the number of transitions in the automaton
	 */
	public abstract long getTransitionCount();

	/**
	 * @return the number of events in the automaton
	 */
	public abstract long getEventCount();

	/**
	 * removes the state from the automaton and all transitions leading to the
	 * state and originating from the state
	 * 
	 * @param s
	 *            the state to be removed
	 */
	public abstract void remove(FSAState s);

	/**
	 * @return a custom list iterator for the states
	 */
	public abstract ListIterator<FSAState> getStateIterator();

	/**
	 * searches for the state with the given id.
	 * 
	 * @param id
	 *            the id of the state
	 * @return the state, null if it doesn't exist
	 */
	public abstract FSAState getState(long id);

	/**
	 * Adds a transition the the automaton and adds the transition to the list
	 * of sources and targets in the source and target state of the transition.
	 * 
	 * @param t
	 *            the transition to be added to the state
	 */
	public abstract void add(FSATransition t);

	/**
	 * Removes a transition from the automaton. Removes the transition from the
	 * list of sourcetransitions and the list of target transitions in the right
	 * states.
	 * 
	 * @param t
	 *            the transition to be removed
	 */
	public abstract void remove(FSATransition t);

	/**
	 * searches for the transition with the given id.
	 * 
	 * @param id
	 *            the id of the transition.
	 * @return the transition, null if the transition is not in the automaton.
	 */
	public abstract FSATransition getTransition(long id);

	/**
	 * @return a custom list iterator for the transitions.
	 */
	public abstract ListIterator<FSATransition> getTransitionIterator();

	/**
	 * Adds an event to the automaton.
	 * 
	 * @param e
	 *            the event that shall be added to the automaton.
	 */
	public abstract void add(FSAEvent e);

	/**
	 * Removes an event from the automaton.
	 * 
	 * @param e
	 *            the event to be removed
	 */
	public abstract void remove(FSAEvent e);

	/**
	 * @return a custom list iterator for the events.
	 */
	public abstract ListIterator<FSAEvent> getEventIterator();

	/**
	 * Obtains the set of events in the model. If there are no events in the
	 * model, returns an empty set.
	 * 
	 * @return the set of events in the model.
	 */
	public abstract DESEventSet getEventSet();

	/**
	 * searches for the event with the given event id.
	 * 
	 * @param id
	 *            the id of the event
	 * @return the event, null if it doesn't exist
	 */
	public abstract FSAEvent getEvent(long id);

	/**
	 * Creates and returns a copy of this FSAModel.
	 * 
	 * @return a copy of the model
	 */
	public FSAModel clone();
}