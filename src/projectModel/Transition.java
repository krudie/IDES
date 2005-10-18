package projectModel;

import java.io.PrintStream;

/**
 * 
 * @author Axel Gottlieb Michelsen
 * 
 */
public class Transition extends SubElementContainer {
    private State sourceS, targetS;

    private Event e = null;

    private int id;

    public Transition(int id, State sourceS, State targetS) {
        this.id = id;
        this.sourceS = sourceS;
        this.targetS = targetS;
    }

    public Transition(int id, State sourceS, State targetS, Event e) {
        this.id = id;
        this.sourceS = sourceS;
        this.targetS = targetS;
        this.e = e;
    }

    public Transition(Transition t, State sourceS, State targetS, Event e){
        super(t);
        this.id = t.id;
        this.sourceS = sourceS;
        this.targetS = targetS;
        this.e = e;
    }
    public Transition(Transition t, State sourceS, State targetS){
        super(t);
        this.id = t.id;
        this.sourceS = sourceS;
        this.targetS = targetS;
    }

    public void setSource(State s) {
        this.sourceS = s;
    }

    public State getSource() {
        return this.sourceS;
    }

    public void setTarget(State s) {
        this.targetS = s;
    }

    public State getTarget() {
        return this.targetS;
    }

    public void setEvent(Event e) {
        this.e = e;
    }

    public Event getEvent() {
        return e;
    }

    public int getId() {
        return id;
    }

    public void toXML(PrintStream ps, String indent) {
        if (isEmpty()) {
            ps.println(indent + "<transition" + " id=\"" + id + "\""
                    + " source=\"" + sourceS.getId() + "\"" + " target=\""
                    + targetS.getId() + "\""
                    + ((e != null) ? " event=\"" + e.getId() + "\"" : "")
                    + "/>");
        } else {
            ps
                    .println(indent
                            + "<transition"
                            + " id=\""
                            + id
                            + "\""
                            + " source=\""
                            + sourceS.getId()
                            + "\""
                            + " target=\""
                            + targetS.getId()
                            + "\""
                            + ((e != null) ? " event=\"" + e.getId() + "\""
                                    : "") + ">");
            super.toXML(ps, indent + "  ");
            ps.println(indent + "</transition>");
        }
    }
}
