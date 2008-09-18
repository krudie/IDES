package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAEventSet;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.plugin.operation.FilterOperation;

import java.util.Iterator;

import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.Transition;

public class SelfLoop extends AbstractOperation implements FilterOperation
{

	public SelfLoop()
	{
		NAME = "selfloop";
		DESCRIPTION = "Unknown";

		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class, FSAEventSet.class };
		inputDesc = new String[] { "Finite-state automaton", "Events to self-loop" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { FSAModel.class };
		outputDesc = new String[] { "modifiedAutomaton" };
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
		Automaton fsa = (Automaton)((Automaton)inputs[0]).clone();
		FSAEventSet events = ((FSAEventSet)inputs[1]).copy();
		for (FSAEvent e : events)
		{
			e.setId(fsa.getFreeEventId());
			fsa.add(e);
		}
		for (Iterator<FSAState> i = fsa.getStateIterator(); i.hasNext();)
		{
			FSAState s = i.next();
			for (FSAEvent e : events)
			{
				fsa.add(new Transition(fsa.getFreeTransitionId(), s, s, e));
			}
		}
		return new Object[] { fsa };
	}

}
