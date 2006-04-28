package model.fsa.ver1;

import java.util.*;

/** 
 * The topmost class in the model. 
 * It contains a set of automata.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class Project {

    private String name = null;

    private LinkedList<Automaton> automata;

    /**
     * constructs a project with the given name.
     * @param name the name of the project.
     */
    public Project(String name) {
        automata = new LinkedList<Automaton>();
        this.name = name;
    }

    /**
     * adds an automaton to the project.
     * @param a the automaton to add to the project.
     */
    public void addAutomaton(Automaton a) {
        automata.add(a);
    }

    /**
     * returns all automata in the project
     * @return a list containing all automata in the project.
     */
    public LinkedList<Automaton> getAutomata() {
        return automata;
    }

    /**
     * Returns the name of the project.
     * @return the name of the project.
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the project to name.
     * @param name the new name of the project.
     */
    public void setName(String name) {
        this.name = name;
    }


    public Automaton getAutomatonByName(String name) {

        Iterator<Automaton> ai = automata.iterator();
        while (ai.hasNext()) {
            Automaton a = ai.next();
            if ((a != null) && (a.getName().equals(name))) {
                return a;
            }
        }
        return null;
    }

    /**
     * removes an automaton from the project.
     * @param a the automaton that is to be removed.
     */
    public void removeAutomaton(Automaton a) {
        automata.remove(a);
    }
}
