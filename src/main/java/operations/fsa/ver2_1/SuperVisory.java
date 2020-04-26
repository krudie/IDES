package operations.fsa.ver2_1;

import java.util.ListIterator;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.model.ModelManager;
import util.AnnotationKeys;

/**
 * This class contains methods for supervisory control of discrete event
 * systems.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian edlund
 * @author Lenko Grigorov
 */
public class SuperVisory {

    /**
     * Finds the supremal controllable sublanguage of a legal language wrt. a given
     * plant
     * 
     * @param plant  The plant
     * @param legal  The legal language
     * @param result An empty automaton to use for the result
     */
    public static void supC(FSAModel plant, FSAModel legal, FSAModel result) {

        // This is implemented accourding to "Introduction to discrete event
        // systems of Cassandras and Lafortune.
        // Page 177

        // step 1
        // take the product of the plant and the legal language
        supCProduct(plant, legal, result);

        boolean changed = true;

        // While we keep removing stuff continue
        while (changed) {
            changed = false;
            // step 2
            ListIterator<FSAState> si = result.getStateIterator();
            // step 2.1
            // For all states in the result of the product check to see if there
            // are any uncontrollable events that are disabled
            // in that case, delete the state from the result
            while (si.hasNext()) {
                FSAState s = si.next();

                FSAState pln = plant.getState(((long[]) s.getAnnotation(AnnotationKeys.COMPOSED_OF))[0]);
                ListIterator<FSATransition> plsti = pln.getOutgoingTransitionsListIterator();

                while (plsti.hasNext()) {
                    FSATransition plst = plsti.next();
                    // if the event is not controllable, check if the event is
                    // in the product as well
                    if (plst.getEvent() != null && !plst.getEvent().isControllable()) {
                        ListIterator<FSATransition> sti = s.getOutgoingTransitionsListIterator();
                        boolean found = false;
                        while (sti.hasNext()) {
                            FSATransition t = sti.next();
                            // if we find the event we might as well break out
                            // of the search
                            if (t.getEvent() != null && t.getEvent().equals(plst.getEvent())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            si.remove();
                            changed = true;
                            break;
                        }
                    }
                }
            }
            // Step 2.2
            long stateCount = result.getStateCount();
            Unary.trim(result);
            if (result.getStateCount() != stateCount) {
                changed = true;
            }
        }
    }

    /**
     * Checks to see if a given legal language is controllable wrt. a plant
     * 
     * @param plant The plant
     * @param legal The legal language
     * @return The answer to the controllable question
     */
    public static boolean controllable(FSAModel plant, FSAModel legal) {
        // This function is very similar to supC besides that it will only run
        // trough the automaton once to see if anyhitng should be cut of.
        FSAModel result = ModelManager.instance().createModel(FSAModel.class, "");

        supCProduct(plant, legal, result);

        ListIterator<FSAState> si = result.getStateIterator();

        while (si.hasNext()) {
            FSAState s = si.next();

            FSAState pln = plant.getState(((long[]) s.getAnnotation(AnnotationKeys.COMPOSED_OF))[0]);
            ListIterator<FSATransition> plsti = pln.getOutgoingTransitionsListIterator();

            while (plsti.hasNext()) {
                FSATransition plst = plsti.next();
                // if the event is not controllable, check if the event is in
                // the product as well
                if (plst.getEvent() != null && !plst.getEvent().isControllable()) {
                    ListIterator<FSATransition> sti = s.getOutgoingTransitionsListIterator();
                    boolean found = false;
                    while (sti.hasNext()) {
                        FSATransition t = sti.next();
                        // if we find the event we might as well break out of
                        // the search
                        if (t.getEvent() != null && t.getEvent().equals(plst.getEvent())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // if the event is not found in the product as well it
                        // is not controllable
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Computes the accessible product of the two automata a and b for use with the
     * supremal controllable sublanguage. [?This is made as a special an extra flag
     * is set in the resulting automaton?]
     * 
     * @param a       an automaton
     * @param b       an automaton
     * @param product the accesible product of a and b.
     */
    private static void supCProduct(FSAModel a, FSAModel b, FSAModel product) {
        Composition.product(a, b, product);
        Unary.accessible(product);
    }
}
