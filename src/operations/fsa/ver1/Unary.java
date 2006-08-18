/**
 * Contains all operations defined as unary operations
 */
package operations.fsa.ver1;

import io.fsa.ver1.SubElement;
import io.fsa.ver1.SubElementContainer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import model.fsa.ver1.Automaton;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;


/**
 * This class contatins methods for unary operations on automata.
 * 
 * @author Kristian Edlund
 * @author Axel Gottlieb Michelsen
 */
public class Unary{
    /**
     * Finds the accessible part of an automaton
     * The function does not return a new automaton, so if the original automaton should not be changed,
     *  make sure to copy it first.
     * @param automaton The automaton which to find the accessible part of
     */    
    public static void accessible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        // find initial states, mark them as reached and add them to the que
        Iterator stateIterator = automaton.getStateIterator();
        while(stateIterator.hasNext()){
            State state = (State) stateIterator.next();
            if(state.getSubElement("properties").hasSubElement("initial")){
                searchList.addFirst(state);
                state.addSubElement(new SubElement("accesible"));
            }
        }
        // for all accesible states
        while(!searchList.isEmpty()){
            State state = searchList.removeFirst();
            // mark all states that are accesible from this state as accesible
            // if they have not previously been marked as accesible.
            Iterator transitionIterator = state.getSourceTransitionsListIterator();
            while(transitionIterator.hasNext()){
                Transition transition = (Transition) transitionIterator.next();
                if(!((SubElementContainer) transition.getTarget()).hasSubElement("accessible")){
                    ((SubElementContainer) transition.getTarget()).addSubElement(new SubElement("accessible"));
                    searchList.addFirst((State) transition.getTarget());
                }
            }
        }
        // tidy up. remove all states that aren't accessible.
        stateIterator = automaton.getStateIterator();
        while(stateIterator.hasNext()){
            State state = (State) stateIterator.next();
            if(state.hasSubElement("accessible")) state.removeSubElement("accessible");
            else stateIterator.remove();
        }
    }
    
    /**
     * Finds the coaccessible part of an automaton
     * The function does not return a new automaton, so if the original automaton should not be changed,
     * make sure to copy it first.
     * @param automaton The automaton which to find the coaccessible part of
     */  
    public static  void coaccessible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        ListIterator states = automaton.getStateIterator();
        // mark all marked states as coaccessible and add them to the list.
        while(states.hasNext()){
            State s = (State) states.next();
            if(s.getSubElement("properties").hasSubElement("marked")){
                s.addSubElement(new SubElement("coaccessible"));
                searchList.add(s);
            }
        }
        // for all states in the list mark all states that can access this state
        // as coaccessible and add it to the list (if it isn't allready marked as
        // coaccessible.)
        while(!searchList.isEmpty()){
            State s = searchList.removeFirst();
            ListIterator tli = s.getTargetTransitionListIterator();
            while(tli.hasNext()){
                State source = (State) ((Transition) tli.next()).getSource();
                if(!source.hasSubElement("coaccessible")){
                    source.addSubElement(new SubElement("coaccessible"));
                    searchList.addFirst(source);
                }
            }
        }
        // tidy up. Remove all states that aren't coaccessible.
        states = automaton.getStateIterator();
        while(states.hasNext()){
            State s = (State) states.next();
            if(s.hasSubElement("coaccessible")) s.removeSubElement("coaccessible");
            else states.remove();
        }
    }
    
    /**
     * Finds the trim part of an automaton
     * The function does not return a new automaton, so if the original automaton should not be changed,
     * make sure to copy it first.
     * @param automaton The automaton which to trim.
     */   
    public static void trim(Automaton automaton){
        accessible(automaton);
        coaccessible(automaton);
    }
    
    /**
     * Makes an automaton prefix closed
     * The function does not return a new automaton, so if the original automaton should not be changed,
     * make sure to copy it first.
     * @param automaton The automaton to prefix close
     */    
    public static  void prefixClosure(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        ListIterator states = automaton.getStateIterator();
        // add all marked states to the list.
        while(states.hasNext()){
            State s = (State) states.next();
            if(s.getSubElement("properties").hasSubElement("marked")){
                searchList.add(s);
            }
        }
        // for all states in the list mark all states that can access this state
        // as marked and add it to the list (if it isn't allready marked.)
        while(!searchList.isEmpty()){
            State s = searchList.removeFirst();
            ListIterator tli = s.getTargetTransitionListIterator();
            while(tli.hasNext()){
                State source = (State) ((Transition) tli.next()).getSource();
                if(!source.getSubElement("properties").hasSubElement("marked")){
                    source.getSubElement("properties").addSubElement(new SubElement("marked"));
                    searchList.addFirst(source);
                }
            }
        }
    }
}
