package fsa.model;

/**
 * Represents an events in an automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class FSAEvent extends SubElementContainer implements fsa.model.Event {
    private int id;

    /**
     * Constructs an event with the given id.
     * @param id the id of the event.
     */
    public FSAEvent(int id){
        this.id = id;
    }

    
    /**
     * constructs an event with all internal variables equal to the event e.
     * @param e the event the new event must equal.
     */
    public FSAEvent(FSAEvent e){
        super(e);
        this.id = e.id;
    }

    /**
     * returns the id of the event.
     * @return the id of the event.
     */
    public int getId(){
        return id;
    }

    /**
     * sets the id of the event.
     * @param id the id to be set.
     */
    public void setId(int id){
        this.id = id;
    }
}
