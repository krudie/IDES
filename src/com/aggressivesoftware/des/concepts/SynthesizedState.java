/*
 * Created on Jan 4, 2005
 */
package com.aggressivesoftware.des.concepts;

import java.util.Vector;

/**
 * This class represents a State that has been created based on other stats, such as in a meet, or shuffel.
 * 
 * @author Michael Wood
 */
public class SynthesizedState 
{
	/**
	 * Posible status values for a State.
	 */
	private static final int UNDEFINED = 0;
	
	/**
	 * The status of this State.
	 */
	private int status = UNDEFINED;

	/**
	 * Whether or not this State is marked.
	 */
	private boolean is_marked = false;

	/**
	* The set of States from which this State is directly reachable via an uncontrollable event.
	*/
	private Vector incomming_uncontrollable_neighbours = null; 
	
	/**
	* The set of States from which this State is directly reachable via a controllable event.
	*/
	private Vector incomming_controllable_neighbours = null; 

	/**
	* The set of closest controllable events on paths leading to this State, 
	* hence we can prevent this State from being reached by disabling these events.
	* consider a,b,c,d as the only path to this state with a,b controllable and c,d uncontrollable,
	* then b is the latest controllable event.
	*/
	private Vector latest_controllable_events = null;

	/**
	* The States from which this SynthesizedState was derived.
	*/
	private Vector source_states = null;
		
	/**
	 * Construct a new State
	 */
	public SynthesizedState(State source1, State source2) 
	{
		source_states = new Vector();
		source_states.add(source1);
		source_states.add(source2);
		
		// note the default values of status and is_marked above.
		incomming_uncontrollable_neighbours = new Vector();
		incomming_controllable_neighbours = new Vector();
		latest_controllable_events = new Vector();
		setMarkingValue();
		// open < opne u s ?
	}
	
	/**
	 * 
	 */
	public void setMarkingValue()
	{
		// true if it is marked in both?
	}

	/**
	 * String representation of this State.
	 * 
	 * @return	A String representation of this State.
	 */
	public String toString()
	{
		String to_string = "" + source_states.elementAt(0);
		for (int i=1; i<source_states.size(); i++) { to_string = to_string + ", " + source_states.elementAt(0); }
		return to_string;
	}
}
