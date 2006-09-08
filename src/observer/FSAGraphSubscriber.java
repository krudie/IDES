package observer;

/**
 * Implemented by Subscribers that wish to receive change notifications 
 * from FSMGraph(Publisher). 
 * 
 * @author helen bretzke
 */
public interface FSAGraphSubscriber {

	public void fsmGraphChanged(FSAGraphMessage message);	
	
	// NOTES thumbnails (filmstrip) don't need to respond to selection events
	public void fsmGraphSelectionChanged(FSAGraphMessage message);
	
}
