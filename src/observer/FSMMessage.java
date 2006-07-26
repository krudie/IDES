/**
 * 
 */
package observer;

/**
 * @author helen
 *
 */
public class FSMMessage {

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
	private FSMPublisher source; // the publisher that sent this message
	private String messageText;
	
	public FSMMessage(int elementType, long elementId, int eventType, FSMPublisher source) {
		this(elementType, elementId, eventType, source, "");
	}	

	public FSMMessage(int elementType, long elementId, int eventType, FSMPublisher source, String messageText) {
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

	public FSMPublisher getSource() {
		return source;
	}



	public String getMessageText() {
		return messageText;
	}
	
	
}
