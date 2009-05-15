package ides.api.model.fsa;

import ides.api.plugin.model.DESEvent;

/**
 * Defines an event in a finite state automaton.
 * 
 * @author Helen Bretzke
 */
public interface FSAEvent extends DESEvent, Comparable<FSAEvent>
{
	/**
	 * Returns true iff this event's controllable property is set to true.
	 * 
	 * @return true iff this event is controllable
	 */
	public abstract boolean isControllable();

	/**
	 * Sets this event's controllable property to <code>b</code>.
	 * 
	 * @param b
	 *            the value to set
	 */
	public abstract void setControllable(boolean b);

	/**
	 * Returns true iff this event's observable property is set to true.
	 * 
	 * @return true iff this event is observable
	 */
	public abstract boolean isObservable();

	/**
	 * Sets this event's observable property to <code>b</code>.
	 * 
	 * @param b
	 *            the value to set
	 */
	public abstract void setObservable(boolean b);

	/**
	 * Returns true iff <code>o</code> is of type FSAEvent and has the same name
	 * as this FSAEvent.
	 * 
	 * @param o
	 *            another object
	 * @return true iff <code>o</code> is of type FSAEvent and has the same name
	 *         as this FSAEvent.
	 */
	public abstract boolean equals(Object o);

}