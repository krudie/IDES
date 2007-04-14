package presentation;

import java.awt.geom.Rectangle2D;

import presentation.fsa.FSAGraph;

public class LayoutShellMessage {

	/* the possible events that caused this message to be sent */
	/** Layout shell became dirty (clean to dirty) */
	public static final int DIRTY = 0;
	/** Changes to layout shell were saved (dirty ro clean) */
	public static final int CLEAN = 1;

	/** the publisher that sent this message */
	private LayoutShell source;
	
	/** the type of event that occurred */
	private int eventType;
	
	/** a description of the event fired */
	private String messageText;

	/**
	 * Creates a change notification message for LayoutShells to pass
	 * to LayoutShellSubscribers. 
	 * 
	 * @param eventType DIRTY or CLEAN
	 * @param source message sender
	 */
	public LayoutShellMessage(int eventType, LayoutShell source) {
		this(eventType, source, "");		
	}

	/**
	 * Creates a change notification message for LayoutShells to pass
	 * to LayoutShellSubscribers. 
	 * 
	 * @param eventType DIRTY or CLEAN
	 * @param source message sender
	 * @param message a description of the event fired
	 */
	public LayoutShellMessage(int eventType, LayoutShell source, String message) {
		super();		
		this.source = source;
		this.eventType = eventType;
		this.messageText = message;
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
	 * Returns the graph that sent this message. 
	 * 
	 * @return the graph that sent this message
	 */
	public LayoutShell getSource() {
		return source;
	}
	
	/**
	 * Returns a string describing the message. 
	 * 
	 * @return a description of the change event
	 */
	public String getMessage() {
		return messageText;
	}

}
