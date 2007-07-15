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
public class SupCon implements Operation {

	public final static String NAME="SupCon";
	public final static String DESCRIPTION="Returns the supremal" +
			" controllable sublanguage of the plant with respect to the" +
			" given specification.";
	
	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getName()
	 */
	public String getName() {
		return NAME;
	}
	
	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getNumberOfInputs()
	 */
	public int getNumberOfInputs() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfInputs()
	 */
	public Class[] getTypeOfInputs() {
		return new Class[]{FSAModel.class,FSAModel.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfInputs()
	 */
	public String[] getDescriptionOfInputs() {
		return new String[]{"Plant","Specification"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getNumberOfOutputs()
	 */
	public int getNumberOfOutputs() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfOutputs()
	 */
	public Class[] getTypeOfOutputs() {
		return new Class[]{FSAModel.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfOutputs()
	 */
	public String[] getDescriptionOfOutputs() {
		return new String[]{"automaton of supremal controllable sublanguage"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=ModelManager.createModel(FSAModel.class,"none");
		SuperVisory.supC((FSAModel)inputs[0],(FSAModel)inputs[1],a);
		FilterOperation fo=OperationManager.getFilterOperation("control map");
		fo.filter(new Object[]{a,inputs[0]});
		return new Object[]{a};
	}

}
