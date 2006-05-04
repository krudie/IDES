package model.fsa;

public interface FSAMetaData {

	/**
	 * Returns an object of the given class name instantiated with data 
	 * extracted from this metadata.
	 * 
	 * @param moduleID the class name of the object to be returned
	 * @return an object of the given class instantiated with data extracted
	 * from this metadata.
	 * @throws ClassNotFoundException if moduleID is not valid
	 */
	public abstract Object getData(String moduleID) throws ClassNotFoundException;
	
	/**
	 * Stores the metadata contained in the given object.
	 * 
	 * @param data the data to be stored
	 */
	public abstract void setData(Object data);	
}
