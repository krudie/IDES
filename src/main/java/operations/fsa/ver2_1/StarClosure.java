package operations.fsa.ver2_1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ides.api.model.fsa.FSAModel;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

/**
 * Creates an FSAModel representation of the Kleene closure of an event set.
 * 
 * @author Valerie Sugarman
 */
public class StarClosure implements Operation {

    protected LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {
        return "Produces an automaton generating all finite sequences over the given event set, including the empty string. Also known as the Kleene star.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Event set" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { "FSA of Kleene Closure" };
    }

    public String getName() {
        return "kleeneclosure";
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

        FSAModel model = ModelManager.instance().createModel(FSAModel.class);
        for (Iterator<DESEvent> i = set.iterator(); i.hasNext();) {
            SupervisoryEvent e = model.assembleCopyOf(i.next());
            model.add(e);
        }
        model = (FSAModel) OperationManager.instance().getFilterOperation("complement")
                .filter(new Object[] { model })[0];

        return new Object[] { model };

    }

}
