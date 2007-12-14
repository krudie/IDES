package main;

/**
 * Publisher of Workspace messages.
 * 
 * @author Lenko Grigorov
 */
public interface WorkspacePublisher
{
	/**
	 * Attaches the given subscriber to this publisher. The given subscriber
	 * will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(WorkspaceSubscriber subscriber);

	/**
	 * Removes the given subscriber to this publisher. The given subscriber will
	 * no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(WorkspaceSubscriber subscriber);

	/**
	 * Triggers a notification to all subscribers that a repaint is required.
	 */
	public void fireRepaintRequired();
}
