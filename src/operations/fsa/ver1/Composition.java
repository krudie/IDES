
package operations.fsa.ver1;

import io.fsa.ver1.SubElement;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import model.fsa.FSAEvent;
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
    	
        // Add the intersection between the eventsets as the products eventset.
        ListIterator<FSAEvent> eventsa = a.getEventIterator();
        while(eventsa.hasNext()){
            FSAEvent eventa = eventsa.next();
            ListIterator<FSAEvent> eventsb = b.getEventIterator();
            while(eventsb.hasNext()){
                FSAEvent eventb = eventsb.next();
                if(eventa.getSymbol().equals(eventb.getSymbol()))
                {
                    //TODO: is this right? Does the new event have the same
                    // properties as the old event?
                    Event event = new Event(eventa);
                    product.add(event);
                    break;
                }
            }
        }

        // find initial states, mark them as reached and add them to the que
        FSAState[] initial = new FSAState[2];
        int stateNumber = 0;
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
        int transitionNumber = 0;
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
                    if((t0.getEvent() == null && t1.getEvent() == null || (t0.getEvent() != null
                            && t1.getEvent() != null && t0.getEvent().getSymbol().equals(t1.getEvent().getSymbol())))){

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
    public static void parallel(Automaton a, Automaton b, Automaton parallel){
        // Add the union of the eventsets as the parallel compositions eventset.
        // mark all events in the intersection as being in the intersection.
        int eventid = 0;
        ListIterator<FSAEvent> events = a.getEventIterator();
        while(events.hasNext()){
            Event event = (Event)events.next();
            SubElement ref = new SubElement("ref");
            ref.setChars(Integer.toString(eventid));
            event.addSubElement(ref);
            Event temp = new Event(event);
            temp.setId(eventid++);
            parallel.add(temp);
        }

        events = b.getEventIterator();
        while(events.hasNext()){
            Event event = (Event)events.next();
            long id = getId(event, a);
            if(id == -1){
                SubElement ref = new SubElement("ref");
                ref.setChars(Integer.toString(eventid));
                event.addSubElement(ref);
                event = new Event(event);
                event.setId(eventid++);
                parallel.add(event);
            }
            else{
                SubElement intersection = new SubElement("intersection");
                event.addSubElement(intersection);
                ((Event)a.getEvent(id)).addSubElement(intersection);
            }
        }

        // find initial states, mark them as reached and add them to the que
        State[] initial = new State[2];
        int stateNumber = 0;
        LinkedList<State[]> searchList = new LinkedList<State[]>();

        Iterator<FSAState> sia = a.getStateIterator();
        while(sia.hasNext()){
            initial[0] = (State)sia.next();
            if(initial[0].getSubElement("properties").hasSubElement("initial")){
                Iterator<FSAState> sib = b.getStateIterator();
                while(sib.hasNext()){
                    initial[1] = (State)sib.next();
                    if(initial[1].getSubElement("properties").hasSubElement("initial")){
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
        int transitionNumber = 0;
        State[] s = new State[2];
        while(!searchList.isEmpty()){
            State[] sa = searchList.removeFirst();
            State source = (State)parallel.getState(getStateId(sa));

            // add all transitions in sa[0] and sa[1] that
            // aren't in the intersection between E_a and E_b
            for(int i = 0; i < 2; i++){
                ListIterator<FSATransition> stli = sa[i].getSourceTransitionsListIterator();
                while(stli.hasNext()){
                    Transition t = (Transition)stli.next();
                    if(t.getEvent() == null || !((Event)t.getEvent()).hasSubElement("intersection")){
                        Event event = (t.getEvent() == null) ? null : (Event)parallel.getEvent(Integer
                                .parseInt(((Event)t.getEvent()).getSubElement("ref").getChars()));

                        s[(i + 1) % 2] = sa[(i + 1) % 2];
                        s[i] = (State)t.getTarget();

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
                Transition t0 = (Transition)sti0.next();
                if(t0.getEvent() != null && !((Event)t0.getEvent()).hasSubElement("intersection")) continue;
                ListIterator<FSATransition> sti1 = sa[1].getSourceTransitionsListIterator();
                while(sti1.hasNext()){
                    Transition t1 = (Transition)sti1.next();
                    if(t1.getEvent() != null && !((Event)t1.getEvent()).hasSubElement("intersection")) continue;
                    if((t0.getEvent() == null && t1.getEvent() == null || (t0.getEvent() != null
                            && t1.getEvent() != null && ((Event)t0.getEvent()).getSubElement("name")
                            .getChars().equals(((Event)t1.getEvent()).getSubElement("name").getChars())))){

                        Event event = (t0.getEvent() == null) ? null : (Event)parallel.getEvent(Integer
                                .parseInt(((Event)t0.getEvent()).getSubElement("ref").getChars()));

                        s[0] = (State)t0.getTarget();
                        s[1] = (State)t1.getTarget();

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
        // tidy up the mess I left.
        ListIterator<FSAState> sli = a.getStateIterator();
        while(sli.hasNext()){
            ((State)sli.next()).removeSubElement("searched");
        }
        Automaton[] aa = {a, b};
        for(int i = 0; i < aa.length; i++){
            ListIterator<FSAEvent> eli = aa[i].getEventIterator();
            while(eli.hasNext()){
                Event e = (Event)eli.next();
                e.removeSubElement("intersection");
                e.removeSubElement("ref");
            }
        }
    }

    /**
     * Computes an observer for the non-deterministic automaton P(a), i.e., the
     * automaton where all unobservable events have been replaced with the empty
     * event, epsilon.
     * 
     * @param a a non-deterministic automaton
     * @param observer the output, a deterministic observer of the automaton a.
     */
    public static void observer(Automaton a, Automaton observer){
        ListIterator<FSAEvent> eli = a.getEventIterator();
        while(eli.hasNext()){
            Event e = (Event)eli.next();
            if(e.getSubElement("properties").hasSubElement("observable")) observer
                    .add(new Event(e));
        }

        LinkedList<LinkedList<State>> searchList = new LinkedList<LinkedList<State>>();
        int id = 0, transitionid = 0;

        // find initial states, mark them as reached and add them to the
        // searchlist
        LinkedList<State> states = new LinkedList<State>();
        Iterator<FSAState> sia = a.getStateIterator();
        while(sia.hasNext()){
            State initial = (State)sia.next();
            if(initial.getSubElement("properties").hasSubElement("initial")){
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

        states = new LinkedList<State>();

        // find the accesible states in the observer.
        while(!searchList.isEmpty()){
            LinkedList<State> sourceList = searchList.remove();
            source = (State)observer.getState(Integer.parseInt(isIn(sourceList)));
            eli = a.getEventIterator();
            while(eli.hasNext()){
                Event event = (Event)eli.next();
                if(!event.getSubElement("properties").hasSubElement("observable")) continue;
                ListIterator<State> sli = sourceList.listIterator();
                while(sli.hasNext()){
                    State s = sli.next();
                    ListIterator tli = s.getSourceTransitionsListIterator();
                    while(tli.hasNext()){
                        Transition t = (Transition)tli.next();
                        if(t.getEvent() == event && !states.contains(t.getTarget())){
                            states.add((State)t.getTarget());
                        }
                    }
                }
                if(!states.isEmpty()){
                    unobservableReach(states);
                    sort(states);
                    String stateid = isIn(states);
                    if(stateid == null){
                        target = makeState(states, id, false);
                        setIn(states, id++);
                        observer.add(target);
                        searchList.add(states);
                    }
                    else {
                    	target = (State)observer.getState(Integer.parseInt(stateid));
                    }
                    event = (event == null) ? null : (Event)observer.getEvent(event.getId());
                    Transition t = new Transition(transitionid++, source, target, event);
                    observer.add(t);
                    states = new LinkedList<State>();
                }
            }
        }
        // clean
        sia = a.getStateIterator();
        while(sia.hasNext()){
            ((State)sia.next()).removeSubElement("in");
        }
    }

    /**
     * Private function  to check if a stateset is in the observer
     * @param sll the stateset to check
     * @return null if it is not in, else returns the id of the observer state 
     */
    private static String isIn(LinkedList<State> sll){
        if(sll.isEmpty() || !sll.peek().hasSubElement("in")) return null;
        return sll.peek().getSubElement("in").getAttribute(id(sll));
    }

    /**
     * private function for setting an in subelement a state
     * @param sll the stateset to set a new id in
     * @param n the new id
     */
    private static void setIn(LinkedList<State> sll, int n){
        if(sll.isEmpty()) return;
        State s = sll.peek();
        if(!s.hasSubElement("in")) s.addSubElement(new SubElement("in"));
        s.getSubElement("in").setAttribute(id(sll), Integer.toString(n));
    }

    /**
     * Makes a string of the ides in a stateset
     * @param sll the stateset to compile a string from
     * @return the id string . seperated
     */
    private static String id(LinkedList<State> sll){
        ListIterator<State> sli = sll.listIterator();
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
    private static State makeState(LinkedList<State> sll, int id, boolean initial){
        ListIterator<State> sli = sll.listIterator();
        State s, rs;
        s = sli.next();
        boolean marked = s.getSubElement("properties").hasSubElement("marked");
        String name = "{" + s.getSubElement("name").getChars();

        while(sli.hasNext()){
            s = sli.next();
            marked |= s.getSubElement("properties").hasSubElement("marked");
            name += ", " + s.getSubElement("name").getChars();
        }
        name += "}";
        rs = new State(id);
        SubElement sname = new SubElement("name");
        rs.addSubElement(sname);
        sname.setChars(name);
        SubElement properties = new SubElement("properties");
        rs.addSubElement(properties);
        if(marked) properties.addSubElement(new SubElement("marked"));
        if(initial) properties.addSubElement(new SubElement("initial"));
        return rs;
    }

    /**
     * Sorting algorithm for sorting a list of states after id.
     * Right now it uses bublesort
     * @param sll the list of states to sort
     */
    private static void sort(LinkedList<State> sll){
        if(sll.size() < 2)
        ;
        else if(sll.size() == 2){
            State s1 = sll.getFirst();
            State s2 = sll.getLast();
            if(s1.getId() <= s2.getId()) return;
            else{
                sll.clear();
                sll.addFirst(s2);
                sll.addLast(s1);
            }
        }
        else{
            LinkedList<State> l1 = new LinkedList<State>(sll.subList(0, sll.size() / 2));
            LinkedList<State> l2 = new LinkedList<State>(sll.subList(sll.size() / 2, sll.size()));
            sort(l1);
            sort(l2);
            sll.clear();
            while(!l1.isEmpty() || !l2.isEmpty()){
                if(l1.isEmpty()) sll.addLast(l2.removeFirst());
                else if(l2.isEmpty()) sll.addLast(l1.removeFirst());
                else if(l1.peek().getId() <= l2.peek().getId()) sll.addLast(l1.removeFirst());
                else sll.addLast(l2.removeFirst());
            }
        }
    }

    /**
     * calculates the unoberservable reach for a stateset
     * 
     * @param sll the stateset to use.
     */
    private static void unobservableReach(LinkedList<State> sll){
        ListIterator<State> sli = sll.listIterator();
        while(sli.hasNext()){
            sli.next().addSubElement(new SubElement("reached"));
        }
        sli = sll.listIterator();
        while(sli.hasNext()){
            State s = sli.next();
            ListIterator stli = s.getSourceTransitionsListIterator();
            while(stli.hasNext()){
                Transition t = (Transition)stli.next();
                if((t.getEvent() == null || !((Event)t.getEvent()).getSubElement("properties")
                        .hasSubElement("observable"))
                        && !((State)t.getTarget()).hasSubElement("reached")){
                    ((State)t.getTarget()).addSubElement(new SubElement("reached"));
                    sli.add((State)t.getTarget());
                    sli.previous();
                }
            }
        }
        sli = sll.listIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("reached");
        }
    }

    /**
     * Gets the id for an event in another automaton. It is used for finding an event id based on the event name.
     * 
     * @param e the event to search for
     * @param a the automaton to search in
     * @return returns -1 if it couldn't find the event, else returns the id of the event in automaton a
     */
    private static long getId(Event e, Automaton a){
        ListIterator eli = a.getEventIterator();
        while(eli.hasNext()){
            Event temp = (Event)eli.next();
            if(temp.getSubElement("name").getChars().equals(e.getSubElement("name").getChars())){
                return temp.getId();
            }
        }
        return -1;
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
