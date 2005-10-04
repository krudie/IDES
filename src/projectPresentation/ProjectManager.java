/**
 * 
 */
package projectPresentation;

import projectModel.*;

/**
 * @author edlund
 *
 */
public class ProjectManager implements ProjectPresentation {

    Project project = null;
    
    public void newProject(){
       project = new Project(""); 
    }
    
    public void setProjectName(String name){
        project.setName(name);
    }
    
    public String getProjectName(){
        return project.getName();
    } 
}
