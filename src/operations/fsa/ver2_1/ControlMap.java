package operations.fsa.ver2_1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSASupervisor;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.FilterOperation;

public class ControlMap extends AbstractOperation implements FilterOperation {

    public ControlMap() {
        NAME = "controlmap";
        DESCRIPTION = "Produces a supervisor with " + "a control map.";
        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSASupervisor.class, FSAModel.class };
        inputDesc = new String[] { "Supervisor", "Plant" };
        ;

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Finite-state Automaton" };
    }

    /**
     * To be used to store the ids of pairs of states
     */
    protected static Map<String, Long> pairIds = new TreeMap<String, Long>();

    public Object[] filter(Object[] inputs) {
        warnings.clear();
        FSASupervisor supervisor;
        FSAModel plant;
        if (inputs.length >= 2) {
            if (inputs[0] instanceof FSASupervisor && inputs[1] instanceof FSAModel) {
                supervisor = (FSASupervisor) inputs[0];
                plant = (FSAModel) inputs[1];
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { inputs[0] };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { inputs[0] };
        }

        // Unary.buildStateCompositionOfClone((Automaton)supervisor);

        if (!FSAToolbox.isDeterministic(plant) || !FSAToolbox.isDeterministic(supervisor)) {
            warnings.add(FSAToolbox.NON_DETERM);
            return new Object[] { supervisor };
        }

        FSAModel product = ModelManager.instance().createModel(FSAModel.class, "temp");

        // find initial states, mark them as reached and add them to the que
        FSAState[] initial = new FSAState[2];
        long stateNumber = 0;
        LinkedList<FSAState[]> searchList = new LinkedList<FSAState[]>();

        Iterator<FSAState> sia = plant.getStateIterator();
        while (sia.hasNext()) {
            initial[0] = sia.next();
            if (initial[0].isInitial()) {
                Iterator<FSAState> sib = supervisor.getStateIterator();
                while (sib.hasNext()) {
                    initial[1] = sib.next();
                    if (initial[1].isInitial()) {
                        searchList.add(initial.clone());
                        product.add(makeState(initial, stateNumber, product.assembleState()));
                        setStateId(initial, stateNumber++);
                    }
                }
            }
        }

        // accessibility. All accessible states are added to product.
        // Transitions are only traversible if they can be traversed from both
        // states in sa
        // firing the same event, i.e., the intersection of the transitions
        // originating from the two
        // states are the transitions of state in product.
        FSAState[] s = new FSAState[2];
        while (!searchList.isEmpty()) {
            FSAState[] sa = searchList.removeFirst();

            ListIterator<FSATransition> sti0 = sa[0].getOutgoingTransitionsListIterator();
            while (sti0.hasNext()) {
                FSATransition t0 = sti0.next();
                ListIterator<FSATransition> sti1 = sa[1].getOutgoingTransitionsListIterator();
                SupervisoryEvent notMatched = t0.getEvent();
                while (sti1.hasNext()) {
                    FSATransition t1 = sti1.next();
                    if ((t0.getEvent() == null && t1.getEvent() == null) || (t0.getEvent() != null
                            && t1.getEvent() != null && t0.getEvent().equals(t1.getEvent()))) {

                        s[0] = t0.getTarget();
                        s[1] = t1.getTarget();

                        long id = getStateId(s);
                        if (id == -1) {
                            FSAState target = makeState(s, stateNumber, product.assembleState());
                            product.add(target);
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                        notMatched = null;
                    }
                }
                if (supervisor.getDisabledEvents(sa[1]) == null) {
                    supervisor.setDisabledEvents(sa[1], ModelManager.instance().createEmptyEventSet());
                }
                if (notMatched != null) {
                    DESEventSet de = supervisor.getDisabledEvents(sa[1]);
                    de.add(notMatched);
                    supervisor.setDisabledEvents(sa[1], de);
                }
            }
        }

        pairIds.clear();

        // TODO the block below is only for debugging
        // for(Iterator<FSAState> i=supervisor.getStateIterator();i.hasNext();)
        // {
        // System.out.println(supervisor.getDisabledEvents(i.next()).toString());
        // }

        return new Object[] { supervisor };
    }

    /**
     * Private function for making a new state from a stateset
     * 
     * @param s           the stateset to make a new state from
     * @param stateNumber the id of the new state
     * @param state       a state that has been pre-assembled in the model to which
     *                    this new state is to be added
     * @return the newly created state
     */
    private static FSAState makeState(FSAState[] s, long stateNumber, FSAState state) {
        // FSAState state = new State(stateNumber);
        state.setId(stateNumber);

        if (s[0].isInitial() && s[1].isInitial()) {
            state.setInitial(true);
        }

        if (s[0].isMarked() && s[1].isMarked()) {
            state.setMarked(true);
        }
        return state;
    }

    /**
     * set the stateid for a set of states
     * 
     * @param s       the stateset
     * @param stateId the id to set
     */
    private static void setStateId(FSAState[] s, long stateId) {
        pairIds.put("" + s[0].getId() + "," + s[1].getId(), new Long(stateId));
    }

    /**
     * Gets the id from a set of states
     * 
     * @param s the stateset
     * @return the id of the stateset
     */
    private static long getStateId(FSAState[] s) {
        String key = "" + s[0].getId() + "," + s[1].getId();
        if (pairIds.containsKey(key)) {
            return pairIds.get(key).longValue();
        }
        return -1;
    }

    public int[] getInputOutputIndexes() {
        return new int[] { 0 };
    }

    @Override
    public Object[] perform(Object[] inputs) {
        FSAModel supervisor = ((FSAModel) inputs[0]).clone();
        FSAModel plant = (FSAModel) inputs[1];
        filter(new Object[] { supervisor, plant });
        return new Object[] { supervisor };
    }

}
