package main;

import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;

/**
 * Manages a collection of FSAModels.
 * Requires that each FSA has a unique name.  
 * 
 * @author helen bretzke
 *
 */
public interface Workspace {
	
	public FSAModel getFSAModel(String name);
	public void addFSAModel(FSAModel fsa);
	public void removeFSAModel(String name);
	public FSAEventsModel getEventsModel();
	public FSAModel getActiveModel();	
	
}
