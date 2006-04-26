package model.fsa;

import java.awt.Point;
import java.util.*;

import presentation.GraphicalLayout;

import model.DESTransition;


/**
 * Model of the state
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
/**
 * @author helen
 *
 */
/**
 * @author helen
 *
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
	
	/**
	 * @return true iff this is an initial state
	 */	
	public boolean isInitial() {		
		return getSubElement("properties").getSubElement("initial") != null;
	}

	/**
	 * @return true iff this is marked (final) state
	 */
	public boolean isMarked() {
		return getSubElement("properties").getSubElement("marked") != null;		
	}   
	
	/**
	 * @return an object encapsulating all of the graphical layout 
	 * 	information required to display this state.
	 */
	public StateLayout getLayout() {
		// radius, centre point, label text and arrow vector (if initial)
		SubElement layout = getSubElement("graphic").getSubElement("circle");
		int radius = Integer.parseInt(layout.getAttribute("r"));
		Point centre = new Point(Integer.parseInt(layout.getAttribute("x")),
								 Integer.parseInt(layout.getAttribute("y")));
				
		SubElement name = getSubElement("name");
        String n = (name.getChars() != null) ? name.getChars() : "";
        
        if(isInitial()) {
        	SubElement a = getSubElement("graphic").getSubElement("arrow");
        	Point arrow = new Point((int)Float.parseFloat(a.getAttribute("x")),
								 (int)Float.parseFloat(a.getAttribute("y")));
        	return new StateLayout(centre, radius, n, arrow);
        } else {
		 	return new StateLayout(centre, radius, n);
        }	
	}
	
 }
