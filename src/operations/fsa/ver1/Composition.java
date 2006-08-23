
package operations.fsa.ver1;

import io.fsa.ver1.SubElement;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;


/**
 * This class contains methods for composing new automata from existing automata.
 * @author Kristian Edlund 
 * @author Axel Gottlieb Michelsen
 * @author Lenko Grigorov
 */
public class Composition{
	
	/**
	 * To be used to store the ids of pairs of states
	 */
	protected static Map<String,Long> pairIds=new TreeMap<String,Long>(); 

    /**
     * Takes multiple automata and makes the product of them all
     * 
     * @param automata an array of automata
     * @param name The name of the end product
     * @return The result of the product
     */
    public static Automaton product(Automaton[] automata, String name){

        if(automata.length < 2) return null;

        Automaton prevAnswer = new Automaton("temp");
        Automaton newAnswer;

        product(automata[0], automata[1], prevAnswer);

        for(int i = 2; i < automata.length; i++){
            newAnswer = new Automaton("temp");
            product(prevAnswer, automata[i], newAnswer);

            prevAnswer = newAnswer;
        }
        prevAnswer.setName(name);
        return prevAnswer;
    }

    /**
     * Computes the accessible product of the two automata a and b.
     * 
     * @param a an automaton
     * @param b an automaton
     * @param product the accesible product of a and b.
     */
    public static void product(FSAModel a, FSAModel b, Automaton product){
    	
    	product.setAutomataCompositionList(new String[]{a.getId(),b.getId()});

    	//long eventid = 0;
    	
        // Add the intersection between the eventsets as the products eventset.
        ListIterator<FSAEvent> eventsa = a.getEventIterator();
        while(eventsa.hasNext()){
            FSAEvent eventa = eventsa.next();
            ListIterator<FSAEvent> eventsb = b.getEventIterator();
            while(eventsb.hasNext()){
                FSAEvent eventb = eventsb.next();
                if(eventa.equals(eventb))
                {
                    //TODO: is this right? Does the new event have the same
                    // properties as the old event?
                    Event event = new Event(eventa);
                    //event.setId(eventid++);
                    product.add(event);
                    break;
                }
            }
        }

        // find initial states, mark them as reached and add them to the que
        FSAState[] initial = new FSAState[2];
        long stateNumber = 0;
        LinkedList<FSAState[]> searchList = new LinkedList<FSAState[]>();

        Iterator<FSAState> sia = a.getStateIterator();
        while(sia.hasNext()){
            initial[0] = sia.next();
            if(initial[0].isInitial()){
                Iterator<FSAState> sib = b.getStateIterator();
                while(sib.hasNext()){
                    initial[1] = (State)sib.next();
                    if(initial[1].isInitial()){
                        searchList.add(initial.clone());
                        product.add(makeState(initial,stateNumber));
                        setStateId(initial, stateNumber++);
                    }
                }
            }
        }

        // accessibility. All accessible states are added to product.
        // Transitions are only traversible if they can be traversed from both
        // states in sa
        // firing the same event, i.e., the intersection of the transitions
        // originating from the two
        // states are the transitions of state in product.
        long transitionNumber = 0;
        FSAState[] s = new FSAState[2];
        while(!searchList.isEmpty()){
            FSAState[] sa = searchList.removeFirst();
            FSAState source = product.getState(getStateId(sa));

            ListIterator<FSATransition> sti0 = sa[0].getSourceTransitionsListIterator();
            while(sti0.hasNext()){
                FSATransition t0 = sti0.next();
                ListIterator<FSATransition> sti1 = sa[1].getSourceTransitionsListIterator();
                while(sti1.hasNext()){
                    FSATransition t1 = sti1.next();
                    if((t0.getEvent() == null && t1.getEvent() == null) || (t0.getEvent() != null
                            && t1.getEvent() != null && t0.getEvent().equals(t1.getEvent()))){

                        FSAEvent event = (t0.getEvent() == null) ? null : product.getEvent(t0
                                .getEvent().getId());

                        s[0] = (State)t0.getTarget();
                        s[1] = (State)t1.getTarget();

                        long id = getStateId(s);
                        if(id != -1){
                            product.add(new Transition(transitionNumber++, source, product
                                    .getState(id), event));
                        }
                        else{
                            State target = makeState(s, stateNumber);
                            product.add(target);
                            product.add(new Transition(transitionNumber++, source, target, event));
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                    }
                }
            }
        }

        pairIds.clear();
    }

    /**
     * Takes multiple automata and makes the product of them all
     * 
     * @param automata an array of automata
     * @param name The name of the end product of the parallel composition
     * @return The result of the parallel composition
     */
    public static Automaton parallel(Automaton[] automata, String name){

        if(automata.length < 2) return null;

        Automaton prevAnswer = new Automaton("temp");
        Automaton newAnswer;

        parallel(automata[0], automata[1], prevAnswer);

        for(int i = 2; i < automata.length; i++){
            newAnswer = new Automaton("temp");
            parallel(prevAnswer, automata[i], newAnswer);
            prevAnswer = newAnswer;
        }
        prevAnswer.setName(name);
        return prevAnswer;
    }

    /**
     * Computes the accessible parallel composition of the two automata a and b.
     * 
     * @param a an automaton
     * @param b an automaton
     * @param parallel a pointer to the result for the accesible parallel product of a and b.
     */
    public static void parallel(FSAModel a, FSAModel b, Automaton parallel){

    	parallel.setAutomataCompositionList(new String[]{a.getId(),b.getId()});

    	// Add the union of the eventsets as the parallel compositions eventset.
        // mark all events in the intersection as being in the intersection.
        long eventid = 0;
        
        //key=event from original automata,value=correpsponding new event in result
        HashMap<FSAEvent,Event> events=new HashMap<FSAEvent,Event>();
        //key=new event,value=the two original events that intersect
        HashSet<Event> intersection=new HashSet<Event>();

        ListIterator<FSAEvent> it = a.getEventIterator();
        while(it.hasNext()){
            FSAEvent event = it.next();
            Event temp = new Event(event);
            temp.setId(eventid++);
            parallel.add(temp);
            events.put(event,temp);
        }

        it = b.getEventIterator();
        while(it.hasNext()){
            FSAEvent eventb = it.next();
            FSAEvent eventa = getId(eventb, a);
            if(eventa==null){
                Event temp = new Event(eventb);
                temp.setId(eventid++);
                parallel.add(temp);
                events.put(eventb,temp);
            }
            else{
            	Event e=events.get(eventa);
            	intersection.add(e);
            	events.put(eventb,e);
            }
        }

        // find initial states, mark them as reached and add them to the que
        FSAState[] initial = new FSAState[2];
        long stateNumber = 0;
        LinkedList<FSAState[]> searchList = new LinkedList<FSAState[]>();

        Iterator<FSAState> sia = a.getStateIterator();
        while(sia.hasNext()){
            initial[0] = sia.next();
            if(initial[0].isInitial()){
                Iterator<FSAState> sib = b.getStateIterator();
                while(sib.hasNext()){
                    initial[1] = sib.next();
                    if(initial[1].isInitial()){
                        searchList.add(initial.clone());
                        parallel.add(makeState(initial, stateNumber));
                        setStateId(initial, stateNumber++);
                    }
                }
            }
        }

        // accessibility. All accessible states are added to parallel.
        // Transitions are traversible if they can be traversed from both
        // states in sa firing the same event, i.e., the intersection of the
        // transitions
        // originating from the two
        // states are the transitions of state in product, or if the event
        // firing the transition isn't in the intersection between E_a and E_b.
        long transitionNumber = 0;
        FSAState[] s = new FSAState[2];
        while(!searchList.isEmpty()){
            FSAState[] sa = searchList.removeFirst();
            FSAState source = parallel.getState(getStateId(sa));

            // add all transitions in sa[0] and sa[1] that
            // aren't in the intersection between E_a and E_b
            for(int i = 0; i < 2; i++){
                ListIterator<FSATransition> stli = sa[i].getSourceTransitionsListIterator();
                while(stli.hasNext()){
                    FSATransition t = stli.next();
                    if(t.getEvent() == null || !intersection.contains(events.get(t.getEvent()))){
                        Event event = (t.getEvent() == null) ? null : events.get(t.getEvent());

                        s[(i + 1) % 2] = sa[(i + 1) % 2];
                        s[i] = t.getTarget();

                        long id = getStateId(s);
                        if(id != -1){
                            parallel.add(new Transition(transitionNumber++, source, parallel
                                    .getState(id), event));
                        }
                        else{
                            State target = makeState(s, stateNumber);
                            parallel.add(target);
                            parallel.add(new Transition(transitionNumber++, source, target, event));
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                    }
                }
            }

            ListIterator<FSATransition> sti0 = sa[0].getSourceTransitionsListIterator();
            while(sti0.hasNext()){
                FSATransition t0 = sti0.next();
                if(t0.getEvent() != null && !intersection.contains(events.get(t0.getEvent()))) continue;
                ListIterator<FSATransition> sti1 = sa[1].getSourceTransitionsListIterator();
                while(sti1.hasNext()){
                    FSATransition t1 = sti1.next();
                    if(t1.getEvent() != null && !intersection.contains(events.get(t1.getEvent()))) continue;
                    //System.out.println(""+t0.getEvent()+", "+t1.getEvent()+". "+)
                    if((t0.getEvent() == null && t1.getEvent() == null) || (t0.getEvent() != null
                            && t1.getEvent() != null && t0.getEvent().equals(t1.getEvent()))){

                        Event event = (t0.getEvent() == null) ? null : events.get(t0.getEvent());

                        s[0] = t0.getTarget();
                        s[1] = t1.getTarget();

                        long id = getStateId(s);
                        if(id != -1){
                            parallel.add(new Transition(transitionNumber++, source, parallel
                                    .getState(id), event));
                        }
                        else{
                            State target = makeState(s, stateNumber);
                            parallel.add(target);
                            parallel.add(new Transition(transitionNumber++, source, target, event));
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                    }
                }
            }
        }
        
        pairIds.clear();
    }

    /**
     * Computes an observer for the non-deterministic automaton P(a), i.e., the
     * automaton where all unobservable events have been replaced with the empty
     * event, epsilon.
     * 
     * @param a a non-deterministic automaton
     * @param observer the output, a deterministic observer of the automaton a.
     */
    public static void observer(FSAModel a, Automaton observer){
    	
    	observer.setAutomataCompositionList(new String[]{a.getId()});

    	//long eventid=0;
    	
    	ListIterator<FSAEvent> eli = a.getEventIterator();
        while(eli.hasNext()){
            FSAEvent e = eli.next();
            if(e.isObservable())
            {
            	Event event=new Event(e);
            	//event.setId(eventid++);
            	observer.add(event);
            }
        }

        LinkedList<LinkedList<FSAState>> searchList = new LinkedList<LinkedList<FSAState>>();
        long id = 0, transitionid = 0;

        // find initial states, mark them as reached and add them to the
        // searchlist
        LinkedList<FSAState> states = new LinkedList<FSAState>();
        Iterator<FSAState> sia = a.getStateIterator();
        while(sia.hasNext()){
            FSAState initial = sia.next();
            if(initial.isInitial()){
                states.add(initial);
            }
        }
        if(states.size() == 0) return;
        unobservableReach(states);
        sort(states);
        State rState = makeState(states, id, true);
        setIn(states, id++);

        observer.add(rState);
        searchList.add(states);
        State target, source;

        states = new LinkedList<FSAState>();

        // find the accesible states in the observer.
        while(!searchList.isEmpty()){
            LinkedList<FSAState> sourceList = searchList.remove();
            source = (State)observer.getState(isIn(sourceList));
            eli = a.getEventIterator();
            while(eli.hasNext()){
                FSAEvent event = eli.next();
                if(!event.isObservable()) continue;
                ListIterator<FSAState> sli = sourceList.listIterator();
                while(sli.hasNext()){
                    FSAState s = sli.next();
                    ListIterator<FSATransition> tli = s.getSourceTransitionsListIterator();
                    while(tli.hasNext()){
                        FSATransition t = tli.next();
                        if(t.getEvent().equals(event) && !states.contains(t.getTarget())){
                            states.add(t.getTarget());
                        }
                    }
                }
                if(!states.isEmpty()){
                    unobservableReach(states);
                    sort(states);
                    long stateid = isIn(states);
                    if(stateid < 0){
                        target = makeState(states, id, false);
                        setIn(states, id++);
                        observer.add(target);
                        searchList.add(states);
                    }
                    else {
                    	target = (State)observer.getState(stateid);
                    }
                    event = (event == null) ? null : observer.getEvent(event.getId());
                    Transition t = new Transition(transitionid++, source, target, event);
                    observer.add(t);
                    states = new LinkedList<FSAState>();
                }
            }
        }

        pairIds.clear();
    }

    /**
     * Private function  to check if a stateset is in the observer
     * @param sll the stateset to check
     * @return null if it is not in, else returns the id of the observer state 
     */
    private static long isIn(LinkedList<FSAState> sll){
    	Long id=pairIds.get(id(sll));
    	if(id==null)
    		return -1;
    	else
    		return id.longValue();
    }

    /**
     * private function for setting an in subelement a state
     * @param sll the stateset to set a new id in
     * @param n the new id
     */
    private static void setIn(LinkedList<FSAState> sll, long n){
    	pairIds.put(id(sll),new Long(n));
    }

    /**
     * Makes a string of the ides in a stateset
     * @param sll the stateset to compile a string from
     * @return the id string . seperated
     */
    private static String id(LinkedList<FSAState> sll){
        ListIterator<FSAState> sli = sll.listIterator();
        String name = "";
        while(sli.hasNext()){
            name += sli.next().getId() + ".";
        }
        return name;
    }

    /**
     * Private function for making a new state from a stateset
     * 
     * @param sll the state set to make a new state from
     * @param id the id it should use for the new state
     * @param initial sets the state as initial if needed
     * @return the newly created state
     */
    private static State makeState(LinkedList<FSAState> sll, long id, boolean initial){
        ListIterator<FSAState> sli = sll.listIterator();
        FSAState s;
        State rs;
        boolean marked = false;
        int cId=0;
        long[] compositionIds=new long[sll.size()];

        while(sli.hasNext()){
            s = sli.next();
            marked |= s.isMarked();
            compositionIds[cId++]=s.getId();
        }

        rs = new State(id);
        rs.setMarked(marked);
        rs.setInitial(initial);
        rs.setStateCompositionList(compositionIds);
        return rs;
    }

    /**
     * Sorting algorithm for sorting a list of states after id.
     * Right now it uses bublesort
     * @param sll the list of states to sort
     */
    private static void sort(LinkedList<FSAState> sll){
    	Collections.sort(sll,new Comparator<FSAState>()
    			{
    				public int compare(FSAState s1, FSAState s2)
    				{
    					return (int)Math.signum(s1.getId()-s2.getId());
    				}
    			});
//        if(sll.size() < 2)
//        ;
//        else if(sll.size() == 2){
//            FSAState s1 = sll.getFirst();
//            FSAState s2 = sll.getLast();
//            if(s1.getId() <= s2.getId()) return;
//            else{
//                sll.clear();
//                sll.addFirst(s2);
//                sll.addLast(s1);
//            }
//        }
//        else{
//            LinkedList<State> l1 = new LinkedList<State>(sll.subList(0, sll.size() / 2));
//            LinkedList<State> l2 = new LinkedList<State>(sll.subList(sll.size() / 2, sll.size()));
//            sort(l1);
//            sort(l2);
//            sll.clear();
//            while(!l1.isEmpty() || !l2.isEmpty()){
//                if(l1.isEmpty()) sll.addLast(l2.removeFirst());
//                else if(l2.isEmpty()) sll.addLast(l1.removeFirst());
//                else if(l1.peek().getId() <= l2.peek().getId()) sll.addLast(l1.removeFirst());
//                else sll.addLast(l2.removeFirst());
//            }
//        }
    }

    /**
     * calculates the unoberservable reach for a stateset
     * 
     * @param sll the stateset to use.
     */
    private static void unobservableReach(LinkedList<FSAState> sll){
        ListIterator<FSAState> sli = sll.listIterator();
        HashSet<FSAState> reached=new HashSet<FSAState>();
        while(sli.hasNext()){
            reached.add(sli.next());
        }
        sli = sll.listIterator();
        while(sli.hasNext()){
            FSAState s = sli.next();
            ListIterator<FSATransition> stli = s.getSourceTransitionsListIterator();
            while(stli.hasNext()){
                FSATransition t = stli.next();
                if((t.getEvent() == null || !t.getEvent().isObservable())
                        && !reached.contains(t.getTarget())){
                    reached.add(t.getTarget());
                    sli.add(t.getTarget());
                    sli.previous();
                }
            }
        }
    }

    /**
     * Gets the id for an event in another automaton. It is used for finding an event id based on the event name.
     * 
     * @param e the event to search for
     * @param a the automaton to search in
     * @return returns -1 if it couldn't find the event, else returns the id of the event in automaton a
     */
    private static FSAEvent getId(FSAEvent e, FSAModel a){
        ListIterator eli = a.getEventIterator();
        while(eli.hasNext()){
            FSAEvent temp = (FSAEvent)eli.next();
            if(temp.equals(e)){
                return temp;
            }
        }
        return null;
    }

    /**
     * Private function for making a new state from a stateset
     * @param s the stateset to make a new state from
     * @param stateNumber the id of the new state
     * @return the newly created state
     */
    private static State makeState(FSAState[] s, long stateNumber){
        State state = new State(stateNumber);
//        SubElement name = new SubElement("name");
//        name.setChars("(" + s[0].getSubElement("name").getChars() + ", "
//                + s[1].getSubElement("name").getChars() + ")");
//        state.addSubElement(name);

//        SubElement properties = new SubElement("properties");

        state.setStateCompositionList(
        		new long[]{s[0].getId(),s[1].getId()});
        
        if(s[0].isInitial() && s[1].isInitial())
        	state.setInitial(true);

        if(s[0].isMarked() && s[1].isMarked())
        	state.setMarked(true);
        return state;
    }

    /**
     * set the stateid for a set of states 
     * @param s the stateset
     * @param stateId the id to set
     */
    private static void setStateId(FSAState[] s, long stateId){
    	pairIds.put(""+s[0].getId()+","+s[1].getId(),new Long(stateId));
    }

    /**
     * Gets the id from a set of states
     * 
     * @param s the stateset
     * @return the id of the stateset
     */
    private static long getStateId(FSAState[] s){
    	String key=""+s[0].getId()+","+s[1].getId();
        if(pairIds.containsKey(key))
            return pairIds.get(key).longValue();
        return -1;
    }
}
