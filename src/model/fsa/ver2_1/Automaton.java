package model.fsa.ver2_1;
import io.fsa.ver2_1.SubElement;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import services.General;
import util.StupidSetWrapper;

import main.Annotable;
import main.Hub;
import model.DESModel;
import model.ModelDescriptor;
import model.ModelManager;
import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAMessage;
import model.fsa.FSAModel;
import model.fsa.FSAPublisherAdaptor;
import model.fsa.FSAState;
import model.fsa.FSASupervisor;
import model.fsa.FSATransition;


/**
 * This class is the topmost class in the automaton hierarchy. It serves as the datastructure for
 * states, transitions and events, and gives access to data iterators that are customized to 
 * maintain data integrity of the automaton at all times.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Lenko Grigorov
 */
public class Automaton extends FSAPublisherAdaptor implements Cloneable, FSASupervisor {	
	
	protected static class AutomatonDescriptor implements ModelDescriptor
	{
		public Class[] getModelInterfaces()
		{
			return new Class[]{FSAModel.class,FSASupervisor.class};
		}
		public Class getPreferredModelInterface()
		{
			return FSAModel.class;
		}
		public String getTypeDescription()
		{
			return "Finite State Automaton";
		}
		public Image getIcon()
		{
			return Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/model_fsa.gif"));
		}
		public DESModel createModel(String id)
		{
			Automaton a=new Automaton("");
			a.setId(id);
			return a;
		}
		public DESModel createModel(String id, String name)
		{
			Automaton a=new Automaton(name);
			a.setId(id);
			return a;
		}
	}
	public static final ModelDescriptor myDescriptor=new AutomatonDescriptor();
	
    private LinkedList<FSAState> states;

    private LinkedList<FSATransition> transitions;

    private LinkedList<FSAEvent> events;

    private String name = null;

//    private File myFile = null;
    
    protected String id = "";

    protected Hashtable<String, Object> annotations=new Hashtable<String,Object>();
    
//    /**
//     * If this automaton represents the composition of other automata,
//     * this will contain a list the ids of these other automata.      
//     */ 
//    protected String[] composedOf = new String[0];
    
    private SubElement meta = null;

	private long maxStateId;
	private long maxEventId;
	private long maxTransitionId;
	
