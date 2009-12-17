package ides.api.model.fsa;

import ides.api.plugin.model.DESElement;

/**
 * Transition of an {@link FSAModel}.
 */
public interface FSATransition extends DESElement
{
	/**
	 * Sets a new source, i.e., state from which this transition originates, for
	 * this transition.
	 * 
	 * @param s
	 *            the new source.
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
	 * @param s
	 *            the new source.
	 */
	public abstract void setTarget(FSAState s);

	/**
	 * returns the state this transition ends in.
	 * 
	 * @return the target state.
	 */
	public abstract FSAState getTarget();

	/**
	 * set the event upon which this transition fires.
	 * 
	 * @param e
	 *            the event this transition fires upon.
	 */
	public abstract void setEvent(FSAEvent e);

	/**
	 * returns the event upon which this transition fires.
	 * 
	 * @return the event this transition fires upon.
	 */
	public abstract FSAEvent getEvent();

}