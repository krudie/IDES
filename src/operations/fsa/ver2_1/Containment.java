/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Containment extends AbstractOperation
{

	public Containment()
	{
		NAME = "subset";
		DESCRIPTION = "Determines if the given candidate sublanguage is contained within the "
				+ "given candidate superlanguage.";
		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class, FSAModel.class };
		inputDesc = new String[] { "Candidate sublanguage",
				"Candidate superlanguage" };

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
					.instance().getOperation("determinize")
					.perform(new Object[] { a })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("determinize").getWarnings());
		}
		if (!CheckingToolbox.isDeterministic(b))
		{
			b = (FSAModel)OperationManager
					.instance().getOperation("determinize")
					.perform(new Object[] { b })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("determinize").getWarnings());
		}

		boolean contained = Subset.subset(a, b);

		String resultMessage = "";
		if (contained)
		{
			resultMessage = "Sublanguage is contained in superlanguage.";
		}
		else
		{
			resultMessage = "Sublanguage is not contained in superlanguage.";
		}
		outputDesc = new String[] { resultMessage };

		return new Object[] { new Boolean(contained) };
	}

}
