package projectModel;

import java.util.*;

/**
 * 
 * @author agmi02
 *
 */
public class Project {
    private LinkedList<Automaton> automata;
    
    public Project(){
        automata = new LinkedList<Automaton>();
    }
    
    public void addAutomaton(Automaton a){
        automata.add(a);
    }
    
    public Iterator<Automaton> getAutomata(){
        return automata.iterator();
    }
}
