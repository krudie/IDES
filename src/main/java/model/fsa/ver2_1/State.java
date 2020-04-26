package model.fsa.ver2_1;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;

/**
 * Model of a state in a finite state automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class State implements ides.api.model.fsa.FSAState {
    public static final String NAME = "name", INITIAL = "initial", MARKED = "marked";

    /*
     * transitions originating from this state and ending in this state
     * respectively.
     */
    private LinkedList<FSATransition> sourceT, targetT;

    private long id;

    protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

    /**
     * constructs a state with the given id.
     * 
     * @param id the id of the state.
     */
    public State(long id) {
        this.id = id;
        sourceT = new LinkedList<FSATransition>();
        targetT = new LinkedList<FSATransition>();
        // addSubElement(new SubElement("properties"));
        // addSubElement(new SubElement("name"));
    }

    /**
     * constructs a state that is similiar to the given state, except the new state
     * doesn't have any transitions.
     * 
     * @param s a state.
     */
    public State(FSAState s) {
        super();
        setId(s.getId());
        setInitial(s.isInitial());
        setMarked(s.isMarked());
        setName(s.getName());
        sourceT = new LinkedList<FSATransition>();
        targetT = new LinkedList<FSATransition>();
    }

    /**
     * adds a transition that originates from the state to the state's list of
     * transitions originating from it.
     * 
     * @param t the transition to be removed
     */
    public void addOutgoingTransition(FSATransition t) {
        sourceT.add(t);
    }

    /**
     * removes a transition that originates from the state from the state's list of
     * transtions originating from it.
     * 
     * @param t the transition to be removed
     */
    public void removeOutgoingTransition(FSATransition t) {
        sourceT.remove(t);
    }

    /**
     * returns an iterator for the transitions originating from this state.
     * 
     * @return a source transition iterator
     */
    public ListIterator<FSATransition> getOutgoingTransitionsListIterator() {
        return sourceT.listIterator();
    }

    /**
     * @return a linked list of the transitions originating from this state.
     */
    public LinkedList<FSATransition> getSourceTransitions() {
        return sourceT;
    }

    /**
     * adds a transition that ends in this state to this state's list of transitions
     * ending in it.
     * 
     * @param t the transition to be added.
     */
    public void addIncomingTransition(FSATransition t) {
        targetT.add(t);
    }

    /**
     * removes a transition that ends in this state from this state's list of
     * transitions ending in it.
     * 
     * @param t the transition to be removed.
     */
    public void removeIncomingTransition(FSATransition t) {
        targetT.remove(t);
    }

    /**
     * @return an iterator for the transitions ending in this state
     */
    public ListIterator<FSATransition> getIncomingTransitionsListIterator() {
        return targetT.listIterator();
    }

    /**
     * @return a list of the transitions ending in this state.
     */
    public LinkedList<FSATransition> getTargetTransitions() {
        return targetT;
    }

    /**
     * @return true iff this is an initial state
     */
    public boolean isInitial() {
        return (this.getAnnotation(INITIAL) == null ? false : ((Boolean) this.getAnnotation(INITIAL)).booleanValue());
    }

    /**
     * @return true iff this is marked (final) state
     */
    public boolean isMarked() {
        return (this.getAnnotation(MARKED) == null ? false : ((Boolean) this.getAnnotation(MARKED)).booleanValue());
    }

    /**
     * Flags this state as initial iff <code>initial</code> is true.
     * 
     * @param initial the initial property to set
     */
    public void setInitial(boolean initial) {
        this.setAnnotation(INITIAL, initial);
    }

    /**
     * Marks this state as final iff <code>mark</code> is true. i.e. sets the marked
     * property to the given value.
     * 
     * @param mark the marked property to set
     */
    public void setMarked(boolean mark) {
        this.setAnnotation(MARKED, mark);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    /**
     * Returns the annotation for the given key.
     * 
     * @param key key for the annotation
     * @return if there is no annotation for the given key, returns
     *         <code>null</code>, otherwise returns the annotation for the key
     */
    public Object getAnnotation(String key) {
        return annotations.get(key);
    }

    /**
     * Sets an annotation for a given key. If there is already an annotation for the
     * key, it is replaced.
     * 
     * @param key        the key for the annotation
     * @param annotation the annotation
     */
    public void setAnnotation(String key, Object annotation) {
        annotations.put(key, annotation);
    }

    /**
     * Removes the annotation for the given key.
     * 
     * @param key key for the annotation
     */
    public void removeAnnotation(String key) {
        annotations.remove(key);
    }

    /**
     * Returns <code>true</code> if there is an annotation for the given key.
     * Otherwise returns <code>false</code>.
     * 
     * @param key key for the annotation
     * @return <code>true</code> if there is an annotation for the given key,
     *         <code>false</code> otherwise
     */
    public boolean hasAnnotation(String key) {
        return annotations.containsKey(key);
    }

    /**
     * Gets the name of the state
     * 
     * @return a String countaining the name of the state
     */
    public String getName() {
        if (getAnnotation(NAME) == null) {
            setAnnotation(NAME, String.valueOf(getId()));
        }
        return (String) getAnnotation(NAME);
    }

    /**
     * Sets an annotation for the name of the state
     * 
     * @param name , the name for the String
     */
    public void setName(String name) {
        setAnnotation(NAME, name);
    }

    public String toString() {
        return getAnnotation(NAME) + "(" + id + ")";
    }

    public int getIncomingTransitionsCount() {
        return targetT.size();
    }

    public int getOutgoingTransitionsCount() {
        return sourceT.size();
    }
}
