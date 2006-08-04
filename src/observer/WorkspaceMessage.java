/**
 * 
 */
package observer;

/**
 * @author helen bretzke
 *
 */
public class WorkspaceMessage {

	/**
	 * possible event types
	 */
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int MODIFY = 2;
	
	/**
	 * possible model types
	 * other types will be required for those who write plugins for different
	 * model types e.g. hierarchical, petri etc.
	 */
	public static final int DISPLAY = 0;
	public static final int FSM = 1;
	 
	
	/*
	 * Indicates the model type or general DISPLAY has been changed.
	 * TODO need a better name for this field 
	 */
	private int type;
	private String idString; // of model but not of display
	
	private int eventType;
	private WorkspacePublisher source;
	
	private String messageText;
	
	/**
	 * Creates a Workspace change notification message with descriptive string.
	 * 
	 * @param type FSM or DISPLAY
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
	 * Creates a Workspace change notification message with empty string.
	 * 
	 * @param type FSM or DISPLAY
	 * @param id the model's id, ignored if display changed
	 * @param eventType ADD, REMOVE or MODIFY
	 * @param source sender of notification
	 */
	public WorkspaceMessage(int type, String id, int eventType, WorkspacePublisher source) {				
		this(type, id, eventType, source, "");
	}

	public int getEventType() {
		return eventType;
	}

	public String getIdString() {
		return idString;
	}

	public String getMessageText() {
		return messageText;
	}

	public WorkspacePublisher getSource() {
		return source;
	}

	public int getType() {
		return type;
	}	
	
}
