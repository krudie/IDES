package operations.fsa.ver2_1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;
import util.AnnotationKeys;

/**
 * A test to see whether a given language is observable with respect to a plant.
 * Algorithm and logic taken from Introduction to Discrete Event Systems by
 * Christos G. Cassandras and Stephane Lafortune (2nd Edition) p. 188 Polynomial
 * Test for Observability (available as a pdf from the Queen's library website
 * as of summer 2010)
 * 
 * @author Valerie Sugarman - with some ideas taken from Composition so credit
 *         given to
 * @author Kristian Edlund
 * @author Axel Gottlieb Michelsen
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Observable implements Operation {

    private LinkedList<String> warnings = new LinkedList<String>();

    private String resultMessage;

    private final static String nullString = "epsilon";

    private static Map<String, Long> pairIds = new TreeMap<String, Long>();

    public String getDescription() {
        return "Determines if the sublanguage is observable with respect to the plant.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Plant", "Sublanguage" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { resultMessage };
    }

    public String getName() {
        return "observable";
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

    public Object[] perform(Object[] arg0) {

        warnings.clear();
        resultMessage = Hub.string("errorUnableToCompute");
        pairIds.clear();
        FSAModel plant;
        FSAModel sublanguage;

        if (arg0.length >= 2) {
            if (arg0[0] instanceof FSAModel && arg0[1] instanceof FSAModel) {
                plant = (FSAModel) arg0[0];
                sublanguage = (FSAModel) arg0[1];
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { new Boolean(false) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { new Boolean(false) };
        }

        if (!FSAToolbox.isDeterministic(plant)) {
            plant = (FSAModel) OperationManager.instance().getOperation("determinize")
                    .perform(new Object[] { plant })[0];
            warnings.addAll(OperationManager.instance().getOperation("determinize").getWarnings());
        }
        if (!FSAToolbox.isDeterministic(sublanguage)) {
            sublanguage = (FSAModel) OperationManager.instance().getOperation("determinize")
                    .perform(new Object[] { sublanguage })[0];
            warnings.addAll(OperationManager.instance().getOperation("determinize").getWarnings());
        }

        if (FSAToolbox.hasObservabilityConflict(new FSAModel[] { plant, sublanguage })) {
            warnings.add(FSAToolbox.ERROR_OBSERVE);
            return new Object[] { new Boolean(false) };
        }

        // Double check that sublanguage is in fact a sublanguage of the
        // plant. (don't call the operation since that will run determinize
        // again)
        // NOTE: use subset of L(G) not Lm(G) as per Karen's suggestion when
        // presented with problem of prefix-closed infimal observable language
        // not being official observable (because not a sublanguage)

        FSAModel prefixClosedPlant = (FSAModel) OperationManager.instance().getOperation("prefixclose")
                .perform(new Object[] { plant })[0];
        warnings.addAll(OperationManager.instance().getOperation("prefixclose").getWarnings());

        boolean isSublanguage = Subset.subset(sublanguage, prefixClosedPlant);

        if (!isSublanguage) {
            warnings.add(Hub.string("errorNotSublanguage"));
            return new Object[] { new Boolean(false) };
        }

        FSAModel obsTest = ModelManager.instance().createModel(FSAModel.class);

        boolean observable = obsTest(sublanguage, plant, obsTest);

        if (warnings.size() != 0) {
            return new Object[] { new Boolean(false) };
        }

        if (observable) {
            resultMessage = "Sublanguage is observable with respect to the plant.";
        } else {
            resultMessage = "Sublanguage is not observable with respect to the plant.";
        }

        return new Object[] { new Boolean(observable) };
    }

    /**
     * Able to compute a deterministic automaton that captures all violations in
     * observability as described in Introduction to Discrete Event Systems by
     * Christos G. Cassandras and Stephane Lafortune (2nd Edition) p. 188 Polynomial
     * Test for Observability (available as a pdf from the Queen's library website
     * as of summer 2010). If the automaton reaches the marked state "dead" then the
     * sublanguage is not observable with respect to the plant, and the method
     * immediately returns false (i.e. construction of the automaton is terminated).
     * 
     * @param sublanguage The determinisitic automaton representing the language
     *                    under consideration, that is a sublanguage of the plant.
     * @param plant       The the deterministic automaton representing the plant or
     *                    superlanguage that observability of the sublanguage is
     *                    being determined with respect to.
     * @param obsTest     An empty model that will contain the result automaton.
     * @return <code> true </code> if the sublanguage is observable with respect to
     *         the plant, <code>false</code> otherwise.
     */
    /*
     * This method was originally written to, and is is easily modified to return
     * the created automaton. Simply set the return type to void, remove the return
     * false statement when it reaches the stopping condition and change the return
     * true to return obsTest at the end. The input/output information needs to be
     * changed above in order to display this automaton.
     */
    private static boolean obsTest(FSAModel sublanguage, FSAModel plant, FSAModel obsTest) {
        obsTest.setAnnotation(AnnotationKeys.COMPOSED_OF, new String[] { sublanguage.getName(), plant.getName() });

        HashMap<String, SupervisoryEvent> newEvents = new HashMap<String, SupervisoryEvent>();
        HashMap<FSAState[], FSATransition[]> stateEventMap = new HashMap<FSAState[], FSATransition[]>();
        HashSet<SupervisoryEvent> currentEvents = new HashSet<SupervisoryEvent>();
        LinkedList<FSAState[]> searchList = new LinkedList<FSAState[]>();
        FSAState[] stateTriple = new FSAState[3];
        long id = 0;

        // create dead state, but don't add to model until needed
        boolean deadAdded = false;
        FSAState dead = obsTest.assembleState();
        dead.setMarked(true);
        dead.setId(id++);
        dead.setAnnotation(AnnotationKeys.COMPOSED_OF_NAMES, new String[] { "dead" });

        // it is known there is only one initial state (since if not initially
        // determinize was run)
        stateTriple[0] = DuplicationToolbox.getInitial(sublanguage);
        stateTriple[1] = stateTriple[0];
        stateTriple[2] = DuplicationToolbox.getInitial(plant);

        FSAState initial = obsTest.assembleState();
        makeStateFromTriple(stateTriple, initial, id, true);
        setIn(stateTriple, id++);
        obsTest.add(initial);
        searchList.add(stateTriple);

        // used for the transition function. null interpreted as no transition.
        // (there won't be any "real" epsilon transitions because determinized)
        FSATransition[] transitionTriple;
        FSATransition[] tempTransitionTriple;
        FSAState[] sourceTriple, targetTriple;
        FSAState source, target;

        while (!searchList.isEmpty()) {
            sourceTriple = searchList.remove();
            source = obsTest.getState(isIn(sourceTriple));
            currentEvents.clear();

            // gather the outgoing events from states 0 and 1 (which are in the
            // sublanguage) so that only these are looped over in the next
            // section. Also, no need to worry about transitions with null
            // events since these would have been removed when determinized.
            for (Iterator<FSATransition> i = sourceTriple[0].getOutgoingTransitionsListIterator(); i.hasNext();) {
                currentEvents.add(i.next().getEvent());
            }
            for (Iterator<FSATransition> i = sourceTriple[1].getOutgoingTransitionsListIterator(); i.hasNext();) {
                currentEvents.add(i.next().getEvent());
            }

            for (SupervisoryEvent event : currentEvents) {

                stateEventMap.clear();
                transitionTriple = getOutgoingTransitionTriple(sourceTriple, event);

                if (isStoppingCondition(transitionTriple, event)) {

                    if (deadAdded == false) {
                        obsTest.add(dead);
                        deadAdded = true;
                    }
                    SupervisoryEvent toDeadEvent = obsTest.assembleEvent(
                            "(" + event.getSymbol() + ", " + nullString + ", " + event.getSymbol() + ")");
                    obsTest.add(toDeadEvent);
                    FSATransition toDead = obsTest.assembleTransition(source.getId(), dead.getId(),
                            toDeadEvent.getId());
                    obsTest.add(toDead);
                    return false; // getting here means not observable
                } else {
                    if (event.isObservable()) {
                        if (transitionTriple[0] != null && transitionTriple[1] != null && transitionTriple[2] != null) {
                            targetTriple = next(sourceTriple, transitionTriple);
                            stateEventMap.put(targetTriple, transitionTriple);
                        }
                    } else {
                        if (transitionTriple[0] != null) {
                            tempTransitionTriple = new FSATransition[] { transitionTriple[0], null, null };
                            targetTriple = next(sourceTriple, tempTransitionTriple);
                            stateEventMap.put(targetTriple, tempTransitionTriple);
                        }
                        if (transitionTriple[1] != null && transitionTriple[2] != null) {
                            tempTransitionTriple = new FSATransition[] { null, transitionTriple[1],
                                    transitionTriple[2] };
                            targetTriple = next(sourceTriple, tempTransitionTriple);
                            stateEventMap.put(targetTriple, tempTransitionTriple);
                        }
                    }

                    // if there are targetTriples generated from this event,
                    // transition to them
                    if (!stateEventMap.isEmpty()) {
                        for (Iterator<FSAState[]> i = stateEventMap.keySet().iterator(); i.hasNext();) {
                            targetTriple = i.next();
                            long stateid = isIn(targetTriple);

                            if (stateid < 0) {
                                target = obsTest.assembleState();
                                makeStateFromTriple(targetTriple, target, id, false);
                                setIn(targetTriple, id++);
                                obsTest.add(target);
                                searchList.add(targetTriple);
                            } else {
                                target = obsTest.getState(stateid);
                            }

                            FSATransition[] transitions = stateEventMap.get(targetTriple);
                            String newEventName = getEventName(transitions);
                            SupervisoryEvent newEvent;
                            if (newEvents.containsKey(newEventName)) {
                                newEvent = newEvents.get(newEventName);
                            } else {
                                newEvent = obsTest.assembleEvent(newEventName);
                                newEvents.put(newEventName, newEvent);
                                obsTest.add(newEvent);
                            }

                            FSATransition transition = obsTest.assembleTransition(source.getId(), target.getId(),
                                    newEvent.getId());
                            obsTest.add(transition);

                        }
                    } // end adding to model if isn't stopping condition
                } // end is/isn't stopping condition
            } // end iterating through valid events
        } // end big state while loop

        return true; // if get here that means is observable

    }// end obsTest

    /**
     * Private method to determine whether the stopping condition has been reached
     * for a given set of transitions and event.
     * 
     * @param transitionTriple An array containing the transitions from the source
     *                         state labelled with the event under consideration. If
     *                         no such transition exists, the array entry should be
     *                         <code>null</code> .
     * @param event            the event under consideration
     * @return <code>true</code> if the stopping condition has been met,
     *         <code>false</code> otherwise.
     */
    private static boolean isStoppingCondition(FSATransition[] transitionTriple, SupervisoryEvent event) {
        if (!event.isControllable()) {
            return false;
        } else {
            for (int i = 0; i < 3; i++) {
                if ((i == 0 || i == 2) && transitionTriple[i] == null) {
                    return false;
                }
                if ((i == 1) && transitionTriple[i] != null) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * A private method to identify the outgoing transitions labelled with a given
     * event from a state triple. Since the automata are all deterministic, there is
     * either only one or no such transition.
     * 
     * @param stateTriple The state triple under consideration.
     * @param event       The event under consideration.
     * @return An array containing the transitions from the source states labelled
     *         with the given event. If no such transitions exist, the array entry
     *         for that state is <code>null</code>.
     */
    private static FSATransition[] getOutgoingTransitionTriple(FSAState[] stateTriple, SupervisoryEvent event) {
        boolean isDefined;
        FSATransition[] transitionTriple = new FSATransition[3];
        for (int i = 0; i < 3; i++) {
            isDefined = false;
            Iterator<FSATransition> oti = stateTriple[i].getOutgoingTransitionsListIterator();
            while (oti.hasNext()) {
                FSATransition t = oti.next();
                if (t.getEvent().equals(event)) {
                    transitionTriple[i] = t;
                    isDefined = true;
                    // if found, no need to keep searching
                    break;
                }
            }
            if (isDefined == false) {
                transitionTriple[i] = null;
            }
        }
        return transitionTriple;
    }

    /**
     * A private function that implements the transition function of the obsTest
     * automaton. Determines the next state triple based on the transition triple.
     * 
     * @param stateTriple      The source state triple under consideration.
     * @param transitionTriple The outgoing transitions corresponding to the source
     *                         states.
     * @return The successor state triple.
     */
    private static FSAState[] next(FSAState[] stateTriple, FSATransition[] transitionTriple) {

        FSAState[] newStateTriple = new FSAState[3];

        for (int i = 0; i < 3; i++) {

            if (transitionTriple[i] == null) {
                newStateTriple[i] = stateTriple[i];
            } else {
                newStateTriple[i] = transitionTriple[i].getTarget();
            }
        }
        return newStateTriple;
    }

    /**
     * Private method to make a new state from an array containing 3 states.
     * 
     * @param s        the array of 3 states
     * @param newState a pre-assembled state that will be modified into the new
     *                 state
     * @param id       the id given to the new state
     * @param initial  when <code>true</code> the new state will be an initial state
     */
    private static void makeStateFromTriple(FSAState[] s, FSAState newState, long id, boolean initial) {
        newState.setAnnotation(AnnotationKeys.COMPOSED_OF, new long[] { s[0].getId(), s[1].getId(), s[2].getId() });
        newState.setAnnotation(AnnotationKeys.COMPOSED_OF_NAMES,
                new String[] { s[0].getName(), s[1].getName(), s[2].getName() });
        newState.setId(id);
        newState.setInitial(initial);
    }

    private static String getEventName(FSATransition[] transitions) {
        String newEventName = "(";
        for (int j = 0; j < 3; j++) {
            if (transitions[j] != null) {
                newEventName += transitions[j].getEvent().getSymbol();
            } else {
                newEventName += nullString;
            }
            if (j == 0 || j == 1) {
                newEventName += ", ";
            }
        }
        newEventName += ")";
        return newEventName;
    }

    /**
     * Private function to check if a state triple is in the test model
     * 
     * @param s the state triple to check
     * @return -1 if it is not in, else returns the id of the state triple
     */
    private static long isIn(FSAState[] s) {
        Long id = pairIds.get(id(s));
        if (id == null) {
            return -1;
        } else {
            return id.longValue();
        }
    }

    /**
     * private function for setting a state triple into the mapping
     * 
     * @param s the state triple to set a new id for
     * @param n the new id
     */
    private static void setIn(FSAState[] s, long n) {
        pairIds.put(id(s), new Long(n));
    }

    /**
     * Makes a string of the ids in a state triple
     * 
     * @param sll the state triple to compile a string from
     * @return the id string . separated
     */
    private static String id(FSAState[] s) {
        String name = "";
        for (int i = 0; i < s.length; i++) {
            name += s[i].getId() + ".";
        }
        return name;
    }

}
