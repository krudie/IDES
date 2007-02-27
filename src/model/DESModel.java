/**
 * 
 */
package model;

import services.General;
import main.Annotable;

/**
 * The interface for all models which IDES will handle. 
 * @author Lenko Grigorov
 */
public interface DESModel extends Annotable {

	/**
	 * Sets the ID of the model. It is highly desirable that
	 * this ID is a unique string. Use {@link General#getRandomId()}
	 * to get a unique ID.
	 * @param id the new ID for the module
	 * @see General#getRandomId()
	 */
	public void setId(String id);
	
	/**
	 * Returns the ID of the model.
	 * @return the ID of the model
	 * @see General#getRandomId()
	 */
	public String getId();
	
	/**
	 * Sets the display name of the model.
	 * @param name new display name for the model
	 */
	public void setName(String name);
	
	/**
	 * Returns the display name of the model. 
	 * @return the display name of the model
	 */
	public abstract String getName();
	
	/**
	 * Returns the model descriptor for the model.
	 * @return the model descriptor for the model
	 */
	public ModelDescriptor getModelDescriptor();
}
