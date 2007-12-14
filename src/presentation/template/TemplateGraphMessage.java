package presentation.template;

import java.awt.geom.Rectangle2D;

public class TemplateGraphMessage
{

	/* the possible graph elements that were changed by the event */
	/** Indicates element of type Module was affected by event */
	public static final int MODULE = 0;

	/** Indicates element of type Channel was affected by event */
	public static final int CHANNEL = 1;

	/** Indicates element of type Link was affected by event */
	public static final int LINK = 2;

	/** Indicates element of type SelectionGroup was affected by event */
	public static final int SELECTION = 3;

	/** Indicates that the whole graph was affected by the event */
	public static final int GRAPH = 4;

	/** Default for when type of element is unknown. */
	public static final int UNKNOWN_TYPE = -1;

	/* the possible events that caused this message to be sent */
	/** An addition occurred */
	public static final int ADD = 0;

	/** A removal (deletion) occurred */
	public static final int REMOVE = 1;

	/** A modification occurred */
	public static final int MODIFY = 2;

	/** Default id for a group of elements or when id is unknown. */
	public static final long UNKNOWN_ID = -1;

	/** the publisher that sent this message */
	private TemplateGraph source;

	/** type of graph element that was changed by the event */
	private int elementType;

	/** the id of the element changed */
	private long elementId;

	/** the type of event that occurred */
	private int eventType;

	/** the location on the source canvas where the event occurred */
	private Rectangle2D location;

	/** a description of the event fired */
	private String messageText;

	/**
	 * Creates a change notification message for TemplateGraph to pass to
	 * TemplateGraphSubscribers.
	 * 
	 * @param eventType
	 *            ADD, REMOVE, or MODIFY
	 * @param elementType
	 *            MODULE, CHANNEL, LINK or SELECTION
	 * @param elementId
	 *            the unique id (by type) of the element
	 * @param location
	 *            area in the display where the event occurred
	 * @param source
	 *            message sender
	 */
	public TemplateGraphMessage(int eventType, int elementType, long elementId,
			Rectangle2D location, TemplateGraph source)
	{
		this(eventType, elementType, elementId, location, source, "");
	}

	/**
	 * Creates a change notification message for TemplateGraph to pass to
	 * TemplateGraphSubscribers.
	 * 
	 * @param eventType
	 *            ADD, REMOVE, or MODIFY
	 * @param elementType
	 *            MODULE, CHANNEL, LINK or SELECTION
	 * @param elementId
	 *            the unique id (by type) of the element
	 * @param location
	 *            area in the display where the event occurred
	 * @param source
	 *            message sender
	 * @param message
	 *            a description of the event fired
	 */
	public TemplateGraphMessage(int eventType, int elementType, long elementId,
			Rectangle2D location, TemplateGraph source, String message)
	{
		super();
		this.source = source;
		this.elementType = elementType;
		this.elementId = elementId;
		this.eventType = eventType;
		this.location = location;
		this.messageText = message;
	}

	/**
	 * Returns the id of the element changed.
	 * 
	 * @return the id of the element changed
	 */
	public long getElementId()
	{
		return elementId;
	}

	/**
	 * Returns the type of the element changed.
	 * 
	 * @return the type of the element changed
	 */
	public int getElementType()
	{
		return elementType;
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
	 * Returns the graph that sent this message.
	 * 
	 * @return the graph that sent this message
	 */
	public TemplateGraph getSource()
	{
		return source;
	}

	/**
	 * Returns a rectangle the location on the source canvas where the event
	 * occurred.
	 * 
	 * @return a rectangle representing the region affected by the change
	 */
	public Rectangle2D getLocation()
	{
		return location;
	}

	/**
	 * Returns a string describing the message.
	 * 
	 * @return a description of the change event
	 */
	public String getMessage()
	{
		return messageText;
	}

}
