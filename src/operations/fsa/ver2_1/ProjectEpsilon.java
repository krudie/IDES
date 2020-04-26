package operations.fsa.ver2_1;

import java.util.LinkedList;
import java.util.List;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;

/**
 * @author Valerie Sugarman
 */
public class ProjectEpsilon implements Operation {

    private LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {
        return "Computes an automaton which recognizes the same language as the given automaton and contains"
                + " no epsilon transitions.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Finite-State automaton" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { "FSA with null transitions projected out" };
    }

    public String getName() {
        return "removeepsilon";
    }

    public int getNumberOfInputs() {
        return 1;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public Class<?>[] getTypeOfInputs() {
        return new Class<?>[] { FSAModel.class };
    }

    public Class<?>[] getTypeOfOutputs() {
        return new Class<?>[] { FSAModel.class };
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public Object[] perform(Object[] arg0) {

        warnings.clear();
        FSAModel model;
        FSAModel projection = ModelManager.instance().createModel(FSAModel.class);

        if (arg0.length == 1) {
            if (arg0[0] instanceof FSAModel) {
                model = ((FSAModel) arg0[0]);
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        DESEventSet eventSet = ModelManager.instance().createEmptyEventSet();

        projection = Project.projectCustom(model, eventSet, true);

        return new Object[] { projection };
    }
}
