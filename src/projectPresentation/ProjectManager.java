/**
 * 
 */
package projectPresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import projectModel.*;

/**
 * @author edlund
 * 
 */
public class ProjectManager implements ProjectPresentation{

    private Project project = null;

    private boolean unsaved = false;

    
    /**
     * Creates a new project
     * @param name The name of the new project
     */
    public void newProject(String name){
        project = new Project(name);
        unsaved = true;
    }
  

   /**
    * A check to see there is an open project
    * @return true if the project is open
    */   
    public boolean isProjectOpen(){
        return (project != null);
    }
  
    /**
     *  Changes the open project' name
     *  @param name the new name of the project
     */
    public void setProjectName(String name){
        if(project != null){
            project.setName(name);
        }
        unsaved = true;
    }

    /**
     * Gets the project name
     * @return the project name
     */   
    public String getProjectName(){
        if(project != null){
            return project.getName();
        }
        return null;
    }

    /**
     *  Opens a project
     *  @param file the filename of the project file to open
     *  @return the parsing error
     */    
    public String openProject(File file){
        ProjectParser pp = new ProjectParser();

        project = pp.parse(file);
        return pp.getParsingErrors();
    }
    
    
    /**
     * Open an automaton
     * @param file The file to open
     * @param name The name for the automaton
     * @return The parsing errors from parsing the automaton file
     */
    public String openAutomaton(File file, String name){
        AutomatonParser ap = new AutomatonParser();
        Automaton automaton = ap.parse(file);
        automaton.setName(name);
        project.addAutomaton(automaton);
        return ap.getParsingErrors();

    }

    /**
     * Get a list of automaton names
     * @return an array of automaton names in the project
     */    
    public String[] getAutomataNames(){
        LinkedList<Automaton> al = project.getAutomata();
        String[] sa = new String[al.size()];
        Iterator<Automaton> ai = al.iterator();
        int i = 0;
        while(ai.hasNext()){
            Automaton a = ai.next();
            if(a != null){
                sa[i++] = a.getName();
            }
            else i++;
        }
        return sa;
    }

    
    /**
     * changes the name of an automaton
     * @param oldname The name of the automaton to changes name of
     * @param newName the new name for the automaton
     */    
    public void setAutomatonName(String oldName, String newName){
        project.getAutomatonByName(oldName).setName(newName);
        unsaved = true;
    }
        
    private PrintStream getPrintStream(File file){
        PrintStream ps = null;
        if(!file.exists()){
            try{
                file.createNewFile();
            }
            catch(IOException ioe){
                System.err.println("ProjectManager: unable to create file, message: "
                        + ioe.getMessage());
                return null;
            }
        }
        if(!file.isFile()){
            System.err.println("ProjectManager: " + file.getName() + " is no file. ");
            return null;
        }
        if(!file.canWrite()){
            System.err.println("ProjectManager: can not write to file: " + file.getName());
            return null;
        }
        try{
            ps = new PrintStream(file);
        }
        catch(FileNotFoundException fnfe){
            System.out.println("ProjectManager: file disapeared, message: " + fnfe.getMessage());
            return null;
        }
        return ps;
    }

