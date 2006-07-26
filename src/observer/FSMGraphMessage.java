/**
 * 
 */
package observer;

import java.awt.Rectangle;

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
	private FSMGraphPublisher source;
	
	// type of graph element that was changed by the event
	private int elementType;	
	// the id of the element changed
	private long elementId;

	// the type of event that occurred
	private int eventType;
	
	// the location on the source canvas where the event occurred
	private Rectangle location;
	
	public FSMGraphMessage(FSMGraphPublisher source, int elementType, long elementId, int eventType, Rectangle location) {
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

	public FSMGraphPublisher getSource() {
		return source;
	}	

}
