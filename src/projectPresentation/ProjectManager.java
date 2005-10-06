/**
 * 
 */
package projectPresentation;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import projectModel.*;

/**
 * @author edlund
 *
 */
public class ProjectManager implements ProjectPresentation {

    private Project project = null;
    private ProjectParser pp = null;
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
        if(pp == null){
            pp = new ProjectParser();
        }
        project = pp.parse(file);      
        return pp.getParsingErrors();  
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
    
    
    
}
