package io;

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
        return automaton;
		
//		openDialog.setText(ResourceManager.getToolTipText(ResourceManager.FILE_OPEN_AUTOMATON));
//        openDialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
//        if(SystemVariables.last_used_path != null && SystemVariables.last_used_path.length() > 0){
//            openDialog.setFilterPath(SystemVariables.last_used_path);
//        }
//        String openLocation = openDialog.open();
//        if(openLocation != null){
//            SystemVariables.last_used_path = openDialog.getFilterPath();
//
//            String filename = MainWindow.getProjectExplorer().getTitle(ParsingToolbox.removeFileType(openDialog.getFileName()));
//            String error = Userinterface.getProjectPresentation().openAutomaton(new File(openLocation), filename);
//
//            if(!error.trim().equals("")){
//                MainWindow.errorPopup(ResourceManager.getString("parsing_error"), error);
//            }
//            MainWindow.getProjectExplorer().updateProject();
//        }		
	}
	
	public static void saveSystem(FSAModel model) {
		// TODO call saveSystemAs with default directory from SystemVariables
		
	}
	
	public static void saveSystemAs(FSAModel model, File f) {
		// TODO implement
		// open file output stream
		
		// write file
		
		// handle runtime errors
		
	}
}
