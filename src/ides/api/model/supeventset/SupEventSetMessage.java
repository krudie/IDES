package ides.api.model.supeventset;

/**
 * A message sent to subscribers listening to a {@link SupervisoryEventSet}.
 * 
 * @author Valerie Sugarman
 */
public class SupEventSetMessage {
    /** The supervisory event was added */
    public static final int ADD = 0;

    /** The supervisory event was removed */
    public static final int REMOVE = 1;

    /** The supervisory event was modified */
    public static final int MODIFY = 2;

    /** the publisher that sent this message */
    private SupervisoryEventSet source;

    /** the type of event (add, remove, modify) that occurred */
    private int eventType;

    /** a description of the event (add, remove, modify) that occured */
    private String messageText;

    /** the id of the supervisory event that was modified */
    private long supEventId;

    /**
     * Creates a {@link SupervisoryEventSet} message to be passed on to any
     * {@link SupervisoryEventSet} subscribers.
     * 
     * @param eventType   the type of change (add, remove, modify).
     * @param supEventId  the id of the {@link SupervisoryEvent} in the event set.
     * @param source      the {@link SupervisoryEventSet} where the message
     *                    originates.
     * @param messageText a descriptions of the message.
     */
    public SupEventSetMessage(int eventType, long supEventId, SupervisoryEventSet source, String messageText) {
        this.eventType = eventType;
        this.supEventId = supEventId;
        this.source = source;
        this.messageText = messageText;
    }

    /**
     * Creates a {@link SupervisoryEventSet} message to be passed on to any
     * {@link SupervisoryEventSet} subscribers.
     * 
     * @param eventType  the type of change (add, remove, modify).
     * @param supEventId the id of the Supervisory Event in the event set.
     * @param source     the {@link SupervisoryEventSet} where the message
     *                   originates.
     */
    public SupEventSetMessage(int eventType, long supEventId, SupervisoryEventSet source) {
        this.eventType = eventType;
        this.supEventId = supEventId;
        this.source = source;

    }

    /**
     * Retrieve the source of the message.
     * 
     * @return the source of the message
     */
    public SupervisoryEventSet getSource() {
        return source;
    }

    /**
     * Retrieve the type of change (addition, removal or modification).
     * 
     * @return the type of change (addition, removal or modification)
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * Retrieve the description of the message.
     * 
     * @return the description of the message
     */
    public String getMessage() {
        return messageText;
    }

    /**
     * Retrieve the ID of the {@link SupervisoryEvent} in the message.
     * 
     * @return the ID of the {@link SupervisoryEvent} in the message
     */
    public long getEventId() {
        return supEventId;
    }
}
