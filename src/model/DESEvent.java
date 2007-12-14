package model;

/**
 * Defines an abstract event in a discrete-event system.
 * 
 * @author Lenko Grigorov
 */
public interface DESEvent extends DESElement
{
	/**
	 * Returns the symbol that represents this event in the (local?) alphabet.
	 * 
	 * @return the symbol that represents this event
	 */
	public abstract String getSymbol();

	/**
	 * Sets the symbol for this event to <code>symbol</code>.
	 * 
	 * @param symbol
	 *            the symbol to set
	 */
	public abstract void setSymbol(String symbol);

	/**
	 * Returns true iff <code>o</code> is of type DESEvent and has the same
	 * properties as this DESEvent.
	 * 
	 * @param o
	 *            another object
	 * @return true iff <code>o</code> is of type DESEvent and has the same
	 *         properties as this DESEvent.
	 */
	public abstract boolean equals(Object o);
}
