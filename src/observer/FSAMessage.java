/**
 * 
 */
package observer;

/**
 * @author helen
 *
 */
public class FSAMessage {

	/**
	 * possible element types
	 */
	public static final int STATE = 0;
	public static final int TRANSITION = 1;
	public static final int EVENT = 2;
	
	/**
	 * possible event types
	 */
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int MODIFY = 2;
	
	private int elementType; // the type of element that was affected by the event	
	private long elementId; // unique id of the element
	private int eventType; // the type of event that occurred	
	private FSAPublisher source; // the publisher that sent this message
	private String messageText;
	
	public FSAMessage(int eventType, int elementType, long elementId, FSAPublisher source) {
		this(eventType, elementType, elementId, source, "");
	}	

	public FSAMessage(int eventType, int elementType, long elementId, FSAPublisher source, String messageText) {
		super();		
		this.elementType = elementType;
		this.elementId = elementId;
		this.eventType = eventType;
		this.source = source;
		this.messageText = messageText;
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

	public FSAPublisher getSource() {
		return source;
	}



	public String getMessageText() {
		return messageText;
	}
	
	
}