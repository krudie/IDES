/**
 * 
 */
package main;

/**
 * Message sent by <code>WorkspacePublisher<code>s to classes that implement 
 * WorkspaceSubscriber i.e. that subscribe to change notifications
 * from a workspace containing multiple discrete event system models.
 * 
 * @author Helen Bretzke
 */
public class WorkspaceMessage {

	/**
	 * Possible event types
	 */
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int MODIFY = 2;
	
	/**
	 * If changes occurred in the display or an opened model.
	 */
	public static final int DISPLAY = 0;
	public static final int MODEL = 1;
	 
	
	/**
	 * Indicates the model type or general DISPLAY has been changed.
	 * TODO need a better name for this field 
	 */
	private int type;
	
	/** ID of the model changed; does not apply to display changes */ 
	private String idString; 
	
	/** the type of workspace event that occurred */
	private int eventType;
	
	/** the publisher that sent this message */
	private WorkspacePublisher source;
	
	/** a description of the event fired */
	private String messageText;
	
	/**
	 * Creates a change notification message for the given model type, model id, event type, 
	 * source and descriptive text. 
	 * 
	 * @param type MODEL or DISPLAY
	 * @param id the model's id, ignored if display changed
	 * @param eventType ADD, REMOVE or MODIFY
	 * @param source sender of notification
	 * @param messageText descriptive message as a string
	 */
	public WorkspaceMessage(int type, String id, int eventType, WorkspacePublisher source, String messageText) {
		super();		
		this.type = type;
		this.idString = id;
		this.eventType = eventType;
		this.source = source;
		this.messageText = messageText;
	}

	/**
	 * Creates a change notification message for the given model type, model id, event type, 
	 * and source.  Descriptive text is blank. 
	 *  
	 * @param type MODEL or DISPLAY
	 * @param id the model's id, ignored if display changed
	 * @param eventType ADD, REMOVE or MODIFY
	 * @param source sender of notification
	 */
	public WorkspaceMessage(int type, String id, int eventType, WorkspacePublisher source) {				
		this(type, id, eventType, source, "");
	}

	/**
	 * Returns the type of event that occurred. 
	 * 
	 * @return the type of event that occurred
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * Returns the id of the model that was changed. 
	 * 
	 * @return the id of the model that was changed
	 */
	public String getIdString() {
		return idString;
	}

	/**
	 * Returns a string describing the message. 
	 * 
	 * @return a description of the change event
	 */	
	public String getMessageText() {
		return messageText;
	}

	/**
	 * Returns the workspace publisher that sent this message. 
	 * 
	 * @return the workspace publisher that sent this message
	 */
	public WorkspacePublisher getSource() {
		return source;
	}

	/**
	 * Returns the type of entity that was changed; either
	 * model type or display.
	 * 
	 * @return the type of entity that was changed by the event
	 */
	public int getType() {
		return type;
	}	
	
}
