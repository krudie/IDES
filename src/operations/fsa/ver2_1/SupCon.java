/**
 * 
 */
package operations.fsa.ver2_1;

import java.util.Iterator;

import model.ModelManager;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.State;
import pluggable.operation.FilterOperation;
import pluggable.operation.Operation;
import pluggable.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class SupCon extends AbstractOperation {

	public SupCon() {
		NAME = "SupCon";
		DESCRIPTION = "Returns an automaton that generates the supremal" +
				" controllable sublanguage of the plant with respect to the" +
				" given specification.";
		
		//WARNING - Ensure that input type and description always match!	
		inputType = new Class[]{FSAModel.class,FSAModel.class};
		inputDesc = new String[]{"Plant","Specification"};

		//WARNING - Ensure that output type and description always match!
		outputType = new Class[]{FSAModel.class};
		outputDesc = new String[]{"composedAutomaton"};
	}
	
	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=ModelManager.createModel(FSAModel.class,"none");
		SuperVisory.supC((FSAModel)inputs[0],(FSAModel)inputs[1],a);
		FilterOperation fo=OperationManager.getFilterOperation("Control Map");
		fo.filter(new Object[]{a,inputs[0]});
		return new Object[]{a};
	}

}
