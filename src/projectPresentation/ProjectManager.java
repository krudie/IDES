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
 * The main file that implements the interface available for the userinterface
 * 
 * @author Kristian Edlund
 */
/**
 * @author edlund
 *
 */
public class ProjectManager implements ProjectPresentation{

    private Project project = null;

    private boolean unsaved = false;

    /**
     * @see projectPresentation.ProjectPresentation#newProject(java.lang.String)
     */
    public void newProject(String name){
        project = new Project(name);
        unsaved = true;
    }
  
    /**
     * @see projectPresentation.ProjectPresentation#isProjectOpen()
     */
    public boolean isProjectOpen(){
        return (project != null);
    }
  
    
    /**
     * @see projectPresentation.ProjectPresentation#setProjectName(java.lang.String)
     */
    public void setProjectName(String name){
        if(project != null){
            project.setName(name);
        }
        unsaved = true;
    }

    /**
     * @see projectPresentation.ProjectPresentation#getProjectName()
     */
    public String getProjectName(){
        if(project != null){
            return project.getName();
        }
        return null;
    }
   
    
    /**
     * @see projectPresentation.ProjectPresentation#openProject(java.io.File)
     */
    public String openProject(File file){
        ProjectParser pp = new ProjectParser();

        project = pp.parse(file);
        return pp.getParsingErrors();
    }
    
    /**
     * @see projectPresentation.ProjectPresentation#openAutomaton(java.io.File, java.lang.String)
     */
    public String openAutomaton(File file, String name){
        AutomatonParser ap = new AutomatonParser();
        Automaton automaton = ap.parse(file);
        automaton.setName(name);
        project.addAutomaton(automaton);
        return ap.getParsingErrors();

    }
  
    /**
     * @see projectPresentation.ProjectPresentation#getAutomataNames()
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
     * @see projectPresentation.ProjectPresentation#setAutomatonName(java.lang.String, java.lang.String)
     */
    public void setAutomatonName(String oldName, String newName){
        project.getAutomatonByName(oldName).setName(newName);
        unsaved = true;
    }
        
    /**
     * Method for getting a printstream wrapped around a file
     * @param file the file that needs a printstream wrapped around it
     * @return The printstream pointing to a the file
     */
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
     * @see projectPresentation.ProjectPresentation#saveProject(java.lang.String)
     */
    public void saveProject(String path){
        File file = new File(path, project.getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
        XMLexporter.projectToXML(project, ps);
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
    private void saveAutomaton(Automaton a, String path){
        File file = new File(path, a.getName() + ".xml");
        PrintStream ps = getPrintStream(file);
        if(ps == null) return;
        XMLexporter.automatonToXML(a, ps);        
    }

  
    /**
     * @see projectPresentation.ProjectPresentation#newAutomaton(java.lang.String)
     */
    public void newAutomaton(String name){
        if(project == null) return;
        project.addAutomaton(new Automaton(name));
        unsaved = true;
    }
            
    /**
     * @see projectPresentation.ProjectPresentation#addAutomaton(projectModel.Automaton)
     */
    public void addAutomaton(Automaton automaton){
        if(project == null) return;
        project.addAutomaton(automaton);
        unsaved = true;
    }

    /**
     * @see projectPresentation.ProjectPresentation#hasUnsavedData()
     */
    public boolean hasUnsavedData(){
        return unsaved;
    }

    /**
     * @see projectPresentation.ProjectPresentation#setUnsavedData(boolean)
     */
    public void setUnsavedData(boolean state){
        unsaved = state;
    }

    /**
     * @see projectPresentation.ProjectPresentation#deleteAutomatonByName(java.lang.String)
     */
    public void deleteAutomatonByName(String name){
        project.removeAutomaton(project.getAutomatonByName(name));
    }

    /**
     * @see projectPresentation.ProjectPresentation#removeFileName(java.lang.String)
     */
    public String removeFileName(String name){
        return ParsingToolbox.removeFileType(name);
    }

    /**
     * @see projectPresentation.ProjectPresentation#getAutomatonByName(java.lang.String)
     */
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

    /**
     * @see projectPresentation.ProjectPresentation#copyAutomaton(java.lang.String, java.lang.String)
     */
    public Automaton copyAutomaton(String source, String clonedName){
        Automaton cloned = project.getAutomatonByName(source).clone();
        cloned.setName(clonedName);
        project.addAutomaton(cloned);
        return cloned;
    }
    
    
    /**
     * @see projectPresentation.ProjectPresentation#layout(java.lang.String)
     */
    public void layout(String name) throws Exception{
       new Layouter(project.getAutomatonByName(name));
    }

}
