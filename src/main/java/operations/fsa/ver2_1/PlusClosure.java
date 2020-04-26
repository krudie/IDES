package operations.fsa.ver2_1;

import java.util.LinkedList;
import java.util.List;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;
import util.AnnotationKeys;

/**
 * Creates an FSAModel representation of the Kleene closure of an event set
 * *without the null string*.
 * 
 * @author Valerie Sugarman
 */
public class PlusClosure implements Operation {

    protected LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {
        return "Generates the Kleene closure minus the empty string.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Event set" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { "Finite State Automaton" };
    }

    public String getName() {
        return "+closure";
    }

    public int getNumberOfInputs() {
        return 1;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public Class<?>[] getTypeOfInputs() {
        return new Class<?>[] { DESEventSet.class };
    }

    public Class<?>[] getTypeOfOutputs() {
        return new Class<?>[] { FSAModel.class };
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public Object[] perform(Object[] inputs) {
        warnings.clear();
        DESEventSet set;

        if (inputs.length >= 1) {
            if (inputs[0] instanceof DESEventSet) {
                set = (DESEventSet) inputs[0];

            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        FSAModel model = (FSAModel) OperationManager.instance().getOperation("kleeneclosure")
                .perform(new Object[] { set })[0];
        warnings.addAll(OperationManager.instance().getOperation("kleeneclosure").getWarnings());
        FSAModel epsilon = ModelManager.instance().createModel(FSAModel.class);
        FSAState state = epsilon.assembleState();
        state.setInitial(true);
        state.setMarked(true);
        epsilon.add(state);

        model = (FSAModel) OperationManager.instance().getOperation("setminus")
                .perform(new Object[] { model, epsilon })[0];
        warnings.addAll(OperationManager.instance().getOperation("setminus").getWarnings());
        model.removeAnnotation(AnnotationKeys.COMPOSED_OF);
        return new Object[] { model };

    }

}
