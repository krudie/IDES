package operations.fsa.ver2_1;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

/**
 * "A Course in Formal Languages, Automata and Groups" by Ian M. Chiswell
 * Springer London, 2009. Take the union of the transition diagrams of M1 and
 * M2, with new null string edges from the final states of M1 to the initial
 * state of M2. The new initial state is that of M1, and the final states are
 * those of M2. The null string transitions are then projected out of the
 * result.
 * 
 * @author Valerie Sugarman algorithm taken from
 */
public class Concatenation implements Operation {

    private LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {

        return "Computes an automaton representing the concatenation of the languages of two automata.";
    }

    public String[] getDescriptionOfInputs() {

        return new String[] { "Prefix finite-state automaton", "Suffix finite-state automaton" };
    }

    public String[] getDescriptionOfOutputs() {

        return new String[] { "Automaton recognizing the concatenation" };
    }

    public String getName() {

        return "concatenate";
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

        return new Class<?>[] { FSAModel.class };
    }

    public List<String> getWarnings() {

        return warnings;
    }

    /**
     * Performs the concatenation operation.
     * 
     * @param arg0 An array of type Object. The object at index 0 is the prefix
     *             language and the object at index 1 is the suffix language.
     * @return The concatenation of the prefix and the suffix. If either are the
     *         empty language, or the input array is not as expected an empty model
     *         will be returned.
     */
    public Object[] perform(Object[] arg0) {
        warnings.clear();
        FSAModel model1;
        FSAModel model2;

        /*
         * In the following hash table, the original state is the key, copy is the
         * value. This is used to avoid duplication when copying states from one model
         * to another and calling copyStateInto
         */
        Hashtable<FSAState, FSAState> states = new Hashtable<FSAState, FSAState>();
        Hashtable<String, SupervisoryEvent> events = new Hashtable<String, SupervisoryEvent>();
        HashSet<Long> model2CopiedInitialStateIds = new HashSet<Long>();

        // Verify validity of parameters
        if (arg0.length == 2) {
            if ((arg0[0] instanceof FSAModel) && (arg0[1] instanceof FSAModel)) {
                model1 = ((FSAModel) arg0[0]).clone();
                model2 = ((FSAModel) arg0[1]);
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }
        // Special case: if either is the empty language, the concatenation is
        // the empty language. To get past this step, both models will have an
        // initial and marked state.
        if (FSAToolbox.isEmptyLanguage(model1) || FSAToolbox.isEmptyLanguage(model2)) {
            FSAModel emptyLanguage = ModelManager.instance().createModel(FSAModel.class);
            return new Object[] { emptyLanguage };
        }

        // FSAState model2Initial = DuplicationToolbox.getInitial(model2);

        /*
         * Identify the marked states of model1
         */
        Set<Long> model1MarkedStateIds = FSAToolbox.getMarkedStates(model1);

        /*
         * Add the events in model1 to the hashtable "events". Later on, if an event
         * with the same name exists in model2, it will not be added, but the event
         * (with the same name) in model1 will be retrieved.
         */
        for (ListIterator<SupervisoryEvent> b = model1.getEventIterator(); b.hasNext();) {
            SupervisoryEvent currEvent = b.next();
            events.put(currEvent.getSymbol(), currEvent);
        }

        if (model2.getTransitionCount() > 0) {
            for (ListIterator<FSATransition> d = model2.getTransitionIterator(); d.hasNext();) {
                FSATransition origTransition = d.next();

                FSAState origSource = origTransition.getSource();
                FSAState copySource = DuplicationToolbox.copyStateInto(model1, origSource, states, false);
                if (copySource.isInitial())
                    model2CopiedInitialStateIds.add(copySource.getId());

                FSAState origTarget = origTransition.getTarget();
                FSAState copyTarget = DuplicationToolbox.copyStateInto(model1, origTarget, states, false);
                if (copyTarget.isInitial())
                    model2CopiedInitialStateIds.add(copyTarget.getId());

                SupervisoryEvent origEvent = origTransition.getEvent();

                FSATransition copyTransition;
                if (origEvent == null) {
                    copyTransition = model1.assembleEpsilonTransition(copySource.getId(), copyTarget.getId());
                } else {
                    SupervisoryEvent copyEvent = DuplicationToolbox.copyEventInto(model1, origEvent, events, false);
                    copyTransition = model1.assembleTransition(copySource.getId(), copyTarget.getId(),
                            copyEvent.getId());
                }

                model1.add(copyTransition);
            }
        } else { // if there are no transitions in model2
            DuplicationToolbox.copyAllEvents(model1, model2, events, false);
            for (Iterator<FSAState> i = model2.getStateIterator(); i.hasNext();) {
                FSAState origState = i.next();
                FSAState copyState = DuplicationToolbox.copyStateInto(model1, origState, states, false);
                if (copyState.isInitial())
                    model2CopiedInitialStateIds.add(copyState.getId());
            }
        }

        for (Long markedId : model1MarkedStateIds) {
            for (Long initialId : model2CopiedInitialStateIds) {
                model1.getState(markedId).setMarked(false);
                model1.getState(initialId).setInitial(false);
                FSATransition t = model1.assembleEpsilonTransition(markedId, initialId);
                model1.add(t);
            }
        }

        // project out the null string transitions (no associated event)
        model1 = (FSAModel) OperationManager.instance().getOperation("removeepsilon")
                .perform(new Object[] { model1 })[0];
        warnings.addAll(OperationManager.instance().getOperation("removeepsilon").getWarnings());

        return new Object[] { model1 };
    }

}
