package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.CheckingToolbox;
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

	private LinkedList<String> warnings = new LinkedList<String>();

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

		return warnings;
	}

	public Object[] perform(Object[] inputs)
	{

		warnings.clear();
		FSAModel a, b;
		if (inputs.length == 2)
		{
			if (inputs[0] instanceof FSAModel && inputs[1] instanceof FSAModel)
			{
				a = (FSAModel)inputs[0];
				b = (FSAModel)inputs[1];
			}
			else
			{
				warnings.add(CheckingToolbox.ILLEGAL_ARGUMENT);
				return new Object[] { ModelManager
						.instance().createModel(FSAModel.class) };
			}
		}
		else
		{
			warnings.add(CheckingToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}

		if (!CheckingToolbox.isDeterministic(a))
		{
			a = (FSAModel)OperationManager
					.instance().getOperation("NFAtoDFA")
					.perform(new Object[] { a })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("NFAtoDFA").getWarnings());
		}
		if (!CheckingToolbox.isDeterministic(b))
		{
			b = (FSAModel)OperationManager
					.instance().getOperation("NFAtoDFA")
					.perform(new Object[] { b })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("NFAtoDFA").getWarnings());
		}

		boolean equals;

		equals = Subset.subset(a, b);

		equals &= Subset.subset(b, a);

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
