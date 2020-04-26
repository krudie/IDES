package operations.fsa.ver2_1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.OperationManager;
import util.AnnotationKeys;

public class MultiAgentProductFSA extends AbstractOperation {

    protected class FSAEventSet extends HashSet<SupervisoryEvent> {
        private static final long serialVersionUID = -8053527967393272262L;
    }

    public MultiAgentProductFSA() {
        NAME = "maproduct-fsa";
        DESCRIPTION = "Computes the multi-agent product of scalar automata. The result is a scalar automaton.";
        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class };
        inputDesc = new String[] { "Finite-state automata" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Multi-agent composition of the automata (scalar)" };
    }

    @Override
    public int getNumberOfInputs() {
        return -1;
    }

    int maOrder;

    HashMap<String, FSAState> stateMap;

    HashMap<String, SupervisoryEvent> eventMap;

    protected class PseudoTransition {
        public LinkedList<LinkedList<SupervisoryEvent>> events = new LinkedList<LinkedList<SupervisoryEvent>>();

        public LinkedList<LinkedList<FSAState>> targets = new LinkedList<LinkedList<FSAState>>();
    }

    @Override
    public Object[] perform(Object[] inputs) {
        warnings.clear();

        LinkedList<FSAModel> newInputs = new LinkedList<FSAModel>();
        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] instanceof FSAModel) {
                FSAModel model = (FSAModel) inputs[i];
                if (!FSAToolbox.isDeterministic(model)) {
                    model = (FSAModel) OperationManager.instance().getOperation("determinize")
                            .perform(new Object[] { model })[0];
                    warnings.addAll(OperationManager.instance().getOperation("determinize").getWarnings());
                }
                newInputs.add(model);
            }
        }

        if (newInputs.size() == 0) {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        FSAModel a = ModelManager.instance().createModel(FSAModel.class);
        maOrder = newInputs.size();
        FSAState[] initial = new FSAState[maOrder];
        String[] fsaIds = new String[maOrder];
        for (int i = 0; i < maOrder; ++i) {
            fsaIds[i] = newInputs.get(i).getName();
            initial[i] = null;
            for (Iterator<FSAState> j = newInputs.get(i).getStateIterator(); j.hasNext();) {
                FSAState state = j.next();
                if (state.isInitial()) {
                    initial[i] = state;
                    break;
                }
            }
            if (initial[i] == null) {
                warnings.add("FSA \'" + fsaIds[i] + "\' does not have an initial state");
                return new Object[] { ides.api.plugin.model.ModelManager.instance().createModel(FSAModel.class) };
            }
        }
        a.setAnnotation(AnnotationKeys.COMPOSED_OF, fsaIds);
        LinkedList<FSAState[]> openStates = new LinkedList<FSAState[]>();
        HashSet<String> closedStates = new HashSet<String>();
        stateMap = new HashMap<String, FSAState>();
        eventMap = new HashMap<String, SupervisoryEvent>();
        openStates.add(initial);
        FSAState newS = makeState(initial, 0, a.assembleState());
        a.add(newS);
        stateMap.put(keyOf(initial), newS);
        while (!openStates.isEmpty()) {
            FSAState[] states = openStates.getFirst();
            openStates.removeFirst();
            if (closedStates.contains(keyOf(states))) {
                continue;
            }
            closedStates.add(keyOf(states));
            FSAState state = stateMap.get(keyOf(states));
            FSAEventSet[] eventSets = new FSAEventSet[maOrder];
            for (int i = 0; i < states.length; ++i) {
                FSAEventSet eventSet = new FSAEventSet();
                for (Iterator<FSATransition> ti = states[i].getOutgoingTransitionsListIterator(); ti.hasNext();) {
                    FSATransition t = ti.next();
                    if (t.getEvent() != null) {
                        eventSet.add(t.getEvent());
                    }
                }
                eventSets[i] = eventSet;
            }
            PseudoTransition pt = computeTransitions(new SupervisoryEvent[0], eventSets, states);
            Iterator<LinkedList<SupervisoryEvent>> ei = pt.events.iterator();
            Iterator<LinkedList<FSAState>> si = pt.targets.iterator();
            for (; ei.hasNext();) {
                SupervisoryEvent[] e = ei.next().toArray(new SupervisoryEvent[0]);
                FSAState[] s = si.next().toArray(new FSAState[0]);
                SupervisoryEvent event = eventMap.get(keyOf(e));
                if (event == null) {
                    event = makeEvent(e, a.getEventCount(), a.assembleEvent(""));
                    a.add(event);
                    eventMap.put(keyOf(e), event);
                }
                FSAState target = stateMap.get(keyOf(s));
                if (target == null) {
                    target = makeState(s, a.getStateCount(), a.assembleState());
                    a.add(target);
                    stateMap.put(keyOf(s), target);
                }
                a.add(a.assembleTransition(state.getId(), target.getId(), event.getId()));
                openStates.addLast(s);
            }
        }
        return new Object[] { a };
    }

    protected PseudoTransition computeTransitions(SupervisoryEvent[] selectedEvents, FSAEventSet[] freeEvents,
            FSAState[] origin) {
        PseudoTransition pt = new PseudoTransition();
        for (SupervisoryEvent e : freeEvents[0]) {
            boolean includeEvent = true;
            for (int i = 0; i < selectedEvents.length; ++i) {
                if (!e.getSymbol().equals(selectedEvents[i].getSymbol())) {
                    if (hasOutgoingTransitionOn(origin[i], e)
                            || hasOutgoingTransitionOn(origin[selectedEvents.length], selectedEvents[i])) {
                        includeEvent = false;
                        break;
                    }
                }
            }
            if (includeEvent) {
                FSAState target = null;
                for (Iterator<FSATransition> i = origin[selectedEvents.length].getOutgoingTransitionsListIterator(); i
                        .hasNext();) {
                    FSATransition t = i.next();
                    if (t.getEvent() != null && t.getEvent().getSymbol().equals(e.getSymbol())) {
                        target = t.getTarget();
                    }
                }
                if (freeEvents.length == 1) {
                    LinkedList<SupervisoryEvent> events = new LinkedList<SupervisoryEvent>();
                    events.add(e);
                    LinkedList<FSAState> targets = new LinkedList<FSAState>();
                    targets.add(target);
                    pt.events.add(events);
                    pt.targets.add(targets);
                } else {
                    SupervisoryEvent[] extended = new SupervisoryEvent[selectedEvents.length + 1];
                    System.arraycopy(selectedEvents, 0, extended, 0, selectedEvents.length);
                    extended[selectedEvents.length] = e;
                    FSAEventSet[] shorter = new FSAEventSet[freeEvents.length - 1];
                    System.arraycopy(freeEvents, 1, shorter, 0, shorter.length);
                    PseudoTransition subPT = computeTransitions(extended, shorter, origin);
                    Iterator<LinkedList<SupervisoryEvent>> ei = subPT.events.iterator();
                    Iterator<LinkedList<FSAState>> si = subPT.targets.iterator();
                    for (; ei.hasNext();) {
                        LinkedList<SupervisoryEvent> events = ei.next();
                        LinkedList<FSAState> targets = si.next();
                        LinkedList<SupervisoryEvent> extendedEvents = new LinkedList<SupervisoryEvent>(events);
                        extendedEvents.addFirst(e);
                        LinkedList<FSAState> extendedTargets = new LinkedList<FSAState>(targets);
                        extendedTargets.addFirst(target);
                        pt.events.add(extendedEvents);
                        pt.targets.add(extendedTargets);
                    }
                }
            }
        }
        return pt;
    }

    protected boolean hasOutgoingTransitionOn(FSAState s, SupervisoryEvent e) {
        for (Iterator<FSATransition> i = s.getOutgoingTransitionsListIterator(); i.hasNext();) {
            FSATransition t = i.next();
            if (t.getEvent() != null && t.getEvent().getSymbol().equals(e.getSymbol())) {
                return true;
            }
        }
        return false;
    }

    protected FSAState makeState(FSAState[] states, long id, FSAState newS) {
        // FSAState newS = new State(id);
        newS.setId(id);
        boolean isInitial = true;
        boolean isMarked = true;
        long[] composedIds = new long[states.length];
        String[] composedNames = new String[states.length];
        for (int i = 0; i < states.length; ++i) {
            isInitial &= states[i].isInitial();
            isMarked &= states[i].isMarked();
            composedIds[i] = states[i].getId();
            composedNames[i] = states[i].getName();
        }
        newS.setInitial(isInitial);
        newS.setMarked(isMarked);
        newS.setAnnotation(AnnotationKeys.COMPOSED_OF, composedIds);
        newS.setAnnotation(AnnotationKeys.COMPOSED_OF_NAMES, composedNames);
        return newS;
    }

    protected SupervisoryEvent makeEvent(SupervisoryEvent[] events, long id, SupervisoryEvent e) {
        // SupervisoryEvent e = new Event(id);
        e.setId(id);
        boolean isUncontrollable = true;
        String label = "$\\left[\\begin{array}{c}";
        for (SupervisoryEvent event : events) {
            label += "\\mbox{" + event.getSymbol() + "}\\\\";
            if (event.isControllable()) {
                isUncontrollable = false;
            }
        }
        if (label.endsWith("\\\\")) {
            label = label.substring(0, label.length() - 2);
        }
        label += "\\end{array}\\right]$";
        e.setSymbol(label);
        e.setControllable(!isUncontrollable);
        return e;
    }

    protected String keyOf(FSAState[] states) {
        String key = "";
        for (FSAState s : states) {
            key += s.getId() + ",";
        }
        return key;
    }

    protected String keyOf(SupervisoryEvent[] events) {
        String key = "";
        for (SupervisoryEvent e : events) {
            key += e.getId() + ",";
        }
        return key;
    }
}
