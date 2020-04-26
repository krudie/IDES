package ides.api.plugin.operation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.ModelManager;

/**
 * A class of methods useful for checking {@link FSAModel}s for certain
 * criteria.
 * 
 * @author Valerie Sugarman
 */
public class FSAToolbox {
    // prevent instantiation
    private FSAToolbox() {
    }

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * None or more than one initial states.
     */
    public static final String NOT_1_INITIAL_STATE = Hub.string("errorNoOrManyInitStates");

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * No marked states.
     */
    public static final String NO_MARKED_STATES = Hub.string("errorNoMarkedStates");

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * Illegal argument.
     */
    public static final String ILLEGAL_ARGUMENT = Hub.string("errorIllegalArgument");

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * Unsupported number of arguments.
     */
    public static final String ILLEGAL_NUMBER_OF_ARGUMENTS = Hub.string("errorIllegalNumberOfArguments");

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * Nondeterministic model.
     */
    public static final String NON_DETERM = Hub.string("errorNonDeterministic");

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * Mismatch between the controllability attributes of events.
     */
    public static final String ERROR_CONTROL = Hub.string("errorControl");

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * Mismatch between the observability attributes of events.
     */
    public static final String ERROR_OBSERVE = Hub.string("errorObserve");

    /**
     * A standard error message which can be used by {@link Operation}s.
     * <p>
     * Unable to compute the operation.
     */
    public static final String ERROR_UNABLE_TO_COMPUTE = Hub.string("errorUnableToCompute");

