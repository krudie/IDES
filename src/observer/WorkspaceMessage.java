/**
 * 
 */
package observer;

/**
 * @author helen
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
	private long id; // of model but not of display
	
	private int eventType;
	private WorkspacePublisher source;
	
	private String messageText;
	
	public WorkspaceMessage(int type, long id, int eventType, WorkspacePublisher source, String messageText) {
		super();		
		this.type = type;
		this.id = id;
		this.eventType = eventType;
		this.source = source;
		this.messageText = messageText;
	}

	public WorkspaceMessage(int type, long id, int eventType, WorkspacePublisher source) {				
		this(type, id, eventType, source, "");
	}

	public int getEventType() {
		return eventType;
	}

	public long getId() {
		return id;
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
