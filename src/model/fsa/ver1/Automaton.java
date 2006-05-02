package model.fsa.ver1;
import java.util.*;


import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSAObserver;
import model.fsa.FSAState;
import model.fsa.FSATransition;


/**
 * This class is the topmost class in the automaton hierarchy. It serves as the datastructure for
 * states, transitions and events, and gives access to data iterators that are customized to 
 * maintain data integrity of the automaton at all times.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Automaton extends FSAModel implements Cloneable {
	
	
    private LinkedList<FSAState> states;

    private LinkedList<FSATransition> transitions;

    private LinkedList<FSAEvent> events;

    private String name = null;

    
    /**
     * constructs a nem automaton with the name name
     * @param name the name of the automaton
     */
    public Automaton(String name){
        states = new LinkedList<FSAState>();
        transitions = new LinkedList<FSATransition>();
        events = new LinkedList<FSAEvent>();
        this.name = name;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    public Automaton clone(){
        Automaton clone = new Automaton(this.name);
        ListIterator<FSAEvent> ei = getEventIterator();
        while(ei.hasNext()){
            clone.add(new Event((Event)ei.next()));
        }
        ListIterator<FSAState> si = getStateIterator();
        while(si.hasNext()){
            clone.add(new State((State) si.next()));
        }
        ListIterator<FSATransition> ti = getTransitionIterator();
        while(ti.hasNext()){
            Transition oldt = (Transition)ti.next();
            State source = (State)clone.getState(oldt.getSource().getId());
            State target = (State)clone.getState(oldt.getTarget().getId());
            if(oldt.getEvent() == null) clone.add(new Transition(oldt, source, target));
            else{
                Event event = (Event)clone.getEvent(oldt.getEvent().getId());
                clone.add(new Transition(oldt, source, target, event));
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
    public void add(FSAState s){
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
    public void remove(FSAState s){
        ListIterator<FSATransition> sources = s.getSourceTransitionsListIterator();
        while(sources.hasNext()){
            FSATransition t = sources.next();
            sources.remove();
            t.getSource().removeSourceTransition(t);
            t.getTarget().removeTargetTransition(t);
        }
        ListIterator<FSATransition> targets = s.getTargetTransitionListIterator();
        while(targets.hasNext()){
            FSATransition t = targets.next();
            targets.remove();
            t.getSource().removeSourceTransition(t);
            t.getTarget().removeTargetTransition(t);            
        }
        states.remove(s);
    }
    
    /**
     * @return a custom list iterator for the states
     */
    public ListIterator<FSAState> getStateIterator(){
        return new StateIterator(states.listIterator(), this);
    }

    /**
     * searches for the state with the given id.
     * @param id the id of the state
     * @return the state, null if it doesn't exist
     */
    public FSAState getState(int id){
        ListIterator<FSAState> si = states.listIterator();
        while(si.hasNext()){
            FSAState s = si.next();
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
    public void add(FSATransition t){
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
    public void remove(FSATransition t){
        t.getSource().removeSourceTransition(t);
        t.getTarget().removeTargetTransition(t);
        transitions.remove(t);
    }

    /**
     * searches for the transition with the given id.
     * @param Id the id of the transition.
     * @return the transition, null if the transition is not in the automaton.
     */
    public FSATransition getTransition(int Id){
        ListIterator<FSATransition> tli = transitions.listIterator();
        while(tli.hasNext()){
            FSATransition t = tli.next();
            if(t.getId() == Id) return t;
        }
        return null;
    }
    
    /**
     * @return a custom list iterator for the transitions.
     */
    public ListIterator<FSATransition> getTransitionIterator(){
        return new TransitionIterator(transitions.listIterator());
    }

    /**
     * Adds an event to the aotumaton.
     * @param e the event that shall be added to the automaton.
     */
    public void add(FSAEvent e){
        events.add(e);
    }

    /**
     * Removes an event from the automaton.
     * @param e the event to be removed
     */
    public void remove(FSAEvent e){
        events.remove(e);
    }

    /**
     * @return a custom list iterator for the events.
     */
    public ListIterator<FSAEvent> getEventIterator(){
        return new EventIterator(events.listIterator(), this);
    }

    
    /**
     * searches for the event with the given event id.
     * @param id the id of the event
     * @return the event, null if it doesn't exist
     */
    public FSAEvent getEvent(int id){
        ListIterator<FSAEvent> ei = events.listIterator();
        while(ei.hasNext()){
            FSAEvent e = ei.next();
            if(e.getId() == id) return e;
        }
        return null;
    }
    
    /**
     * @author agmi02
     * A custom list iterator for states. Preserves data integrity.
     */
    private class StateIterator implements ListIterator<FSAState>{
        private ListIterator<FSAState> sli;
        private FSAState current;
        private Automaton a;
        
        /**
         * constructs a stateIterator
         * @param sli the listiterator this listiterator shall encapsulate
         * @param a the automaton that is backing the listiterator
         */
        public StateIterator(ListIterator<FSAState> sli, Automaton a){
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
        public FSAState next(){
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
        public FSAState previous(){
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
            ListIterator<FSATransition> sources = current.getSourceTransitionsListIterator();
            while(sources.hasNext()){
                FSATransition t = sources.next();
                sources.remove();
                a.remove(t);
                t.getSource().removeSourceTransition(t);
                t.getTarget().removeTargetTransition(t);
            }
            ListIterator<FSATransition> targets = current.getTargetTransitionListIterator();
            while(targets.hasNext()){
                FSATransition t = targets.next();
                targets.remove();
                a.remove(t);
                t.getSource().removeSourceTransition(t);
                t.getTarget().removeTargetTransition(t);            
            }
            sli.remove();
        }
        public void set(FSAState s){
            ListIterator<FSATransition> sources = current.getSourceTransitionsListIterator();
            while(sources.hasNext()){
                FSATransition t = sources.next();
                t.setSource(s);
                s.addSourceTransition(t);
            }
            ListIterator<FSATransition> targets = current.getTargetTransitionListIterator();
            while(targets.hasNext()){
                FSATransition t = targets.next();
                t.setTarget(s);
                s.addTargetTransition(t);
            }
            sli.set(s);
        }
        public void add(FSAState s){
            sli.add(s);
        }
    }
    /**
     * A custom list iterator for transitions. Conserves data integrity.
     * @author Axel Gottlieb Michelsen
     * @author Kristian Edlund
     */
    private class TransitionIterator implements ListIterator<FSATransition>{
        private ListIterator<FSATransition> tli;
        private FSATransition current;
        
        /**
         * Constructor for the customized transition iterator
         * @param tli the original transition iterator that is going to be wrapped
         */
        public TransitionIterator(ListIterator<FSATransition> tli){
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
        public FSATransition next(){
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
        public FSATransition previous(){
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
        public void set(FSATransition t){
            remove();
            add(t);
        }
        
        /**
         * Adds a new transition to the correct states and to the list of transitions
         * @param t the transition to be added
         */
        public void add(FSATransition t){
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
    private class EventIterator implements ListIterator<FSAEvent>{
        private FSAEvent current;
        private ListIterator<FSAEvent> eli;
        private Automaton a;
                
        
        /**
         * The constructor for the event iterator
         * @param eli the event iterator to be wrapped
         * @param a the automaton the event list is connected to
         */
        public EventIterator(ListIterator<FSAEvent> eli, Automaton a){
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
        public FSAEvent next(){
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
        public FSAEvent previous(){
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
            ListIterator<FSATransition> tli = a.getTransitionIterator();
            while(tli.hasNext()){
                FSATransition t = tli.next();
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
        public void set(FSAEvent e){
            ListIterator<FSATransition> tli = a.getTransitionIterator();
            while(tli.hasNext()){
                FSATransition t = tli.next();
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
        public void add(FSAEvent e){
            eli.add(e);
        }
    }	
}
