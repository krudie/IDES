package projectPresentation;

import java.io.File;

import projectModel.Automaton;

public interface ProjectPresentation {

    public void newProject(String name);

    public void setProjectName(String name);

    public String getProjectName();

    public String openProject(File file);

    public String openAutomaton(File file, String name);

    public String[] getAutomataNames();

    public void setAutomatonName(String oldName, String newName);

    public boolean isProjectOpen();

    public void newAutomaton(String name);

    public boolean hasUnsavedData();

    public void setUnsavedData(boolean state);

    public void deleteAutomatonByName(String name);

    public void saveProject(String path);

    public String removeFileName(String name);
    
    public Automaton getAutomatonByName(String name);      
    
    public Automaton copyAutomaton(String source, String clonedName);

    public void layout(String name) throws Exception;

    public void product(Automaton a, Automaton b, Automaton product);
}
