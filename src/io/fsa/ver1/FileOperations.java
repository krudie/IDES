package io.fsa.ver1;

import java.io.File;

import javax.swing.JFileChooser;

import ui.Publisher;

import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.FileDialog;
//
//import projectPresentation.ParsingToolbox;
//import userinterface.MainWindow;
//import userinterface.ResourceManager;
//import userinterface.Userinterface;

public class FileOperations {
	
	public static Publisher openSystem(File f) {	
	    AutomatonParser ap = new AutomatonParser();	    	
        Automaton automaton = ap.parse(f);	        
        automaton.setName(ParsingToolbox.removeFileType(f.getName()));
        return automaton;		
	}
	

	
    // Extracts metadata (layout information for graphical presentation) and stores
    // in a separate object.
    // IDEA pull the subelement "graphic" from each state, transition and event and store these
    // in a subelementcontainer.    
	private MetaData extractMetaData(Automaton model){	
		SubElementContainer sec = new SubElementContainer(); 
				
		
		return new MetaData(sec);
	}
	
	public void mergeMetaData(Automaton model, MetaData metadata){
		
		
	}
	
	public static void saveSystem(Publisher model) {
		// TODO call saveSystemAs with default directory from SystemVariables
				
	}
	
	public static void saveSystemAs(Publisher model, File f) {
		// TODO implement
		
		// merge metadata with automaton data structure and use legacy code to write the xml
		
		// open file output stream
		
		// write file
		
		// handle runtime errors
		
	}
}
