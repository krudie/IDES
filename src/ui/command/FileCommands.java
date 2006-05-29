package ui.command;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import io.fsa.ver1.FileOperations;

import main.IDESWorkspace;
import main.SystemVariables;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractFileOpenCommand;
import org.pietschy.command.file.AbstractSaveAsCommand;

import ui.GraphModel;
import ui.UIStateModel;


public class FileCommands {	
	
	public static class SaveAutomatonCommand extends ActionCommand {
	
		public SaveAutomatonCommand(){
			super("save.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			FileOperations.saveAutomaton((Automaton)IDESWorkspace.instance().getActiveModel(), SystemVariables.instance().getLast_used_path());
		}	
	}
	
	public static class OpenAutomatonCommand extends AbstractFileOpenCommand {
		
		public OpenAutomatonCommand(CommandManager cm, String id, FileFilter filter) {
			super(cm, id, filter);			
		}

//		public OpenSystemCommand(){
//			
//		}
		
//		@Override
//		protected void handleExecute() {
//			
//			JFileChooser chooser = new JFileChooser(FileOperations.DEFAULT_DIRECTORY);
//			  Automaton fsa = null;
//			  // TODO move this to a place that can attach the dialog to a container 
//			  int returnVal = chooser.showOpenDialog(null);
//			  if(returnVal == JFileChooser.APPROVE_OPTION) {
//				  File f = chooser.getSelectedFile();
//				  // DEBUG
//				  // System.out.println(f.getAbsolutePath());
//				  fsa = (Automaton)FileOperations.openSystem(f);
//			  }
//			  
//			  // TODO figure out which file menu item was selected
//			  //if(item.getName().equals(""))
//			  // For now just open an existing system			  
//			  if(fsa != null){
//				  UIStateModel uism = UIStateModel.instance(); 
//				  uism.setAutomaton(fsa);
//				  uism.setMetadata(new MetaData(fsa));
//				  uism.setGraphModel(new GraphModel(fsa, uism.getMetadata()));
//				  uism.refreshViews();
//			  }
//		  }

		@Override
		protected void performOpen(File[] files) {			
			Automaton fsa = (Automaton)FileOperations.openSystem(files[0]);
			if(fsa != null){
				  UIStateModel uism = UIStateModel.instance(); 
				  uism.setAutomaton(fsa);
				  uism.setMetadata(new MetaData(fsa));
				  uism.setGraphModel(new GraphModel(fsa, uism.getMetadata()));
				  uism.refreshViews();
			  }
		}
	}	
	
	
	public static class OpenWorkspaceCommand extends ActionCommand {
		
		public OpenWorkspaceCommand(){
			super("open.workspace.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO implement
		}
	
	}
	
	public static class SaveWorkspaceCommand extends ActionCommand {

		public SaveWorkspaceCommand(){
			super("save.workspace.command");
		}
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static class ExportToGIFCommand extends AbstractSaveAsCommand {

		public ExportToGIFCommand(CommandManager arg0, String arg1, FileFilter arg2) {
			super(arg0, arg1, arg2);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void performSave(File arg0) {
			// TODO Auto-generated method stub
			System.out.println("TODO: Save as GIF");
		}
		
	}
	
	public static class ExportToLatexCommand extends AbstractSaveAsCommand {

		public ExportToLatexCommand(CommandManager arg0, String arg1, FileFilter arg2) {
			super(arg0, arg1, arg2);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void performSave(File arg0) {
			// TODO Auto-generated method stub
			System.out.println("TODO: Save as LaTeX");
		}
		
	}
}