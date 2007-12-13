/**
 * 
 */
package model.fsa;

/**
 * Implemented by Subscribers to change notifications from FSAPublishers.
 * 
 * @author helen bretzke
 *
 */
public interface FSASubscriber {
	public void fsaStructureChanged(FSAMessage message);
	public void fsaEventSetChanged(FSAMessage message);
}
