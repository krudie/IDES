/**
 * 
 */
package projectPresentation;

import java.util.Iterator;
import java.util.LinkedList;

import projectModel.Automaton;
import projectModel.State;
import projectModel.SubElement;
import projectModel.Transition;

/**
 * @author edlund
 *
 */
public class Accessible{

    public static void accesible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        // find initial states, mark them as reached and add them to the que
        Iterator<State> stateIterator = automaton.getStateIterator();
        while(stateIterator.hasNext()){
            State state = stateIterator.next();
            if(state.getSubElement("properties").getSubElement("initial").getChars().equals("true")){
                searchList.addFirst(state);
                state.addSubElement(new SubElement("accesible"));
            }
        }
        // for all accesible states
        while(!searchList.isEmpty()){
            State state = searchList.removeFirst();
            // mark all states that are accesible from this state as accesible
            // if they have not previously been marked as accesible.
            Iterator<Transition> transitionIterator = state.getSourceTransitionsListIterator();
            while(transitionIterator.hasNext()){
                Transition transition = transitionIterator.next();
                if(!transition.getTarget().hasSubElement("accesible")){
                    transition.getTarget().addSubElement(new SubElement("accesible"));
                    searchList.addFirst(transition.getTarget());
                }
            }
        }
        // tidy up. remove all states that aren't accesible.
        stateIterator = automaton.getStateIterator();
        while(stateIterator.hasNext()){
            State state = stateIterator.next();
            if(state.hasSubElement("accesible")) state.removeSubElement("accesible");
            else stateIterator.remove();
        }
    }
    
}
