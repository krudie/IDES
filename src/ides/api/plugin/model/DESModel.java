/**
 * 
 */
package ides.api.plugin.model;

import ides.api.core.Annotable;
import presentation.fsa.FSAGraph;

/**
 * The interface for all models which IDES will handle.
 * 
 * @author Lenko Grigorov
 */
public interface DESModel extends Annotable, DESModelPublisher
{
	/**
	 * Sets the display name of the model.
	 * 
	 * @param name
	 *            new display name for the model
	 */
	public void setName(String name);

	/**
	 * Returns the display name of the model.
	 * 
	 * @return the display name of the model
	 */
	public abstract String getName();

	/**
	 * Returns the model type.
	 * 
	 * @return the model type
	 */
	public DESModelType getModelType();

	/**
	 * Notifies the model that some associated metadata has been changed.
	 */
	public void metadataChanged();

	/**
	 * Notifies the model that it has been saved.
	 */
	public void modelSaved();

	/**
	 * Returns <code>true</code> if the information about the model needs to be
	 * saved; <code>false</code> otherwise. This method should also consider
	 * changes to the associated metadata (see {@link #metadataChanged()}).
	 * E.g., if the user moves a node of a {@link FSAGraph}, this method will
	 * return <code>true</code> since the updated position of the node has to be
	 * saved.
	 * 
	 * @return <code>true</code> if the information maintained by the model
	 *         needs to be saved; <code>false</code> otherwise
	 */
	public boolean needsSave();
}
