package projectModel;

import java.io.PrintStream;
import java.util.*;

/**
 * 
 * @author Axel Gottlieb Michelsen
 * 
 */
public class State extends SubElementContainer{
    private LinkedList<Transition> sourceT, targetT;

    private int id;

    /**
     * constructs a state with the given id.
     * @param id the id of the state.
     */
    public State(int id){
        this.id = id;
        sourceT = new LinkedList<Transition>();
        targetT = new LinkedList<Transition>();
    }

    /**
     * constructs a state that is similiar to the given state, except the
     * new state doesn't have any transitions.
     * @param s a state.
     */
    public State(State s){
        super(s);
        sourceT = new LinkedList<Transition>();
        targetT = new LinkedList<Transition>();
        this.id = s.id;
    }

    /**
     * adds a transition that originates from the state to the state's list
     * of transitions originating from it.
     * @param t the transition to be removed
     */
    public void addSourceTransition(Transition t){
        sourceT.add(t);
    }
    
    /**
     * removes a transition that originates from the state from the state's list
     * of transtions originating from it.
     * @param t the transition to be removed
     */
    public void removeSourceTransition(Transition t){
        sourceT.remove(t);
    }
    /**
     * returns an iterator for the transitions originating from this state.
     * @return
     */
    public ListIterator<Transition> getSourceTransitionsListIterator(){
        return sourceT.listIterator();
    }
    /**
     * @return a linked list of the transitions originating from this state.
     */
    public LinkedList<Transition> getSourceTransitions(){
        return sourceT;
    }

    /**
     * adds a transition that ends in this state to this state's list of
     * transitions ending in it.
     * @param t the transition to be added.
     */
    public void addTargetTransition(Transition t){
        targetT.add(t);
    }

    /**
     * removes a transition that ends in this state from this state's list of
     * transitions ending in it.
     * @param t the transition to be removed.
     */
    public void removeTargetTransition(Transition t){
        targetT.remove(t);
    }

    /**
     * @return an iterator for the transitions ending in this state
     */
    public ListIterator<Transition> getTargetTransitionListIterator(){
        return targetT.listIterator();
    }
    
    /**
     * @return a list of the transitions ending in this state.
     */
    public LinkedList<Transition> getTargetTransitions(){
        return targetT;
    }
    
    /**
     * @return the id of this state.
     */
    public int getId(){
        return id;
    }
    /**
     * @param id the id of this state
     */
    public void setId(int id){
        this.id = id;
    }
 }
