package model.fsa;

/**
 * Manages a collection of FSAModels.
 * Requires that each FSA has a unique name.  
 * 
 * @author helen bretzke
 *
 */
public interface FSAWorkspace {

	public FSAModel getFSAModel(String name);
	public void addFSAModel(FSAModel fsa);
	public FSAEventsModel getEventsModel();
		
}
