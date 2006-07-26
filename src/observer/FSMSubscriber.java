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
public interface FSMSubscriber {

	public void fsmStructureChanged(FSMMessage message);
	public void fsmEventSetChanged(FSMMessage message);
	
}
