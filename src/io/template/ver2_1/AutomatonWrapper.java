package io.template.ver2_1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import des.interfaces.Automaton;
import des.interfaces.Event;
import des.interfaces.State;
import des.interfaces.Transition;

public class AutomatonWrapper implements Automaton
{

	protected Vector<Event> events = new Vector<Event>();

	protected Vector<State> states = new Vector<State>();

	protected Vector<Transition> transitions = new Vector<Transition>();

	public AutomatonWrapper(FSAModel model)
	{
		HashMap<FSAEvent, Event> eventMap = new HashMap<FSAEvent, Event>();
		HashMap<FSAState, State> stateMap = new HashMap<FSAState, State>();
		for (Iterator<FSAEvent> i = model.getEventIterator(); i.hasNext();)
		{
			FSAEvent fsae = i.next();
			Event e = new EventWrapper(fsae);
			eventMap.put(fsae, e);
			events.add(e);
		}
		for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();)
		{
			FSAState fsas = i.next();
			State s = new StateWrapper(fsas);
			stateMap.put(fsas, s);
			states.add(s);
		}
		for (Iterator<FSATransition> i = model.getTransitionIterator(); i
				.hasNext();)
		{
			FSATransition fsat = i.next();
			Transition t = new TransitionWrapper(
					stateMap.get(fsat.getSource()),
					eventMap.get(fsat.getEvent()),
					stateMap.get(fsat.getTarget()));
			t.getSource().addSourceTransition(t);
			t.getTarget().addTargetTransition(t);
			transitions.add(t);
		}
	}

	public void add(State arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void add(Transition arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void add(Event arg0)
	{
		throw new UnsupportedOperationException();
	}

	public Vector<Event> getEvents()
	{
		return events;
	}

	public State getInitialState()
	{
		State initial = null;
		for (State s : states)
		{
			if (s.isInitial())
			{
				initial = s;
				break;
			}
		}
		return initial;
	}

	public Vector<State> getMarkedStates()
	{
		Vector<State> marked = new Vector<State>();
		for (State s : states)
		{
			if (s.isMarked())
			{
				marked.add(s);
			}
		}
		return marked;
	}

	public Vector<State> getStates()
	{
		return states;
	}

	public Vector<Transition> getTransitions()
	{
		return transitions;
	}

	public void remove(State arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void remove(Transition arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void remove(Event arg0)
	{
		throw new UnsupportedOperationException();
	}

}
