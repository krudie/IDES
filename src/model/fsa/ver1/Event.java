package model.fsa.ver1;

import io.fsa.ver1.SubElement;
import io.fsa.ver1.SubElementContainer;

/**
 * Represents an events in an automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Event extends SubElementContainer implements model.fsa.FSAEvent {
    private long id;

    /**
     * Constructs an event with the given id.
     * @param id the id of the event.
     */
    public Event(long id){
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
     * TODO need constants to access attribute values in hash tables
     * and when reading and writing to file.
     * 
     * @return the symbol that represents this event
     */
	public String getSymbol() {
		SubElement eventSymbol = getSubElement("SYMBOL"); 
		return eventSymbol != null ? eventSymbol.getName() : "";
	}


	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

	public String toString(){
		return getSymbol();
	}	
}
