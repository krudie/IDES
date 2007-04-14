package model.fsa.ver2_1;

import io.fsa.ver2_1.SubElement;
import io.fsa.ver2_1.SubElementContainer;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import model.fsa.FSATransition;


/**
 * Model of a state in a finite state automaton. 
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Helen Bretzke
 * @author Lenko Grigorov
 *
 */
public class State extends SubElementContainer implements model.fsa.FSAState {
    
    /* transitions originating from this state and ending in this state respectively. */
    private LinkedList<FSATransition> sourceT, targetT;

    private long id;
    
    // if this state represents the composition of the states of other automata,
    // this will contain a list the ids of these other states
    //  TODO move to meta info
    protected long[] composedOf=new long[0];
    
    //TODO make the state use a common annotation repository
    protected Hashtable<String, Object> annotations=new Hashtable<String,Object>();
    

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
        setInitial(s.isInitial());
        setMarked(s.isMarked());
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

	/**
	 * Flags this state as initial iff <code>initial</code> is true.
	 * 
	 * @param initial the initial property to set
	 */
	public void setInitial(boolean initial){
		SubElement props = getSubElement("properties");
		if(initial && !isInitial()){			
			props.addSubElement(new SubElement("initial"));			
		}
		if(!initial && isInitial()){
			props.removeSubElement("initial");
		}
	}
	
	/**
	 * Marks this state as final iff <code>mark</code> is true.
	 * i.e. sets the marked property to the given value.
	 * 
	 * @param mark the marked property to set
	 */
	public void setMarked(boolean mark){
		SubElement props = getSubElement("properties");
		if(mark && !isMarked()){					
			props.addSubElement(new SubElement("marked"));				
		}
		if(!mark && isMarked()){
			props.removeSubElement("marked");
		}
	}
	
	public void setId(long id) {
		this.id = id;		
	}

	public long getId() {		
		return id;
	}

	
	/**
	 * Gets the list of ids of the states of which this state is a composition.
	 * @return the list of ids of the states of which this state is a composition
	 */
	public long[] getStateCompositionList() //TODO move to meta info
	{
		return composedOf;
	}
	
	/**
	 * Sets the list of ids of the states of which this state is a composition.
	 * @param list the list of ids of the states of which this state is a composition
	 */
	public void setStateCompositionList(long[] list) //TODO move to meta info
	{
		composedOf=list;
	}
	
	public static final String ATTR_MARKED = "marked";
	public static final String ATTR_INITIAL = "initial";
	
	/**
	 * Returns the annotation for the given key.
	 * @param key key for the annotation
	 * @return if there is no annotation for the given key,
	 * returns <code>null</code>, otherwise returns the annotation
	 * for the key
	 */
	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}

	/**
	 * Sets an annotation for a given key. If there is already
	 * an annotation for the key, it is replaced. 
	 * @param key the key for the annotation
	 * @param annotation the annotation
	 */
	public void setAnnotation(String key, Object annotation)
	{
		annotations.put(key, annotation);
	}
	
	/**
	 * Returns <code>true</code> if there is an annotation
	 * for the given key. Otherwise returns <code>false</code>.
	 * @param key key for the annotation
	 * @return <code>true</code> if there is an annotation
	 * for the given key, <code>false</code> otherwise
	 */
	public boolean hasAnnotation(String key)
	{
		return annotations.containsKey(key);
	}

 }
