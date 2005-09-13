/*
 * Created on Jan 4, 2005
 */
package com.aggressivesoftware.des;

import java.util.Vector;

import com.aggressivesoftware.des.concepts.Event;
import com.aggressivesoftware.des.concepts.FiniteStateMachine;
import com.aggressivesoftware.des.concepts.State;
import com.aggressivesoftware.des.concepts.SynthesizedState;

/**
 * @author Michael Wood
 */
public class StaticControllerGeneration 
{
	/**
	* 
	*/
	private static Vector closed = null; 

	/**
	* 
	*/
	private static Vector open = null; 	
	
	/**
	* Create the two static FSMs and find the supervisor.
	* 
    * @param	args	The command line arguments.
    */
	public static void main(String[] args)
	{
		Event[] events = new Event[] {new Event("a",true), new Event("b",true), new Event("c",false), new Event("d",true)};
		
		State state1 = null;
		State state2 = null;
		State state3 = null;
		
		state1 = new State(false);
		FiniteStateMachine plant = new FiniteStateMachine(state1);
		state2 = plant.newState(false);
		plant.newTransition(state1,state2,events[0]);
		plant.newTransition(state2,state2,events[1]);
		state1 = plant.newState(true);
		plant.newTransition(state2,state1,events[0]);
		plant.newTransition(state1,state1,events[3]);
		plant.newTransition(state1,state2,events[2]);
		System.out.println("plant");
		plant.printTransitions();
		
		state1 = new State(false);
		FiniteStateMachine legal = new FiniteStateMachine(state1);
		state2 = legal.newState(false);
		legal.newTransition(state1,state2,events[0]);
		state3 = legal.newState(true);
		legal.newTransition(state2,state3,events[0]);
		state1 = legal.newState(true);
		legal.newTransition(state3,state1,events[3]);
		state3 = legal.newState(true);
		legal.newTransition(state1,state3,events[3]);
		legal.newTransition(state3,state2,events[2]);
		System.out.println("\nlegal");
		legal.printTransitions();
		
		//////////////////////////////////////////////////////////////////////////////
		
		closed = new Vector();
		open = new Vector();
		
		SynthesizedState start_state = new SynthesizedState((State)plant.start_state, (State)legal.start_state);
		FiniteStateMachine supervisor = new FiniteStateMachine(start_state);
		System.out.println("\nsupervisor");
		supervisor.printStates();
	}
}