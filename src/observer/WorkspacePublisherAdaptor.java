/**
 * 
 */
package observer;

import java.util.ArrayList;

/**
 * A publisher of change notification events in a workspace containing multiple 
 * discrete event system models. Stores a collection of 
 * <code>WorkspaceSubscriber</code>s to which it sends notifications
 * of changes in the workspace. 
 * 
 * @author Helen Bretzke
 */
public class WorkspacePublisherAdaptor implements WorkspacePublisher {

	// List of subscribers to be notified of change events
	private ArrayList<WorkspaceSubscriber> subscribers;
	
	/**
	 * Creates a workspace publisher with an empty list of 
	 * subscribers.
	 */
	public WorkspacePublisherAdaptor() {
		subscribers = new ArrayList<WorkspaceSubscriber>();
	}
	
	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(WorkspaceSubscriber subscriber) {
		subscribers.add(subscriber);		
	}
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(WorkspaceSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	/**
	 * Triggers a notification to all subscribers that a repaint is required.
	 */
	public void fireRepaintRequired() {
		fireRepaintRequired(new WorkspaceMessage(WorkspaceMessage.DISPLAY,
												null,
												WorkspaceMessage.MODIFY,
												this));
	}
	
	/**
	 * Sends a notification to subscribers of changes to the display options such as 
	 * Zoom, or toggling show grid, LaTeX rendering, UniformNode size etc. Passes the
	 * given message to each subscriber.
	 * 
	 * @param message details of the change notification
	 */
	protected void fireRepaintRequired(WorkspaceMessage message) {
		for( WorkspaceSubscriber s : subscribers ) {
			s.repaintRequired(message);
		}
	}
	
	/**
	 * Sends notification to subscribers when a DES model is created or opened (added), 
	 * closed (removed) or renamed.
	 * 
	 * @param message
	 */
	protected void fireModelCollectionChanged(WorkspaceMessage message)	{
		for(WorkspaceSubscriber s : subscribers) {
			s.modelCollectionChanged(message);
		}
	}	
	
	/**
	 * Sends notification to subscribers of changes to the type of active model
	 * e.g. from FSM to petri net.
	 * Intended to facilitate changes to the current set of interface tools (a.k.a. work bench) 
	 * needed to view and manipulate different kinds of DES models.
	 * 
	 * @param message
	 */
	protected void fireModelSwitched(WorkspaceMessage message){
		for(WorkspaceSubscriber s : subscribers) {
			s.modelSwitched(message);
		}
	}
	
	/*protected void fireSaved() {
		for( WorkspaceSubscriber s : subscribers ) {
			s.workspaceSaved();
		}
	}*/
}
