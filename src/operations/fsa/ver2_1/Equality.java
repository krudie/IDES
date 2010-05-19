package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Valerie Sugarman
 */
public class Equality implements Operation
{

	protected String resultMessage;

	public String getDescription()
	{

		return "Determines if the two languages are equal.";
	}

	public String[] getDescriptionOfInputs()
	{

		return new String[] { "Finite-state automaton",
				"Finite-state automaton" };
	}

	public String[] getDescriptionOfOutputs()
	{

		return new String[] { resultMessage };
	}

	public String getName()
	{

		return "equals";
	}

	public int getNumberOfInputs()
	{

		return 2;
	}

	public int getNumberOfOutputs()
	{

		return 1;
	}

	public Class<?>[] getTypeOfInputs()
	{

		return new Class<?>[] { FSAModel.class, FSAModel.class };
	}

	public Class<?>[] getTypeOfOutputs()
	{

		return new Class<?>[] { Boolean.class };
	}

	public List<String> getWarnings()
	{

		return new LinkedList<String>();
	}

	public Object[] perform(Object[] arg0)
	{

		FSAModel model1 = ((FSAModel)arg0[0]);
		FSAModel model2 = ((FSAModel)arg0[1]);

		boolean equals;

		equals = (Boolean)OperationManager
				.instance().getOperation("subset").perform(new Object[] {
						model1, model2 })[0];

		equals &= (Boolean)OperationManager
				.instance().getOperation("subset").perform(new Object[] {
						model2, model1 })[0];

		if (equals)
		{
			resultMessage = "The languages are equal.";
		}
		else
		{
			resultMessage = "The languages are not equal.";
		}

		return new Object[] { new Boolean(equals) };

	}

}
