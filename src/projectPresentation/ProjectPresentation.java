package projectPresentation;

import java.io.File;

import projectModel.Automaton;

/**
 * Interface for the userinterface to call things in the presentation layer
 * 
 * @author Kristian Edlund
 */
public interface ProjectPresentation {
    
    /**
     * Creates a new project
     * @param name The name of the new project
     */
    public void newProject(String name);

    /**
     * A check to see there is an open project
     * @return true if the project is open
     */ 
    public boolean isProjectOpen();
    
    /**
     *  Changes the open project' name
     *  @param name the new name of the project
     */
    public void setProjectName(String name);

    /**
     * Gets the project name
     * @return the project name
     */ 
    public String getProjectName();
    
    /**
     *  Opens a project
     *  @param file the filename of the project file to open
     *  @return the parsing error
     */  
    public String openProject(File file);
    
    /**
     * Open an automaton
     * @param file The file to open
     * @param name The name for the automaton
     * @return The parsing errors from parsing the automaton file
     */
    public String openAutomaton(File file, String name);
    
    /**
     * Get a list of automaton names
     * @return an array of automaton names in the project
     */  
    public String[] getAutomataNames();
    
    /**
     * changes the name of an automaton
     * @param oldName The name of the automaton to changes name of
     * @param newName the new name for the automaton
     */  
    public void setAutomatonName(String oldName, String newName);

    /**
     * Function for saving the project to a file
     * @param path The path where the project should be saved
     */  
    public void saveProject(String path);
        
    /**
     * Creates a new automaton
     * @param name The name of the new automaton
     */
    public void newAutomaton(String name);

    /**
     * Adds an automaton to the project. It just returns if there is no open project
     * @param automaton the automaton to add to the open project.
     */
    public void addAutomaton(Automaton automaton);
    
    
    /**
     * Checks to see if there is unsaved data in the open project
     * @return Returns true if there is unsaved data 
     */
    public boolean hasUnsavedData();

    /**
     * Sets the unsaved data state
     * @param state the new state set to true if there is unsaved data
     */
    public void setUnsavedData(boolean state);

    
    /**
     * Deletes the automaton with the given name
     * @param name the name of the automaton to delete
     */
    public void deleteAutomatonByName(String name);

    /**
     * Removes the file type from a string i.e test.xml would become test
     * @param name The filename
     * @return the trimmed version of the name
     */
    public String removeFileName(String name);
    
    
    /**
     * Finds an automaton with the given name
     * @param name The name of the automaton to find
     * @return The automaton with the name. Returns null if the automaton is not found.
     */
    public Automaton getAutomatonByName(String name);      
    
    /**
     * Copies an automaton
     * @param source The name of the automaton to copy
     * @param clonedName The name of the new automaton
     * @return The copied automaton
     */
    public Automaton copyAutomaton(String source, String clonedName);

    /**
     * Used for calling the layoutter
     * @param name The name of the automaton to layout
     * @throws Exception Thrown if anything weird happens
     */
    public void layout(String name) throws Exception;  
    
    
}
