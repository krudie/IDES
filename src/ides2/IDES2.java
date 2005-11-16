package ides2;

import projectPresentation.ProjectManager;
import projectPresentation.ProjectPresentation;

/**
 * @author Kristian Edlund
 * 
 */
public class IDES2 {

    /**
     * Used as the main class for IDES2
     * @param args not used
     */
    public static void main(String[] args) {
        SystemVariables sv = new SystemVariables();
        ProjectPresentation projectPresentation = new ProjectManager();

        new userinterface.Userinterface(projectPresentation);
        sv.saveValues();
    }

}
