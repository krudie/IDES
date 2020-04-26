package ides.api.model.fsa;

import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESElement;

/**
 * Transition of an {@link FSAModel}.
 */
public interface FSATransition extends DESElement {
    /**
     * Sets a new source, i.e., state from which this transition originates, for
     * this transition.
     * 
     * @param s the new source.
     */
    public abstract void setSource(FSAState s);

    /**
     * Sets a new source, i.e., state from which this transition originates, for
     * this transition.
     */
    public abstract FSAState getSource();

    /**
     * Sets a new target, i.e., state from which this transition originates, for
     * this transition.
     * 
     * @param s the new source.
     */
    public abstract void setTarget(FSAState s);

    /**
     * returns the state this transition ends in.
     * 
     * @return the target state.
     */
    public abstract FSAState getTarget();

    /**
     * set the event upon which this transition fires. If the parameter is
     * <code>null</code>, then this will be an epsilon transition.
     * 
     * @param e the event this transition fires upon; set to <code>null</code> for
     *          an epsilon transition.
     */
    public abstract void setEvent(SupervisoryEvent e);

    /**
     * returns the event upon which this transition fires.
     * 
     * @return the event this transition fires upon; or <code>null</code> if this is
     *         an epsilon transition.
     */
    public abstract SupervisoryEvent getEvent();

    /**
     * Check if this is an epsilon transition (with <code>null</code> event) or not.
     * 
     * @return returns <code>true</code> if this transition is an epsilon
     *         transition, <code>false</code> otherwise.
     */
    public abstract boolean isEpsilonTransition();

}