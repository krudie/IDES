/**
 * 
 */
package operations.fsa.ver1;

import main.Hub;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import pluggable.operation.Operation;

/**
 *
 * @author Lenko Grigorov
 */
public class Controllable implements Operation {

	public final static String NAME="controllable";

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
		Hub.displayAlert("The result is: "+SuperVisory.controllable((FSAModel)inputs[0],(FSAModel)inputs[1]));
		return new Object[]{new Automaton("")};
	}

}
