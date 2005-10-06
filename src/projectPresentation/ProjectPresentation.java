package projectPresentation;

import java.io.File;


public interface ProjectPresentation { 
     
    public void newProject(String name);

    public void setProjectName(String name);
 
    public String getProjectName();
    
    public String openProject(File file);
    
    public String[] getAutomataNames();
    
    public void setAutomatonName(String oldName, String newName);
    
    public boolean isProjectOpen();
    
    public void addAutomaton(String name);
    
    public boolean hasUnsavedData();
    
    public void setUnsavedData(boolean state);
    
    public void deleteAutomatonByName(String name);
    
    public void saveProject(String path);
    
    public String removeFileName(String name);
    
}
