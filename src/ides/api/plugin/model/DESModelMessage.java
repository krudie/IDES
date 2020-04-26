package ides.api.plugin.model;

/**
 * Message sent to the subscribers listening for updates of a {@link DESModel}.
 */
public class DESModelMessage {

    /** DESModel became dirty (clean to dirty) */
    public static final int DIRTY = 0;

    /** Changes to DESModel were saved (dirty to clean) */
    public static final int CLEAN = 1;

    /** DESModel was renamed */
    public static final int NAME = 1;

    /** the publisher that sent this message */
    private DESModel source;

    /** the type of event that occurred */
    private int eventType;

    /** a description of the event fired */
    private String messageText;

    /**
     * Creates a change notification message for DESModels to pass to
     * DESModelSubscribers.
     * 
     * @param eventType DIRTY or CLEAN or NAME
     * @param source    message sender
     */
    public DESModelMessage(int eventType, DESModel source) {
        this(eventType, source, "");
    }

    /**
     * Creates a change notification message for DESModels to pass to
     * DESModelSubscribers.
     * 
     * @param eventType DIRTY or CLEAN or NAME
     * @param source    message sender
     * @param message   a description of the event fired
     */
    public DESModelMessage(int eventType, DESModel source, String message) {
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
     * Returns the model that sent this message.
     * 
     * @return the model that sent this message
     */
    public DESModel getSource() {
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
