/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.FilterOperation;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class PrefixClosure extends AbstractOperation implements FilterOperation {

    public PrefixClosure() {
        NAME = "prefixclose";
        DESCRIPTION = "Computes an automaton that " + "generates the prefix closure of the language accepted by the "
                + "input automaton.";

        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class };
        inputDesc = new String[] { "Finite-state automaton" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Prefix-closed version of the automaton" };
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluggable.operation.Operation#perform(java.lang.Object[])
     */
    @Override
    public Object[] perform(Object[] inputs) {
        warnings.clear();
        FSAModel a = ((FSAModel) inputs[0]).clone();
        Unary.prefixClosure(a);
        return new Object[] { a };
    }

    public int[] getInputOutputIndexes() {
        return new int[] { 0 };
    }

    // unknown if this code is ever reached...
    public Object[] filter(Object[] inputs) {
        warnings.clear();
        FSAModel a = (FSAModel) inputs[0];
        Unary.prefixClosure(a);
        return new Object[] { a };
    }
}
