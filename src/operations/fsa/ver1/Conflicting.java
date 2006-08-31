/**
 * 
 */
package operations.fsa.ver1;

import model.fsa.FSAModel;
import pluggable.operation.Operation;
import pluggable.operation.OperationManager;

/**
 * @author lenko
 *
 */
public class Conflicting implements Operation {

	public final static String NAME="conflicting";

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getName()
	 */
	public String getName() {
		return NAME;
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
		return new String[]{"are the two languages conflicting"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=(FSAModel)inputs[0];
		FSAModel b=(FSAModel)inputs[1];

		FSAModel l=(FSAModel)OperationManager.getOperation("meet").perform(new Object[]{
			OperationManager.getOperation("prefix closure").perform(new Object[]{a})[0],
			OperationManager.getOperation("prefix closure").perform(new Object[]{b})[0]
		})[0];
		FSAModel r=(FSAModel)OperationManager.getOperation("prefix closure").perform(new Object[]{
				OperationManager.getOperation("meet").perform(new Object[]{a,b})[0]})[0];
		boolean equal=((Boolean)OperationManager.getOperation("containment").perform(new Object[]{
				l,r})[0]).booleanValue()&&
			((Boolean)OperationManager.getOperation("containment").perform(new Object[]{
				r,l})[0]).booleanValue();
		return new Object[]{new Boolean(!equal)};
	}

}
