package operations.fsa.ver2_1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import main.Annotable;
import model.ModelManager;
import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Event;
import model.fsa.ver2_1.EventSet;
import model.fsa.ver2_1.State;
import model.fsa.ver2_1.Transition;

public class MultiAgentProductFSA extends AbstractOperation
{

	public MultiAgentProductFSA()
	{
		NAME = "maproduct-fsa";
		DESCRIPTION = "Computes the multi-agent product of scalar automata. The result is as a scalar automaton.";
		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class };
		inputDesc = new String[] { "Finite-state automaton" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { FSAModel.class };
		outputDesc = new String[] { "composedAutomaton" };
	}

	@Override
	public int getNumberOfInputs()
	{
		return -1;
	}

	int maOrder;

	HashMap<String, FSAState> stateMap;

	HashMap<String, FSAEvent> eventMap;

	protected class PseudoTransition
	{
		public LinkedList<LinkedList<FSAEvent>> events = new LinkedList<LinkedList<FSAEvent>>();

		public LinkedList<LinkedList<FSAState>> targets = new LinkedList<LinkedList<FSAState>>();
	}

	@Override
	public Object[] perform(Object[] inputs)
	{
		if (inputs.length == 0)
		{
			return new Object[0];
		}
		FSAModel a = ModelManager.createModel(FSAModel.class);
		maOrder = inputs.length;
		FSAState[] initial = new FSAState[maOrder];
		String[] fsaIds = new String[maOrder];
		for (int i = 0; i < inputs.length; ++i)
		{
			fsaIds[i] = ((FSAModel)inputs[i]).getName();
			initial[i] = null;
			for (Iterator<FSAState> j = ((FSAModel)inputs[i])
					.getStateIterator(); j.hasNext();)
			{
				FSAState state = j.next();
				if (state.isInitial())
				{
					initial[i] = state;
					break;
				}
			}
			if (initial[i] == null)
			{
				warnings.add("FSA \'" + fsaIds[i]
						+ "\' does not have an initial state");
				return new Object[0];
			}
		}
		a.setAnnotation(Annotable.COMPOSED_OF, fsaIds);
		LinkedList<FSAState[]> openStates = new LinkedList<FSAState[]>();
		HashSet<String> closedStates = new HashSet<String>();
		stateMap = new HashMap<String, FSAState>();
		eventMap = new HashMap<String, FSAEvent>();
		openStates.add(initial);
		FSAState newS = makeState(initial, 0);
		a.add(newS);
		stateMap.put(keyOf(initial), newS);
		while (!openStates.isEmpty())
		{
			FSAState[] states = openStates.getFirst();
			openStates.removeFirst();
			if (closedStates.contains(keyOf(states)))
			{
				continue;
			}
			closedStates.add(keyOf(states));
			FSAState state = stateMap.get(keyOf(states));
			FSAEventSet[] eventSets = new FSAEventSet[maOrder];
			for (int i = 0; i < states.length; ++i)
			{
				FSAEventSet eventSet = new EventSet();
				for (Iterator<FSATransition> ti = states[i]
						.getOutgoingTransitionsListIterator(); ti.hasNext();)
				{
					FSATransition t = ti.next();
					if (t.getEvent() != null)
					{
						eventSet.add(t.getEvent());
					}
				}
				eventSets[i] = eventSet;
			}
			PseudoTransition pt = computeTransitions(new FSAEvent[0],
					eventSets,
					states);
			Iterator<LinkedList<FSAEvent>> ei = pt.events.iterator();
			Iterator<LinkedList<FSAState>> si = pt.targets.iterator();
			for (; ei.hasNext();)
			{
				FSAEvent[] e = ei.next().toArray(new FSAEvent[0]);
				FSAState[] s = si.next().toArray(new FSAState[0]);
				FSAEvent event = eventMap.get(keyOf(e));
				if (event == null)
				{
					event = makeEvent(e, a.getEventCount());
					a.add(event);
					eventMap.put(keyOf(e), event);
				}
				FSAState target = stateMap.get(keyOf(s));
				if (target == null)
				{
					target = makeState(s, a.getStateCount());
					a.add(target);
					stateMap.put(keyOf(s), target);
				}
				FSATransition transition = new Transition(a
						.getTransitionCount(), state, target, event);
				a.add(transition);
				openStates.addLast(s);
			}
		}
		return new Object[] { a };
	}