    /**
     * Function for saving the project to a file
     * @param path The path where the project should be saved
     */    
    public void saveProject(String path){
        File file = new File(path, project.getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
        project.toXML(ps);
        Iterator<Automaton> ai = project.getAutomata().iterator();
        while(ai.hasNext()){
            Automaton a = ai.next();
            saveAutomaton(a, path);
        }
    }

    /**
     * Saves an automaton to a file
     * @param a the automaton to save
     * @param path the path to save it to
     */    
    public void saveAutomaton(Automaton a, String path){
        File file = new File(path, a.getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
        a.toXML(ps);
    }

  
    /**
     * Creates a new automaton
     * @param name The name of the new automaton
     */
    public void newAutomaton(String name){
        project.addAutomaton(new Automaton(name));
        unsaved = true;
    }

    public boolean hasUnsavedData(){
        return unsaved;
    }

    public void setUnsavedData(boolean state){
        unsaved = state;
    }

    public void deleteAutomatonByName(String name){
        project.removeAutomaton(project.getAutomatonByName(name));
    }

    public String removeFileName(String name){
        return ParsingToolbox.removeFileType(name);
    }

    public Automaton getAutomatonByName(String name){
        return project.getAutomatonByName(name);
    }

    public void trim(Automaton automaton){
        accesible(automaton);
        coAccesible(automaton);
    }

    public void accesible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        // find initial states, mark them as reached and add them to the que
        Iterator<State> stateIterator = automaton.getStateIterator();
        while(stateIterator.hasNext()){
            State state = stateIterator.next();
            if(state.getSubElement("properties").getSubElement("initial").getChars().equals("true")){
                searchList.addFirst(state);
                state.addSubElement(new SubElement("accesible"));
            }
        }
        // for all accesible states
        while(!searchList.isEmpty()){
            State state = searchList.removeFirst();
            // mark all states that are accesible from this state as accesible
            // if they have not previously been marked as accesible.
            Iterator<Transition> transitionIterator = state.getSourceTransitionsListIterator();
            while(transitionIterator.hasNext()){
                Transition transition = transitionIterator.next();
                if(!transition.getTarget().hasSubElement("accesible")){
                    transition.getTarget().addSubElement(new SubElement("accesible"));
                    searchList.addFirst(transition.getTarget());
                }
            }
        }
        // tidy up. remove all states that aren't accesible.
        stateIterator = automaton.getStateIterator();
        while(stateIterator.hasNext()){
            State state = stateIterator.next();
            if(state.hasSubElement("accesible")) state.removeSubElement("accesible");
            else stateIterator.remove();
        }
    }

    public void coAccesible(Automaton automaton){
        LinkedList<State> searchList = new LinkedList<State>();
        ListIterator<State> states = automaton.getStateIterator();
        // mark all marked states as coaccesible and add them to the list.
        while(states.hasNext()){
            State s = states.next();
            if(s.getSubElement("properties").getSubElement("marked").getChars().equals("true")){
                s.addSubElement(new SubElement("coaccesible"));
                searchList.add(s);
            }
        }
        // for all states in the list mark all states that can access this state
        // as coaccesible and add it to the list (if it isn't allready marked as
        // coaccesible.)
        while(!searchList.isEmpty()){
            State s = searchList.removeFirst();
            ListIterator<Transition> tli = s.getTargetTransitionListIterator();
            while(tli.hasNext()){
                State source = tli.next().getSource();
                if(!source.hasSubElement("coaccesible")){
                    source.addSubElement(new SubElement("coaccesible"));
                    searchList.addFirst(source);
                }
            }
        }
        // tidy up. Remove all states that aren't coaccesible.
        states = automaton.getStateIterator();
        while(states.hasNext()){
            State s = states.next();
            if(s.hasSubElement("coaccesible")) s.removeSubElement("coaccesible");
            else states.remove();
        }
    }

    public boolean equals(LinkedList set1, LinkedList set2){
        if(set1.size() != set2.size()) return false;
        return set1.containsAll(set2) && set2.containsAll(set1);
    }

    public boolean in(LinkedList<LinkedList> setSet, LinkedList set){
        ListIterator<LinkedList> setIterator = setSet.listIterator();
        while(setIterator.hasNext()){
            LinkedList temp = setIterator.next();
            if(temp.containsAll(set) && set.containsAll(temp)) return true;
        }
        return false;
    }

    private int getStateId(State[] s){
        if(s[0].hasSubElement("searched")
                && s[0].getSubElement("searched").hasAttribute(Integer.toString(s[1].getId()))){
            return Integer.parseInt(s[0].getSubElement("searched").getAttribute(
                    Integer.toString(s[1].getId())));
        }
        return -1;
    }

    private void setStateId(State[] s, int stateId){
        if(!s[0].hasSubElement("searched")) 
            s[0].addSubElement(new SubElement("searched"));
        
        s[0].getSubElement("searched").setAttribute(Integer.toString(s[1].getId()),
                Integer.toString(stateId));
        
        if(!s[1].hasSubElement("searched"))
        s[1].addSubElement(new SubElement("searched"));
        s[1].getSubElement("searched").setAttribute(Integer.toString(s[0].getId()),
                Integer.toString(stateId));
    }

    private State makeState(State[] s, int stateNumber){
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

    private Event getEventByName(String name, Automaton automaton){
        ListIterator<Event> eli = automaton.getEventIterator();
        while(eli.hasNext()){
            Event event = eli.next();
            if(event.getSubElement("name").getChars().equals(name)) return event;
        }
        return null;
    }

    public void parallel(Automaton a, Automaton b, Automaton parallel){
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
                    parallel.add(event);
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
                        parallel.add(makeState(initial, stateNumber));
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
            State source = parallel.getState(getStateId(sa));

            ListIterator<Transition> sti0 = sa[0].getSourceTransitionsListIterator();
            while(sti0.hasNext()){
                Transition t0 = sti0.next();
                ListIterator<Transition> sti1 = sa[1].getSourceTransitionsListIterator();
                while(sti1.hasNext()){
                    Transition t1 = sti1.next();
                    if(t0.getEvent() == null && t1.getEvent() == null){
                        s[0] = t0.getTarget();
                        s[1] = t1.getTarget();

                        int id = getStateId(s);
                        if(id != -1){
                            parallel.add(new Transition(transitionNumber++, source, parallel
                                    .getState(id)));
                        }
                        else{
                            State target = makeState(s, stateNumber);
                            parallel.add(target);
                            parallel.add(new Transition(transitionNumber++, source, target));
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                    }
                    else if(t0.getEvent() != null && t1.getEvent() != null && t0.getEvent().getSubElement("name").getChars().equals(
                            t1.getEvent().getSubElement("name").getChars())){
                        Event event = getEventByName(
                                t0.getEvent().getSubElement("name").getChars(), parallel);
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
        //tidy up the mess I left.
        ListIterator<State> sli = a.getStateIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("searched");
        }
        sli = b.getStateIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("searched");
        }
    }

    public void product(Automaton a, Automaton b, Automaton product){
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
                    if(t0.getEvent() == null && t1.getEvent() == null){
                        s[0] = t0.getTarget();
                        s[1] = t1.getTarget();

                        int id = getStateId(s);
                        if(id != -1){
                            product.add(new Transition(transitionNumber++, source, product
                                    .getState(id)));
                        }
                        else{
                            State target = makeState(s, stateNumber);
                            product.add(target);
                            product.add(new Transition(transitionNumber++, source, target));
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                    }
                    else if(t0.getEvent() != null && t1.getEvent() != null && t0.getEvent().getSubElement("name").getChars().equals(
                            t1.getEvent().getSubElement("name").getChars())){
                        Event event = getEventByName(
                                t0.getEvent().getSubElement("name").getChars(), product);
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
        //tidy up the mess I left.
        ListIterator<State> sli = a.getStateIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("searched");
        }
        sli = b.getStateIterator();
        while(sli.hasNext()){
            sli.next().removeSubElement("searched");
        }
    }

    public Automaton copyAutomaton(String source, String clonedName){
        Automaton cloned = project.getAutomatonByName(source).clone();
        cloned.setName(clonedName);
        project.addAutomaton(cloned);
        return cloned;
    }
    
    
    public void layout(String name) throws Exception{
       new Layouter(project.getAutomatonByName(name));
    }

}
