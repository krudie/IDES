/**
 * 
 */
package pluggable.operation;

/**
 * @author Lenko Grigorov
 *
 */
public interface Operation {

	public abstract String getName();
	public abstract int getNumberOfInputs();
	public abstract Class[] getTypeOfInputs();
	public abstract String[] getDescriptionOfInputs();
	public abstract int getNumberOfOutputs();
	public abstract Class[] getTypeOfOutputs();
	public abstract String[] getDescriptionOfOutputs();
	
	public abstract Object[] perform(Object[] inputs);
}
