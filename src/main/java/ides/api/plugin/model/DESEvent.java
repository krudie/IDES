package ides.api.plugin.model;

/**
 * Defines an abstract event in a discrete-event system.
 * 
 * @author Lenko Grigorov
 */
public interface DESEvent extends DESElement {
    /**
     * Returns the symbol that represents this event in the (local?) alphabet.
     * 
     * @return the symbol that represents this event
     */
    public abstract String getSymbol();

    /**
     * Sets the symbol for this event to <code>symbol</code>.
     * 
     * @param symbol the symbol to set
     */
    public abstract void setSymbol(String symbol);

    /**
     * Returns true iff <code>o</code> is of type DESEvent and has the same name as
     * this DESEvent.
     * 
     * @param o another object
     * @return true iff <code>o</code> is of type DESEvent and has the same name as
     *         this DESEvent.
     */
    public abstract boolean equals(Object o);

    /**
     * Returns an integer for the event which can be used for hashing. If
     * {@link #equals(Object)} returns <code>true</code> for another DESEvent, the
     * {@link #hashCode()} methods for the two events have to return the same
     * integer.
     * 
     * @return an integer for the event which can be used for hashing
     */
    public abstract int hashCode();
}
