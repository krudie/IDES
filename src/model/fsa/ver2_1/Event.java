package model.fsa.ver2_1;

import java.util.Hashtable;

import model.fsa.FSAEvent;
import io.fsa.ver2_1.SubElement;
import io.fsa.ver2_1.SubElementContainer;

/**
 * Represents an event in an automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Event implements model.fsa.FSAEvent, Comparable {
    private long id;
    private static final String NAME="name", CONTROLLABLE="controlable", OBSERVABLE="observable";
	protected Hashtable<String, Object> annotations=new Hashtable<String,Object>();
    /**
     * Constructs an event with the given id.
     * @param id the id of the event.
     */
    public Event(long id){
        this.id = id;
//        addSubElement(new SubElement("properties"));
//        addSubElement(new SubElement("name"));
    }

    
    /**
     * Constructs an event with all internal variables equal to the event e.
     * @param e the event the new event must equal.
     */
    public Event(Event e){
//        super(e);
        this.id = e.id;
//        addSubElement(new SubElement("properties"));
//        addSubElement(new SubElement("name"));
        this.setSymbol(e.getSymbol());
        this.setControllable(e.isControllable());
        this.setObservable(e.isObservable());
        //TODO: also transfer other properties
    }

    /**
     * Constructs an event with all internal variables equal to the event e.
     * @param e the event the new event must equal.
     */
    public Event(FSAEvent e){
        this.id = e.getId();
//        addSubElement(new SubElement("properties"));
//        addSubElement(new SubElement("name"));
        this.setSymbol(e.getSymbol());
        this.setControllable(e.isControllable());
        this.setObservable(e.isObservable());
        //TODO: also transfer other properties
    }

    /**
     * Returns the symbol that represents this event in the (local?) alphabet.
     *      
     * @return the symbol that represents this event
     */
	public String getSymbol() {
		String s = (String)this.getAnnotation(NAME);
		return (s==null?"":s);
	}

	/**
	 * Sets the symbol for this event to <code>symbol</code>.
	 * 
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol){		
		this.setAnnotation(NAME, symbol);
	}

	/**
	 * Returns the unique id for this event. 
	 * 
	 * @return the unique id among the (local?) event set
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the unique id among the (local?) event set to <code>id</code>. 
	 * 
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the event symbol. 
	 * 
	 * @return event symbol as a string
	 */
	public String toString(){
		return getSymbol();
	}
	
	public void setControllable(boolean b){
		this.setAnnotation(CONTROLLABLE, b); 
	}
	
	public void setObservable(boolean b){
		this.setAnnotation(OBSERVABLE, b);
	}
	
	public boolean isControllable() {
		return (this.getAnnotation(CONTROLLABLE) == null?false:((Boolean)this.getAnnotation(CONTROLLABLE)).booleanValue());
	}


	public boolean isObservable() {
		return (this.getAnnotation(OBSERVABLE) == null?false:((Boolean)this.getAnnotation(OBSERVABLE)).booleanValue());
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object arg0) {		
		return getSymbol().compareTo(((Event)arg0).getSymbol());
	}
	
	public boolean equals(Object o)	{
		if(!(o instanceof FSAEvent)) {
			return false;
		}
		return getSymbol().equals(((FSAEvent)o).getSymbol());
	}
	
	/**
	 * Returns the annotation for the given key.
	 * @param key key for the annotation
	 * @return if there is no annotation for the given key,
	 * returns <code>null</code>, otherwise returns the annotation
	 * for the key
	 */
	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}
	
	/**
	 * Sets an annotation for a given key. If there is already
	 * an annotation for the key, it is replaced. 
	 * @param key the key for the annotation
	 * @param annotation the annotation
	 */
	public void setAnnotation(String key, Object annotation)
	{
		annotations.put(key, annotation);
	}
	
	
	/**
	 * Removes the annotation for the given key.
	 * @param key key for the annotation
	 */
	public void removeAnnotation(String key)
	{
		annotations.remove(key);
	}
	
	/**
	 * Returns <code>true</code> if there is an annotation
	 * for the given key. Otherwise returns <code>false</code>.
	 * @param key key for the annotation
	 * @return <code>true</code> if there is an annotation
	 * for the given key, <code>false</code> otherwise
	 */
	public boolean hasAnnotation(String key)
	{
		return false;
	}

}
