package ides.api.model.fsa;

/**
 * Message sent to subscribers listening for changes to a FSA.
 * 
 * @author helen bretzke
 */
public class FSAMessage {

    /**
     * The message is about a state.
     */
    public static final int STATE = 0;

    /**
     * The message is about a transition.
     */
    public static final int TRANSITION = 1;

    /**
     * The message is about an event.
     */
    public static final int EVENT = 2;

    /**
     * The element was added.
     */
    public static final int ADD = 0;

    /**
     * The element was removed.
     */
    public static final int REMOVE = 1;

    /**
     * The element was modified.
     */
    public static final int MODIFY = 2;

    /**
     * the type of element that was affected by the event
     */
    private int elementType;

    /**
     * unique id of the element
     */
    private long elementId;

    /**
     * the type of event that occurred
     */
    private int eventType;

    /**
     * the publisher that sent this message
     */
    private FSAModel source;

    /**
     * a description to go with this notification.
     */
    private String messageText;

    /**
     * Creates an FSA modification message to be passed to {@link FSASubscriber} s.
     * 
     * @param eventType   the type of event (addition, removal or modification)
     * @param elementType the element type (state, transition or event)
     * @param elementId   the ID of the element
     * @param source      the {@link FSAModel} where the message originates
     */
    public FSAMessage(int eventType, int elementType, long elementId, FSAModel source) {
        this(eventType, elementType, elementId, source, "");
    }

    /**
     * Creates an FSA modification message to be passed to {@link FSASubscriber} s.
     * 
     * @param eventType   the type of event (addition, removal or modification)
     * @param elementType the element type (state, transition or event)
     * @param elementId   the ID of the element
     * @param source      the {@link FSAModel} where the message originates
     * @param messageText a description of the message
     */
    public FSAMessage(int eventType, int elementType, long elementId, FSAModel source, String messageText) {
        super();
        this.elementType = elementType;
        this.elementId = elementId;
        this.eventType = eventType;
        this.source = source;
        this.messageText = messageText;
    }

    /**
     * Retrieve the ID of the element in the message.
     * 
     * @return the ID of the element in the message
     */
    public long getElementId() {
        return elementId;
    }

    /**
     * Retrieve the type of the element in the message.
     * 
     * @return the type of the element in the message
     */
    public int getElementType() {
        return elementType;
    }

    /**
     * Retrieve the type of event (addition, removal or modification).
     * 
     * @return the type of event (addition, removal or modification)
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * Retrieve the source of the message.
     * 
     * @return the source of the message
     */
    public FSAModel getSource() {
        return source;
    }

    /**
     * Retrieve the description of the message.
     * 
     * @return the description of the message
     */
    public String getMessageText() {
        return messageText;
    }

}
