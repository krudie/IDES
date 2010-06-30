package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;

import ides.api.plugin.model.DESEventSet;

import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;



import java.util.LinkedList;
import java.util.List;


/**
 * algorithm taken from "A Course in Formal Languages, Automata and Groups" by
 * Ian M. Chiswell Springer London, 2009. Construct a new FSA, whose transition
 * diagram is the union of the transition diagrams for M1 and M2, modified as
 * follows. There is one extra vertex as initial state and two extra edges from
 * this new vertex to the initial states of M1 and M2, having label "epsilon".
 * The final states are those of M1 and M2.
 * 
 * @author Valerie Sugarman
 */
public class Test implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();

	public String getDescription()
	{

		return "testing";
	}

	public String[] getDescriptionOfInputs()
	{

		return new String[] { "input" };

	}

	public String[] getDescriptionOfOutputs()
	{

		return new String[] { "result" };
	}

	public String getName()
	{

		return "test";
	}

	public int getNumberOfInputs()
	{

		return 1;
	}

	public int getNumberOfOutputs()
	{

		return 1;
	}

	public Class<?>[] getTypeOfInputs()
	{

		return new Class<?>[] { DESEventSet.class };

	}

	public Class<?>[] getTypeOfOutputs()
	{

		return new Class<?>[] { FSAModel.class };
	}

	public List<String> getWarnings()
	{
		return warnings;
	}

	
	public Object[] perform(Object[] arg0)
	{

		FSAModel ret = FSAToolbox.sigmaPlus((DESEventSet)arg0[0]);
		return new Object[]{ret};
		
	}
}
