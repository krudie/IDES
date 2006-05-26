package ui.command;

import java.io.File;

import javax.swing.JFileChooser;

import io.fsa.ver1.FileOperations;

import main.IDESWorkspace;
import main.SystemVariables;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;

import org.pietschy.command.ActionCommand;

import ui.GraphModel;
import ui.UIStateModel;


public class FileCommands {	
	
	public class SaveSystemCommand extends ActionCommand {
	
		public SaveSystemCommand(){
			super("save.system.command");
		}
		
		@Override
		protected void handleExecute() {
			FileOperations.saveAutomaton((Automaton)IDESWorkspace.instance().getActiveModel(), SystemVariables.instance().getLast_used_path());
		}	
	}
	
	public class OpenSystemCommand extends ActionCommand {
		
		public OpenSystemCommand(){
			super("open.system.command");
		}
		
		@Override
		protected void handleExecute() {
			
			JFileChooser chooser = new JFileChooser(FileOperations.DEFAULT_DIRECTORY);
			  Automaton fsa = null;
			  // TODO move this to a place that can attach the dialog to a container 
			  int returnVal = chooser.showOpenDialog(null);
			  if(returnVal == JFileChooser.APPROVE_OPTION) {
				  File f = chooser.getSelectedFile();
				  // DEBUG
				  // System.out.println(f.getAbsolutePath());
				  fsa = (Automaton)FileOperations.openSystem(f);
			  }
			  
			  // TODO figure out which file menu item was selected
			  //if(item.getName().equals(""))
			  // For now just open an existing system			  
			  if(fsa != null){
				  UIStateModel uism = UIStateModel.instance(); 
				  uism.setAutomaton(fsa);
				  uism.setMetadata(new MetaData(fsa));
				  uism.setGraphModel(new GraphModel(fsa, uism.getMetadata()));
				  uism.refreshViews();
			  }
		  }
	}	
	
	
	public class OpenWorkspaceCommand extends ActionCommand {
		
		public OpenWorkspaceCommand(){
			super("open.workspace.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO implement
		}
	
	}
}