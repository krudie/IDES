/**
 * 
 */
package observer;

/**
 * Implemented by Subscribers to change notifications from FSMPublishers.
 * 
 * @author helen bretzke
 *
 */
public interface FSASubscriber {

	public void fsmStructureChanged(FSAMessage message);
	public void fsmEventSetChanged(FSAMessage message);
	
}
