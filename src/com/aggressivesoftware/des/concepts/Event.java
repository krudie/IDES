/*
 * Created on Jan 4, 2005
 */
package com.aggressivesoftware.des.concepts;

/**
 * This class represents an Event in a FSM.
 * It is not a transition between two specific states, only the concept of an Event.
 *  
 * @author Michael Wood
 */
public class Event 
{
	/**
	 * Whether or not this Event is controllable
	 */
	private boolean is_controllable = false;

	/**
	 * The label of this Event
	 */
	private String label = "";
	
	/**
	 * Construct a new Event
	 * 
	 * @param	label				The label of the new Event.
	 * @param	is_controllable		The controllability of the new Event.
	 */
	public Event(String label, boolean is_controllable)
	{
		this.is_controllable = is_controllable;
		this.label = label;
	}
	
	/**
	 * String representation of this Event.
	 * 
	 * @return	A String representation of this Event.
	 */
	public String toString()
	{
		return label;
	}
	
	/**
	 * Test if this Event is controllable.
	 * 
	 * @return	true if this Event is controllable.
	 */
	public boolean isControllable()
	{
		return is_controllable;
	}
}