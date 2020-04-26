package ides.api.model.fsa;

/**
 * Subscriber to messages sent by FSA models.
 * 
 * @author helen bretzke
 */
public interface FSASubscriber {
    /**
     * The structure (states and transitions) of the FSA changed.
     * 
     * @param message description of the modification
     */
    public void fsaStructureChanged(FSAMessage message);

    /**
     * The event set of the FSA changed.
     * 
     * @param message description of the modification
     */
    public void fsaEventSetChanged(FSAMessage message);
}
