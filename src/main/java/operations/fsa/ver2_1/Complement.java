package operations.fsa.ver2_1;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.FilterOperation;

/**
 * This class performs the Complement Operation on languages. Algorithm taken
 * from - Introduction To Discrete Event Systems by Christos G.Cassandras and
 * Stephane Lafortune Second Edition and PlanetMath.org
 * 
 * @author Utsav Mital
 */
public class Complement implements FilterOperation {
    private LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {
        return "Computes the complement of a language.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Finite-state automaton" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { "Automaton recognizing the complement" };
    }

    public String getName() {

        return "complement";
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

    /**
     * This method performs the complement operation using a copy of the input
     * model.
     * 
     * @param arg0 one input model of type FSAModel whose complement is to be
     *             computed
     * @return one model of FSAModel type which is the complement of the input
     *         model.
     */
    public Object[] perform(Object[] arg0) {
        warnings.clear();
        // Cloning to the input model to leave it modified.
        FSAModel model = ((FSAModel) arg0[0]).clone();

        return filter(new Object[] { model });
    }

    /**
     * This method performs the complement operation directly on the argument
     * passed.
     * 
     * @param arg0 one input of type FSAModel, whose complemented is to be computed
     * @return one model of type FSAModel which is the complement of the input model
     */
    public Object[] filter(Object[] arg0) {
        warnings.clear();
        if (!FSAModel.class.isInstance(arg0[0])) {
            warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
            return new Object[] { arg0[0] };
        }

        FSAModel model = (FSAModel) arg0[0];
        FSAState stateExtracted;
        FSAState addedState;
        FSATransition transitionToAdd;
        DESEventSet eventSetForEachState = ModelManager.instance().createEmptyEventSet();

        boolean newStateNeeded = false;
        Collection<FSATransition> transitionToRemove = new HashSet<FSATransition>();
        HashMap<String, Long> eventsMap = new HashMap<String, Long>();

        if (!FSAToolbox.isDeterministic(model)) {
            warnings.add(FSAToolbox.NON_DETERM);
            return new Object[] { model };
        }

        if (FSAToolbox.isEmptyLanguage(model) && model.getEventSet().isEmpty()) {
            return new Object[] { model };
        }

        addedState = model.assembleState();
        if (FSAToolbox.initialStateCount(model) == 0) {
            addedState.setInitial(true);
            newStateNeeded = true;
        }
        model.add(addedState);

        // populate the events map for use in creating transitions later
        for (Iterator<SupervisoryEvent> i = model.getEventIterator(); i.hasNext();) {
            SupervisoryEvent e = i.next();
            eventsMap.put(e.getSymbol(), e.getId());
        }

        /*
         * Fetching each state for the Model at a time and switching markings also
         * extracting associated outgoing transitions and events and storing in a set
         */
        for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();) {
            stateExtracted = i.next();

            // Changing markings of the state
            stateExtracted.setMarked(!stateExtracted.isMarked());

            // Removing all the events for the previous state
            eventSetForEachState.clear();

            /*
             * Fetching all the outgoing Transitions and related events for the state under
             * consideration
             */
            for (Iterator<FSATransition> k = stateExtracted.getOutgoingTransitionsListIterator(); k.hasNext();) {
                eventSetForEachState.add(k.next().getEvent());
            }

            // Fetching related events for a particular state that need to be
            // added
            for (DESEvent e : model.getEventSet().subtract(eventSetForEachState)) {

                // checking if the state needs to be added.
                if (!stateExtracted.equals(addedState))
                    newStateNeeded = true;

                /*
                 * Adding all the non existing transitions and events to the new state from the
                 * state under consideration
                 */
                transitionToAdd = model.assembleTransition(stateExtracted.getId(), addedState.getId(),
                        eventsMap.get(e.getSymbol()));
                model.add(transitionToAdd);
            }
        }

        /*
         * removing all the self loops the new state and the state itself if it is not
         * required in the model
         */
        if (!newStateNeeded) {

            for (Iterator<FSATransition> j = addedState.getOutgoingTransitionsListIterator(); j.hasNext();) {
                transitionToRemove.add(j.next());
            }

            for (FSATransition t : transitionToRemove) {
                model.remove(t);
            }
            model.remove(addedState);
        }

        return new Object[] { model };
    }

    public int[] getInputOutputIndexes() {
        return new int[] { 0 };
    }
}
