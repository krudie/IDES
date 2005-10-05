package projectModel;

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
    
	public Automaton(String name){
		states = new LinkedList<State>();
		transitions = new LinkedList<Transition>();
		events = new LinkedList<Event>();
        this.name = name; 
	}
	
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
	public void addState(State s){
		states.add(s);
	}
	public void removeState(State s){
		states.remove(s);
	}
	public ListIterator getStateIterator(){
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
	
	public void addTransition(Transition t){
		transitions.add(t);
	}
	public void removeTransition(Transition t){
		transitions.remove(t);
	}
	public ListIterator getTransitionIterator(){
		return transitions.listIterator();
	}
	
	public void addEvent(Event e){
		events.add(e);
	}
	public void removeEvent(Event e){
		events.remove(e);
	}
	public ListIterator getEventIterator(){
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

	public boolean isLegal(){
		ListIterator<State> si = states.listIterator();
		ListIterator<Transition> ti = transitions.listIterator();
		Transition t;
		
		while(si.hasNext()){
			State s = si.next();
			ListIterator<Transition> i = s.getSourceTransitionsListIterator();
			while(i.hasNext()){
				t = i.next();
				if(!transitions.contains(t)
						&& t.getSource() == s) return false;
			}
			i = s.getTargetTransitionListIterator();
			while(i.hasNext()){
				t = i.next();
				if(!transitions.contains(t)
						&& t.getTarget() == s) return false;
			}
		}
		while(ti.hasNext()){
			t=ti.next();
			if(states.contains(t.getSource())
					&& states.contains(t.getTarget())) return false;
		}
		
		return true;
	}
}
