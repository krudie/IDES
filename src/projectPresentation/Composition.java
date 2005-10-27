/**
 * 
 */
package projectPresentation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import projectModel.Automaton;
import projectModel.Event;
import projectModel.State;
import projectModel.SubElement;
import projectModel.Transition;

/**
 * @author edlund
 * 
 */
public class Composition{

    /**
     * Takes multiple automata and makes the product of them all
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
     * @param a
     *            an automaton
     * @param b
     *            an automaton
     * @param product
     *            the accesible product of a and b.
     */
    public static void product(Automaton a, Automaton b, Automaton product){
        // Add the intersection between the eventsets as the products eventset.
        ListIterator<Event> eventsa = a.getEventIterator();
        while(eventsa.hasNext()){
            Event eventa = eventsa.next();
            ListIterator<Event> eventsb = b.getEventIterator();
            while(eventsb.hasNext()){
                Event eventb = eventsb.next();
                if(eventa.getSubElement("name").getChars().equals(
                        eventb.getSubElement("name").getChars())){
                    // is this right? Does the new event have the same
                    // properties as the old event?
                    Event event = new Event(eventa);
                    product.add(event);
                    break;
                }
            }
        }

        // find initial states, mark them as reached and add them to the que
        State[] initial = new State[2];
        int stateNumber = 0;
        LinkedList<State[]> searchList = new LinkedList<State[]>();

        Iterator<State> sia = a.getStateIterator();
        while(sia.hasNext()){
            initial[0] = sia.next();
            if(initial[0].getSubElement("properties").hasSubElement("initial")){
                Iterator<State> sib = b.getStateIterator();
                while(sib.hasNext()){
                    initial[1] = sib.next();
                    if(initial[1].getSubElement("properties").hasSubElement("initial")){
                        searchList.add(initial.clone());
                        product.add(makeState(initial, stateNumber));
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
        State[] s = new State[2];
        while(!searchList.isEmpty()){
            State[] sa = searchList.removeFirst();
            State source = product.getState(getStateId(sa));

            ListIterator<Transition> sti0 = sa[0].getSourceTransitionsListIterator();
            while(sti0.hasNext()){
                Transition t0 = sti0.next();
                ListIterator<Transition> sti1 = sa[1].getSourceTransitionsListIterator();
                while(sti1.hasNext()){
                    Transition t1 = sti1.next();
                    if((t0.getEvent() == null && t1.getEvent() == null || (t0.getEvent() != null
                            && t1.getEvent() != null && t0.getEvent().getSubElement("name")
                            .getChars().equals(t1.getEvent().getSubElement("name").getChars())))){

                        Event event = (t0.getEvent() == null) ? null : product.getEvent(t0
                                .getEvent().getId());

                        s[0] = t0.getTarget();
                        s[1] = t1.getTarget();

                        int id = getStateId(s);
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
        // tidy up the mess I left.
        ListIterator<State> sli = a.getStateIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("searched");
        }
    }

    
    /**
     * Takes multiple automata and makes the product of them all
     * @param automata an array of automata
     * @param name The name of the end product of the parallel composition
     * @return The result of the parallel composition
     */    
    public static Automaton parallel(Automaton[] automata, String name){
        
        if(automata.length < 2) return null;
                           
        Automaton prevAnswer= new Automaton("temp");
        Automaton newAnswer;
        
        parallel(automata[0], automata[1],prevAnswer);
        
        
        for(int i=2; i<automata.length; i++){
            newAnswer = new Automaton("temp");
            product(prevAnswer,automata[i], newAnswer);            
            prevAnswer = newAnswer;
        }
        prevAnswer.setName(name);
        return prevAnswer;
    }
    
    /**
     * Computes the accessible parallel composition of the two automata a and b.
     * 
     * @param a
     *            an automaton
     * @param b
     *            an automaton
     * @param product
     *            the accesible product of a and b.
     */
    public static void parallel(Automaton a, Automaton b, Automaton parallel){
        // Add the union of the eventsets as the parallel compositions eventset.
        // mark all events in the intersection as being in the intersection.
        int eventid = 0;
        ListIterator<Event> events = a.getEventIterator();
        while(events.hasNext()){
            Event event = events.next();
            SubElement ref = new SubElement("ref");
            ref.setChars(Integer.toString(eventid));
            event.addSubElement(ref);
            Event temp = new Event(event);
            temp.setId(eventid++);
            parallel.add(temp);
            ref = new SubElement("ref");
            ref.setChars(Integer.toString(event.getId()));
        }

        events = b.getEventIterator();
        while(events.hasNext()){
            Event event = events.next();
            int id = getId(event, parallel);
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
                a.getEvent(Integer.parseInt(parallel.getEvent(id).getSubElement("ref").getChars()))
                        .addSubElement(intersection);
            }
        }

        // find initial states, mark them as reached and add them to the que
        State[] initial = new State[2];
        int stateNumber = 0;
        LinkedList<State[]> searchList = new LinkedList<State[]>();

        Iterator<State> sia = a.getStateIterator();
        while(sia.hasNext()){
            initial[0] = sia.next();
            if(initial[0].getSubElement("properties").hasSubElement("initial")){
                Iterator<State> sib = b.getStateIterator();
                while(sib.hasNext()){
                    initial[1] = sib.next();
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
            State source = parallel.getState(getStateId(sa));

            // add all transitions in sa[0] and sa[1] that
            // aren't in the intersection between E_a and E_b
            for(int i = 0; i < 2; i++){
                ListIterator<Transition> stli = sa[i].getSourceTransitionsListIterator();
                while(stli.hasNext()){
                    Transition t = stli.next();
                    if(t.getEvent() == null || !t.getEvent().hasSubElement("intersection")){
                        Event event = (t.getEvent() == null) ? null : parallel.getEvent(Integer
                                .parseInt(t.getEvent().getSubElement("ref").getChars()));

                        s[(i + 1) % 2] = sa[(i + 1) % 2];
                        s[i] = t.getTarget();

                        int id = getStateId(s);
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

            ListIterator<Transition> sti0 = sa[0].getSourceTransitionsListIterator();
            while(sti0.hasNext()){
                Transition t0 = sti0.next();
                if(t0.getEvent() != null && !t0.getEvent().hasSubElement("intersection")) continue;
                ListIterator<Transition> sti1 = sa[1].getSourceTransitionsListIterator();
                while(sti1.hasNext()){
                    Transition t1 = sti1.next();
                    if(t1.getEvent() != null && !t1.getEvent().hasSubElement("intersection")) continue;
                    if((t0.getEvent() == null && t1.getEvent() == null || (t0.getEvent() != null
                            && t1.getEvent() != null && t0.getEvent().getSubElement("name")
                            .getChars().equals(t1.getEvent().getSubElement("name").getChars())))){

                        Event event = (t0.getEvent() == null) ? null : parallel.getEvent(t0
                                .getEvent().getId());

                        s[0] = t0.getTarget();
                        s[1] = t1.getTarget();

                        int id = getStateId(s);
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
        ListIterator<State> sli = a.getStateIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("searched");
        }
        Automaton[] aa = {a, b, parallel};
        for(int i = 0; i < 3; i++){
            ListIterator<Event> eli = aa[i].getEventIterator();
            while(eli.hasNext()){
                Event e = eli.next();
                e.removeSubElement("intersection");
                e.removeSubElement("ref");
            }
        }
    }
    
    private static LinkedList<State> reachable(LinkedList<State> sll){
        LinkedList<State> result = new LinkedList<State>(sll);
        ListIterator<State> sli= result.listIterator();
        while(sli.hasNext()){
            State s = sli.next();
            ListIterator<Transition> stli = s.getSourceTransitionsListIterator();
            while(stli.hasNext()){
                Transition t = stli.next();
                if(t.getEvent() == null 
                        || !t.getEvent().getSubElement("properties").hasSubElement("observable")
                        && !result.contains(t.getTarget())){
                    result.add(t.getTarget());                        
                }
            }
        }
        Vector<State> sv = new Vector<State>();
        return result;
    }
    
    

    private static int getId(Event e, Automaton a){
        ListIterator<Event> eli = a.getEventIterator();
        while(eli.hasNext()){
            Event temp = eli.next();
            if(temp.getSubElement("name").getChars().equals(e.getSubElement("name").getChars())){
                return temp.getId();
            }
        }
        return -1;
    }

    private static State makeState(State[] s, int stateNumber){
        State state = new State(stateNumber);
        SubElement name = new SubElement("name");
        name.setChars(s[0].getSubElement("name").getChars() + ", "
                + s[1].getSubElement("name").getChars());
        state.addSubElement(name);

        SubElement properties = new SubElement("properties");

        if(s[0].getSubElement("properties").hasSubElement("initial")
                && s[1].getSubElement("properties").hasSubElement("initial")){
            SubElement initial = new SubElement("initial");
            properties.addSubElement(initial);
        }

        if(s[0].getSubElement("properties").hasSubElement("marked")
                && s[1].getSubElement("properties").hasSubElement("marked")){
            SubElement marked = new SubElement("marked");
            properties.addSubElement(marked);
        }
        state.addSubElement(properties);
        return state;
    }

    private static void setStateId(State[] s, int stateId){
        if(!s[0].hasSubElement("searched")) s[0].addSubElement(new SubElement("searched"));

        s[0].getSubElement("searched").setAttribute(Integer.toString(s[1].getId()),
                Integer.toString(stateId));
    }

    private static int getStateId(State[] s){
        if(s[0].hasSubElement("searched")
                && s[0].getSubElement("searched").hasAttribute(Integer.toString(s[1].getId()))){
            return Integer.parseInt(s[0].getSubElement("searched").getAttribute(
                    Integer.toString(s[1].getId())));
        }
        return -1;
    }
}
