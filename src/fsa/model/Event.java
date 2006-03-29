package fsa.model;

public interface Event {

	/**
	 * returns the id of the event.
	 * @return the id of the event.
	 */
	public abstract int getId();

	/**
	 * sets the id of the event.
	 * @param id the id to be set.
	 */
	public abstract void setId(int id);

}