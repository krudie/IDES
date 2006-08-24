/**
 * 
 */
package pluggable.operation;

/**
 * Interface for DES operations.
 * 
 * @author Lenko Grigorov
 */
public interface Operation {

	/**
	 * Name of the operation. The same string will be used to
	 * index and identify the operation as well as for display purposes.
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
	 * @see #getNumberOfInputs()
	 * @see #getDescriptionOfInputs()
	 */
	public abstract Class[] getTypeOfInputs();
	
	/**
	 * User-readable and understandable description for each input argument.
	 * If the inputs are unbounded, the last
	 * supplied description will be assumed for all inputs with a higher index.
	 * E.g., if {<code>"String 1"</code>,<code>"String 2"</code>} are supplied,
	 * the first input will be described to the user as <code>"String 1"</code>
	 * and all remaining inputs will be described as <code>"String 2"</code>.
	 * This type extension is applied only when the number of inputs is
	 * unbounded (i.e., when {@link #getNumberOfInputs()} returns -1).
	 * @return user-readable descriptions of the inputs
	 * @see #getNumberOfInputs() 
	 */
	public abstract String[] getDescriptionOfInputs();
	
	/**
	 * Nuber of outputs from the operation.
	 * @return nuber of outputs from the operation
	 */
	public abstract int getNumberOfOutputs();
	
	/**
	 * Class types of the outputs.
	 * <p>Whenever possible, please use the general interfaces from the package <code>model.*</code>
	 * (such as <code>FSAModel</code>) instead of specific implementations.
	 * @return class types of the inputs
	 * @see #getNumberOfOutputs()
	 * @see #getDescriptionOfOutputs()
	 */	
	public abstract Class[] getTypeOfOutputs();
	
	/**
	 * User-readable and understandable description for each output argument.
	 * @return user-readable descriptions of the outputs
	 * @see #getNumberOfOutputs()
	 */
	public abstract String[] getDescriptionOfOutputs();
	
	/**
	 * Perform the operation.
	 * @param inputs inputs to the operation (must conform to the types described in {@link #getTypeOfInputs()})
	 * @return outputs of the operation (must conform to the types described in {@link #getTypeOfOutputs()})
	 */
	public abstract Object[] perform(Object[] inputs);
}
