package operations.fsa.ver2_1;

import java.util.ListIterator;
import java.util.Map;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.supeventset.SupervisoryEvent;

/**
 * @author Valerie Sugarman
 */
public class DuplicationToolbox {

    /**
     * Calls {@link #copyStateInto(FSAModel, FSAState, Map, boolean)} for all the
     * states in model2.
     * 
     * @param model1 The model the states are being added too.
     * @param model2 The model the states are being copied from.
     * @param states Map to keep track of original states in model2 and their
     *               counterparts in model1.
     * @param copyId When <code>true</code>, the id of each original state will be
     *               copied to the new state
     */
    protected static void copyAllStates(FSAModel model1, FSAModel model2, Map<FSAState, FSAState> states,
            boolean copyId) {
        for (ListIterator<FSAState> g = model2.getStateIterator(); g.hasNext();) {
            FSAState origState = g.next();
            copyStateInto(model1, origState, states, copyId);
        }

    }

    /**
     * Produces a duplication in model1 of the original state. If a duplication has
     * already been made, an entry will exist in the mapping and the previously
     * duplicated state based on the same original event will be fetched. If the
     * duplication has not already been made, a new state will be assembled,
     * duplicated, then added to model1 and the mapping. Duplication consists of the
     * same name, initial and marked status, and optionally id based on value of
     * copyId as the original state.
     * 
     * @param model1    The destination model for the copied state.
     * @param origState The state to be copied.
     * @param states    Contains a mapping of the original state(s) to the copied
     *                  state(s)
     * @param copyId    When <code>true</code>, the id of the original state will be
     *                  copied to the new state
     * @return An equivalent state in model1 with the same name, initial and marked
     *         status, and optionally id based on value of copyId as the original
     *         state.
     */
    protected static FSAState copyStateInto(FSAModel model1, FSAState origState, Map<FSAState, FSAState> states,
            boolean copyId) {
        FSAState copyState;
        if (states.containsKey(origState)) {
            copyState = states.get(origState);
        } else {
            copyState = model1.assembleState();

            copyState.setName(origState.getName());
            copyState.setInitial(origState.isInitial());
            copyState.setMarked(origState.isMarked());
            if (copyId)
                copyState.setId(origState.getId());

            model1.add(copyState);
            states.put(origState, copyState);
        }
        return copyState;
    }

    /**
     * Calls {@link #copyEventInto(FSAModel, SupervisoryEvent, Map, boolean)} for
     * all the events in the source model.
     * 
     * @param to     The model the events are being added to.
     * @param from   The model the events are being copied from.
     * @param events Map to keep track of events already in the target model
     *               (originally or previously copied).
     * @param copyId When <code>true</code>, the id of each original event will be
     *               copied to the new state
     */

    protected static void copyAllEvents(FSAModel to, FSAModel from, Map<String, SupervisoryEvent> events,
            boolean copyId) {

        for (ListIterator<SupervisoryEvent> f = from.getEventIterator(); f.hasNext();) {
            SupervisoryEvent origEvent = f.next();
            DuplicationToolbox.copyEventInto(to, origEvent, events, copyId);
        }
    }

    /**
     * Produces a duplication in model1 of the original event. If a duplication has
     * already been made, an entry will exist in the map and the previously created
     * event with the same name will be fetched. If the duplication has not already
     * been made, a new event will be assembled, duplicated, then added to model1
     * and the map. Duplication consists of the same symbol, observable and
     * controllable status, and optionally id based on value of copyId as the
     * original event.
     * 
     * @param model1    The destination model for the copied event.
     * @param origEvent The event to be copied.
     * @param events    Contains a mapping of the event symbol to an event
     * @param copyId    When <code>true</code>, the id of the original event will be
     *                  copied to the new state
     * @return An equivalent state in model1 with the same symbol, observable and
     *         controllable status, and optionally id based on value of copyId as
     *         the original event.
     */
    protected static SupervisoryEvent copyEventInto(FSAModel model1, SupervisoryEvent origEvent,
            Map<String, SupervisoryEvent> events, boolean copyId) {

        SupervisoryEvent copyEvent;
        if (events.containsKey(origEvent.getSymbol())) {
            copyEvent = events.get(origEvent.getSymbol());
        } else {
            copyEvent = model1.assembleEvent(origEvent.getSymbol());

            copyEvent.setSymbol(origEvent.getSymbol());
            copyEvent.setControllable(origEvent.isControllable());
            copyEvent.setObservable(origEvent.isObservable());
            if (copyId)
                copyEvent.setId(origEvent.getId());

            model1.add(copyEvent);
            events.put(copyEvent.getSymbol(), copyEvent);
        }
        return copyEvent;
    }

    /**
     * Iterates through all of the states in model and identifies an initial state.
     * It is expected that it has already been verified using the initialStateCount
     * method in FSAToolbox that there is exactly 1 initial state.
     * 
     * @param model The model under consideration
     * @return Returns the initial state. (If there is more than one initial state,
     *         returns the last one in the iterator. If there is no initial state,
     *         returns a newly assembled state in model.)
     */
    protected static FSAState getInitial(FSAModel model) {
        FSAState initial = model.assembleState();
        for (ListIterator<FSAState> a = model.getStateIterator(); a.hasNext();) {
            FSAState currState = a.next();
            if (currState.isInitial()) {
                initial = currState;
            }
        }
        return initial;
    }

}
