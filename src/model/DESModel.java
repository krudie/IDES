/**
 * 
 */
package model;

import main.Annotable;

/**
 * The interface for all models which IDES will handle. 
 * @author Lenko Grigorov
 */
public interface DESModel extends Annotable {
	/**
	 * @return the name of the model
	 */
	public abstract String getName();
	
	/**
	 * Returns the model descriptor for the model.
	 * @return the model descriptor for the model
	 */
	public ModelDescriptor getModelDescriptor();
}
