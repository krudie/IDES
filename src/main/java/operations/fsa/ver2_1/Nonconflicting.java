/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Nonconflicting extends AbstractOperation {

    protected String resultMessage = Hub.string("errorUnableToCompute");

    public Nonconflicting() {
        NAME = "nonconflict";
        DESCRIPTION = "Determines if" + " the two input automata are nonconflicting.";

        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class, FSAModel.class };
        inputDesc = new String[] { "Finite-state automaton", "Finite-state automaton" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { Boolean.class };
        outputDesc = new String[] { resultMessage };
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluggable.operation.Operation#perform(java.lang.Object[])
     */
    @Override
    public Object[] perform(Object[] inputs) {
        warnings.clear();
        resultMessage = Hub.string("errorUnableToCompute");
        FSAModel a = (FSAModel) inputs[0];
        FSAModel b = (FSAModel) inputs[1];

        FSAModel tempInput1 = (FSAModel) OperationManager.instance().getOperation("prefixclose")
                .perform(new Object[] { a })[0];
        warnings.addAll(OperationManager.instance().getOperation("prefixclose").getWarnings());
        FSAModel tempInput2 = (FSAModel) OperationManager.instance().getOperation("prefixclose")
                .perform(new Object[] { b })[0];
        warnings.addAll(OperationManager.instance().getOperation("prefixclose").getWarnings());

        FSAModel l = (FSAModel) OperationManager.instance().getOperation("product")
                .perform(new Object[] { tempInput1, tempInput2 })[0];
        warnings.addAll(OperationManager.instance().getOperation("product").getWarnings());

        FSAModel r = (FSAModel) OperationManager.instance().getOperation("prefixclose").perform(new Object[] {
                OperationManager.instance().getOperation("product").perform(new Object[] { a, b })[0] })[0];
        warnings.addAll(OperationManager.instance().getOperation("prefixclose").getWarnings());
        warnings.addAll(OperationManager.instance().getOperation("product").getWarnings());

        boolean equal = ((Boolean) OperationManager.instance().getOperation("subset").perform(new Object[] { l, r })[0])
                .booleanValue();
        warnings.addAll(OperationManager.instance().getOperation("subset").getWarnings());

        if (warnings.size() != 0) {
            return new Object[] { new Boolean(false) };
        }

        if (equal) {
            resultMessage = "The two languages are nonconflicting.";
        } else {
            resultMessage = "The two languages are not nonconflicting.";
        }

        outputDesc = new String[] { resultMessage };

        return new Object[] { new Boolean(equal) };
    }
}
