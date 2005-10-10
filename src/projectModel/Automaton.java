package projectModel;

import java.io.PrintStream;
import java.util.*;

/**
 * 
 * @author Axel Gottlieb Michelsen
 * 
 */
public class Automaton {
    private LinkedList<State> states;

    private LinkedList<Transition> transitions;

    private LinkedList<Event> events;

    private String name = null;

    public Automaton(String name) {
        states = new LinkedList<State>();
        transitions = new LinkedList<Transition>();
        events = new LinkedList<Event>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addState(State s) {
        states.add(s);
    }

    public void removeState(State s) {
        states.remove(s);
    }

    public ListIterator<State> getStateIterator() {
        return states.listIterator();
    }

    public State getState(int id) {
        ListIterator<State> si = states.listIterator();
        while (si.hasNext()) {
            State s = si.next();
            if (s.getId() == id)
                return s;
        }
        return null;
    }

    public void addTransition(Transition t) {
        transitions.add(t);
    }

    public void removeTransition(Transition t) {
        transitions.remove(t);
    }

    public ListIterator getTransitionIterator() {
        return transitions.listIterator();
    }

    public void addEvent(Event e) {
        events.add(e);
    }

    public void removeEvent(Event e) {
        events.remove(e);
    }

    public ListIterator getEventIterator() {
        return events.listIterator();
    }

    public Event getEvent(int id) {
        ListIterator<Event> ei = events.listIterator();
        while (ei.hasNext()) {
            Event e = ei.next();
            if (e.getId() == id)
                return e;
        }
        return null;
    }

    public void toXML(PrintStream ps) {
        ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        ps.println("<automaton>");
        Iterator<State> si = states.iterator();
        while (si.hasNext()) {
            si.next().toXML(ps, "  ");
        }

        Iterator<Event> ei = events.iterator();
        while (ei.hasNext()) {
            ei.next().toXML(ps, "  ");
        }

        Iterator<Transition> ti = transitions.iterator();
        while (ti.hasNext()) {
            ti.next().toXML(ps, "  ");
        }
        ps.println("</automaton>");
    }
}
