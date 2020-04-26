package operations.fsa.ver2_1;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 * @author Valerie Sugarman
 */
public class Subset {

    /**
     * The algorithm used for the subset and language equality operations
     * 
     * @param a candidate sublanguage
     * @param b candidate superlanguage
     * @return <code>true</code> if the sublanguage is contained in the
     *         superlanguage, <code>false</code> otherwise
     */
    public static boolean subset(FSAModel a, FSAModel b) {

        a = (FSAModel) OperationManager.instance().getOperation("trim").perform(new Object[] { a })[0];
        b = (FSAModel) OperationManager.instance().getOperation("trim").perform(new Object[] { b })[0];

        LinkedList<FSAState[]> searchList = new LinkedList<FSAState[]>();
        Set<String> pairs = new HashSet<String>();
        FSAState[] cState = new FSAState[2];

        Iterator<FSAState> sia = a.getStateIterator();
        while (sia.hasNext()) {
            cState[0] = sia.next();
            if (cState[0].isInitial()) {
                Iterator<FSAState> sib = b.getStateIterator();
                while (sib.hasNext()) {
                    cState[1] = sib.next();
                    if (cState[1].isInitial()) {
                        searchList.add(cState.clone());
                    }
                }
            }
        }

        boolean contained = !searchList.isEmpty() || a.getStateCount() == 0;

        while (!searchList.isEmpty()) {
            cState = searchList.removeFirst();
            contained &= !(cState[0].isMarked() && !cState[1].isMarked());
            if (!contained) {
                break;
            }
            pairs.add("" + cState[0].getId() + "," + cState[1].getId());
            for (Iterator<FSATransition> i = cState[0].getOutgoingTransitionsListIterator(); i.hasNext();) {
                FSATransition ta = i.next();
                boolean matchingFound = false;
                for (Iterator<FSATransition> j = cState[1].getOutgoingTransitionsListIterator(); j.hasNext();) {
                    FSATransition tb = j.next();
                    if ((ta.getEvent() == null && tb.getEvent() == null)
                            || (ta.getEvent() != null && ta.getEvent().equals(tb.getEvent()))) {
                        if (!pairs.contains("" + ta.getTarget().getId() + "," + tb.getTarget().getId())) {
                            searchList.add(new FSAState[] { ta.getTarget(), tb.getTarget() });
                        }
                        matchingFound = true;
                        break;
                    }
                }
                contained &= matchingFound;
                if (!contained) {
                    break;
                }
            }
        }

        return contained;
    }
}
