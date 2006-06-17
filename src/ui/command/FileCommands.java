package ui.command;

import io.fsa.ver1.FileOperations;

import java.io.File;

import javax.swing.JOptionPane;

import main.Hub;
import main.IDESWorkspace;
import main.SystemVariables;
import model.fsa.ver1.Automaton;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractFileOpenCommand;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;


public class FileCommands {	
	
	public static class NewAutomatonCommand extends ActionCommand {
		
		public NewAutomatonCommand(){
			super("new.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO
			JOptionPane.showMessageDialog(null, "Create new automaton");
			Automaton fsa = new Automaton(Hub.string("newAutomatonName"));
			IDESWorkspace.instance().addFSAModel(fsa);
		}	
	}
	
	public static class OpenAutomatonCommand extends AbstractFileOpenCommand {
		
		public OpenAutomatonCommand() {
			super(CommandManager.defaultInstance(), "open.automaton.command", 
					new ExtensionFileFilter("xml", "eXtensible Markup Language"));			
		}

		@Override
		/**
		 * FIXME Don't add workspace files as if they were automata...
		 */
		protected void performOpen(File[] files) {			
			Automaton fsa = (Automaton)FileOperations.openAutomaton(files[0]);
			if(fsa != null){
				IDESWorkspace.instance().addFSAModel(fsa);			
				SystemVariables.instance().setLast_used_path(files[0].getPath());
			}
		}
	}	
	
	public static class SaveAutomatonCommand extends ActionCommand {
		
		public SaveAutomatonCommand(){
			super("save.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Automaton fsa = (Automaton)IDESWorkspace.instance().getActiveModel();			
			if( fsa.getName().equals(Hub.string("newAutomatonName"))){
				FileOperations.saveAutomatonAs(fsa);
			}else{			
				FileOperations.saveAutomaton(fsa);				
			}
			setEnabled(false);
		}	
	}
	
	public static class CloseAutomatonCommand extends ActionCommand {
		
		public CloseAutomatonCommand(){
			super("close.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO
			JOptionPane.showMessageDialog(null, "Close automaton");
		}	
	}
		
	
	public static class NewWorkspaceCommand extends ActionCommand {
			
			public NewWorkspaceCommand(){
				super("new.workspace.command");
			}
			
			@Override
			protected void handleExecute() {
				// TODO
				JOptionPane.showMessageDialog(null, "Create new workspace");
			}	
	}
	
	public static class OpenWorkspaceCommand extends AbstractFileOpenCommand {
		
		public OpenWorkspaceCommand(){
			super(CommandManager.defaultInstance(), "open.workspace.command", 
					new ExtensionFileFilter("xml", "eXtensible Markup Language"));
		}
		
		@Override
		protected void performOpen(File[] arg0) {
			// TODO Auto-generated method stub
			// NOTE 
			// Get the list of automata from loadWorkspace and then execute
			// a set of OpenAutomatonCommands
			// NOT e.g. in a loop in FileOperations.loadWorkspace(File)
			JOptionPane.showMessageDialog(null, "Open workspace");
		}
	
	}
	
	public static class SaveWorkspaceCommand extends ActionCommand {

		public SaveWorkspaceCommand(){
			super("save.workspace.command");
		}
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			JOptionPane.showMessageDialog(null, "Save workspace");
			setEnabled(false);
		}
	}
	
	public static class ExportToGIFCommand extends AbstractSaveAsCommand {

		public ExportToGIFCommand() {
			super(CommandManager.defaultInstance(), "export.gif.command", 
					new ExtensionFileFilter("gif", "Graphical Interchange Format"));
		}

		@Override
		protected void performSave(File arg0) {
			// TODO Auto-generated method stub
			System.out.println("TODO: Save as GIF");
		}
		
	}
	
	public static class ExportToLatexCommand extends AbstractSaveAsCommand {

		public ExportToLatexCommand() {
			super(CommandManager.defaultInstance(), "export.latex.command", 
					new ExtensionFileFilter("tex", "LaTeX"));
		}

		@Override
		protected void performSave(File arg0) {
			// TODO Auto-generated method stub
			System.out.println("TODO: Save as LaTeX");
		}
		
	}
	
	public static class ExportToPNGCommand extends AbstractSaveAsCommand {

		public ExportToPNGCommand() {
			super(CommandManager.defaultInstance(), "export.png.command",
					new ExtensionFileFilter("png", "Portable Network Graphic"));			
		}

		@Override
		protected void performSave(File arg0) {
			// TODO Auto-generated method stub
			System.out.println("TODO: Save as PNG");
		}
		
	}
	
public static class ExitCommand extends ActionCommand {
		
		public ExitCommand(){
			super("exit.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO check all dirty bits and display confirm exit/do you wish to save
			// dialogs and then fire appropriate save commands.
			int retVal = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit IDES 2.1?");
			if(retVal == JOptionPane.OK_OPTION){
				SystemVariables.instance().saveSettings();
				System.exit(0);
			}else{
				// TODO
			}
		}	
	}
}