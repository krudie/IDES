package model.fsa;

public interface FSATransition {

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
	 * set the event this transiton fires uppon to e.
	 * 
	 * @param e the event this transition fires uppon.
	 */
	public abstract void setEvent(FSAEvent e);

	/**
	 * returns the event this transition fires uppon.
	 * 
	 * @return the event this transition fires uppon.
	 */
	public abstract FSAEvent getEvent();

	/**
	 * returns the id of this transition.
	 * 
	 * @return the id of this transition.
	 */
	public abstract int getId();

}