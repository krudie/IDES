/**
 * 
 */
package operations.fsa.ver1;

import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import pluggable.operation.Operation;

/**
 *
 * @author Lenko Grigorov
 */
public class Observer implements Operation {

	public final static String NAME="observer";

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
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfInputs()
	 */
	public Class[] getTypeOfInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfInputs()
	 */
	public String[] getDescriptionOfInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getNumberOfOutputs()
	 */
	public int getNumberOfOutputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfOutputs()
	 */
	public Class[] getTypeOfOutputs() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfOutputs()
	 */
	public String[] getDescriptionOfOutputs() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		Automaton a=new Automaton("none");
		Composition.observer((FSAModel)inputs[0],a);
		return new Object[]{a};
	}

}
