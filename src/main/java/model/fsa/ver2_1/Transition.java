package model.fsa.ver2_1;

import java.util.Hashtable;

import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;

// import io.fsa.ver2_1.SubElementContainer;

/**
 * Represents a transition in an automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Transition implements ides.api.model.fsa.FSATransition {

    private FSAState sourceS, targetS;

    private SupervisoryEvent e = null;

    private long id;

    // TODO make the state use a common annotation repository
    protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

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
     * Constructs a new transition originating in state source and ending in state
     * target. This transition is fired by the null event (epsilon).
     * 
     * @param id     the id of this transition.
     * @param source the source state.
     * @param target the target state.
     */
    public Transition(long id, FSAState source, FSAState target) {
        this.id = id;
        this.sourceS = source;
        this.targetS = target;
    }

    /**
     * Constructs a new transition with the given id, originating in state source
     * and ending in state target and firing upon receival of event e.
     * 
     * @param id     the id of the state.
     * @param source the source state.
     * @param target the target state.
     * @param e      the event this transition fires uppon receival of.
     */
    public Transition(long id, FSAState source, FSAState target, SupervisoryEvent e) {
        this(id, source, target);
        this.e = e;
    }

    /**
     * Constructs a new transition where the internal variables of the new
     * transition are the same as t. The transition originates in state source and
     * ends in state target and fires uppon receival of event e.
     * 
     * @param t      the transiton the new transition is to resemble.
     * @param source the source state.
     * @param target the target state.
     * @param e      the event this transition fires uppon receival of.
     */
    public Transition(FSATransition t, FSAState source, FSAState target, SupervisoryEvent e) {
        this.id = t.getId();
        this.sourceS = source;
        this.targetS = target;
        this.e = e;
    }

    /**
     * Constructs a new transition where the internal variables of the new
     * transition are the same as t. The transition originates in state source and
     * ends in state target and fires uppon receival of event null (epsillon).
     * 
     * @param t       the transiton the new transition is to resemble.
     * @param sourceS the source state.
     * @param targetS the target state.
     */
    public Transition(FSATransition t, FSAState sourceS, FSAState targetS) {
        this.id = t.getId();
        this.sourceS = sourceS;
        this.targetS = targetS;
    }

    /**
     * Sets a new source, i.e., state from which this transition originates, for
     * this transition.
     * 
     * @param s the new source.
     */
    public void setSource(FSAState s) {
        this.sourceS = s;
    }

    /**
     * Sets a new source, i.e., state from which this transition originates, for
     * this transition.
     */
    public FSAState getSource() {
        return this.sourceS;
    }

    /**
     * Sets a new target, i.e., state from which this transition originates, for
     * this transition.
     * 
     * @param s the new source.
     */
    public void setTarget(FSAState s) {
        this.targetS = s;
    }

    /**
     * returns the state this transition ends in.
     * 
     * @return the target state.
     */
    public FSAState getTarget() {
        return this.targetS;
    }

    /**
     * set the event this transiton fires uppon to e.
     * 
     * @param e the event this transition fires uppon.
     */
    public void setEvent(SupervisoryEvent e) {
        this.e = e;
    }

    /**
     * returns the event this transition fires uppon.
     * 
     * @return the event this transition fires uppon.
     */
    public SupervisoryEvent getEvent() {
        return e;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isEpsilonTransition() {

        return e == null;
    }
}
