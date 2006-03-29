package fsa.model;

public interface Transition {

	/**
	 * Sets a new source, i.e., state from which this transition originates, for
	 * this transition.
	 * 
	 * @param s the new source.
	 */
	public abstract void setSource(State s);

	/**
	 * Sets a new source, i.e., state from which this transition originates, for
	 * this transition.
	 */
	public abstract State getSource();

	/**
	 * Sets a new target, i.e., state from which this transition originates, for
	 * this transition.
	 * 
	 * @param s the new source.
	 */
	public abstract void setTarget(State s);

	/**
	 * returns the state this transition ends in.
	 * 
	 * @return the target state.
	 */
	public abstract State getTarget();

	/**
	 * set the event this transiton fires uppon to e.
	 * 
	 * @param e the event this transition fires uppon.
	 */
	public abstract void setEvent(Event e);

	/**
	 * returns the event this transition fires uppon.
	 * 
	 * @return the event this transition fires uppon.
	 */
	public abstract Event getEvent();

	/**
	 * returns the id of this transition.
	 * 
	 * @return the id of this transition.
	 */
	public abstract int getId();

}