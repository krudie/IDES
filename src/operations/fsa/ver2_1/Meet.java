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
public class Meet extends AbstractOperation
{

	public Meet()
	{
		NAME = "product";
		DESCRIPTION = "Produces an automaton that accepts the"
				+ " intersection of the languages of the given automata. "
				+ "Also known as intersection or meet.";

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
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	@Override
	public Object[] perform(Object[] inputs)
	{
		FSAModel meetAutomata = ModelManager
				.createModel(FSAModel.class, "none");
		Composition.product((FSAModel)inputs[0],
				(FSAModel)inputs[1],
				meetAutomata);
		return new Object[] { meetAutomata };
	}

}
