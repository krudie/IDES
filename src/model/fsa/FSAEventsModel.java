package model.fsa;

import java.util.List;

/**
 * Interface for managers responsible for synchronizing global and local event
 * set (or alphabets).
 * 
 * @author helen bretzke
 *
 */
public interface FSAEventsModel {

	public List getGlobalEvents();
	
//	 not really necessary since FSAModel returns an iterator of events
	public List getLocalEvents(FSAModel fsa);
	
	public void addGlobalEvent(FSAEvent e);
	public void addLocalEvents(FSAModel fsa);
	
	/**
	 * Removes any events from the global alphabet that do not appear 
	 * in any of the local alphabets.
	 * 
	 * WARNING: use with extreme caution; should have a flag set in any constructor
	 * to prevent frivolous use of this method.	 
	 */
	public void pruneGlobalEvents();  
	
}
