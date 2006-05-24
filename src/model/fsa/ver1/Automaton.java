package model.fsa.ver1;
import java.util.LinkedList;
import java.util.ListIterator;

import model.Publisher;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
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
public class Automaton extends Publisher implements Cloneable, FSAModel {	
	
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
    public FSAModel clone(){
        FSAModel clone = new Automaton(this.name);
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

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getName()
	 */
    public String getName(){
        return name;
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#setName(java.lang.String)
	 */
    public void setName(String name){
        this.name = name;
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#add(model.fsa.FSAState)
	 */
    public void add(FSAState s){
        states.add(s);
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getStateCount()
	 */
    public int getStateCount(){
        return states.size();
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getTransitionCount()
	 */
    public int getTransitionCount(){
        return transitions.size();
    }

    public int getEventCount(){
    	return events.size();
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#remove(model.fsa.FSAState)
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
    
    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getStateIterator()
	 */
    public ListIterator<FSAState> getStateIterator(){
        return new StateIterator(states.listIterator(), this);
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getState(int)
	 */
    public FSAState getState(long id){
        ListIterator<FSAState> si = states.listIterator();
        while(si.hasNext()){
            FSAState s = si.next();
            if(s.getId() == id) return s;
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#add(model.fsa.FSATransition)
	 */
    public void add(FSATransition t){
        t.getSource().addSourceTransition(t);
        t.getTarget().addTargetTransition(t);
        transitions.add(t);
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#remove(model.fsa.FSATransition)
	 */
    public void remove(FSATransition t){
        t.getSource().removeSourceTransition(t);
        t.getTarget().removeTargetTransition(t);
        transitions.remove(t);
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getTransition(int)
	 */
    public FSATransition getTransition(long id){
        ListIterator<FSATransition> tli = transitions.listIterator();
        while(tli.hasNext()){
            FSATransition t = tli.next();
            if(t.getId() == id) return t;
        }
        return null;
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getTransitionIterator()
	 */
    public ListIterator<FSATransition> getTransitionIterator(){
        return new TransitionIterator(transitions.listIterator());
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#add(model.fsa.FSAEvent)
	 */
    public void add(FSAEvent e){
        events.add(e);
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#remove(model.fsa.FSAEvent)
	 */
    public void remove(FSAEvent e){
        events.remove(e);
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getEventIterator()
	 */
    public ListIterator<FSAEvent> getEventIterator(){
        return new EventIterator(events.listIterator(), this);
    }

    
    /* (non-Javadoc)
	 * @see model.fsa.ver1.FSAModel#getEvent(int)
	 */
    public FSAEvent getEvent(long id){
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
        private FSAModel a;
        
        /**
         * constructs a stateIterator
         * @param sli the listiterator this listiterator shall encapsulate
         * @param a the automaton that is backing the listiterator
         */
        public StateIterator(ListIterator<FSAState> sli, FSAModel a){
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
        private FSAModel a;
                
        
        /**
         * The constructor for the event iterator
         * @param eli the event iterator to be wrapped
         * @param a the automaton the event list is connected to
         */
        public EventIterator(ListIterator<FSAEvent> eli, FSAModel a){
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