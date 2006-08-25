/**
 * 
 */
package operations.fsa.ver1;

import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import pluggable.operation.FilterOperation;
import pluggable.operation.Operation;

/**
 *
 * @author Lenko Grigorov
 */
public class Coaccessible implements FilterOperation {

	public final static String NAME="coaccessible";

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
		return 1;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfInputs()
	 */
	public Class[] getTypeOfInputs() {
		return new Class[]{FSAModel.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfInputs()
	 */
	public String[] getDescriptionOfInputs() {
		return new String[]{"Finite-state automaton"};
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
		return new String[]{"coaccessible automaton"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=((Automaton)inputs[0]).clone();
		Unary.buildStateCompositionOfClone((Automaton)a);		
		Unary.coaccessible(a);
		return new Object[]{a};
	}

	public int[] getInputOutputIndexes()
	{
		return new int[]{0};
	}
	
	public Object[] filter(Object[] inputs)
	{
		FSAModel a=(FSAModel)inputs[0];
		Unary.coaccessible(a);
		return new Object[]{a};
	}
}
