package projectPresentation;

import java.io.File;


public interface ProjectPresentation { 
     
    public void newProject(String name);

    public void setProjectName(String name);
 
    public String getProjectName();
    
    public String openProject(File file);
    
    public String[] getAutomataNames();
    
    public void setAutomatonName(String oldName, String newName);
    
}
