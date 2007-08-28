/**
 * 
 */
package operations.fsa.ver2_1;

import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;
import pluggable.operation.FilterOperation;

/**
 *
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class PrefixClosure extends AbstractOperation implements FilterOperation {
	
	public PrefixClosure() {
		NAME = "Prefix Closure";
		DESCRIPTION = "Creates an automaton that " +
				"generates the prefix closure of the language accepted by the " +
				"input automaton.";
		
		//WARNING - Ensure that input type and description always match!	
		inputType = new Class[]{FSAModel.class};
		inputDesc = new String[]{"Finite-state automaton"};

		//WARNING - Ensure that output type and description always match!
		outputType = new Class[]{FSAModel.class};
		outputDesc = new String[]{"modifiedAutomaton"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=((FSAModel)inputs[0]).clone();
		Unary.buildStateCompositionOfClone(a);		
		Unary.prefixClosure(a);
		return new Object[]{a};
	}

	public int[] getInputOutputIndexes()
	{
		return new int[]{0};
	}
	
	//unknown if this code is ever reached...
	public Object[] filter(Object[] inputs)
	{
		FSAModel a=(FSAModel)inputs[0];
		Unary.prefixClosure(a);
		return new Object[]{a};
	}
}
