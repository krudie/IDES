package io.fsa.ver1;

import java.io.File;

import javax.swing.JFileChooser;

import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.FileDialog;
//
//import projectPresentation.ParsingToolbox;
//import userinterface.MainWindow;
//import userinterface.ResourceManager;
//import userinterface.Userinterface;

public class FileOperations {

	public static FSAModel openSystem(File f) {	
	    AutomatonParser ap = new AutomatonParser();	    	
        Automaton automaton = ap.parse(f);	        
        automaton.setName(ParsingToolbox.removeFileType(f.getName()));
        
        // TODO extract metadata (layout information for graphical presentation) and store
        // in a separate object.  How should this be returned to the caller?
        // IDEA pull the subelement "graphic" from each state, transition and event and store these
        // in a subelementcontainer withing a class called MetaData or LayoutData
        // How will the caller know which pieces of data are available in the DS?
        // Currently accessed by name as String.
        
        // *** Do this in AutomatonParser.parse just before returning the Automaton.
        
        return automaton;		
	}
	
	public static void saveSystem(FSAModel model) {
		// TODO call saveSystemAs with default directory from SystemVariables
				
	}
	
	public static void saveSystemAs(FSAModel model, File f) {
		// TODO implement
		
		// merge metadata with automaton data structure and use legacy code to write the xml
		
		// open file output stream
		
		// write file
		
		// handle runtime errors
		
	}
}
