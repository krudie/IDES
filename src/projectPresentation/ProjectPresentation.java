package projectPresentation;

import java.io.File;


public interface ProjectPresentation { 
     
    public void newProject(String name);

    public void setProjectName(String name);
 
    public String getProjectName();
    
    public void openProject(File file);
    
    public String[] getAutomataNames();
    
}
