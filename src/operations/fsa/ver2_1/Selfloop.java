package operations.fsa.ver2_1;

import java.util.Iterator;
import java.util.Set;

import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.Transition;
import pluggable.operation.FilterOperation;

public class Selfloop implements FilterOperation {

	public Object[] filter(Object[] inputs) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getInputOutputIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getDescriptionOfInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getDescriptionOfOutputs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getDescription() {
		return null;
	}

	public int getNumberOfInputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumberOfOutputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Class[] getTypeOfInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class[] getTypeOfOutputs() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] perform(Object[] inputs) {
		Automaton fsa=(Automaton)((Automaton)inputs[0]).clone();
		FSAEventSet events=(FSAEventSet)inputs[1];
		for(FSAEvent e:events)
		{
			e.setId(fsa.getFreeEventId());
			fsa.add(e);
		}
		for(Iterator<FSAState> i=fsa.getStateIterator();i.hasNext();)
		{
			FSAState s=i.next();
			for(FSAEvent e:events)
			{
				fsa.add(new Transition(fsa.getFreeTransitionId(),s,s,e));
			}
		}
		return new Object[]{fsa};
	}

}
