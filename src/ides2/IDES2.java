package ides2;

import projectPresentation.ProjectManager;
import projectPresentation.ProjectPresentation;

/**
 * This is the mainclass for IDES2, and is the class that should be run as deafult
 * @author Kristian Edlund
 * @author Axel Gottlieb Michelsen
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
