/*
 * Created on Jan 4, 2005
 */
package com.aggressivesoftware.des.concepts;

import java.util.Vector;

/**
 * This class represents a State in a FSM
 * 
 * @author Michael Wood
 */
public class State 
{
	/**
	 * Whether or not this State is marked.
	 */
	private boolean is_marked = false;

	/**
	 * A unique id for this State within its FSM
	 */
	public int id = -1;

	/**
	* All events incident with this State.
	*/
	private Vector transitions = null; 

	/**
	 * Construct a new State
	 * 
	 * @param	is_marked	The marking value for the new State.
	 */
	public State(boolean is_marked)
	{
		this.is_marked = is_marked;
		transitions = new Vector();
	}
			
	/**
	 * Connect a transition to this State
	 */
	public void addTransition(Transition transition)
	{
		transitions.add(transition);
	}
	
	/**
	 * String representation of this State.
	 * 
	 * @return	A String representation of this State.
	 */
	public String toString()
	{
		return "" + id;
	}
}