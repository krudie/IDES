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

	public void fsaStructureChanged(FSAMessage message);
	public void fsaEventSetChanged(FSAMessage message);
	public void fsaSaved();
	
}
