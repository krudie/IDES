package operations.fsa.ver2_1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

/*
 * TODO how to deal with epsilon... subset will determinize. If given a new LinkedList() with no events, will turn it into and epsilon language model.
 * DECISON: don't allow them to be added to a string, no point.
 */

/**
 * @author Valerie Sugarman
 */
public class ContainsString implements Operation {

    private LinkedList<String> warnings = new LinkedList<String>();

    private String resultMessage;

    public String getDescription() {
        return "Checks whether a given string is recognized by the given automaton.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Finite-State Automaton", "Event String" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { resultMessage };
    }

    public String getName() {
        return "stringrecognized";
    }

    public int getNumberOfInputs() {
        return 2;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public Class<?>[] getTypeOfInputs() {
        return new Class<?>[] { FSAModel.class, new LinkedList<DESEvent>().getClass() };
    }

    public Class<?>[] getTypeOfOutputs() {
        return new Class<?>[] { Boolean.class };
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public Object[] perform(Object[] inputs) {
        warnings.clear();
        resultMessage = FSAToolbox.ERROR_UNABLE_TO_COMPUTE;
        FSAModel model;
        List<?> input;
        List<DESEvent> events = new LinkedList<DESEvent>();

        if (inputs.length >= 1) {
            if (inputs[0] instanceof FSAModel && inputs[1] instanceof List<?>) {
                model = (FSAModel) inputs[0];
                input = (List<?>) inputs[1];
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { new Boolean(false) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { new Boolean(false) };
        }

        for (Iterator<?> i = input.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof DESEvent) {
                events.add((DESEvent) o);
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { new Boolean(false) };
            }
        }

        FSAModel testString = FSAToolbox.modelFromList(events);

        boolean contained = (Boolean) OperationManager.instance().getOperation("subset")
                .perform(new Object[] { testString, model })[0];
        warnings.addAll(OperationManager.instance().getOperation("subset").getWarnings());

        if (warnings.size() != 0) {
            return new Object[] { new Boolean(false) };
        }

        if (contained) {
            resultMessage = "The string is contained in the model.";
        } else {
            resultMessage = "The string is not contained in the model.";
        }

        return new Object[] { new Boolean(contained) };
    }

}
