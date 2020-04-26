package presentation.fsa;

/**
 * Implemented by Subscribers that wish to receive change notifications from
 * FSMGraph(Publisher).
 * 
 * @author Helen Bretzke
 */
public interface FSAGraphSubscriber {

    public void fsaGraphChanged(FSAGraphMessage message);

    public void fsaGraphSelectionChanged(FSAGraphMessage message);

}
