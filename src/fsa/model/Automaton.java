package fsa.model;

import java.util.*;


/**
 * This class is the topmost class in the automaton hierarchy. It serves as the datastructure for
 * states, transitions and events, and gives access to data iterators that are customized to 
 * maintain data integrity of the automaton at all times.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Automaton implements Cloneable{
    private LinkedList<State> states;

    private LinkedList<Transition> transitions;

    private LinkedList<Event> events;

    private String name = null;

    
    /**
     * constructs a nem automaton with the name name
     * @param name the name of the automaton
     */
    public Automaton(String name){
        states = new LinkedList<State>();
        transitions = new LinkedList<Transition>();
        events = new LinkedList<Event>();
        this.name = name;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    public Automaton clone(){
        Automaton clone = new Automaton(this.name);
        ListIterator<Event> ei = getEventIterator();
        while(ei.hasNext()){
            clone.add(new FSAEvent((FSAEvent)ei.next()));
        }
        ListIterator<State> si = getStateIterator();
        while(si.hasNext()){
            clone.add(new FSAState((FSAState) si.next()));
        }
        ListIterator<Transition> ti = getTransitionIterator();
        while(ti.hasNext()){
            FSATransition oldt = (FSATransition)ti.next();
            FSAState source = (FSAState)clone.getState(oldt.getSource().getId());
            FSAState target = (FSAState)clone.getState(oldt.getTarget().getId());
            if(oldt.getEvent() == null) clone.add(new FSATransition(oldt, source, target));
            else{
                FSAEvent event = (FSAEvent)clone.getEvent(oldt.getEvent().getId());
                clone.add(new FSATransition(oldt, source, target, event));
            }
        }
        return clone;
    }

    /**
     * @return the name of the automaton
     */
    public String getName(){
        return name;
    }

    /**
     * @param name the name of the automaton
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * @param s a state that needs to be added.
     */
    public void add(State s){
        states.add(s);
    }
    
    /**
     * @return the number of states in the automaton
     */
    public int getStateCount(){
        return states.size();
    }
    
    /**
     * @return the number of transitions in the automaton
     */
    public int getTransitionCount(){
        return transitions.size();
    }

    /**
     * removes the state from the automaton and all transitions leading to 
     * the state and originating from the state
     * @param s the state to be removed
     */
    public void remove(State s){
        ListIterator<Transition> sources = s.getSourceTransitionsListIterator();
        while(sources.hasNext()){
            Transition t = sources.next();
            sources.remove();
            t.getSource().removeSourceTransition(t);
            t.getTarget().removeTargetTransition(t);
        }
        ListIterator<Transition> targets = s.getTargetTransitionListIterator();
        while(targets.hasNext()){
            Transition t = targets.next();
            targets.remove();
            t.getSource().removeSourceTransition(t);
            t.getTarget().removeTargetTransition(t);            
        }
        states.remove(s);
    }
    
    /**
     * @return a custom list iterator for the states
     */
    public ListIterator<State> getStateIterator(){
        return new StateIterator(states.listIterator(), this);
    }

    /**
     * searches for the state with the given id.
     * @param id the id of the state
     * @return the state, null if it doesn't exist
     */
    public State getState(int id){
        ListIterator<State> si = states.listIterator();
        while(si.hasNext()){
            State s = si.next();
            if(s.getId() == id) return s;
        }
        return null;
    }

    /**
     * Adds a transition the the automaton and adds the transition to
     * the list of sources and targets in the source and target state of the
     * transition.
     * @param t the transition to be added to the state
     */
    public void add(Transition t){
        t.getSource().addSourceTransition(t);
        t.getTarget().addTargetTransition(t);
        transitions.add(t);
    }

    /**
     * Removes a transition from the automaton. Removes the transition from the 
     * list of sourcetransitions and the list of target transitions in the 
     * right states.
     * @param t the transition to be removed
     */
    public void remove(Transition t){
        t.getSource().removeSourceTransition(t);
        t.getTarget().removeTargetTransition(t);
        transitions.remove(t);
    }

    /**
     * searches for the transition with the given id.
     * @param Id the id of the transition.
     * @return the transition, null if the transition is not in the automaton.
     */
    public Transition getTransition(int Id){
        ListIterator<Transition> tli = transitions.listIterator();
        while(tli.hasNext()){
            Transition t = tli.next();
            if(t.getId() == Id) return t;
        }
        return null;
    }
    
    /**
     * @return a custom list iterator for the transitions.
     */
    public ListIterator<Transition> getTransitionIterator(){
        return new TransitionIterator(transitions.listIterator());
    }

    /**
     * Adds an event to the aotumaton.
     * @param e the event that shall be added to the automaton.
     */
    public void add(Event e){
        events.add(e);
    }

    /**
     * Removes an event from the automaton.
     * @param e the event to be removed
     */
    public void remove(Event e){
        events.remove(e);
    }

    /**
     * @return a custom list iterator for the events.
     */
    public ListIterator<Event> getEventIterator(){
        return new EventIterator(events.listIterator(), this);
    }

    
    /**
     * searches for the event with the given event id.
     * @param id the id of the event
     * @return the event, null if it doesn't exist
     */
    public Event getEvent(int id){
        ListIterator<Event> ei = events.listIterator();
        while(ei.hasNext()){
            Event e = ei.next();
            if(e.getId() == id) return e;
        }
        return null;
    }
    
    /**
     * @author agmi02
     * A custom list iterator for states. Conserves data integrity.
     */
    private class StateIterator implements ListIterator<State>{
        private ListIterator<State> sli;
        private State current;
        private Automaton a;
        
        /**
         * constructs a stateIterator
         * @param sli the listiterator this listiterator shall encapsulate
         * @param a the automaton that is backing the listiterator
         */
        public StateIterator(ListIterator<State> sli, Automaton a){
            this.a = a;
            this.sli = sli;
        }
        
        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext(){
            return sli.hasNext();
        }
        /**
         * @see java.util.Iterator#next()
         */
        public State next(){
            current = sli.next();
            return current;
        }
        /**
         * @see java.util.ListIterator#hasPrevious()
         */
        public boolean hasPrevious(){
            return sli.hasPrevious();
        }
        /**
         * @see java.util.ListIterator#previous()
         */
        public State previous(){
            current = sli.previous();
            return current;
        }
        /**
         * @see java.util.ListIterator#nextIndex()
         */
        public int nextIndex(){
            return sli.nextIndex();
        }
        /**
         * @see java.util.ListIterator#previousIndex()
         */
        public int previousIndex(){
            return sli.previousIndex();
        }
        /** 
         * removes the element last returned by either next or previous. 
         * removes the transitions originating from this state and leading
         * to this state in order to maintain data integrity.
         */
        public void remove(){
            ListIterator<Transition> sources = current.getSourceTransitionsListIterator();
            while(sources.hasNext()){
                Transition t = sources.next();
                sources.remove();
                a.remove(t);
                t.getSource().removeSourceTransition(t);
                t.getTarget().removeTargetTransition(t);
            }
            ListIterator<Transition> targets = current.getTargetTransitionListIterator();
            while(targets.hasNext()){
                Transition t = targets.next();
                targets.remove();
                a.remove(t);
                t.getSource().removeSourceTransition(t);
                t.getTarget().removeTargetTransition(t);            
            }
            sli.remove();
        }
        public void set(State s){
            ListIterator<Transition> sources = current.getSourceTransitionsListIterator();
            while(sources.hasNext()){
                Transition t = sources.next();
                t.setSource(s);
                s.addSourceTransition(t);
            }
            ListIterator<Transition> targets = current.getTargetTransitionListIterator();
            while(targets.hasNext()){
                Transition t = targets.next();
                t.setTarget(s);
                s.addTargetTransition(t);
            }
            sli.set(s);
        }
        public void add(State s){
            sli.add(s);
        }
    }
    /**
     * A custom list iterator for transitions. Conserves data integrity.
     * @author Axel Gottlieb Michelsen
     * @author Kristian Edlund
     */
    private class TransitionIterator implements ListIterator<Transition>{
        private ListIterator<Transition> tli;
        private Transition current;
        
        /**
         * Constructor for the customized transition iterator
         * @param tli the original transition iterator that is going to be wrapped
         */
        public TransitionIterator(ListIterator<Transition> tli){
            this.tli = tli;
        }
        
        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext(){
            return tli.hasNext();
        }
        
        /**
         * @see java.util.Iterator#next()
         */
        public Transition next(){
            current = tli.next();
            return current;
        }
        
        /**
         * @see java.util.ListIterator#hasPrevious()
         */
        public boolean hasPrevious(){
            return tli.hasPrevious();
        }
        
        /**
         * @see java.util.ListIterator#previous()
         */
        public Transition previous(){
            current = tli.previous();
            return current;
        }
        
        /**
         * @see java.util.ListIterator#nextIndex()
         */
        public int nextIndex(){
            return tli.nextIndex();
        }
        
        /**
         * @see java.util.ListIterator#previousIndex()
         */
        public int previousIndex(){
            return tli.previousIndex();
        }
        
        /**
         * Removes the transition from the states it is attached to and then removes it from the transition list
         * @see java.util.Iterator#remove()
         */
        public void remove(){
            current.getTarget().removeTargetTransition(current);
            current.getSource().removeSourceTransition(current);
            tli.remove();
        }
                 
       /**
        * Removes the last returned transition and replaces it with the new one
        * @param t the transition to replace the old one
        */
        public void set(Transition t){
            remove();
            add(t);
        }
        
        /**
         * Adds a new transition to the correct states and to the list of transitions
         * @param t the transition to be added
         */
        public void add(Transition t){
            t.getSource().addSourceTransition(t);
            t.getTarget().addTargetTransition(t);
            transitions.add(t);
        }
    }
    
    
    /**
     * A custom list iterator for events. Conserves data integrity.
     * @author Axel Gottlieb Michelsen
     * @author Kristian Edlund
     */
    private class EventIterator implements ListIterator<Event>{
        private Event current;
        private ListIterator<Event> eli;
        private Automaton a;
                
        
        /**
         * The constructor for the event iterator
         * @param eli the event iterator to be wrapped
         * @param a the automaton the event list is connected to
         */
        public EventIterator(ListIterator<Event> eli, Automaton a){
            this.eli = eli;
            this.a = a;
        }
        
        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext(){
            return eli.hasNext();
        }
        
        /**
         * @see java.util.Iterator#next()
         */
        public Event next(){
            current = eli.next();
            return current;
        }
        
        /**
         * @see java.util.ListIterator#hasPrevious()
         */
        public boolean hasPrevious(){
            return eli.hasPrevious();
        }
        
        /**
         * @see java.util.ListIterator#previous()
         */
        public Event previous(){
            current = eli.previous();
            return current;
        }
        /**
         * @see java.util.ListIterator#nextIndex()
         */
        public int nextIndex(){
            return eli.nextIndex();
        }
        
        /**
         * @see java.util.ListIterator#previousIndex()
         */
        public int previousIndex(){
            return eli.previousIndex();
        }
        /**
         * removes the current event from all the transitions it was involved in
         * @see java.util.Iterator#remove()
         */
        public void remove(){
            ListIterator<Transition> tli = a.getTransitionIterator();
            while(tli.hasNext()){
                Transition t = tli.next();
                if(t.getEvent() == current){
                    t.setEvent(null);
                }
            }
            eli.remove();
        }
        
        /**
         * Replaces the current event with the given event
         * @param e the event to replace the current event
         */
        public void set(Event e){
            ListIterator<Transition> tli = a.getTransitionIterator();
            while(tli.hasNext()){
                Transition t = tli.next();
                if(t.getEvent() == current){
                    t.setEvent(e);
                }
            }
            eli.set(e);
        }
        /**
         * Adds an event to the event list
         * @param e the event to add
         */
        public void add(Event e){
            eli.add(e);
        }
    }
}
