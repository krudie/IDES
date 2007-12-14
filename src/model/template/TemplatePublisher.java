package model.template;

/**
 * A publisher of Template Model messages.
 * 
 * @author Lenko Grigorov
 */
public interface TemplatePublisher
{
	/**
	 * Attaches the given subscriber to this publisher. The given subscriber
	 * will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(TemplateSubscriber subscriber);

	/**
	 * Removes the given subscriber to this publisher. The given subscriber will
	 * no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(TemplateSubscriber subscriber);

	/**
	 * Returns all current subscribers to this publisher.
	 * 
	 * @return all current subscribers to this publisher
	 */
	public TemplateSubscriber[] getTemplateSubscribers();

	/**
	 * Triggers a notification to all subscribers that the structure of the
	 * Template model has changed.
	 * 
	 * @param message
	 *            message with additional info about the change
	 * @see TemplateMessage
	 */
	public void fireTemplateStructureChanged(TemplateMessage message);

	/**
	 * Triggers a notification to all subscribers that the template model was
	 * saved to disk.
	 */
	public void fireTemplateSaved();

	/**
	 * Returns the model for which events are published.
	 * 
	 * @return the model for which events are published
	 */
	public TemplateModel getTemplateModel();
}
