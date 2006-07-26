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
	
	public void fireModelCollectionChanged(WorkspaceMessage message)
	{
		for(WorkspaceSubscriber s : subscribers)
		{
			s.modelCollectionChanged(message);
		}
	}
	
	/**
	 * Fired on changes to display such as Zoom, or
	 * toggling show grid, LaTeX rendering, UniformNode size etc.
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
	
	public void fireModelSwitched(WorkspaceMessage message){
		for(WorkspaceSubscriber s : subscribers)
		{
			s.modelSwitched(message);
		}
	}
	
	
}
