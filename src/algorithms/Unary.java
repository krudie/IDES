/**
 * Contains all operations defined as unary operations
 */
package algorithms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import projectModel.Automaton;
import projectModel.State;
import projectModel.SubElement;
import projectModel.Transition;

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
    public static void accesible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        // find initial states, mark them as reached and add them to the que
        Iterator<State> stateIterator = automaton.getStateIterator();
        while(stateIterator.hasNext()){
            State state = stateIterator.next();
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
    
    /**
     * Finds the coaccessible part of an automaton
     * The function does not return a new automaton, so if the original automaton should not be changed,
     * make sure to copy it first.
     * @param automaton The automaton which to find the coaccessible part of
     */  
    public static  void coAccesible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        ListIterator<State> states = automaton.getStateIterator();
        // mark all marked states as coaccesible and add them to the list.
        while(states.hasNext()){
            State s = states.next();
            if(s.getSubElement("properties").hasSubElement("marked")){
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
    
    /**
     * Finds the trim part of an automaton
     * The function does not return a new automaton, so if the original automaton should not be changed,
     * make sure to copy it first.
     * @param automaton The automaton which to trim.
     */   
    public static void trim(Automaton automaton){
        accesible(automaton);
        coAccesible(automaton);
    }
    
    /**
     * Makes an automaton prefix closed
     * The function does not return a new automaton, so if the original automaton should not be changed,
     * make sure to copy it first.
     * @param automaton The automaton to prefix close
     */    
    public static  void prefixClosure(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        ListIterator<State> states = automaton.getStateIterator();
        // add all marked states to the list.
        while(states.hasNext()){
            State s = states.next();
            if(s.getSubElement("properties").hasSubElement("marked")){
                searchList.add(s);
            }
        }
        // for all states in the list mark all states that can access this state
        // as marked and add it to the list (if it isn't allready marked.)
        while(!searchList.isEmpty()){
            State s = searchList.removeFirst();
            ListIterator<Transition> tli = s.getTargetTransitionListIterator();
            while(tli.hasNext()){
                State source = tli.next().getSource();
                if(!source.getSubElement("properties").hasSubElement("marked")){
                    source.getSubElement("properties").addSubElement(new SubElement("marked"));
                    searchList.addFirst(source);
                }
            }
        }
    }
}
