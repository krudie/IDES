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
public class NFAtoDFA implements Operation {

    private LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {

        return "Computes an equivalent deterministic automaton from the input automaton.";
    }

    public String[] getDescriptionOfInputs() {

        return new String[] { "Finite-state automaton" };

    }

    public String[] getDescriptionOfOutputs() {

        return new String[] { "Language-equivalent deterministic automaton" };
    }

    public String getName() {

        return "determinize";
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
        FSAModel nfa;
        FSAModel dfa = ModelManager.instance().createModel(FSAModel.class);

        if (arg0.length == 1) {
            if (arg0[0] instanceof FSAModel) {
                nfa = ((FSAModel) arg0[0]);
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        DESEventSet eventSet = ModelManager.instance().createEmptyEventSet();

        dfa = Project.projectCustom(nfa, eventSet, true);

        return new Object[] { dfa };
    }
}
