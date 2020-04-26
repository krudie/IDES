/**
 * 
 */
package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.FilterOperation;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class SupCon extends AbstractOperation {

    public SupCon() {
        NAME = "supcon";
        DESCRIPTION = "Computes an automaton that accepts the supremal"
                + " controllable sublanguage of the specification with respect to the" + " given plant.";

        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class, FSAModel.class };
        inputDesc = new String[] { "Plant", "Specification" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Supervisor" };
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluggable.operation.Operation#perform(java.lang.Object[])
     */
    @Override
    public Object[] perform(Object[] inputs) {
        warnings.clear();
        FSAModel model1;
        FSAModel model2;
        if (inputs.length == 2) {
            if (inputs[0] instanceof FSAModel && inputs[1] instanceof FSAModel) {
                model1 = (FSAModel) inputs[0];
                model2 = (FSAModel) inputs[1];
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        if (FSAToolbox.hasControllabilityConflict(new FSAModel[] { model1, model2 })) {
            warnings.add(FSAToolbox.ERROR_CONTROL);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        FSAModel a = ModelManager.instance().createModel(FSAModel.class, "none");
        SuperVisory.supC(model1, model2, a);
        FilterOperation fo = new ControlMap();
        fo.filter(new Object[] { a, inputs[0] });
        warnings.addAll(fo.getWarnings());
        return new Object[] { a };
    }

}
