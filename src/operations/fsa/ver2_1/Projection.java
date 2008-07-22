/**
 * 
 */
package operations.fsa.ver2_1;

import model.ModelManager;
import model.fsa.FSAModel;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Projection extends AbstractOperation
{

	public Projection()
	{
		NAME = "project";
		DESCRIPTION = "Returns a projection"
				+ " of the given automaton such that all unobservable events"
				+ " have been removed.";

		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class };
		inputDesc = new String[] { "Finite-state automaton" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { FSAModel.class };
		outputDesc = new String[] { "modifiedAutomaton" };
	}

	/*
	 * (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	@Override
	public Object[] perform(Object[] inputs)
	{
		FSAModel a = ModelManager.createModel(FSAModel.class, "none");
		Composition.observer((FSAModel)inputs[0], a);
		return new Object[] { a };
	}

}
