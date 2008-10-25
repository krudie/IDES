/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class SynchronousProduct extends AbstractOperation
{

	public SynchronousProduct()
	{
		NAME = "sync";
		DESCRIPTION = "Computes the synchronous product of automata (also known as parallel composition). "
				+ "Resultant automaton forces "
				+ "shared events to occur simultaneously, and allows unshared events"
				+ " to interleave.";
		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class };
		inputDesc = new String[] { "Finite-state automata" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { FSAModel.class };
		outputDesc = new String[] { "Composed automata" };
	}

	public int getNumberOfInputs()
	{
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	@Override
	public Object[] perform(Object[] inputs)
	{
		FSAModel[] models = new FSAModel[inputs.length];
		for (int i = 0; i < inputs.length; ++i)
		{
			if (!(inputs[i] instanceof FSAModel))
			{
				throw new IllegalArgumentException();
			}
			models[i] = (FSAModel)inputs[i];
		}
		return new Object[] { Composition.parallel(models, "none") };
		// FSAModel a = ModelManager
		// .instance().createModel(FSAModel.class, "none");
		// Composition.parallel((FSAModel)inputs[0], (FSAModel)inputs[1], a);
		// return new Object[] { a };
	}

}
