package io;

import java.io.File;

import javax.swing.JFileChooser;
import model.fsa.Automaton;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.FileDialog;
//
//import projectPresentation.ParsingToolbox;
//import userinterface.MainWindow;
//import userinterface.ResourceManager;
//import userinterface.Userinterface;
import model.DESModel;

public class FileOperations {

	public static DESModel openSystem() {
	
		// open input dialog
		
		// check for valid filename
		
		// open file and read file into DESModel
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	AutomatonParser ap = new AutomatonParser();
	    	File f = chooser.getSelectedFile();	    	
	        Automaton automaton = ap.parse(f);	        
	        automaton.setName(ParsingToolbox.removeFileType(f.getName()));
	        return automaton;
	    }
		
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
		return null;
	}	
}
