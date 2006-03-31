package model.fsa;

import model.Event;
import model.State;

/**
 * This class represent a transition in an automaton.
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class FSATransition extends SubElementContainer implements model.Transition {
    
	private State sourceS, targetS;

    private Event e = null;

    private int id;

    /**
     * Constructs a new transition originating in state source and ending in
     * state target. This transition is fired by the null event (epsilon).
     * 
     * @param id the id of this transition.
     * @param source the source state.
     * @param target the target state.
     */
    public FSATransition(int id, FSAState source, FSAState target){
        this.id = id;
        this.sourceS = source;
        this.targetS = target;
    }

    /**
     * Constructs a new transition with the given id, originating in state
     * source and ending in state target and firing upon receival of event e.
     * 
     * @param id the id of the state.
     * @param source the source state.
     * @param target the target state.
     * @param e the event this transition fires uppon receival of.
     */
    public FSATransition(int id, FSAState source, FSAState target, FSAEvent e){
        this.id = id;
        this.sourceS = source;
        this.targetS = target;
        this.e = e;
    }

    /**
     * Constructs a new transition where the internal variables of the new
     * transition are the same as t. The transition originates in state source
     * and ends in state target and fires uppon receival of event e.
     * 
     * @param t the transiton the new transition is to resemble.
     * @param source the source state.
     * @param target the target state.
     * @param e the event this transition fires uppon receival of.
     */
    public FSATransition(FSATransition t, FSAState source, FSAState target, FSAEvent e){
        super(t);
        this.id = t.id;
        this.sourceS = source;
        this.targetS = target;
        this.e = e;
    }

    /**
     * Constructs a new transition where the internal variables of the new
     * transition are the same as t. The transition originates in state source
     * and ends in state target and fires uppon receival of event null
     * (epsillon).
     * 
     * @param t the transiton the new transition is to resemble.
     * @param sourceS the source state.
     * @param targetS the target state.
     */
    public FSATransition(FSATransition t, FSAState sourceS, FSAState targetS){
        super(t);
        this.id = t.id;
        this.sourceS = sourceS;
        this.targetS = targetS;
    }

    /**
     * Sets a new source, i.e., state from which this transition originates, for
     * this transition.
     * 
     * @param s the new source.
     */
    public void setSource(State s){
        this.sourceS = s;
    }

    /**
     * Sets a new source, i.e., state from which this transition originates, for
     * this transition.
     */
    public State getSource(){
        return this.sourceS;
    }

    /**
     * Sets a new target, i.e., state from which this transition originates, for
     * this transition.
     * 
     * @param s the new source.
     */
    public void setTarget(State s){
        this.targetS = s;
    }

    /**
     * returns the state this transition ends in.
     * 
     * @return the target state.
     */
    public State getTarget(){
        return this.targetS;
    }

    /**
     * set the event this transiton fires uppon to e.
     * 
     * @param e the event this transition fires uppon.
     */
    public void setEvent(Event e){
        this.e = e;
    }

    /**
     * returns the event this transition fires uppon.
     * 
     * @return the event this transition fires uppon.
     */
    public Event getEvent(){
        return e;
    }

    /**
     * returns the id of this transition.
     * 
     * @return the id of this transition.
     */
    public int getId(){
        return id;
    }
}