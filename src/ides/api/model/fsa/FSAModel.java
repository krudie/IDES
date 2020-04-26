package ides.api.model.fsa;

import java.util.ListIterator;

import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESModel;

/**
 * Interface for models of Finite-State Automata.
 */
public interface FSAModel extends DESModel, FSAPublisher {

    /**
     * Assembles a new state which can be added to the model. The state is not
     * automatically added to the model.
     * 
     * @return a new state
     */
    public abstract FSAState assembleState();

    /**
     * Assembles a new state with the same name and properties as the given state,
     * which can then be added to the model. The state is not automatically added to
     * the model.
     * 
     * @param state the state whose properties are to be copied.
     * @return A new FSAState with the same name and controllable and obsersable
     *         properties as the given state.
     */
    public abstract FSAState assembleCopyOf(FSAState state);

    /**
     * Assembles a new event which can be added to the model. The event is not
     * automatically added to the model.
     * 
     * @param symbol the name for the event
     * @return a new event with the given name
     */

    public abstract SupervisoryEvent assembleEvent(String symbol);

    /**
     * Assembles a new event with the same name and properties as the given event,
     * which can then be added to the model. The event is not automatically added to
     * the model.
     * 
     * @param event the event whose properties are to be copied.
     * @return A new FSAState with the same name as the given state. If the given
     *         event is a SupervisoryEvent, the new event will have the same
     *         controllable and observable properties as the given event.
     */
    public abstract SupervisoryEvent assembleCopyOf(DESEvent event);

    /**
     * Assembles a new transition which can be added to the model. The transition is
     * not automatically added to the model.
     * 
     * @param source the id of the source state of the transition
     * @param target the id of the target state of the transition
     * @param event  the id of the event of the transition
     * @return a new transition with the given properties
     * @throws IllegalArgumentException when the given source, target or event are
     *                                  not part of the model
     */
    public abstract FSATransition assembleTransition(long source, long target, long event);

    /**
     * Assembles a new epsilon transition which can be added to the model. The
     * transition is not automatically added to the model. An epsilon transition is
     * a transition with no associated event (null). It is interpreted as a
     * transition on the epsilon (empty) string.
     * 
     * @param source the id of the source state of the transition
     * @param target the id of the target state of the transition
     * @return a new transition with the given properties and no event
     * @throws IllegalArgumentException when the given source or target are not part
     *                                  of the model
     */
    public abstract FSATransition assembleEpsilonTransition(long source, long target);

    /**
     * Add a state.
     * 
     * @param s a state that needs to be added.
     */
    public abstract void add(FSAState s);

    /**
     * Get the number of states in the FSA.
     * 
     * @return the number of states in the automaton
     */
    public abstract long getStateCount();

    /**
     * Get the number of transitions in the FSA.
     * 
     * @return the number of transitions in the automaton
     */
    public abstract long getTransitionCount();

    /**
     * Get the number of events in the event set of the FSA.
     * 
     * @return the number of events in the automaton
     */
    public abstract long getEventCount();

    /**
     * removes the state from the automaton and all transitions leading to the state
     * and originating from the state
     * 
     * @param s the state to be removed
     */
    public abstract void remove(FSAState s);

    /**
     * Get an iterator for the states in the FSA.
     * 
     * @return a custom list iterator for the states
     */
    public abstract ListIterator<FSAState> getStateIterator();

    /**
     * Get the state with the given id.
     * 
     * @param id the id of the state
     * @return the state, null if it doesn't exist
     */
    public abstract FSAState getState(long id);

    /**
     * Adds a transition the the automaton and adds the transition to the list of
     * sources and targets in the source and target state of the transition.
     * 
     * @param t the transition to be added to the state
     */
    public abstract void add(FSATransition t);

    /**
     * Removes a transition from the automaton. Removes the transition from the list
     * of source transitions and the list of target transitions in the right states.
     * 
     * @param t the transition to be removed
     */
    public abstract void remove(FSATransition t);

    /**
     * Get the transition with the given id.
     * 
     * @param id the id of the transition.
     * @return the transition, null if the transition is not in the automaton.
     */
    public abstract FSATransition getTransition(long id);

    /**
     * Get an iterator for the transitions in the FSA.
     * 
     * @return a custom list iterator for the transitions.
     */
    public abstract ListIterator<FSATransition> getTransitionIterator();

    /**
     * Adds an event to the event set of the automaton.
     * 
     * @param e the event that shall be added to the automaton.
     */
    public abstract void add(SupervisoryEvent e);

    /**
     * Removes an event from the automaton.
     * 
     * @param e the event to be removed
     */
    public abstract void remove(SupervisoryEvent e);

    /**
     * Get an iterator for the events in the event set of the FSA.
     * 
     * @return a custom list iterator for the events.
     */
    public abstract ListIterator<SupervisoryEvent> getEventIterator();

    /**
     * Obtains the set of events in the model. If there are no events in the model,
     * returns an empty set.
     * 
     * @return the set of events in the model.
     */
    public abstract SupervisoryEventSet getEventSet();

    /**
     * Get the event with the given event id.
     * 
     * @param id the id of the event
     * @return the event, null if it doesn't exist
     */
    public abstract SupervisoryEvent getEvent(long id);

    /**
     * Creates and returns a copy of this FSAModel.
     * 
     * @return a copy of the model
     */
    public FSAModel clone();
}