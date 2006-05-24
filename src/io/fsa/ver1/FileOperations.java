package io.fsa.ver1;

import java.io.File;

import main.IDESWorkspace;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;

/**
 * TODO 
 * extract interface to package io.fsa
 * implement most methods
 * 
 * @author helen bretzke
 *
 */
public class FileOperations {
	
	public static final String DEFAULT_DIRECTORY = "C:/Documents and Settings/helen/My Documents/development/output/";
	
	public static FSAModel openSystem(File f) {	
	    AutomatonParser ap = new AutomatonParser();	    	
        Automaton automaton = ap.parse(f);	        
        automaton.setName(ParsingToolbox.removeFileType(f.getName()));
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
	
	public static void exportSystemAsLatex(FSAModel model, File f){
		
	}
	
	public static void exportSystemAsEPS(FSAModel model, File f){
		
		
	}
	
	public static IDESWorkspace loadWorkspace(File f){
		return null;
	}
}
