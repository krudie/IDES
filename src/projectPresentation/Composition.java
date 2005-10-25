/**
 * 
 */
package projectPresentation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

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
     * Computes the accessible product of the two automata a and b.
     * @param a an automaton
     * @param b an automaton
     * @param product the accesible product of a and b.
     */
    public static void product(Automaton a, Automaton b, Automaton product){
        // Add the intersection between the eventsets as the products
        // eventset.
        int eventNumber = 0;
        ListIterator<Event> eventsa = a.getEventIterator();
        while(eventsa.hasNext()){
            Event eventa = eventsa.next();
            ListIterator<Event> eventsb = b.getEventIterator();
            while(eventsb.hasNext()){
                Event eventb = eventsb.next();
                if(eventa.getSubElement("name").getChars().equals(eventb.getSubElement("name").getChars())){
                    //is this right? Does the new event have the same properties as the old event?
                    Event event = new Event(eventa);
                    event.setId(eventNumber++);
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
            if(initial[0].getSubElement("properties").getSubElement("initial").getChars().equals("true")){
                Iterator<State> sib = b.getStateIterator();
                while(sib.hasNext()){
                    initial[1] = sib.next();
                    if(initial[1].getSubElement("properties").getSubElement("initial").getChars().equals("true")){
                        
                        searchList.add(initial.clone());
                        product.add(makeState(initial, stateNumber));
                        setStateId(initial, stateNumber++);                        
                    }
                }
            }
        }        
        
        //accessibility. All accessible states are added to product.
        //Transitions are only traversible if they can be traversed from both states in sa 
        //firing the same event, i.e., the intersection of the transitions originating from the two
        //states are the transitions of state in product.
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
                    if((t0.getEvent() == null && t1.getEvent() == null || (t0.getEvent() != null && t1.getEvent() != null && t0.getEvent().getSubElement("name").getChars().equals(
                            t1.getEvent().getSubElement("name").getChars())))){
                        
                        Event event = (t0.getEvent() == null )? null : getEventByName(t0.getEvent().getSubElement("name").getChars(), product);                        
                        
                        s[0] = t0.getTarget();
                        s[1] = t1.getTarget();

                        int id = getStateId(s);
                        if(id != -1){
                            product.add(new Transition(transitionNumber++, source, product.getState(id), event));
                        }
                        else{
                            State target = makeState(s, stateNumber);
                            product.add(target);
                            product.add(new Transition(transitionNumber++, source, target));
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                    }
                }
            }
        }
        //tidy up the mess I left.
        ListIterator<State> sli = a.getStateIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("searched");
        }
    }
    
    private static Event getEventByName(String name, Automaton automaton){
        ListIterator<Event> eli = automaton.getEventIterator();
        while(eli.hasNext()){
            Event event = eli.next();
            if(event.getSubElement("name").getChars().equals(name)) return event;
        }
        return null;
    }
    
    private static State makeState(State[] s, int stateNumber){
        State state = new State(stateNumber);
        SubElement name = new SubElement("name");
        name.setChars(s[0].getSubElement("name").getChars() 
                + ", " 
                + s[1].getSubElement("name").getChars());
        state.addSubElement(name);

        SubElement properties = new SubElement("properties");
        SubElement initial = new SubElement("initial");
        initial.setChars(Boolean.toString(s[0].getSubElement("properties").getSubElement("initial").getChars()
                .equals("true")
                && s[1].getSubElement("properties").getSubElement("initial").getChars().equals("true")));
        properties.addSubElement(initial);

        SubElement marked = new SubElement("marked");
        marked.setChars(Boolean.toString(s[0].getSubElement("properties").getSubElement("marked").getChars()
                .equals("true")
                || s[1].getSubElement("properties").getSubElement("marked").getChars().equals("true")));
        properties.addSubElement(marked);
        state.addSubElement(properties);

        return state;
    }
    
    private static void setStateId(State[] s, int stateId){
        if(!s[0].hasSubElement("searched")) 
            s[0].addSubElement(new SubElement("searched"));
        
        s[0].getSubElement("searched").setAttribute(Integer.toString(s[1].getId()), Integer.toString(stateId));        
    }
    
    private static int getStateId(State[] s){
        if(s[0].hasSubElement("searched") && s[0].getSubElement("searched").hasAttribute(Integer.toString(s[1].getId()))){
            return Integer.parseInt(s[0].getSubElement("searched").getAttribute(Integer.toString(s[1].getId())));
        }
        return -1;
    }
}
