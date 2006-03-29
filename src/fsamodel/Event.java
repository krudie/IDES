package fsamodel;

/**
 * Represents an events in an automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Event extends SubElementContainer{
    private int id;

    /**
     * Constructs an event with the given id.
     * @param id the id of the event.
     */
    public Event(int id){
        this.id = id;
    }

    
    /**
     * constructs an event with all internal variables equal to the event e.
     * @param e the event the new event must equal.
     */
    public Event(Event e){
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
