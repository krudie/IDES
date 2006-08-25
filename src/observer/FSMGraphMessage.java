/**
 * 
 */
package observer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import presentation.fsa.FSMGraph;

/**
 * Message sent to subscribers to event notifications from 
 * FSMGraphPublishers.
 * 
 * @author helen bretzke
 */
public class FSMGraphMessage {
	
	/* the possible graph elements that were changed by the event */
	/** Indicates element of type Node was affected by event */	
	public static final int NODE = 0;
	/** Indicates element of type Edge was affected by event */	
	public static final int EDGE = 1;
	/** Indicates element of type Label was affected by event */
	public static final int LABEL = 2;	
	/** Indicates element of type SelectionGroup was affected by event */
	public static final int SELECTION = 3;	
	/** Default for when type of element is unknown. */
	public static final int UNKNOWN_TYPE = -1;
	
	/* the possible events that fired this message */
	/** An addition occurred */
	public static final int ADD = 0;
	/** A removal (deletion) occurred */
	public static final int REMOVE = 1;
	/** A modification occurred */
	public static final int MODIFY = 2;	
	/** Default id for a group of elements or when id is unknown. */
	public static final long UNKNOWN_ID = -1;	
	
	
	/** the publisher that sent this message */
	private FSMGraph source;
	
	/** type of graph element that was changed by the event */
	private int elementType;	
	
	/** the id of the element changed */
	private long elementId;

	/** the type of event that occurred */
	private int eventType;
	
	/** the location on the source canvas where the event occurred */
	private Rectangle2D location;
	
	/** a description of the event fired */
	private String message;
	
	/**
	 * Creates a change notification message for FSMGraph to pass
	 * to FSMGraphSubscribers. 
	 * 
	 * @param eventType ADD, REMOVE, or MODIFY
	 * @param elementType NODE, EDGE, LABEL or SELECTION
	 * @param elementId the unique id (by type) of the element
	 * @param location area in the display where the event occurred
	 * @param source message sender
	 */
	public FSMGraphMessage(int eventType, int elementType, long elementId, Rectangle2D location, FSMGraph source) {
		this(eventType, elementType, elementId, location, source, "");		
	}

	/**
	 * Creates a change notification message for FSMGraph to pass
	 * to FSMGraphSubscribers. 
	 * 
	 * @param eventType ADD, REMOVE, or MODIFY
	 * @param elementType NODE, EDGE, LABEL or SELECTION
	 * @param elementId the unique id (by type) of the element
	 * @param location area in the display where the event occurred
	 * @param source message sender
	 * @param message a description of the event fired
	 */
	public FSMGraphMessage(int eventType, int elementType, long elementId, Rectangle2D location, FSMGraph source, String message) {
		super();		
		this.source = source;
		this.elementType = elementType;
		this.elementId = elementId;
		this.eventType = eventType;
		this.location = location;
		this.message = message;
	}
	
	
	public long getElementId() {
		return elementId;
	}

	public int getElementType() {
		return elementType;
	}

	public int getEventType() {
		return eventType;
	}

	public FSMGraph getSource() {
		return source;
	}

	public Rectangle2D getLocation() {
		return location;
	}	

}
