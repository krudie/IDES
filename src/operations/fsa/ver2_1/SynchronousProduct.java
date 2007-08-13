/**
 * 
 */
package operations.fsa.ver2_1;

import model.ModelManager;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;
import pluggable.operation.Operation;

/**
 *
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class SynchronousProduct extends OperationParent {

	public SynchronousProduct() {
		NAME = "Synchronous Product";
		DESCRIPTION = "Resultant automaton forces " +
			"shared events to occur simultaneously, and allows unshared events" +
			" to interleave.";
		//WARNING - Ensure that input type and description always match!	
		inputType = new Class[]{FSAModel.class,FSAModel.class};
		inputDesc = new String[]{"Finite-state automaton","Finite-state automaton"};

		//WARNING - Ensure that output type and description always match!
		outputType = new Class[]{FSAModel.class};
		outputDesc = new String[]{"composedAutomaton"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=ModelManager.createModel(FSAModel.class,"none");
		Composition.parallel((FSAModel)inputs[0],(FSAModel)inputs[1],a);
		return new Object[]{a};
	}

	
}
