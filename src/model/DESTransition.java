package model;

public interface DESTransition {

	/**
	 * Sets a new source, i.e., state from which this transition originates, for
	 * this transition.
	 * 
	 * @param s the new source.
	 */
	public abstract void setSource(DESState s);

	/**
	 * Sets a new source, i.e., state from which this transition originates, for
	 * this transition.
	 */
	public abstract DESState getSource();

	/**
	 * Sets a new target, i.e., state from which this transition originates, for
	 * this transition.
	 * 
	 * @param s the new source.
	 */
	public abstract void setTarget(DESState s);

	/**
	 * returns the state this transition ends in.
	 * 
	 * @return the target state.
	 */
	public abstract DESState getTarget();

	/**
	 * set the event this transiton fires uppon to e.
	 * 
	 * @param e the event this transition fires uppon.
	 */
	public abstract void setEvent(DESEvent e);

	/**
	 * returns the event this transition fires uppon.
	 * 
	 * @return the event this transition fires uppon.
	 */
	public abstract DESEvent getEvent();

	/**
	 * returns the id of this transition.
	 * 
	 * @return the id of this transition.
	 */
	public abstract int getId();

}