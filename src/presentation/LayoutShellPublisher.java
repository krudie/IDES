package presentation;

import main.WorkspaceSubscriber;

public interface LayoutShellPublisher {
	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(LayoutShellSubscriber subscriber);
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(LayoutShellSubscriber subscriber);

	/**
	 * Returns all current subscribers to this publisher.
	 * @return all current subscribers to this publisher
	 */
	public LayoutShellSubscriber[] getLayoutShellSubscribers();

}
