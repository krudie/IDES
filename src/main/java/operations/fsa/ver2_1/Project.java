package operations.fsa.ver2_1;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import util.AnnotationKeys;

/**
 * Adapted from Composition.observer algorithm.
 * 
 * @author Kristian Edlund
 * @author Axel Gottlieb Michelsen
 * @author Lenko Grigorov
 * @author Chris Dragert
 * @author Valerie Sugarman
 */
public class Project {

    /**
     * To be used to map the string representation of a state to its id in the
     * model.
     */
    private static Map<String, Long> pairIds = new TreeMap<String, Long>();

    /**
     * A method to project out certain specified events from a model.
     * 
     * @param model          the original model
     * @param eventsToRemove a DESEventSet containing the events to project out of
     *                       the model. Can be empty or null. If null, an empty
     *                       DESEventSet will be created.
     * @param removeNulls    a flag to determine whether or not to project out the
     *                       epsilon (<code>null</code>) transitions in addition to
     *                       the DESEventsToRemove. When <code>true</code>, the null
     *                       events will be projected out, when <code>false</code>,
     *                       they will not.
     * @return An FSAModel with the specified events projected out.
     */
    protected static FSAModel projectCustom(FSAModel model, DESEventSet eventsToRemove, boolean removeNulls) {

        pairIds.clear();

        if (eventsToRemove == null) {
            eventsToRemove = ModelManager.instance().createEmptyEventSet();
        }

        FSAModel projection = ModelManager.instance().createModel(FSAModel.class);

        projection.setAnnotation(AnnotationKeys.COMPOSED_OF, new String[] { model.getName() });

        HashMap<String, SupervisoryEvent> newEvents = new HashMap<String, SupervisoryEvent>();
        LinkedList<LinkedList<FSAState>> searchList = new LinkedList<LinkedList<FSAState>>();
        LinkedList<FSAState> states = new LinkedList<FSAState>();
        long id = 0;

        // copy the events from the original model to the projection only if
        // they were not specified as events to remove
        for (Iterator<SupervisoryEvent> i = model.getEventIterator(); i.hasNext();) {
            SupervisoryEvent origEvent = i.next();
            if (!eventsToRemove.contains(origEvent)) {
                DuplicationToolbox.copyEventInto(projection, origEvent, newEvents, true);
            }
        }
        // add the null event to newEvents for later
        newEvents.put("null", null);

        for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();) {
            FSAState initial = i.next();
            if (initial.isInitial()) {
                states.add(initial);
            }
        }

        // add all of the states reached through events to be removed into
        // states
        reach(states, eventsToRemove, removeNulls);
        sort(states);
        FSAState rState = projection.assembleState();
        // make a state based on the states contained in states
        rState = makeState(states, rState, id, true);
        projection.add(rState);
        setIn(states, id++);

        searchList.add(states);
        FSAState source;
        FSAState target;

        while (!searchList.isEmpty()) {

            LinkedList<FSAState> sourceList = searchList.remove();
            source = projection.getState(isIn(sourceList));

            // iterate through all the events in projection (including null
            // which was added above)
            Iterator<SupervisoryEvent> ei = newEvents.values().iterator();
            while (ei.hasNext()) {
                SupervisoryEvent event = ei.next();
                states = new LinkedList<FSAState>();
                ListIterator<FSAState> sli = sourceList.listIterator();
                while (sli.hasNext()) {

                    FSAState s = sli.next();
                    ListIterator<FSATransition> tli = s.getOutgoingTransitionsListIterator();
                    while (tli.hasNext()) {
                        FSATransition t = tli.next();

                        if ((t.getEvent() != null) && t.getEvent().equals(event) && !states.contains(t.getTarget())) {
                            states.add(t.getTarget());
                        }

                        if (!removeNulls && (event == null)) {
                            if ((t.getEvent() == null) && !states.contains(t.getTarget())) {
                                states.add(t.getTarget());

                            }
                        }

                    }
                }
                if (!states.isEmpty()) {
                    reach(states, eventsToRemove, removeNulls);
                    sort(states);
                    long stateid = isIn(states);

                    if (stateid < 0) {
                        target = projection.assembleState();
                        target = makeState(states, target, id, false);
                        projection.add(target);
                        setIn(states, id++);
                        searchList.add(states);
                    } else {
                        target = projection.getState(stateid);

                    }

                    FSATransition t;
                    if (event == null) {
                        t = projection.assembleEpsilonTransition(source.getId(), target.getId());
                    } else {
                        t = projection.assembleTransition(source.getId(), target.getId(), event.getId());
                    }

                    projection.add(t);

                }
            }
        }

