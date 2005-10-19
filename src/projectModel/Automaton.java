package projectModel;

import java.io.PrintStream;
import java.util.*;

/**
 * 
 * @author Axel Gottlieb Michelsen
 * 
 */
public class Automaton implements Cloneable{
    private LinkedList<State> states;

    private LinkedList<Transition> transitions;

    private LinkedList<Event> events;

    private String name = null;

    public Automaton(String name){
        states = new LinkedList<State>();
        transitions = new LinkedList<Transition>();
        events = new LinkedList<Event>();
        this.name = name;
    }

    public Automaton clone(){
        Automaton clone = new Automaton(this.name);
        ListIterator<Event> ei = getEventIterator();
        while(ei.hasNext()){
            clone.add(new Event(ei.next()));
        }
        ListIterator<State> si = getStateIterator();
        while(si.hasNext()){
            clone.add(new State(si.next()));
        }
        ListIterator<Transition> ti = getTransitionIterator();
        while(ti.hasNext()){
            Transition oldt = ti.next();
            State source = clone.getState(oldt.getSource().getId());
            State target = clone.getState(oldt.getTarget().getId());
            if(oldt.getEvent() == null) clone.add(new Transition(oldt, source, target));
            else{
                Event event = clone.getEvent(oldt.getEvent().getId());
                clone.add(new Transition(oldt, source, target, event));
            }
        }
        return clone;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void add(State s){
        states.add(s);
    }

    public void remove(State s){
        states.remove(s);
        ListIterator<Transition> sources = s.getSourceTransitionsListIterator();
        while(sources.hasNext()){
            remove(sources.next());
        }
        ListIterator<Transition> targets = s.getTargetTransitionListIterator();
        while(targets.hasNext()){
            remove(targets.next());
        }
    }

    public ListIterator<State> getStateIterator(){
        return states.listIterator();
    }

    public State getState(int id){
        ListIterator<State> si = states.listIterator();
        while(si.hasNext()){
            State s = si.next();
            if(s.getId() == id) return s;
        }
        return null;
    }

    public void add(Transition t){
        t.getSource().addSourceTransition(t);
        t.getTarget().addTargetTransition(t);
        transitions.add(t);
    }

    public void remove(Transition t){
        t.getSource().removeSourceTransition(t);
        t.getTarget().removeTargetTransition(t);
        transitions.remove(t);
    }

    public ListIterator<Transition> getTransitionIterator(){
        return transitions.listIterator();
    }

    public void add(Event e){
        events.add(e);
    }

    public void remove(Event e){
        events.remove(e);
    }

    public ListIterator<Event> getEventIterator(){
        return events.listIterator();
    }

    public Event getEvent(int id){
        ListIterator<Event> ei = events.listIterator();
        while(ei.hasNext()){
            Event e = ei.next();
            if(e.getId() == id) return e;
        }
        return null;
    }

    public void toXML(PrintStream ps){
        ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        ps.println("<automaton>");
        Iterator<State> si = states.iterator();
        while(si.hasNext()){
            si.next().toXML(ps, "  ");
        }

        Iterator<Event> ei = events.iterator();
        while(ei.hasNext()){
            ei.next().toXML(ps, "  ");
        }

        Iterator<Transition> ti = transitions.iterator();
        while(ti.hasNext()){
            ti.next().toXML(ps, "  ");
        }
        ps.println("</automaton>");
    }
}