    /**
     * constructs a nem automaton with the name name.
     * <p>Do not instantiate directly. Use {@link ModelManager#createModel(Class, String)} instead.
     * @param name the name of the automaton
     * @see ModelManager#createModel(Class)
     * @see ModelManager#createModel(Class, String)
     */
    protected Automaton(String name){
        states = new LinkedList<FSAState>();
        transitions = new LinkedList<FSATransition>();
        events = new LinkedList<FSAEvent>();
        this.name = name;
        maxStateId = -1;
		maxTransitionId = -1;
		maxEventId = -1;
		id=General.getRandomId();
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    public FSAModel clone(){
        Automaton clone = new Automaton(this.name);
//        clone.setAutomataCompositionList(new String[]{id});
		clone.setId(General.getRandomId());
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

    public String getId()
    {
    	return id;
    }
    
    public void setId(String id)
    {
    	this.id=id;
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getName()
	 */
    public String getName(){
        return name;
    }

    public FSAModel getFSAModel()
    {
    	return this;
    }
    
    public ModelDescriptor getModelDescriptor()
    {
    	return myDescriptor;
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#setName(java.lang.String)
	 */
    public void setName(String name){
        this.name = name;        
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getStateCount()
	 */
    public int getStateCount(){
        return states.size();
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getTransitionCount()
	 */
    public int getTransitionCount(){
        return transitions.size();
    }

    public int getEventCount(){
    	return events.size();
    }
    
    /**
	 * @return event id which is not in use
	 */
	public long getFreeEventId()
	{
		return ++maxEventId;
	}
	
	/**
	 * @return transition id which is not in use
	 */
	public long getFreeTransitionId()
	{
		return ++maxTransitionId;
	}
	
	/**
	 * @return state id which is not in use
	 */
	public long getFreeStateId()
	{
		return ++maxStateId;
	}
	
    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#add(model.fsa.FSAState)
	 */
	public void add(FSAState s){
	    states.add(s); 
	    maxStateId = maxStateId < s.getId() ? s.getId() : maxStateId;
    	fireFSAStructureChanged(new FSAMessage(FSAMessage.ADD,
    			FSAMessage.STATE, s.getId(), this));

	}

	/* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#remove(model.fsa.FSAState)
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
        fireFSAStructureChanged(new FSAMessage(FSAMessage.REMOVE,
    			FSAMessage.STATE, s.getId(), this));
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getStateIterator()
	 */
    public ListIterator<FSAState> getStateIterator(){
        return new StateIterator(states.listIterator(), this);
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getState(int)
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
	 * @see model.fsa.ver2_1.FSAModel#add(model.fsa.FSATransition)
	 */
    public void add(FSATransition t){
        t.getSource().addSourceTransition(t);
        t.getTarget().addTargetTransition(t);
        transitions.add(t);
        maxTransitionId = maxTransitionId < t.getId() ? t.getId() : maxTransitionId;
        fireFSAStructureChanged(new FSAMessage(FSAMessage.ADD,
    			FSAMessage.TRANSITION, t.getId(), this));
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#remove(model.fsa.FSATransition)
	 */
    public void remove(FSATransition t){
        t.getSource().removeSourceTransition(t);
        t.getTarget().removeTargetTransition(t);
        transitions.remove(t);
        fireFSAStructureChanged(new FSAMessage(FSAMessage.REMOVE,
    			FSAMessage.TRANSITION, t.getId(), this));
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getTransition(int)
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
	 * @see model.fsa.ver2_1.FSAModel#getTransitionIterator()
	 */
    public ListIterator<FSATransition> getTransitionIterator(){
        return new TransitionIterator(transitions.listIterator());
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#add(model.fsa.FSAEvent)
	 */
    public void add(FSAEvent e){
        events.add(e);
    	maxEventId = maxEventId < e.getId() ? e.getId() : maxEventId;    	
    	fireFSAEventSetChanged(new FSAMessage(FSAMessage.ADD,
    			FSAMessage.EVENT, e.getId(), this));
    }

    /**
     * Removes <code>event</code> and all transitions fired by it.
     * 
     * @param event  the event to be removed
	 */
    public void remove(FSAEvent event){
        
        Iterator<FSATransition> trans = getTransitionIterator();
        ArrayList<FSATransition> toRemove = new ArrayList<FSATransition>();
        
        while( trans.hasNext()) {
        	FSATransition t = trans.next() ;
        	FSAEvent e = t.getEvent(); 
        	if( event.equals( e ) ) {
        		toRemove.add( t );
        	}
        }
        
        for( FSATransition t : toRemove ) {        
        	transitions.remove( t );
        	fireFSAStructureChanged( new FSAMessage( FSAMessage.REMOVE, 
        			FSAMessage.TRANSITION, t.getId(), this ) );
        }
        
        events.remove(event);
        
    	fireFSAEventSetChanged(new FSAMessage(FSAMessage.REMOVE,
    			FSAMessage.EVENT, event.getId(), this ) );
    }

    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getEventIterator()
	 */
    public ListIterator<FSAEvent> getEventIterator() {
        return new EventIterator( events.listIterator(), this );
    }

    /**
     * TODO Comment!
     */
    public FSAEventSet getEventSet() {
    	return new StupidSetWrapper( events );
    }
    
    /* (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getEvent(int)
	 */
    public FSAEvent getEvent( long id ){
        ListIterator<FSAEvent> ei = events.listIterator();
        while( ei.hasNext() ) {
            FSAEvent e = ei.next();
            if( e.getId() == id ) {
            	return e;
            }
        }
        return null;
    }
    
    /** 
     * A custom list iterator for states. Preserves data integrity.
     * 
     * @author agmi02
     */
    private class StateIterator implements ListIterator<FSAState> {
    	
        private ListIterator<FSAState> sli;
        private FSAState current;
        private FSAModel a;
        
        /**
         * constructs a stateIterator
         * @param sli the listiterator this listiterator shall encapsulate
         * @param a the automaton that is backing the listiterator
         */
        public StateIterator( ListIterator<FSAState> sli, FSAModel a ) {
            this.a = a;
            this.sli = sli;
        }
        
        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return sli.hasNext();
        }
        /**
         * @see java.util.Iterator#next()
         */
        public FSAState next() {
            current = sli.next();
            return current;
        }
        /**
         * @see java.util.ListIterator#hasPrevious()
         */
        public boolean hasPrevious() {
            return sli.hasPrevious();
        }
        /**
         * @see java.util.ListIterator#previous()
         */
        public FSAState previous() {
            current = sli.previous();
            return current;
        }
        /**
         * @see java.util.ListIterator#nextIndex()
         */
        public int nextIndex() {
            return sli.nextIndex();
        }
        /**
         * @see java.util.ListIterator#previousIndex()
         */
        public int previousIndex() {
            return sli.previousIndex();
        }
        
        /** 
         * Removes the element last returned by either next or previous. 
         * Removes the transitions originating from this state and leading
         * to this state in order to maintain data integrity.
         */
        public void remove() {
            ListIterator<FSATransition> sources = current.getSourceTransitionsListIterator();
            while(sources.hasNext()) {
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
    
//    /**
//     * Set the file for this automaton.
//     * @param f the file
//     */
//    public void setFile(File f)
//    {
//    	myFile=f;
//    }
//    
//    /**
//     * Get this automaton's file.
//     * @return
//     */
//    public File getFile()
//    {
//    	return myFile;
//    }
    
//	/**
//	 * Gets the list of ids of the automata of which this automaton is a composition.
//	 * @return the list of ids of the automata of which this automaton is a composition
//	 */
//	public String[] getAutomataCompositionList()
//	{
//		return composedOf;
//	}
//	
//	/**
//	 * Sets the list of ids of the automata of which this automaton is a composition.
//	 * @param list the list of ids of the automata of which this automaton is a composition
//	 */
//	public void setAutomataCompositionList(String[] list)
//	{
//		composedOf=list;
//	}

    public SubElement getMeta()
    {
    	return meta;
    }
    
    public void setMeta(SubElement m)
    {
    	meta=m;
    }
    
	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}
	
	public void setAnnotation(String key, Object annotation)
	{
		annotations.put(key, annotation);
	}
	
	public boolean hasAnnotation(String key)
	{
		return annotations.containsKey(key);
	}
	
	/**
	 * Returns the events disabled at a given state.
	 * If the control map is undefined (e.g., not
	 * computed yet or out of synch with the rest of
	 * the models), this method will return <code>null</code>.
	 * @param state state of the supervisor
	 * @return the events disabled at a given state; or
	 * <code>null</code> if the control map is undefined
	 */
	public FSAEventSet getDisabledEvents(FSAState state)
	{
		if(!states.contains(state))
			return null;
		else
			return (FSAEventSet)state.getAnnotation(Annotable.CONTROL_MAP);
	}
	
	/**
	 * Sets the events disabled at a given state.
	 * @param state state of the supervisor
	 * @param set set of disabled events for this state 
	 */
	public void setDisabledEvents(FSAState state, FSAEventSet set)
	{
		if(!states.contains(state))
			return;
		else
			state.setAnnotation(Annotable.CONTROL_MAP,set);
	}
}