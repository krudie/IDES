package projectModel;

import java.util.*;
import java.io.*;


/**
 * 
 * @author agmi02
 *
 */
public class Project {

    private String name = null;
    
    private LinkedList<Automaton> automata;
    
    public Project(String name){
        automata = new LinkedList<Automaton>();
        this.name = name;
    }
    
    public void addAutomaton(Automaton a){
        automata.add(a);
    }
        
    public LinkedList<Automaton> getAutomata(){
        return automata;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void toXML(PrintStream ps){
        ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        ps.println("<project>");
        Iterator<Automaton> ai = automata.iterator();
        while(ai.hasNext()){
            Automaton a = ai.next();
            ps.println("  <automaton file=\""+a.getName()+".xml\"/>");
        }
        ps.println("</project>");
    }

    
    public Automaton getAutomatonByName(String name){
        Iterator<Automaton> ai = automata.iterator();
        while(ai.hasNext()){
            Automaton a = ai.next();
            if((a != null) && (a.getName().equals(name))){
                return a;
            }
        }
        return null;
    }
    
    public void removeAutomaton(Automaton a){
        automata.remove(a);
    }
}
