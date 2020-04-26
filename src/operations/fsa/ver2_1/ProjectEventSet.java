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
public class ProjectEventSet implements Operation {

    private LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {
        return "Computes a projection of the given automaton such that all of the specified"
                + " events have been removed. Epsilon transitions are not removed.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Finite-State automaton", "Events to project out" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { "Automaton with events projected out" };
    }

    public String getName() {
        return "project";
    }

    public int getNumberOfInputs() {

        return 2;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public Class<?>[] getTypeOfInputs() {
        return new Class<?>[] { FSAModel.class, DESEventSet.class };
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

        DESEventSet eventsToRemove;
        FSAModel projection = ModelManager.instance().createModel(FSAModel.class);

        if (arg0.length == 2) {
            if ((arg0[0] instanceof FSAModel) && (arg0[1] instanceof DESEventSet)) {
                model = (FSAModel) arg0[0];
                eventsToRemove = (DESEventSet) arg0[1];

            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        projection = Project.projectCustom(model, eventsToRemove, false);

        return new Object[] { projection };
    }

}