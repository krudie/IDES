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
public class Accessible extends AbstractOperation implements FilterOperation {

	public Accessible() {
		NAME = "Accessible";
		DESCRIPTION = "Removes all states" +
				" that are not reachable from the initial state.";
		
		//WARNING - Ensure that input type and description always match!	
		inputType = new Class[]{FSAModel.class};
		inputDesc = new String[]{"Finite-state Automaton"};

		//WARNING - Ensure that output type and description always match!
		outputType = new Class[]{FSAModel.class};
		outputDesc = new String[]{"Finite-state Automaton"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=((FSAModel)inputs[0]).clone();
		Unary.buildStateCompositionOfClone(a);		
		Unary.accessible(a);
		return new Object[]{a};
	}

	public int[] getInputOutputIndexes()
	{
		return new int[]{0};
	}
	
	//code never reached ******************
	public Object[] filter(Object[] inputs)
	{
		FSAModel a=(FSAModel)inputs[0];
		Unary.accessible(a);
		return new Object[]{a};
	}
	//end code never reached **************
}
