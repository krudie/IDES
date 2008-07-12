package model;

import java.awt.Image;

import model.fsa.FSAModel;
import services.General;

/**
 * Descriptor for a DES model type. A plugin wishing to implement a new DES model
 * type should implement this interface and the register it with the
 * {@link ModelManager}.
 * 
 * @see ModelManager#registerModel(DESModelType)
 * @author Lenko Grigorov
 */
public interface DESModelType
{
	/**
	 * Returns all the DES model interfaces models of this type implement. For example,
	 * if the type implements an FSA, it should return {@link FSAModel}. Custom
	 * interfaces can be returned as well.
	 * 
	 * @return all DES model interfaces models of this type implement
	 */
	public Class<?>[] getModelInterfaces();

	/**
	 * Returns the "main" DES model interface to be used with models of this type. For
	 * example, if the model of an FSA provides an EventSet interface for access
	 * to its event set, this method should return {@link FSAModel} since this
	 * is the main purpose of the model.
	 * 
	 * @return the interface for the main purpose of models of this type
	 */
	public Class<?> getMainInterface();

	/**
	 * Returns a human-readable description of this type of model. For
	 * example, <code>"Petri net"</code>.
	 * 
	 * @return human readable description of the model type
	 */
	public String getDescription();

	/**
	 * Returns an small image (icon) which can be displayed to the user to
	 * identify the model type.
	 * 
	 * @return an icon to serve as identification of the model type in the UI
	 */
	public Image getIcon();

	/**
	 * Creates and returns a new model of this type.
	 * 
	 * @param name
	 *            the name which will be used to identify the model instance.
	 *            Preferably a unique string.
	 * @return the new model
	 */
	public DESModel createModel(String name);
}
