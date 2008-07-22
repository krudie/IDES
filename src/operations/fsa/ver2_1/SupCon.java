/**
 * 
 */
package operations.fsa.ver2_1;

import model.ModelManager;
import model.fsa.FSAModel;
import pluggable.operation.FilterOperation;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class SupCon extends AbstractOperation
{

	public SupCon()
	{
		NAME = "supcon";
		DESCRIPTION = "Returns an automaton that generates the supremal"
				+ " controllable sublanguage of the plant with respect to the"
				+ " given specification.";

		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class, FSAModel.class };
		inputDesc = new String[] { "Plant", "Specification" };

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
		FSAModel a = ModelManager.createModel(FSAModel.class, "none");
		SuperVisory.supC((FSAModel)inputs[0], (FSAModel)inputs[1], a);
		FilterOperation fo = new ControlMap();
		fo.filter(new Object[] { a, inputs[0] });
		return new Object[] { a };
	}

}
