package operations.fsa.ver2_1;

import java.util.LinkedList;
import java.util.Vector;

import model.ModelManager;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.fsa.FSAState;

import pluggable.operation.Operation;

public class MultiAgentProduct extends AbstractOperation {
	
	public MultiAgentProduct()
	{
		NAME = "maprod";
		DESCRIPTION = "Computes the multi-agent product of scalar automata.";
		//WARNING - Ensure that input type and description always match!	
		inputType = new Class[]{FSAModel.class};
		inputDesc = new String[]{"Finite-state automaton"};

		//WARNING - Ensure that output type and description always match!
		outputType = new Class[]{FSAModel.class};
		outputDesc = new String[]{"composedAutomaton"};
	}

	public int getNumberOfInputs() {
		return -1;
	}

	int maOrder;
	FSAEventSet[] eventSets;
	LinkedList<FSAState[]> openStates;
	
	public Object[] perform(Object[] inputs) {
		FSAModel a=ModelManager.createModel(FSAModel.class);
		maOrder=inputs.length;
		FSAState[] initial=new FSAState[maOrder];
		eventSets=new FSAEventSet[maOrder];
		return null;
	}

}
