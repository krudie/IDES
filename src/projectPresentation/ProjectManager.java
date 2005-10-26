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
    
    public void addAutomaton(Automaton automaton){
        project.addAutomaton(automaton);
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


/**
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
    
    */

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
