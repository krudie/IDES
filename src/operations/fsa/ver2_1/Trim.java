/**
 * 
 */
package operations.fsa.ver2_1;

import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;
import pluggable.operation.FilterOperation;
import pluggable.operation.Operation;

/**
 *
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Trim extends OperationParent implements FilterOperation {

	public Trim() {
		NAME = "Trim";
		DESCRIPTION = "Removes all states that are not" +
		"both reachable and coreachable.";
		
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
		Unary.trim(a);
		return new Object[]{a};
	}

	public int[] getInputOutputIndexes()
	{
		return new int[]{0};
	}
	
	public Object[] filter(Object[] inputs)
	{
		FSAModel a=(FSAModel)inputs[0];
		Unary.trim(a);
		return new Object[]{a};
	}
}
