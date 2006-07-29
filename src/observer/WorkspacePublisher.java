/**
 * 
 */
package observer;

import java.util.ArrayList;

/**
 * @author helen
 *
 */
public class WorkspacePublisher {

	private ArrayList<WorkspaceSubscriber> subscribers;
	
	public WorkspacePublisher()
	{
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
	 * Sends notification to subscribers when a DES model is created or opened (added), 
	 * closed (removed) or renamed.
	 * 
	 * @param message
	 */
	public void fireModelCollectionChanged(WorkspaceMessage message)
	{
		for(WorkspaceSubscriber s : subscribers)
		{
			s.modelCollectionChanged(message);
		}
	}
	
	/**
	 * Sends notification to subscribers of changes to the display options such as 
	 * Zoom, or toggling show grid, LaTeX rendering, UniformNode size etc.
	 * 
	 * @param message
	 */
	public void fireRepaintRequired(WorkspaceMessage message)
	{
		for(WorkspaceSubscriber s : subscribers)
		{
			s.repaintRequired(message);
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
	public void fireModelSwitched(WorkspaceMessage message){
		for(WorkspaceSubscriber s : subscribers)
		{
			s.modelSwitched(message);
		}
	}
	
	
}
