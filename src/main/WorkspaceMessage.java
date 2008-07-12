/**
 * 
 */
package main;

/**
 * Message sent by {@link Workspace} to classes that implement 
 * {@link WorkspaceSubscriber} i.e. that subscribe to change notifications
 * from the workspace containing multiple discrete event system models.
 * 
 * @author Helen Bretzke
 */
public class WorkspaceMessage
{

	/**
	 * Possible event types
	 */
	public static final int ADD = 0;

	public static final int REMOVE = 1;

	public static final int MODIFY = 2;

	/** Name of the model changed, if a specific model */
	private String modelName;

	/** the type of workspace event that occurred */
	private int eventType;

	/** a description of the event fired */
	private String messageText;

	/**
	 * Creates a change notification message for the given model,
	 * event type and descriptive text.
	 * 
	 * @param name
	 *            the model's name
	 * @param eventType
	 *            ADD, REMOVE or MODIFY
	 * @param messageText
	 *            descriptive message as a string
	 */
	public WorkspaceMessage(String name, int eventType, String messageText)
	{
		super();
		this.modelName = name;
		this.eventType = eventType;
		this.messageText = messageText;
	}

	/**
	 * Creates a change notification message for the given model and
	 * event type. Descriptive text is blank.
	 * 
	 * @param name
	 *            the model's name
	 * @param eventType
	 *            ADD, REMOVE or MODIFY
	 */
	public WorkspaceMessage(String name, int eventType)
	{
		this(name, eventType, "");
	}

	/**
	 * Returns the type of event that occurred.
	 * 
	 * @return the type of event that occurred
	 */
	public int getEventType()
	{
		return eventType;
	}

	/**
	 * Returns the name of the model that was changed.
	 * 
	 * @return the name of the model that was changed
	 */
	public String getModelName()
	{
		return modelName;
	}

	/**
	 * Returns a string describing the message.
	 * 
	 * @return a description of the change event
	 */
	public String getMessageText()
	{
		return messageText;
	}
}
