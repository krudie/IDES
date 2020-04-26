/**
 * 
 */
package ides.api.plugin.operation;

/**
 * Interface for DES operations which modify their arguments.
 * 
 * @author Lenko Grigorov
 */
public interface FilterOperation extends Operation {

    /**
     * The indexes of the inputs which will be modified directly by
     * {@link #filter(Object[])}.
     * 
     * @return indexes of the inputs which will be modified directly by
     *         {@link #filter(Object[])}
     * @see #filter(Object[])
     */
    public abstract int[] getInputOutputIndexes();

    /**
     * Performs the operation directly on the input/output arguments. The inputs
     * accepted by this method have to be of the same number, type and semantics as
     * the inputs for {@link Operation#perform(Object[])}. The outputs produced by
     * this method have to be of the same number, type and semantics as the outputs
     * from {@link Operation#perform(Object[])}. Since some inputs may serve as
     * outputs, they have to be included in the array with outputs.
     * 
     * @param inputs inputs to the operation (must conform to the types described in
     *               {@link Operation#getTypeOfInputs()})
     * @return outputs of the operation (must conform to the types described in
     *         {@link Operation#getTypeOfOutputs()}). The inputs which were modified
     *         have to appear in the array of outputs.
     * @see Operation#perform(Object[])
     */
    public abstract Object[] filter(Object[] inputs);
}
