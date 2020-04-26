package operations.fsa.ver2_1;

import java.util.LinkedList;
import java.util.List;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

/**
 * @author Valerie Sugarman
 */
public class Equality implements Operation {

    protected String resultMessage;

    private LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {

        return "Determines if the languages recognized by two automata are equal.";
    }

    public String[] getDescriptionOfInputs() {

        return new String[] { "Finite-state automaton", "Finite-state automaton" };
    }

    public String[] getDescriptionOfOutputs() {

        return new String[] { resultMessage };
    }

    public String getName() {

        return "langequals";
    }

    public int getNumberOfInputs() {

        return 2;
    }

    public int getNumberOfOutputs() {

        return 1;
    }

    public Class<?>[] getTypeOfInputs() {

        return new Class<?>[] { FSAModel.class, FSAModel.class };
    }

    public Class<?>[] getTypeOfOutputs() {

        return new Class<?>[] { Boolean.class };
    }

    public List<String> getWarnings() {

        return warnings;
    }

    public Object[] perform(Object[] inputs) {

        resultMessage = Hub.string("errorUnableToCompute");
        warnings.clear();
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

        boolean equals;

        equals = Subset.subset(a, b);

        equals &= Subset.subset(b, a);

        if (warnings.size() != 0) {
            return new Object[] { new Boolean(false) };
        }

        if (equals) {
            resultMessage = "The languages are equal.";
        } else {
            resultMessage = "The languages are not equal.";
        }

        return new Object[] { new Boolean(equals) };

    }

}
