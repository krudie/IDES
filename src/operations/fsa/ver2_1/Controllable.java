/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Controllable extends AbstractOperation
{

	public Controllable()
	{
		NAME = "controllable";
		DESCRIPTION = "Determines if the plant"
				+ " is controllable with respect to the specification.";
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

		boolean result = SuperVisory.controllable((FSAModel)inputs[0],
				(FSAModel)inputs[1]);
		String resultMessage;
		if (result)
		{
			resultMessage = "Plant is controllable with respect to the specification.";
		}
		else
		{
			resultMessage = "Plant is not controllable with respect to the specification.";
		}
		outputDesc = new String[] { resultMessage };
		return new Object[] { new Boolean(result) };
	}

}
