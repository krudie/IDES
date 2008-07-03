package model.fsa;

import java.util.Set;

/**
 * Interface for managers responsible for synchronizing global and local event
 * set (or alphabets).
 * 
 * @author Helen Bretzke
 */
public interface FSAEventsModel
{

	/**
	 * Returns the global event set; all events in all models in the workspace.
	 * 
	 * @return set of events in the global alphabet
	 */
	public Set<FSAEvent> getGlobalEvents();

	/**
	 * Returns the set of events local to the given fsa.
	 * 
	 * @param fsa
	 *            the fsa for which to retrieve events
	 * @return set of events in the global alphabet
	 */
	public Set<FSAEvent> getLocalEvents(FSAModel fsa);

	/**
	 * Adds the given event to the global alphabet.
	 * 
	 * @param e
	 *            the event to be added
	 */
	public void addGlobalEvent(FSAEvent e);

	/**
	 * Adds all events local to the given fsa to the set of global events.
	 * 
	 * @param fsa
	 *            the model from which to add events
	 */
	public void addLocalEvents(FSAModel fsa);

	/**
	 * Removes any events from the global alphabet that do not appear in any of
	 * the local alphabets. WARNING: use with extreme caution; should have a
	 * flag set in any constructor to prevent frivolous use of this method.
	 */
	public void pruneGlobalEvents();

}
