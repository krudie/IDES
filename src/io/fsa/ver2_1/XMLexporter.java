package io.fsa.ver2_1;

import io.IOUtilities;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Vector;

import main.WorkspaceDescriptor;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.Event;
import model.fsa.ver2_1.State;
import model.fsa.ver2_1.Transition;

/**
 * Class for exporting workspaces and automata to xml format and 
 * for saving them to file.
 * @deprecated NEVER USE THIS CLASS! IT IS BEING REMOVED FROM THE CODE
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Helen Bretzke 2006
 */
public class XMLexporter{
    
    private static final String INDENT =  "\t";
    
    /**
     * prints a object to XML.
     * @param wd the workspace descriptor to convert to XML
     * @param ps the printstream this object should be printed to.
     */
    public static void workspaceToXML(WorkspaceDescriptor wd, PrintStream ps) {
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<workspace version=\"2.1\">");
        Vector<String> models=wd.getModels();
        for(int i=0;i<models.size();++i)
        {
        	ps.print("\t<model file=\""+models.elementAt(i)+"\" position=\""+i+"\"");
        	if(i==wd.getSelectedModel())
        		ps.print(" selected=\"true\"");
        	ps.println("/>");
        }
        ps.println("</workspace>");
    }
    
    /**
     * writes an automaton to xml
     * @param a The automaton to make into xml
     * @param ps a printstream that the automaton should be written to.
     */
    public static void automatonToXML(FSAModel a, PrintStream ps){
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<model version=\"2.1\" type=\"FSA\" id=\""+a.getId()+"\">");
        ps.println("<data>");
        ListIterator<FSAState> si = a.getStateIterator();
        while(si.hasNext()){
            stateToXML((State)si.next(), ps, INDENT);            
        }

        ListIterator<FSAEvent> ei = a.getEventIterator();
        while(ei.hasNext()){
            eventToXML((Event)ei.next(),ps, INDENT);
        }

        ListIterator<FSATransition> ti = a.getTransitionIterator();
        while(ti.hasNext()){
            transitionToXML((Transition)ti.next(),ps, INDENT);
        }
        ps.println("</data>");
        ps.println("<meta tag=\"layout\" version=\"2.1\">");
        //TODO: the following has to be refactored
        ps.println("\t<font size=\""+(a.getMeta()==null?12:a.getMeta().getAttribute("size"))+"\"/>");
        si = a.getStateIterator();
        while(si.hasNext()){
            stateLayoutToXML((State)si.next(), ps, INDENT);            
        }

        ti = a.getTransitionIterator();
        while(ti.hasNext()){
            transitionLayoutToXML((Transition)ti.next(),ps, INDENT);
        }
        ps.println("</meta>");
        /*ps.println("<meta tag=\"FSAcomposition\" version=\"2.1\">");
        //TODO: the following has to be refactored
        ps.println("\t<models>");
        for(int i=0;i<a.getAutomataCompositionList().length;++i)
        {
        	ps.println("\t\t<id>"+a.getAutomataCompositionList()[i]+"</id>");
        }
        ps.println("\t</models>");
        si = a.getStateIterator();
        while(si.hasNext()){
        	State s=(State)si.next();
        	ps.println("\t<state id=\""+s.getId()+"\">");
            for(int i=0;i<s.getStateCompositionList().length;++i)
            {
            	ps.println("\t\t<id>"+s.getStateCompositionList()[i]+"</id>");
            }
            ps.println("\t</state>");
        }
        ps.println("</meta>");   */     
        ps.println("</model>");
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
        {
        	SubElement se=see.nextElement();
        	if(!"graphic".equals(se.getName()))
        		subElementToXML(se,ps, indent);
        }
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
            ps.println();
            subElementContainerToXML(se, ps, indent + INDENT);
            if(se.getChars() != null && !se.getChars().trim().equals(""))
                ps.println(indent + INDENT +IOUtilities.encodeForXML(se.getChars()));
            ps.println(indent + "</" + se.getName() + ">");
            return;
        }
        if(se.getChars() != null && !se.getChars().trim().equals(""))
            ps.print(IOUtilities.encodeForXML(se.getChars()));
        ps.println("</" + se.getName() + ">");    
    }
    
    
    /**
     * prints a state in xml
     * @param s the state to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void stateToXML(State s, PrintStream ps,String indent){
//        if(s.isEmpty()) ps.println(indent + "<state" + " id=\"" + s.getId() + "\" />");
//        else{
//            ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
//            subElementContainerToXML(s, ps, indent + INDENT);            
//            ps.println(indent + "</state>");
//        }
    }
    /**
     * prints an event in xml
     * @param e the event to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void eventToXML(Event e, PrintStream ps, String indent){
//        if(e.isEmpty()){
//            ps.println(indent + "<event" + " id=\"" + e.getId() + "\" />");
//        }
//        else{
//            ps.println(indent + "<event" + " id=\"" + e.getId() + "\">");
//            subElementContainerToXML(e, ps, indent + INDENT);
//            ps.println(indent + "</event>");
//        }
    }
    /**
     * prints a transition in xml
     * @param t the transition to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void transitionToXML(Transition t, PrintStream ps, String indent){
//        if(t.isEmpty()){
//            ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\""
//                    + t.getSource().getId() + "\"" + " target=\"" + t.getTarget().getId() + "\""
//                    + ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + " />");
//        }
//        else{
//            ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\""
//                    + t.getSource().getId() + "\"" + " target=\"" + t.getTarget().getId() + "\""
//                    
//                    + ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + ">");
//            subElementContainerToXML(t, ps, indent + INDENT);
//            ps.println(indent + "</transition>");
//        }
    }

    /**
     * prints a state in xml
     * @param s the state to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void stateLayoutToXML(State s, PrintStream ps,String indent){
//    	SubElement ge=s.getSubElement("graphic");
//    	if(ge==null)
//    		return;
//        ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
//        layoutContainerToXML(ge, ps, indent + INDENT);            
//        ps.println(indent + "</state>");
    }
    /**
     * prints a transition in xml
     * @param t the transition to convert 
     * @param ps the printstream to print to 
     * @param indent the indentation to be used in the file
     */ 
    private static void transitionLayoutToXML(Transition t, PrintStream ps, String indent){
//    	SubElement ge=t.getSubElement("graphic");
//    	if(ge==null)
//    		return;
//        ps.println(indent + "<transition" + " id=\"" + t.getId() + "\">");
//        layoutContainerToXML(ge, ps, indent + INDENT);
//        ps.println(indent + "</transition>");
    }
    /**
     * Prints this the subelementcontainer and all subelements of this objects to the
     * printsstream as XML.
     * @param sec the subelementcontainer ro export to xml
     * @param ps the printstream this object should be printet to.
     * @param indent the indentation this object should have.
     */
    private static void layoutContainerToXML(SubElementContainer sec, PrintStream ps, String indent){
        Enumeration<SubElement> see = sec.getSubElements();        
        if(see == null) return;
        while(see.hasMoreElements())
        {
        	SubElement se=see.nextElement();
       		subElementToXML(se,ps, indent);
        }
    }    
}
