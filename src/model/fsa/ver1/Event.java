package model.fsa.ver1;

import model.fsa.FSAEvent;
import io.fsa.ver1.SubElement;
import io.fsa.ver1.SubElementContainer;

/**
 * Represents an events in an automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Event extends SubElementContainer implements model.fsa.FSAEvent, Comparable {
    private long id;

    /**
     * Constructs an event with the given id.
     * @param id the id of the event.
     */
    public Event(long id){
        this.id = id;
        addSubElement(new SubElement("properties"));
        addSubElement(new SubElement("name"));
    }

    
    /**
     * constructs an event with all internal variables equal to the event e.
     * @param e the event the new event must equal.
     */
    public Event(Event e){
        super(e);
        this.id = e.id;
        addSubElement(new SubElement("properties"));
        addSubElement(new SubElement("name"));
    }

    /**
     * constructs an event with all internal variables equal to the event e.
     * @param e the event the new event must equal.
     */
    public Event(FSAEvent e){
        this.id = e.getId();
        addSubElement(new SubElement("properties"));
        addSubElement(new SubElement("name"));
        this.setSymbol(e.getSymbol());
        this.setControllable(e.isControllable());
        this.setObservable(e.isObservable());
        //TODO: also transfer other properties
    }

    /**     
     * @return the symbol that represents this event
     */
	public String getSymbol() {
		SubElement eventSymbol = getSubElement("name");
		return eventSymbol != null ? eventSymbol.getChars() : "";
	}

	public void setSymbol(String symbol){		
		SubElement eventSymbol = getSubElement("name");
		if(eventSymbol == null){
			eventSymbol = new SubElement("name");					
		}
		eventSymbol.setChars(symbol);	
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

	public String toString(){
		return getSymbol();
	}
	
	public void setControllable(boolean b){
		SubElement props = getSubElement("properties");
		if(b && !isControllable()){			
			props.addSubElement(new SubElement("controllable"));			
		}
		if(!b && isControllable()){
			props.removeSubElement("controllable");
		}		
	}
	
	public void setObservable(boolean b){
		SubElement props = getSubElement("properties");
		if(b && !isObservable()){			
			props.addSubElement(new SubElement("observable"));			
		}
		if(!b && isObservable()){
			props.removeSubElement("observable");
		}
		
	}
	
	public boolean isControllable() {
		SubElement properties = this.getSubElement("properties");
		return properties.getSubElement("controllable") != null;
	}


	public boolean isObservable() {
		SubElement properties = this.getSubElement("properties");
		return properties.getSubElement("observable") != null;
	}
	
	////////////////////////////////////////////////////////////
	// Not using this code yet.  Had an idea that we'd keep property
	// access generic to reduce number of commands kicking around ...
	/**
	 * Sets the given attribute to the given value.
	 * If <code>attribute</code> is not a valid attribute name,
	 * does nothing.  
	 */
	public void set(String attribute, String value) {
		if(attribute.equals("controllable")){
			
			return;
		}
		if(attribute.equals("observable")){
			
			return;
		}
		if(attribute.equals("symbol")){
			setSymbol(value);
			return;
		}
		// DEBUG
		System.err.println("State: cannot set attribute " + attribute);		
	}
	
	public String get(String attribute) {
		if(attribute.equals("controllable")){
			
			return null;
		}
		if(attribute.equals("observable")){
			
			return null;
		}
		if(attribute.equals("symbol")){
			
			return null;
		}
		// DEBUG
		System.err.println("State: cannot get attribute " + attribute);		
		return null;
	}
	///////////////////////////////////////////////////////////////////


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object arg0) {		
		return getSymbol().compareTo(((Event)arg0).getSymbol());
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof FSAEvent))
			return false;
		return getSymbol().equals(((FSAEvent)o).getSymbol());
	}
}
