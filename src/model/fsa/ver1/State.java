package model.fsa.ver1;

import io.fsa.ver1.SubElement;
import io.fsa.ver1.SubElementContainer;

import java.util.LinkedList;
import java.util.ListIterator;

import model.fsa.FSATransition;


/**
 * Model of the state
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Helen Bretzke
 *
 */
public class State extends SubElementContainer implements model.fsa.FSAState {
    
    // transitions originating from this state and ending in this state respectively.
    private LinkedList<FSATransition> sourceT, targetT;

    private long id;

    /**
     * constructs a state with the given id.
     * @param id the id of the state.
     */
    public State(long id){
        this.id = id;
        sourceT = new LinkedList<FSATransition>();
        targetT = new LinkedList<FSATransition>();
        this.addSubElement(new SubElement("properties"));
    }

    /**
     * constructs a state that is similiar to the given state, except the
     * new state doesn't have any transitions.
     * @param s a state.
     */
    public State(State s){
        super(s);
        sourceT = new LinkedList<FSATransition>();
        targetT = new LinkedList<FSATransition>();
        this.id = s.id;
    }

    /**
     * adds a transition that originates from the state to the state's list
     * of transitions originating from it.
     * @param t the transition to be removed
     */
    public void addSourceTransition(FSATransition t){
        sourceT.add(t);
    }
    
    /**
     * removes a transition that originates from the state from the state's list
     * of transtions originating from it.
     * @param t the transition to be removed
     */
    public void removeSourceTransition(FSATransition t){
        sourceT.remove(t);
    }
    /**
     * returns an iterator for the transitions originating from this state.
     * @return a source transition iterator
     */
    public ListIterator<FSATransition> getSourceTransitionsListIterator(){
        return sourceT.listIterator();
    }
    /**
     * @return a linked list of the transitions originating from this state.
     */
    public LinkedList<FSATransition> getSourceTransitions(){
        return sourceT;
    }

    /**
     * adds a transition that ends in this state to this state's list of
     * transitions ending in it.
     * @param t the transition to be added.
     */
    public void addTargetTransition(FSATransition t){
        targetT.add(t);
    }

    /**
     * removes a transition that ends in this state from this state's list of
     * transitions ending in it.
     * @param t the transition to be removed.
     */
    public void removeTargetTransition(FSATransition t){
        targetT.remove(t);
    }

    /**
     * @return an iterator for the transitions ending in this state
     */
    public ListIterator<FSATransition> getTargetTransitionListIterator(){
        return targetT.listIterator();
    }
    
    /**
     * @return a list of the transitions ending in this state.
     */
    public LinkedList<FSATransition> getTargetTransitions(){
        return targetT;
    }    
	
	/**
	 * @return true iff this is an initial state
	 */	
	public boolean isInitial() {
		SubElement props = this.getSubElement("properties");
		return props != null && props.getSubElement("initial") != null;
	}

	/**
	 * @return true iff this is marked (final) state
	 */
	public boolean isMarked() {
		SubElement props = this.getSubElement("properties");
		return props != null && props.getSubElement("marked") != null;		
	}	

	public void setInitial(boolean b){
		if(b && !isInitial()){
			SubElement props = this.getSubElement("properties");
			if(props == null){
				props = new SubElement("properties");		
				this.addSubElement(props);
			}
			props.addSubElement(new SubElement("initial"));			
		}
	}
	
	public void setMarked(boolean mark){
		if(mark && !isMarked()){			
			SubElement props = this.getSubElement("properties");
			if(props == null){
				props = new SubElement("properties");
				this.addSubElement(props);
			}
			props.addSubElement(new SubElement("marked"));				
		}
	}
	
	public void setName(String name){
		SubElement n = new SubElement("name");
		n.setChars(name);
		addSubElement(n);
	}
	
	/**
	 * If this state has been labelled, returns the name
	 * otherwise returns the empty string.
	 * 
	 * @return the name of this state
	 */
	public String getName(){
		SubElement name = getSubElement("name");
		if(name != null){
			return (name.getChars() != null) ? name.getChars() : "";
		}else{
			return "";
		}
	}
	
	public void setId(long id) {
		this.id = id;		
	}

	public long getId() {		
		return id;
	}
	
 }
