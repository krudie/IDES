package projectModel;

import java.util.*;

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
    
    public Iterator<Automaton> getAutomata(){
        return automata.iterator();
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
}
