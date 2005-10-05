package projectModel;

/**
 * 
 * @author Axel Gottlieb Michelsen
 *
 */
public class Event extends SubElementContainer{
	private int id;
    public Event(int id){
		this.id = id;
	}
    
    public int getId(){
        return id;
    }
}
