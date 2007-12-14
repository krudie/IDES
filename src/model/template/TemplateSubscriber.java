package model.template;

/**
 * Implemented by Subscribers to change notifications from TemplatePublishers.
 * 
 * @author Lenko Grigorov
 */
public interface TemplateSubscriber
{
	public void templateStructureChanged(TemplateMessage message);

	public void modelSaved();
}
