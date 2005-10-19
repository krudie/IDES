package projectModel;

import java.io.PrintStream;

/**
 * 
 * @author Axel Gottlieb Michelsen
 * 
 */
public class Event extends SubElementContainer {
    private int id;

    public Event(int id) {
        this.id = id;
    }
    
    public Event(Event e){
        super(e);
        this.id = e.id;
    }

    public int getId() {
        return id;
    }

    public void toXML(PrintStream ps, String indent) {
        if (isEmpty()) {
            ps.println(indent + "<event" + " id=\"" + id + "\"/>");
        } else {
            ps.println(indent + "<event" + " id=\"" + id + "\">");
            super.toXML(ps, indent + "  ");
            ps.println(indent + "</event>");
        }
    }

}