    /**
     * Obtains a set of the ids of all initial states in the model.
     * 
     * @param model the model in which to find the initial states
     * @return a set of the ids of all initial states in the model
     */
    public static Set<Long> getInitialStates(FSAModel model) {
        Set<Long> initialStateIds = new HashSet<Long>();
        for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();) {
            FSAState s = i.next();
            if (s.isInitial()) {
                initialStateIds.add(s.getId());
            }
        }
        return initialStateIds;
    }

    /**
     * Identifies the number of initial states in the model.
     * 
     * @param model the model in which to find the number of initial states
     * @return the number of initial states in the model
     */
    public static int initialStateCount(FSAModel model) {

        return getInitialStates(model).size();
    }

    /**
     * Obtains a set of the ids of all marked states in the model.
     * 
     * @param model the model in which to find the marked states
     * @return a set of the ids of all marked states in the model
     */
    public static Set<Long> getMarkedStates(FSAModel model) {
        Set<Long> markedStateIds = new HashSet<Long>();
        for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();) {
            FSAState s = i.next();
            if (s.isMarked()) {
                markedStateIds.add(s.getId());
            }
        }
        return markedStateIds;
    }

    /**
     * Identifies the number of marked states in the model.
     * 
     * @param model the model in which to find the number of marked states
     * @return the number of marked states in the model
     */
    public static int markedStateCount(FSAModel model) {
        return getMarkedStates(model).size();
    }

    /**
     * Obtains a set of the ids of all epsilon transitions in the model.
     * 
     * @param model the model in which to find the epsilon transitions
     * @return a set of the ids of all epsilon transitions in the model
     */
    public static Set<Long> getEpsilonTransitions(FSAModel model) {
        Set<Long> epsilonTransitionIds = new HashSet<Long>();
        for (Iterator<FSATransition> i = model.getTransitionIterator(); i.hasNext();) {
            FSATransition t = i.next();
            if (t.isEpsilonTransition()) {
                epsilonTransitionIds.add(t.getId());
            }
        }
        return epsilonTransitionIds;
    }

    /**
     * Determines if there are any epsilon transitions in the model.
     * 
     * @param model the model in which to determine if there are epsilon transitions
     * @return <code>true</code> if there is at least one epsilon transition in the
     *         model, <code>false</code> otherwise.
     */
    public static boolean containsEpsilonTransitions(FSAModel model) {
        for (Iterator<FSATransition> i = model.getTransitionIterator(); i.hasNext();) {
            FSATransition t = i.next();
            if (t.isEpsilonTransition())
                return true;
        }
        return false;
    }

    /**
     * Determines whether the given model is empty (has no states).
     * 
     * @param model the model to be checked
     * @return <code>true</code> if the model contains no states, <code>false</code>
     *         otherwise.
     */
    public static boolean isEmptyModel(FSAModel model) {
        if (model.getStateCount() == 0)
            return true;
        else
            return false;
    }

    /**
     * Determines whether the language represented by the automaton is empty.
     * 
     * @param model the model under consideration
     * @return <code>true</code> if the language represented by the automaton is
     *         empty, <code>false</code> otherwise.
     */
    public static boolean isEmptyLanguage(FSAModel model) {
        FSAModel compareTo = ides.api.plugin.model.ModelManager.instance().createModel(FSAModel.class);
        FSAState s = compareTo.assembleState();
        compareTo.add(s);

        boolean isEmpty = (Boolean) OperationManager.instance().getOperation("langequals")
                .perform(new Object[] { model, compareTo })[0];

        return isEmpty;
    }

    /**
     * Determines whether the language represented by the automaton is the epsilon
     * language.
     * 
     * @param model the model under consideration
     * @return <code>true</code> if the language represented by the automaton is the
     *         epsilon language, <code>false</code> otherwise.
     */
    public static boolean isEpsilonLanguage(FSAModel model) {

        FSAModel compareTo = ides.api.plugin.model.ModelManager.instance().createModel(FSAModel.class);
        FSAState s = compareTo.assembleState();
        s.setInitial(true);
        s.setMarked(true);
        compareTo.add(s);

        boolean isEpsilonLanguage = (Boolean) OperationManager.instance().getOperation("langequals")
                .perform(new Object[] { model, compareTo })[0];

        return isEpsilonLanguage;
    }

    /**
     * Determines whether the model is deterministic, i.e. contains no epsilon
     * transitions, has no more than one initial state (but could have none), and
     * from each state there is not more than one transition with a given event.
     * 
     * @param model the model to be checked
     * @return <code>false</code> if model contains epsilon transitions, has more
     *         than one initial state or has a state with more than one transition
     *         firing on the same event, <code>true</code> otherwise.
     */
    public static boolean isDeterministic(FSAModel model) {
        int initialStateCount = 0;
        if (containsEpsilonTransitions(model))
            return false;

        for (Iterator<FSAState> si = model.getStateIterator(); si.hasNext();) {
            FSAState s = si.next();
            if (s.isInitial())
                initialStateCount++;
            if (initialStateCount > 1)
                return false;

            HashSet<SupervisoryEvent> events = new HashSet<SupervisoryEvent>();
            for (Iterator<FSATransition> ti = s.getOutgoingTransitionsListIterator(); ti.hasNext();) {
                FSATransition t = ti.next();
                SupervisoryEvent e = t.getEvent();
                if (events.contains(e)) {
                    return false;
                }
                events.add(e);
            }
        }
        return true;
    }

    /**
     * A check to see whether there is a conflict in the controllability attribute
     * of events contained in the given models.
     * 
     * @param models the models containing the events whose controllability
     *               attributes have to be checked for conflict
     * @return <code>true</code> if an event with the same name exists in any pair
     *         of models but is controllable in one and uncontrollable in the other,
     *         <code>false</code> otherwise.
     */

    public static boolean hasControllabilityConflict(FSAModel[] models) {

        if (models.length <= 1) {
            return false;
        }

        HashMap<String, SupervisoryEvent> events = new HashMap<String, SupervisoryEvent>();

        for (FSAModel model : models) {
            for (Iterator<SupervisoryEvent> i = model.getEventIterator(); i.hasNext();) {
                SupervisoryEvent e = i.next();
                if (events.containsKey(e.getSymbol())) {
                    SupervisoryEvent compareEvent = events.get(e.getSymbol());
                    if (e.isControllable() != compareEvent.isControllable()) {
                        return true;
                    }
                } else {
                    events.put(e.getSymbol(), e);
                }
            }
        }

        return false;
    }

    /**
     * A check to see whether there is a conflict in the observability attribute of
     * events contained in the given models.
     * 
     * @param models the models containing the events whose observability attributes
     *               have to be checked for conflict
     * @return <code>true</code> if an event with the same name exists in any pair
     *         of models but is observable in one and unobservable in the other,
     *         <code>false</code> otherwise.
     */

    public static boolean hasObservabilityConflict(FSAModel[] models) {

        if (models.length <= 1) {
            return false;
        }

        HashMap<String, SupervisoryEvent> events = new HashMap<String, SupervisoryEvent>();

        for (FSAModel model : models) {
            for (Iterator<SupervisoryEvent> i = model.getEventIterator(); i.hasNext();) {
                SupervisoryEvent e = i.next();
                if (events.containsKey(e.getSymbol())) {
                    SupervisoryEvent compareEvent = events.get(e.getSymbol());
                    if (e.isObservable() != compareEvent.isObservable()) {
                        return true;
                    }
                } else {
                    events.put(e.getSymbol(), e);
                }
            }
        }

        return false;
    }

    /**
     * Constructs an {@link FSAModel} representing a string from a list of
     * {@link DESEvent}s.
     * 
     * @param list The list of {@link DESEvent}s to construct the {@link FSAModel}
     *             from.
     * @return {@link FSAModel} representing the given string.
     */

    public static FSAModel modelFromList(List<DESEvent> list) {
        FSAModel model = ModelManager.instance().createModel(FSAModel.class);
        FSAState initial = model.assembleState();
        initial.setInitial(true);
        model.add(initial);

        FSAState currState = initial;
        FSAState newState;
        FSATransition t;
        SupervisoryEvent e;

        for (ListIterator<DESEvent> i = list.listIterator(); i.hasNext();) {
            DESEvent de = i.next();
            e = model.assembleCopyOf(de);
            model.add(e);

            newState = model.assembleState();
            model.add(newState);

            t = model.assembleTransition(currState.getId(), newState.getId(), e.getId());
            model.add(t);

            currState = newState;
        }
        currState.setMarked(true);

        return model;
    }
}
