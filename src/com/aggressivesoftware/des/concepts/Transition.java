/*
 * Created on Jan 4, 2005
 */
package com.aggressivesoftware.des.concepts;

/**
 * This class represents an Event that is transitioning between two specific States in an FSM
 * 
 * @author obadiah
 */
public class Transition 
{
	/**
	 * The State where this Event initiates.
	 */
	private State initiating_state = null;

	/**
	 * The State where this Event terminates.
	 */
	private State terminating_state = null;

	/**
	 * The Event.
	 */
	private Event event = null;

	/**
	 * Construct a new Transition.
	 * 
	 * @param	initiating_state	The initiating State of the Event.
	 * @param	terminating_state	The terminating State of the Event.
	 * @param	event				The Event.
	 */
	public Transition(State initiating_state, State terminating_state, Event event)
	{
		this.initiating_state = initiating_state;
		this.terminating_state = terminating_state;
		this.event = event;
		initiating_state.addTransition(this);
		if (initiating_state != terminating_state) { terminating_state.addTransition(this); }
	}
	
	/**
	 * String representation of this Transition.
	 * 
	 * @return	A String representation of this Transition.
	 */
	public String toString()
	{
		String controllability = "c";
		if (!event.isControllable()) { controllability = "u"; }
		return controllability + "  " + initiating_state + ", " + event + ", " + terminating_state;
	}
}
