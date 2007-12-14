package model.template;

/**
 * Message sent to subscribers listening for changes to a Template model.
 * 
 * @author Lenko Grigorov
 */
public class TemplateMessage
{
	/**
	 * possible element types
	 */
	public static final int MODULE = 0;

	public static final int CHANNEL = 1;

	public static final int LINK = 2;

	/**
	 * possible event types
	 */
	public static final int ADD = 0;

	public static final int REMOVE = 1;

	public static final int MODIFY = 2;

	private int elementType; // the type of element that was affected by the

	// event

	private long elementId; // unique id of the element

	private int eventType; // the type of event that occurred

	private TemplatePublisher source; // the publisher that sent this message

	private String messageText;

	public TemplateMessage(int eventType, int elementType, long elementId,
			TemplatePublisher source)
	{
		this(eventType, elementType, elementId, source, "");
	}

	public TemplateMessage(int eventType, int elementType, long elementId,
			TemplatePublisher source, String messageText)
	{
		super();
		this.elementType = elementType;
		this.elementId = elementId;
		this.eventType = eventType;
		this.source = source;
		this.messageText = messageText;
	}

	public long getElementId()
	{
		return elementId;
	}

	public int getElementType()
	{
		return elementType;
	}

	public int getEventType()
	{
		return eventType;
	}

	public TemplatePublisher getSource()
	{
		return source;
	}

	public String getMessageText()
	{
		return messageText;
	}
}
