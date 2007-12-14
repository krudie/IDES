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
public class SynchronousProduct extends AbstractOperation
{

	public SynchronousProduct()
	{
		NAME = "sync";
		DESCRIPTION = "Produces the synchronous product of automata (also known as parallel composition). "
				+ "Resultant automaton forces "
				+ "shared events to occur simultaneously, and allows unshared events"
				+ " to interleave.";
		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class, FSAModel.class };
		inputDesc = new String[] { "Finite-state automaton",
				"Finite-state automaton" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { FSAModel.class };
		outputDesc = new String[] { "composedAutomaton" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	@Override
	public Object[] perform(Object[] inputs)
	{
		FSAModel a = ModelManager.createModel(FSAModel.class, "none");
		Composition.parallel((FSAModel)inputs[0], (FSAModel)inputs[1], a);
		return new Object[] { a };
	}

}
