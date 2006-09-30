package main;

import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;

/**
 * Defines operations required by a class that manages a collection 
 * of FSAModels. Requires that each FSA has a unique name.  
 * 
 * @author Helen Bretzke
 */
public interface Workspace {
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public FSAModel getFSAModel(String name);
	
	/**
	 * Adds the given FSAModel to the set of models in this workspace.
	 * If <code>hasFSAMode(fsa.getName())</code>, overwrites the existing
	 * model of the same name.
	 * 
	 *  @param fsa the model to be added
	 */
	public void addFSAModel(FSAModel fsa);
	
	/**
	 * 
	 * @param name
	 */
	public void removeFSAModel(String name);
	
	/**
	 * 
	 * @return
	 */
	public FSAEventsModel getEventsModel();
	
	/**
	 * 
	 * @return
	 */
	public FSAModel getActiveModel();	
	
}
