package model.fsa.ver1;

import io.fsa.ver1.SubElement;
import io.fsa.ver1.SubElementContainer;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import model.fsa.FSATransition;


/**
 * Model of the state
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Helen Bretzke
 * @author Lenko Grigorov
 *
 */
public class State extends SubElementContainer implements model.fsa.FSAState {
    
    // transitions originating from this state and ending in this state respectively.
    private LinkedList<FSATransition> sourceT, targetT;

    private long id;
    
    // if this state represents the composition of the states of other automata,
    // this will contain a list the ids of these other states
    protected long[] composedOf=null;

    /**
     * constructs a state with the given id.
     * @param id the id of the state.
     */
    public State(long id){
        this.id = id;
        sourceT = new LinkedList<FSATransition>();
        targetT = new LinkedList<FSATransition>();
        addSubElement(new SubElement("properties"));
        addSubElement(new SubElement("name"));
    }

    /**
     * constructs a state that is similiar to the given state, except the
     * new state doesn't have any transitions.
     * @param s a state.
     */
    public State(State s){
        super(s);
        this.id = s.id;
        sourceT = new LinkedList<FSATransition>();
        targetT = new LinkedList<FSATransition>();
        addSubElement(new SubElement("properties"));
        addSubElement(new SubElement("name"));
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
		SubElement props = getSubElement("properties");
		return props.getSubElement("initial") != null;
	}

	/**
	 * @return true iff this is marked (final) state
	 */
	public boolean isMarked() {
		SubElement props = getSubElement("properties");
		return props.getSubElement("marked") != null;		
	}	

	public void setInitial(boolean b){
		SubElement props = getSubElement("properties");
		if(b && !isInitial()){			
			props.addSubElement(new SubElement("initial"));			
		}
		if(!b && isInitial()){
			props.removeSubElement("initial");
		}
	}
	
	public void setMarked(boolean mark){
		SubElement props = getSubElement("properties");
		if(mark && !isMarked()){					
			props.addSubElement(new SubElement("marked"));				
		}
		if(!mark && isMarked()){
			props.removeSubElement("marked");
		}
	}
	
//	/******************************************************************
//	 * REMOVE from this class, the name is graphical layout information, 
//	 * not pertinent to the machine.
//	 * 
//	 * @param name
//	 */
//	public void setName(String name){
//		SubElement n = new SubElement("name");
//		n.setChars(name);
//		addSubElement(n);
//	}
//	
//	/**
//	 * If this state has been labelled, returns the name
//	 * otherwise returns the empty string.
//	 * 
//	 * @return the name of this state
//	 */
//	public String getName(){
//		SubElement name = getSubElement("name");
//		if(name != null){
//			return (name.getChars() != null) ? name.getChars() : "";
//		}else{
//			return "";
//		}
//	}
//	/********************************************************************/
	
	public void setId(long id) {
		this.id = id;		
	}

	public long getId() {		
		return id;
	}

	/**
	 * Sets the given attribute to the given value.
	 * If <code>attribute</code> is not a valid attribute name,
	 * does nothing.	 
	 * 
	 * TODO change string literals to constants in a set of valid attribute names. 
	 */
	public void set(String attribute, String value) {
		// TODO Auto-generated method stub
		if(attribute.equals(ATTR_MARKED)){
			setMarked(Boolean.parseBoolean(value)); 
			return;
		}
		
		if(attribute.equals(ATTR_INITIAL)){
			setInitial(Boolean.parseBoolean(value));
			return;
		}

		// DEBUG
		System.err.println("State: cannot set attribute " + attribute);
	}

	public String get(String attribute) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Gets the list of ids of the states of which this state is a composition.
	 * @return the list of ids of the states of which this state is a composition
	 */
	public long[] getStateCompositionList()
	{
		return composedOf;
	}
	
	/**
	 * Sets the list of ids of the states of which this state is a composition.
	 * @param list the list of ids of the states of which this state is a composition
	 */
	public void setStateCompositionList(long[] list)
	{
		composedOf=list;
	}
	
	public static final String ATTR_MARKED = "marked";
	public static final String ATTR_INITIAL = "initial";
 }
