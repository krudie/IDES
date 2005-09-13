/*
 * Created on Jan 4, 2005
 */
package com.aggressivesoftware.des.concepts;

import java.util.Vector;

/**
 * This class represents an FSM with a single start State and zero or more marked States
 * 
 * @author Michael Wood
 */
public class FiniteStateMachine 
{
	/**
	* The set of States.
	*/
	public Vector states = null; 

	/**
	* The set of Transitions.
	*/
	public Vector transitions = null; 
	
	/**
	* The start State.
	*/
	public Object start_state = null;
	
	/**
	 * Construct a new FiniteStateMachine
	 * 
	 * @param	start_state		The start State that will initially comprise this FSM
	 */
	public FiniteStateMachine(State start_state)
	{
		states = new Vector();
		transitions = new Vector();
		this.start_state = start_state;
		states.add(start_state);
		start_state.id = states.size();
	}

	/**
	 * Construct a new FiniteStateMachine
	 * 
	 * @param	start_state		The start State that will initially comprise this FSM
	 */
	public FiniteStateMachine(SynthesizedState start_state)
	{
		states = new Vector();
		transitions = new Vector();
		this.start_state = start_state;
		states.add(start_state);
	}
	
	/**
	 * Create a new State and add it to this FSM (unconected to any other states)
	 *
	 * @param	is_marked	The marking value for the new State. 
	 * @return	The newly created State.
	 */
	public State newState(boolean is_marked)
	{
		State new_state = new State(is_marked);
		states.add(new_state);
		new_state.id = states.size();
		return new_state;
	}
	
	/**
	 * Create a new Event and add it to this FSM.
	 * 
	 * @param	initiating_state	The initiating State of the new Event, which must allready be part of this FiniteStateMachine.
	 * @param	terminating_state	The terminating State of the new Event, which must allready be part of this FiniteStateMachine.
	 * @param	event				The Event.
	 */
	public void newTransition(State initiating_state, State terminating_state, Event event)
	{
		Transition new_transition = new Transition(initiating_state, terminating_state, event);
		transitions.add(new_transition);
	}

	/**
	 * Print all Transitions.
	 */
	public void printTransitions()
	{
		for (int i=0; i<transitions.size(); i++)
		{
			System.out.println(transitions.elementAt(i));
		}
	}

	/**
	 * Print all States.
	 */
	public void printStates()
	{
		for (int i=0; i<states.size(); i++)
		{
			System.out.println(states.elementAt(i));
		}
	}
}