package model;

import java.awt.Image;

import services.General;

import model.fsa.FSAModel;

/**
 * A descriptor for a DES model.
 * A plugin wishing to implement a new DES model should
 * implment this interface and the register it with the
 * {@link ModelManager}.
 * @see ModelManager#registerModel(ModelDescriptor)
 * @author Lenko Grigorov
 */
public interface ModelDescriptor {
	/**
	 * Returns all the DES model interfaces this model
	 * implements. For example, if it implements an FSA,
	 * it should return {@link FSAModel}. Custom interfaces
	 * can be returned as well.
	 * @return all DES model interfaces this model implements
	 */
	public Class[] getModelInterfaces();
	
	/**
	 * Returns the "main" DES model interface to be used with
	 * the model. For example, if the model of an FSA provides
	 * an EventSet interface for access to its event set,
	 * this method should return {@link FSAModel} since this
	 * is the main purpose of the model.
	 * @return the interface for the main purpose of the model
	 */
	public Class getPreferredModelInterface();
	
	/**
	 * Returns a huma-readable description of the type of the model.
	 * For example, <code>"Petri net"</code>.
	 * @return human readable description of the model type
	 */
	public String getTypeDescription();
	
	/**
	 * Returns an small image (icon) which can be displayed
	 * to the user to identify the model type.
	 * @return an icon to serve as identification of the model type in the UI 
	 */
	public Image getIcon();
	
	/**
	 * Creates and returns a new model of this type.
	 * @param id the ID which will be used to identify the model instance.
	 * Preferrably a unique string ({@link General#getRandomId()} can be used).
	 * @return the new model
	 * @see General#getRandomId()
	 */
	public DESModel createModel(String id);

//	/**
//	 * Creates and returns a new model of this type.
//	 * @param id the ID which will be used to identify the model instance.
//	 * Preferrably a unique string ({@link General#getRandomId()} can be used).
//	 * @param name the display name to be assigned to the new model
//	 * @return the new model
//	 * @see General#getRandomId()
//	 */
//	public DESModel createModel(String id, String name);
}
