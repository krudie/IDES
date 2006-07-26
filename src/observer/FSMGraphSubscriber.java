/**
 * 
 */
package observer;

/**
 * Implemented by Subscribers to change notifications from FSMGraphPublishers. 
 * 
 * @author helen bretzke
 */
public interface FSMGraphSubscriber {

	public void fsmGraphChanged(FSMGraphMessage message);	
	
	// NOTES thumbnails (filmstrip) don't need to respond to selection events
	public void fsmGraphSelectionChanged(FSMGraphMessage message);
	
}
