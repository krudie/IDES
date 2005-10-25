/**
 * 
 */
package projectPresentation;

import java.util.LinkedList;
import java.util.ListIterator;

import projectModel.Automaton;
import projectModel.State;
import projectModel.SubElement;
import projectModel.Transition;

/**
 * @author edlund
 *
 */
public class CoAccessible{

    public static  void coAccesible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        ListIterator<State> states = automaton.getStateIterator();
        // mark all marked states as coaccesible and add them to the list.
        while(states.hasNext()){
            State s = states.next();
            if(s.getSubElement("properties").getSubElement("marked").getChars().equals("true")){
                s.addSubElement(new SubElement("coaccesible"));
                searchList.add(s);
            }
        }
        // for all states in the list mark all states that can access this state
        // as coaccesible and add it to the list (if it isn't allready marked as
        // coaccesible.)
        while(!searchList.isEmpty()){
            State s = searchList.removeFirst();
            ListIterator<Transition> tli = s.getTargetTransitionListIterator();
            while(tli.hasNext()){
                State source = tli.next().getSource();
                if(!source.hasSubElement("coaccesible")){
                    source.addSubElement(new SubElement("coaccesible"));
                    searchList.addFirst(source);
                }
            }
        }
        // tidy up. Remove all states that aren't coaccesible.
        states = automaton.getStateIterator();
        while(states.hasNext()){
            State s = states.next();
            if(s.hasSubElement("coaccesible")) s.removeSubElement("coaccesible");
            else states.remove();
        }
    }
}
