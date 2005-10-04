/**
 * 
 */
package projectPresentation;

import java.io.File;
import projectModel.*;

/**
 * @author edlund
 *
 */
public class ProjectManager implements ProjectPresentation {

    Project project = null;
    ProjectParser pp = null;
    
    public void newProject(String name){
       project = new Project(name); 
    }
    
    public void setProjectName(String name){
        if(project != null){
            project.setName(name);
        }        
    }
    
    public String getProjectName(){
        if(project != null){
            return project.getName();
        }
        return null;
    } 
    
    public void openProject(File file){
        if(pp == null){
            pp = new ProjectParser();
        }
        try{
            project = pp.parse(file);
        } catch(Exception e){
         System.out.println(e.getMessage());  
        }      
    }
    
    public String[] getAutomataNames(){
        
        return new String[3];
    }
}
