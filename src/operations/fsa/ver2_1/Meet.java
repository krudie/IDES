/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Meet extends AbstractOperation
{

	public Meet()
	{
		NAME = "product";
		DESCRIPTION = "Computes an automaton that accepts the"
				+ " intersection of the languages of the given automata. "
				+ "Also known as intersection or meet.";

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
		return new Object[] { Composition.product(models, "none") };
		// FSAModel meetAutomata = ModelManager
		// .instance().createModel(FSAModel.class, "none");
		// Composition.product((FSAModel)inputs[0],
		// (FSAModel)inputs[1],
		// meetAutomata);
		// return new Object[] { meetAutomata };
	}

}
