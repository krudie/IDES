package model;

/**
 * An obsolete concept intended to retrieve and store metadata
 * pertinent to a discrete event system model. 
 * 
 * @author Helen Bretzke
 */
public interface DESMetaData {

	/**
	 * Returns an object of the given class name instantiated with data 
	 * extracted from this metadata.
	 * 
	 * @param moduleID the class name of the object to be returned
	 * @param element TODO
	 * @return an object of the given class instantiated with data extracted
	 * from this metadata.
	 * @throws ClassNotFoundException if moduleID is not valid
	 */
	public abstract Object getData(String moduleID, DESElement element);
	
	/**
	 * Stores the metadata contained in the given object.
	 * 
	 * @param data the data to be stored
	 */
	public abstract void setData(Object data);	
}
