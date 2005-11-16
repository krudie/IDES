package projectPresentation;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;

import projectModel.Automaton;
import projectModel.Project;
import projectModel.State;
import projectModel.Event;
import projectModel.SubElement;
import projectModel.SubElementContainer;
import projectModel.Transition;

/**
 * Class for exporting the projects to xml format for saving them on the harddrive
 * 
 * @author Axel Gottlieb Michelsen & Kristian Edlund
 */
public class XMLexporter{
    
    private static final String INDENT =  "  ";
    
    /**
     * prints a object to XML.
     * @param p the project to convert to XML
     * @param ps the printstream this object should be printed to.
     */
    public static void projectToXML(Project p, PrintStream ps) {
        ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        ps.println("<project>");
        Iterator<Automaton> ai = p.getAutomata().iterator();        
        while (ai.hasNext()) {
            Automaton a = ai.next();
            ps.println("  <automaton file=\"" + a.getName() + ".xml\" />");
        }
        ps.println("</project>");
    }
    
    /**
     * writes an automaton to xml
     * @param a The automaton to make into xml
     * @param ps a printstream that the automaton should be written to.
     */
    public static void automatonToXML(Automaton a, PrintStream ps){
        ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        ps.println("<automaton>");
        Iterator<State> si = a.getStateIterator();
        while(si.hasNext()){
            stateToXML(si.next(), ps, INDENT);            
        }

        Iterator<Event> ei = a.getEventIterator();
        while(ei.hasNext()){
            eventToXML(ei.next(),ps, INDENT);
        }

        Iterator<Transition> ti = a.getTransitionIterator();
        while(ti.hasNext()){
            transitionToXML(ti.next(),ps, INDENT);
        }
        ps.println("</automaton>");
    }
    
    /**
     * Prints this the subelementcontainer and all subelements of this objects to the
     * printsstream as XML.
     * @param sec the subelementcontainer ro export to xml
     * @param ps the printstream this object should be printet to.
     * @param indent the indentation this object should have.
     */
    private static void subElementContainerToXML(SubElementContainer sec, PrintStream ps, String indent){
        Enumeration<SubElement> see = sec.getSubElements();        
        if(see == null) return;
        while(see.hasMoreElements())
            subElementToXML(see.nextElement(),ps, indent);
    }
    
    /**
     * prints a subelement in xml
     * @param se the subelement to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */    
    private static void subElementToXML(SubElement se, PrintStream ps, String indent){
        ps.print(indent + "<" + se.getName());
            
        Enumeration<String> av = se.getAttributeValues();
        Enumeration<String> an = se.getAttributeNames();
        
        while(an.hasMoreElements()){
            ps.print(" " + an.nextElement() + "=\"" + av.nextElement() + "\"");
        }

        if(se.isEmpty() && (se.getChars() == null || se.getChars().trim().equals(""))){
            ps.println(" />");
            return;
        }
        ps.print(">");
        
        if(!se.isEmpty()){
            ps.print("\n");
            subElementContainerToXML(se, ps, indent + INDENT);
            if(se.getChars() != null && !se.getChars().trim().equals(""))
                ps.println(indent + INDENT +se.getChars());
            ps.println(indent + "</" + se.getName() + ">");
            return;
        }
        if(se.getChars() != null && !se.getChars().trim().equals(""))
            ps.print(se.getChars());
        ps.print("</" + se.getName() + ">\n");    
    }
    
    
    /**
     * prints a state in xml
     * @param s the state to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void stateToXML(State s, PrintStream ps,String indent){
        if(s.isEmpty()) ps.println(indent + "<state" + " id=\"" + s.getId() + "\" />");
        else{
            ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
            subElementContainerToXML(s, ps, indent + INDENT);            
            ps.println(indent + "</state>");
        }
    }
    /**
     * prints an event in xml
     * @param e the event to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void eventToXML(Event e, PrintStream ps, String indent){
        if(e.isEmpty()){
            ps.println(indent + "<event" + " id=\"" + e.getId() + "\" />");
        }
        else{
            ps.println(indent + "<event" + " id=\"" + e.getId() + "\">");
            subElementContainerToXML(e, ps, indent + INDENT);
            ps.println(indent + "</event>");
        }
    }
    /**
     * prints a transition in xml
     * @param t the transition to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void transitionToXML(Transition t, PrintStream ps, String indent){
        if(t.isEmpty()){
            ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\""
                    + t.getSource().getId() + "\"" + " target=\"" + t.getTarget().getId() + "\""
                    + ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + " />");
        }
        else{
            ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\""
                    + t.getSource().getId() + "\"" + " target=\"" + t.getTarget().getId() + "\""
                    
                    + ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + ">");
            subElementContainerToXML(t, ps, indent + INDENT);
            ps.println(indent + "</transition>");
        }
    }
                
}
