/**
 * 
 */
package operations.fsa.ver2_1;

import model.fsa.FSAModel;
import pluggable.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Nonconflicting extends AbstractOperation
{

	public Nonconflicting()
	{
		NAME = "nonconflict";
		DESCRIPTION = "Determines if"
				+ " the two input automata are nonconflicting.";

		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class, FSAModel.class };
		inputDesc = new String[] { "Finite-state automaton",
				"Finite-state automaton" };

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
		FSAModel a = (FSAModel)inputs[0];
		FSAModel b = (FSAModel)inputs[1];

		FSAModel l = (FSAModel)OperationManager
				.getOperation("product").perform(new Object[] {
						OperationManager
								.getOperation("prefixclose")
								.perform(new Object[] { a })[0],
						OperationManager
								.getOperation("prefixclose")
								.perform(new Object[] { b })[0] })[0];
		FSAModel r = (FSAModel)OperationManager
				.getOperation("prefixclose")
				.perform(new Object[] { OperationManager
						.getOperation("product").perform(new Object[] { a, b })[0] })[0];
		boolean equal = ((Boolean)OperationManager
				.getOperation("subset").perform(new Object[] { l, r })[0])
				.booleanValue();
		String output;
		if (equal)
		{
			output = "The two languages are nonconflicting.";
		}
		else
		{
			output = "The two languages are not nonconflicting.";
		}
		outputDesc = new String[] { output };

		return new Object[] { new Boolean(equal) };
	}
}
