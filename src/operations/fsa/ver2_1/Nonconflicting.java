/**
 * 
 */
package operations.fsa.ver2_1;

import model.fsa.FSAModel;
import pluggable.operation.Operation;
import pluggable.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Nonconflicting implements Operation {

	public final static String NAME="Nonconflicting";
	public final static String DESCRIPTION="Determines if" +
			" the two supplied automata are nonconflicting.";
		

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
		return new String[]{"Finite-state automaton","Finite-state automaton"};
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
		return new Class[]{Boolean.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfOutputs()
	 */
	public String[] getDescriptionOfOutputs() {
		return new String[]{"Are the two languages nonconflicting"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=(FSAModel)inputs[0];
		FSAModel b=(FSAModel)inputs[1];

		FSAModel l=(FSAModel)OperationManager.getOperation("Meet").perform(new Object[]{
			OperationManager.getOperation("Prefix Closure").perform(new Object[]{a})[0],
			OperationManager.getOperation("Prefix Closure").perform(new Object[]{b})[0]
		})[0];
		FSAModel r=(FSAModel)OperationManager.getOperation("Prefix Closure").perform(new Object[]{
				OperationManager.getOperation("Meet").perform(new Object[]{a,b})[0]})[0];
		boolean equal=((Boolean)OperationManager.getOperation("containment").perform(new Object[]{
				l,r})[0]).booleanValue();
		return new Object[]{new Boolean(equal)};
	}

}
