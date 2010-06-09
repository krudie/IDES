/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Controllable extends AbstractOperation
{

	public Controllable()
	{
		NAME = "controllable";
		DESCRIPTION = "Determines if the specification"
				+ " is controllable with respect to the plant.";
		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class, FSAModel.class };
		inputDesc = new String[] { "Plant", "Specification" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { Boolean.class };
		outputDesc = new String[] { "resultMessage" };
	}

	/*
	 * (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	@Override
	public Object[] perform(Object[] inputs)
	{
		warnings.clear();
		FSAModel plant;
		FSAModel specification;
		String resultMessage;
		if (inputs.length >= 2)
		{
			if (inputs[0] instanceof FSAModel && inputs[1] instanceof FSAModel)
			{
				plant = (FSAModel)inputs[0];
				specification = (FSAModel)inputs[1];
			}
			else
			{
				warnings.add(CheckingToolbox.ILLEGAL_ARGUMENT);
				return new Object[] { new Boolean(false) };
			}
		}
		else
		{
			warnings.add(CheckingToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
			return new Object[] { new Boolean(false) };
		}

		if (!CheckingToolbox.isDeterministic(plant))
		{
			plant = (FSAModel)OperationManager
					.instance().getOperation("determinize")
					.perform(new Object[] { plant })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("determinize").getWarnings());
		}
		if (!CheckingToolbox.isDeterministic(specification))
		{
			specification = (FSAModel)OperationManager
					.instance().getOperation("determinize")
					.perform(new Object[] { specification })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("determinize").getWarnings());
		}

		boolean result = SuperVisory.controllable(plant, specification);

		if (result)
		{
			resultMessage = "Specification is controllable with respect to the plant.";
		}
		else
		{
			resultMessage = "Specification is not controllable with respect to the plant.";
		}
		outputDesc = new String[] { resultMessage };
		return new Object[] { new Boolean(result) };
	}

}
