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

    public void newProject(String name){
        project = new Project(name);
        unsaved = true;
    }

    public boolean isProjectOpen(){
        return (project != null);
    }

    public void setProjectName(String name){
        if(project != null){
            project.setName(name);
        }
        unsaved = true;
    }

    public String getProjectName(){
        if(project != null){
            return project.getName();
        }
        return null;
    }

    public String openProject(File file){
        ProjectParser pp = new ProjectParser();

        project = pp.parse(file);
        return pp.getParsingErrors();
    }

    public String openAutomaton(File file, String name){
        AutomatonParser ap = new AutomatonParser();
        Automaton automaton = ap.parse(file);
        automaton.setName(name);
        project.addAutomaton(automaton);
        return ap.getParsingErrors();

    }

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

    public void saveAutomaton(Automaton a, String path){
        File file = new File(path, a.getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
        a.toXML(ps);
    }

    public void addAutomaton(String name){
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
            // mark all states that are accesible from this state as accesible.
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
        //mark all marked states as coaccesible and add them to the list.
        while(states.hasNext()){
            State s = states.next();
            if(s.getSubElement("properties").getSubElement("marked").getChars().equals("true")){
                s.addSubElement(new SubElement("coaccesible"));
                searchList.add(s);
            }
        }
        //for all states in the list mark all states that can access this state
        //as coaccesible and add it to the list (if it isn't allready marked as coaccesible.)
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
        //tidy up. Remove all states that aren't coaccesible.
        states = automaton.getStateIterator();
        while(states.hasNext()){
            State s = states.next();
            if(s.hasSubElement("coaccesible")) s.removeSubElement("coaccesible");
            else states.remove();
        }
    }

    public void copyAutomaton(String source, String clonedName){
        Automaton cloned = project.getAutomatonByName(source).clone();

        try{
            cloned.setName(clonedName);
            project.addAutomaton(cloned);
        }
        catch(Exception e){
            return;
        }
    }

}
