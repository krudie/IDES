package operations.fsa.ver2_1;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.operation.FilterOperation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.Transition;

public class SelfLoop extends AbstractOperation implements FilterOperation
{
	public SelfLoop()
	{
		NAME = "selfloop";
		DESCRIPTION = "Self-loops selected events in every state of an automaton. "
				+ "In other words, computes the inverse projection of these events.";

		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class, DESEventSet.class };
		inputDesc = new String[] { "Finite-state automaton",
				"Events to self-loop" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { FSAModel.class };
		outputDesc = new String[] { "Automaton with self-looped events" };
	}

	public Object[] filter(Object[] inputs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getInputOutputIndexes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] perform(Object[] inputs)
	{
		warnings.clear();
		boolean selfloopDupEvents = false;

		Automaton fsa = (Automaton)((Automaton)inputs[0]).clone();
		DESEventSet eventsCopy = ((DESEventSet)inputs[1]).copy();
		Set<FSAEvent> events = new HashSet<FSAEvent>();
		for (DESEvent e : eventsCopy)
		{
			if (fsa.getEventSet().contains(e))
			{
				selfloopDupEvents = true;
				for (DESEvent event : fsa.getEventSet())
				{
					if (event.equals(e))
					{
						e = event;
						break;
					}
				}
			}
			else
			{
				if (!(e instanceof FSAEvent))
				{
					e = fsa.assembleEvent(e.getSymbol());
				}
				else
				{
					e.setId(fsa.getFreeEventId());
				}
				fsa.add((FSAEvent)e);
			}
			events.add((FSAEvent)e);
		}
		for (Iterator<FSAState> i = fsa.getStateIterator(); i.hasNext();)
		{
			FSAState s = i.next();
			for (FSAEvent e : events)
			{
				boolean hasTransition = false;
				FSATransition emptyLoop = null;
				for (Iterator<FSATransition> j = s
						.getOutgoingTransitionsListIterator(); j.hasNext();)
				{
					FSATransition t = j.next();
					if (e.equals(t.getEvent()))
					{
						hasTransition = true;
						break;
					}
					if (t.getEvent() == null)
					{
						emptyLoop = t;
					}
				}
				if (!hasTransition)
				{
					if (emptyLoop != null)
					{
						emptyLoop.setEvent(e);
					}
					else
					{
						fsa.add(new Transition(
								fsa.getFreeTransitionId(),
								s,
								s,
								e));
					}
				}
			}
		}
		if (selfloopDupEvents)
		{
			warnings.add(Hub.string("warnSelfloopDupEvents"));
		}
		return new Object[] { fsa };
	}

}
