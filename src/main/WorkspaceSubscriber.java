/**
 * 
 */
package main;

/**
 * Implemented by classes that wish to receive change notifications from 
 * <code>WorkspacePublisher</code>s.
 * 
 * @author Helen Bretzke
 */
public interface WorkspaceSubscriber {

	/**
	 * Notifies this subscriber that a model collection change 
	 * (a DES model is created or opened (added), closed (removed) 
	 * or renamed) has occurred in a <code>WorkspacePublisher</code> 
	 * to which I have subscribed.
	 *  
	 * @param message details of the change notification
	 */
	public void modelCollectionChanged(WorkspaceMessage message);
	

	/**
	 * Notifies this subscriber that a change requiring a repaint has
	 * occurred in a <code>WorkspacePublisher</code> to which I have
	 * subscribed.
	 *  
	 * @param message details of the change notification
	 */
	/* NOTE ignore param except possibly for the source field */
	public void repaintRequired(WorkspaceMessage message); 
	

	/**
	 * Notifies this subscriber that a the model type has been switched 
	 * (the type of active model has changed e.g. from FSA to petri net) 
	 * in a <code>WorkspacePublisher</code> to which I have subscribed. 
	 *  
	 * @param message details of the change notification
	 */
	public void modelSwitched(WorkspaceMessage message);
	
}
