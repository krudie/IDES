package model.fsa;

import java.util.*;

import model.DESTransition;


/**
 * Model of the state
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class State extends SubElementContainer implements model.DESState {
    
    // transitions originating from this state and ending in this state respectively.
    private LinkedList<DESTransition> sourceT, targetT;

    private int id;

    /**
     * constructs a state with the given id.
     * @param id the id of the state.
     */
    public State(int id){
        this.id = id;
        sourceT = new LinkedList<DESTransition>();
        targetT = new LinkedList<DESTransition>();
    }

    /**
     * constructs a state that is similiar to the given state, except the
     * new state doesn't have any transitions.
     * @param s a state.
     */
    public State(State s){
        super(s);
        sourceT = new LinkedList<DESTransition>();
        targetT = new LinkedList<DESTransition>();
        this.id = s.id;
    }

    /**
     * adds a transition that originates from the state to the state's list
     * of transitions originating from it.
     * @param t the transition to be removed
     */
    public void addSourceTransition(DESTransition t){
        sourceT.add(t);
    }
    
    /**
     * removes a transition that originates from the state from the state's list
     * of transtions originating from it.
     * @param t the transition to be removed
     */
    public void removeSourceTransition(DESTransition t){
        sourceT.remove(t);
    }
    /**
     * returns an iterator for the transitions originating from this state.
     * @return a source transition iterator
     */
    public ListIterator<DESTransition> getSourceTransitionsListIterator(){
        return sourceT.listIterator();
    }
    /**
     * @return a linked list of the transitions originating from this state.
     */
    public LinkedList<DESTransition> getSourceTransitions(){
        return sourceT;
    }

    /**
     * adds a transition that ends in this state to this state's list of
     * transitions ending in it.
     * @param t the transition to be added.
     */
    public void addTargetTransition(DESTransition t){
        targetT.add(t);
    }

    /**
     * removes a transition that ends in this state from this state's list of
     * transitions ending in it.
     * @param t the transition to be removed.
     */
    public void removeTargetTransition(DESTransition t){
        targetT.remove(t);
    }

    /**
     * @return an iterator for the transitions ending in this state
     */
    public ListIterator<DESTransition> getTargetTransitionListIterator(){
        return targetT.listIterator();
    }
    
    /**
     * @return a list of the transitions ending in this state.
     */
    public LinkedList<DESTransition> getTargetTransitions(){
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
