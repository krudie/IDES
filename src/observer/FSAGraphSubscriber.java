package observer;

/**
 * Implemented by Subscribers that wish to receive change notifications 
 * from FSMGraph(Publisher). 
 * 
 * @author Helen Bretzke
 */
public interface FSAGraphSubscriber {

	public void fsmGraphChanged(FSAGraphMessage message);	

	public void fsmGraphSelectionChanged(FSAGraphMessage message);
	
}
