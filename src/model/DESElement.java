package model;

/**
 * Interface defining the required operations for an element 
 * in a discrete event system. 
 * 
 * @author Helen Bretzke
 */
public interface DESElement {

	/**
	 * Returns the id of this element. 
	 * 
	 * @return the id of this element.
	 */
	public abstract long getId();

	/**
	 * Sets the id of this element to the given id. 
	 * 
	 * @param id the id to be set.
	 */
	public abstract void setId(long id);
		
}
