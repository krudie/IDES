/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Containment extends AbstractOperation {

    protected String resultMessage = Hub.string("errorUnableToCompute");

    public Containment() {
        NAME = "subset";
        DESCRIPTION = "Determines if the given candidate sublanguage is contained within the "
                + "given candidate superlanguage.";
        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class, FSAModel.class };
        inputDesc = new String[] { "Candidate sublanguage", "Candidate superlanguage" };

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
        FSAModel a, b;
        if (inputs.length == 2) {
            if (inputs[0] instanceof FSAModel && inputs[1] instanceof FSAModel) {
                a = (FSAModel) inputs[0];
                b = (FSAModel) inputs[1];
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { new Boolean(false) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { new Boolean(false) };
        }

        if (!FSAToolbox.isDeterministic(a)) {
            a = (FSAModel) OperationManager.instance().getOperation("determinize").perform(new Object[] { a })[0];
            warnings.addAll(OperationManager.instance().getOperation("determinize").getWarnings());
        }
        if (!FSAToolbox.isDeterministic(b)) {
            b = (FSAModel) OperationManager.instance().getOperation("determinize").perform(new Object[] { b })[0];
            warnings.addAll(OperationManager.instance().getOperation("determinize").getWarnings());
        }

        boolean contained = Subset.subset(a, b);

        if (warnings.size() != 0) {
            return new Object[] { new Boolean(false) };
        }

        if (contained) {
            resultMessage = "Sublanguage is contained in superlanguage.";
        } else {
            resultMessage = "Sublanguage is not contained in superlanguage.";
        }
        outputDesc = new String[] { resultMessage };

        return new Object[] { new Boolean(contained) };
    }

}
