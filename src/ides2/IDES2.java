package ides2;

import projectPresentation.ProjectManager;
import projectPresentation.ProjectPresentation;

/**
 * @author edlund
 *
 */
public class IDES2 {
		   
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SystemVariables sv = new SystemVariables();
        ProjectPresentation projectPresentation = new ProjectManager(); 
        
        new userinterface.Userinterface(projectPresentation);
		sv.saveValues();
	}

}
