/**
 * 
 */
package pluggable.operation;

/**
 * @author Lenko Grigorov
 *
 */
public interface Operation {

	/**
	 * Display name of the operation. The same string will be used to
	 * index and identify the operation.
	 * @return the name of the operation
	 */
	public abstract String getName();
	
	/**
	 * Nuber of inputs for the operation. If it can handle an unbounded
	 * number of inputs, return -1.
	 * @return nuber of inputs for the operation; -1 if unbounded
	 */
	public abstract int getNumberOfInputs();
	
	/**
	 * Class types of the inputs. If the inputs are unbounded, the last
	 * supplied class type will be assumed for all inputs with a higher index.
	 * E.g., if {<code>Boolean</code>,<code>FSAModel</code>} are supplied, the first input will be
	 * <code>Boolean</code> and all remaining inputs are assumed to be of type <code>FSAModel</code>.
	 * This type extension is applied only when the number of inputs is
	 * unbounded (i.e., when {@link #getNumberOfInputs()} returns -1).
	 * <p>Whenever possible, please use the general interfaces from the package <code>model.*</code>
	 * (such as <code>FSAModel</code>) instead of specific implementations.
	 * @return class types of the inputs
	 */
	public abstract Class[] getTypeOfInputs();
	public abstract String[] getDescriptionOfInputs();
	public abstract int getNumberOfOutputs();
	public abstract Class[] getTypeOfOutputs();
	public abstract String[] getDescriptionOfOutputs();
	
	public abstract Object[] perform(Object[] inputs);
}
