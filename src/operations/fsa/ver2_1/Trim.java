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
public class Trim extends AbstractOperation implements FilterOperation {

    public Trim() {
        NAME = "trim";
        DESCRIPTION = "Removes all states that are not" + " both accessible and coaccessible.";

        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class };
        inputDesc = new String[] { "Finite-state automaton" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Trim part of the automaton" };
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
        Unary.trim(a);
        return new Object[] { a };
    }

    public int[] getInputOutputIndexes() {
        return new int[] { 0 };
    }

    public Object[] filter(Object[] inputs) {
        warnings.clear();
        FSAModel a = (FSAModel) inputs[0];
        Unary.trim(a);
        return new Object[] { a };
    }
}
