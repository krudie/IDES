package projectModel;

import java.io.PrintStream;
import java.util.*;

/**
 * 
 * @author Axel Gottlieb Michelsen
 * 
 */
public class State extends SubElementContainer{
    private LinkedList<Transition> sourceT, targetT;

    private int id;

    public State(int id){
        this.id = id;
        sourceT = new LinkedList<Transition>();
        targetT = new LinkedList<Transition>();
    }

    public State(State s){
        super(s);
        this.id = s.id;
    }

    public void addSourceTransition(Transition t){
        sourceT.add(t);
    }

    public void removeSourceTransition(Transition t){
        sourceT.remove(t);
    }

    public ListIterator<Transition> getSourceTransitionsListIterator(){
        return sourceT.listIterator();
    }

    public void addTargetTransition(Transition t){
        targetT.add(t);
    }

    public void removeTargetTransition(Transition t){
        targetT.remove(t);
    }

    public ListIterator<Transition> getTargetTransitionListIterator(){
        return targetT.listIterator();
    }

    public int getId(){
        return id;
    }

    
    public void toXML(PrintStream ps, String indent){
        if(isEmpty()) ps.println(indent + "<state" + " id=\"" + id + "\"/>");
        else{
            ps.println(indent + "<state" + " id=\"" + id + "\">");
            super.toXML(ps, indent + "  ");
            ps.println(indent + "</state>");
        }
    }

}
