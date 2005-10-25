/**
 * 
 */
package projectPresentation;

import java.util.ListIterator;

import projectModel.Automaton;
import projectModel.Event;
import projectModel.State;
import projectModel.SubElement;

/**
 * @author edlund
 *
 */
public class HelpFunctions{
    
    
    static Event getEventByName(String name, Automaton automaton){
        ListIterator<Event> eli = automaton.getEventIterator();
        while(eli.hasNext()){
            Event event = eli.next();
            if(event.getSubElement("name").getChars().equals(name)) return event;
        }
        return null;
    }
    
    static State makeState(State[] s, int stateNumber){
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
    
    static void setStateId(State[] s, int stateId){
        if(!s[0].hasSubElement("searched")) 
            s[0].addSubElement(new SubElement("searched"));
        
        s[0].getSubElement("searched").setAttribute(Integer.toString(s[1].getId()),
                Integer.toString(stateId));
        
        if(!s[1].hasSubElement("searched"))
        s[1].addSubElement(new SubElement("searched"));
        s[1].getSubElement("searched").setAttribute(Integer.toString(s[0].getId()),
                Integer.toString(stateId));
    }
    
    static int getStateId(State[] s){
        if(s[0].hasSubElement("searched")
                && s[0].getSubElement("searched").hasAttribute(Integer.toString(s[1].getId()))){
            return Integer.parseInt(s[0].getSubElement("searched").getAttribute(
                    Integer.toString(s[1].getId())));
        }
        return -1;
    }
    
    
}
