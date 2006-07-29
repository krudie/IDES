/**
 * 
 */
package observer;

import java.awt.Rectangle;

import presentation.fsa.FSMGraph;

/**
 * Message sent to subscribers to event notifications from 
 * FSMGraphPublishers.
 * 
 * @author helen bretzke
 */
public class FSMGraphMessage {
	/** 
	 * the possible graph elements that were changed by the event 
	 * */
	public static final int NODE = 0;
	public static final int EDGE = 1;
	public static final int LABEL = 2;
	public static final int SELECTION = 3;
	
	/** 
	 * the possible events that fired this message 
	 * */
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int MODIFY = 2;
	
	/**
	 * the publisher that sent this message
	 */
	private FSMGraph source;
	
	// type of graph element that was changed by the event
	private int elementType;	
	// the id of the element changed
	private long elementId;

	// the type of event that occurred
	private int eventType;
	
	// the location on the source canvas where the event occurred
	private Rectangle location;
	
	/**
	 * 
	 * @param source message sender
	 * @param eventType ADD, REMOVE, or MODIFY
	 * @param elementType NODE, EDGE, LABEL or SELECTION
	 * @param elementId the unique id (by type) of the element
	 * @param location area in the display where the event occurred
	 */
	public FSMGraphMessage(FSMGraph source, int eventType, int elementType, long elementId, Rectangle location) {
		super();		
		this.source = source;
		this.elementType = elementType;
		this.elementId = elementId;
		this.eventType = eventType;
		this.location = location;
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

	public Rectangle getLocation() {
		return location;
	}	

}