	protected PseudoTransition computeTransitions(FSAEvent[] selectedEvents,
			FSAEventSet[] freeEvents, FSAState[] origin)
	{
		PseudoTransition pt = new PseudoTransition();
		for (FSAEvent e : freeEvents[0])
		{
			boolean includeEvent = true;
			for (int i = 0; i < selectedEvents.length; ++i)
			{
				if (!e.getSymbol().equals(selectedEvents[i].getSymbol()))
				{
					if (hasOutgoingTransitionOn(origin[i], e)
							|| hasOutgoingTransitionOn(origin[selectedEvents.length],
									selectedEvents[i]))
					{
						includeEvent = false;
						break;
					}
				}
			}
			if (includeEvent)
			{
				FSAState target = null;
				for (Iterator<FSATransition> i = origin[selectedEvents.length]
						.getOutgoingTransitionsListIterator(); i.hasNext();)
				{
					FSATransition t = i.next();
					if (t.getEvent() != null
							&& t.getEvent().getSymbol().equals(e.getSymbol()))
					{
						target = t.getTarget();
					}
				}
				if (freeEvents.length == 1)
				{
					LinkedList<FSAEvent> events = new LinkedList<FSAEvent>();
					events.add(e);
					LinkedList<FSAState> targets = new LinkedList<FSAState>();
					targets.add(target);
					pt.events.add(events);
					pt.targets.add(targets);
				}
				else
				{
					FSAEvent[] extended = new FSAEvent[selectedEvents.length + 1];
					System.arraycopy(selectedEvents,
							0,
							extended,
							0,
							selectedEvents.length);
					extended[selectedEvents.length] = e;
					FSAEventSet[] shorter = new FSAEventSet[freeEvents.length - 1];
					System.arraycopy(freeEvents, 1, shorter, 0, shorter.length);
					PseudoTransition subPT = computeTransitions(extended,
							shorter,
							origin);
					Iterator<LinkedList<FSAEvent>> ei = subPT.events.iterator();
					Iterator<LinkedList<FSAState>> si = subPT.targets
							.iterator();
					for (; ei.hasNext();)
					{
						LinkedList<FSAEvent> events = ei.next();
						LinkedList<FSAState> targets = si.next();
						LinkedList<FSAEvent> extendedEvents = new LinkedList<FSAEvent>(
								events);
						extendedEvents.addFirst(e);
						LinkedList<FSAState> extendedTargets = new LinkedList<FSAState>(
								targets);
						extendedTargets.addFirst(target);
						pt.events.add(extendedEvents);
						pt.targets.add(extendedTargets);
					}
				}
			}
		}
		return pt;
	}

	protected boolean hasOutgoingTransitionOn(FSAState s, FSAEvent e)
	{
		for (Iterator<FSATransition> i = s.getOutgoingTransitionsListIterator(); i
				.hasNext();)
		{
			FSATransition t = i.next();
			if (t.getEvent() != null
					&& t.getEvent().getSymbol().equals(e.getSymbol()))
			{
				return true;
			}
		}
		return false;
	}

	protected FSAState makeState(FSAState[] states, long id)
	{
		State newS = new State(id);
		boolean isInitial = true;
		boolean isMarked = true;
		long[] composedIds = new long[states.length];
		for (int i = 0; i < states.length; ++i)
		{
			isInitial &= states[i].isInitial();
			isMarked &= states[i].isMarked();
			composedIds[i] = states[i].getId();
		}
		newS.setInitial(isInitial);
		newS.setMarked(isMarked);
		newS.setStateCompositionList(composedIds);
		return newS;
	}

	protected FSAEvent makeEvent(FSAEvent[] events, long id)
	{
		FSAEvent e = new Event(id);
		boolean isUncontrollable = true;
		String label = "$\\left[\\begin{array}{c}";
		for (FSAEvent event : events)
		{
			label += "\\mbox{" + event.getSymbol() + "}\\\\";
			if (event.isControllable())
			{
				isUncontrollable = false;
			}
		}
		if (label.endsWith("\\\\"))
		{
			label = label.substring(0, label.length() - 2);
		}
		label += "\\end{array}\\right]$";
		e.setSymbol(label);
		e.setControllable(!isUncontrollable);
		return e;
	}

	protected String keyOf(FSAState[] states)
	{
		String key = "";
		for (FSAState s : states)
		{
			key += s.getId() + ",";
		}
		return key;
	}

	protected String keyOf(FSAEvent[] events)
	{
		String key = "";
		for (FSAEvent e : events)
		{
			key += e.getId() + ",";
		}
		return key;
	}
}