        return projection;

    }

    /**
     * calculates the reach for a stateset (linked list of states) based on the
     * events that have been specified to be removed. If removeNulls is true, the
     * reach will include states accessible through null events. Otherwise, only
     * those specified in the event set will be removed.
     * 
     * @param sll            the stateset (linked list of states) to use.
     * @param eventsToRemove the events to be removed from the model
     * @param removeNulls    whether or not the nulls should be removed
     */
    private static void reach(LinkedList<FSAState> sll, DESEventSet eventsToRemove, boolean removeNulls) {
        ListIterator<FSAState> sli = sll.listIterator();
        HashSet<FSAState> reached = new HashSet<FSAState>();
        while (sli.hasNext()) {
            reached.add(sli.next());
        }
        sli = sll.listIterator();
        while (sli.hasNext()) {
            FSAState s = sli.next();
            ListIterator<FSATransition> stli = s.getOutgoingTransitionsListIterator();
            while (stli.hasNext()) {
                FSATransition t = stli.next();

                if (!removeNulls) {
                    if (eventsToRemove.contains(t.getEvent()) && !reached.contains(t.getTarget())
                            && t.getEvent() != null) {
                        reached.add(t.getTarget());
                        sli.add(t.getTarget()); // add it to states
                        sli.previous();
                    }
                } else {
                    if ((t.getEvent() == null || eventsToRemove.contains(t.getEvent()))
                            && !reached.contains(t.getTarget())) {
                        reached.add(t.getTarget());
                        sli.add(t.getTarget());
                        sli.previous();
                    }
                }
            }
        }
    }

    /**
     * makes a new state from a linked list of states. Doesn't include states with
     * no label, since these are considered "dummy" states. The name of the new
     * state comes from the names of the composing states. For example, if the
     * composing states are 1,2 and 3, the name of the new state will be (1,2,3).
     * "dummy" states are not included in the name of the new state. If any of the
     * composing states is marked, the new state will also be marked.
     * 
     * @param sll        the state set to make a new state from
     * @param finalState a state pre-assembled in the desired model that will be
     *                   modified to form the new state
     * @param id         the id it should use for the new state
     * @param initial    sets the state as initial if needed
     * @return the newly created state
     */
    private static FSAState makeState(LinkedList<FSAState> sll, FSAState aState, long id, boolean initial) {
        ListIterator<FSAState> sli = sll.listIterator();
        FSAState s;
        boolean marked = false;
        int cId = 0;
        LinkedList<String> compositionNamesLL = new LinkedList<String>();
        long[] compositionIds = new long[sll.size()];

        while (sli.hasNext()) {
            s = sli.next();
            marked |= s.isMarked();
            compositionIds[cId] = s.getId();
            cId++;
            if (s.getName() != "") {
                compositionNamesLL.add(s.getName());
            }
        }

        String[] compositionNames = compositionNamesLL.toArray(new String[compositionNamesLL.size()]);
        aState.setId(id);
        aState.setMarked(marked);
        aState.setInitial(initial);
        aState.setAnnotation(AnnotationKeys.COMPOSED_OF, compositionIds);
        aState.setAnnotation(AnnotationKeys.COMPOSED_OF_NAMES, compositionNames);

        return aState;
    }

    /**
     * Private function to check if a stateset is in the model
     * 
     * @param sll the stateset to check
     * @return the id of the state, or -1 if the stateset is not in the model
     */
    private static long isIn(LinkedList<FSAState> sll) {
        Long id = pairIds.get(id(sll));
        if (id == null) {
            return -1;
        } else {
            return id.longValue();
        }
    }

    /**
     * private function for setting a linkedlist representing a state in the model
     * 
     * @param sll the stateset to set a new id in
     * @param n   the new id
     */
    private static void setIn(LinkedList<FSAState> sll, long n) {
        pairIds.put(id(sll), new Long(n));
    }

    /**
     * Makes a string of the state ids in a stateset. Doesn't include states with no
     * label, since these are considered "dummy" states.
     * 
     * @param sll the stateset to compile a string from
     * @return the id string . seperated
     */
    private static String id(LinkedList<FSAState> sll) {

        ListIterator<FSAState> sli = sll.listIterator();
        String name = "";
        while (sli.hasNext()) {
            FSAState s = sli.next();
            if (s.getName() != "")
                name += s.getId() + ".";
        }

        return name;
    }

    /**
     * Sorting algorithm for sorting a list of states after id.
     * 
     * @param sll the list of states to sort
     */
    private static void sort(LinkedList<FSAState> sll) {
        Collections.sort(sll, new Comparator<FSAState>() {
            public int compare(FSAState s1, FSAState s2) {
                return (int) Math.signum(s1.getId() - s2.getId());
            }
        });
    }

}
