package operations.fsa.ver2_1;

import java.util.Iterator;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.FilterOperation;

public class SelfLoop extends AbstractOperation implements FilterOperation {
    public SelfLoop() {
        NAME = "selfloop";
        DESCRIPTION = "Creates self-loops of given events at every state of an automaton. "
                + "In other words, computes the inverse projection with respect to these events.";

        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class, DESEventSet.class };
        inputDesc = new String[] { "Finite-state automaton", "Events to self-loop" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Automaton with self-looped events" };
    }

    public Object[] filter(Object[] inputs) {
        // TODO Auto-generated method stub
        return null;
    }

    public int[] getInputOutputIndexes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] perform(Object[] inputs) {
        warnings.clear();
        boolean selfloopDupEvents = false;
        FSAModel fsa;
        DESEventSet inputEvents;

        if (inputs.length >= 2) {
            if (inputs[0] instanceof FSAModel && inputs[1] instanceof DESEventSet) {
                fsa = ((FSAModel) inputs[0]).clone();
                inputEvents = (DESEventSet) inputs[1];
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        SupervisoryEventSet events = ModelManager.instance().createModel(SupervisoryEventSet.class);

        for (DESEvent e : inputEvents) {
            if (!fsa.getEventSet().contains(e)) {
                SupervisoryEvent temp = fsa.assembleCopyOf(e);
                fsa.add(temp);
                events.add(temp); // add the events assembled in fsa (i.e. with
                                  // the same ids)
            } else {
                events.add(e);
            }
        }
        for (Iterator<FSAState> i = fsa.getStateIterator(); i.hasNext();) {
            FSAState s = i.next();
            for (Iterator<SupervisoryEvent> i2 = events.iteratorSupervisory(); i2.hasNext();) {
                SupervisoryEvent e = i2.next();
                boolean hasTransition = false;
                FSATransition emptyLoop = null;
                for (Iterator<FSATransition> j = s.getOutgoingTransitionsListIterator(); j.hasNext();) {
                    FSATransition t = j.next();
                    if (e.equals(t.getEvent())) {
                        hasTransition = true;
                        break;
                    }
                    if (t.getEvent() == null && t.getSource() == t.getTarget()) {
                        emptyLoop = t;
                    }
                }
                if (!hasTransition) {
                    if (emptyLoop != null) {
                        emptyLoop.setEvent(e);
                    } else {
                        fsa.add(fsa.assembleTransition(s.getId(), s.getId(), e.getId()));
                    }
                }
            }
        }
        if (selfloopDupEvents) {
            warnings.add(Hub.string("warnSelfloopDupEvents"));
        }
        return new Object[] { fsa };
    }

}
