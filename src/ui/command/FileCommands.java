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
import org.pietschy.command.file.ExtensionFileFilter;

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
		
		public OpenAutomatonCommand() {
			super(CommandManager.defaultInstance(), "open.automaton.command", 
					new ExtensionFileFilter("xml", "eXtensible Markup Language"));			
		}

		@Override
		/**
		 * FIXME Don't add workspace files as if they were automata...
		 */
		protected void performOpen(File[] files) {			
			Automaton fsa = (Automaton)FileOperations.openSystem(files[0]);
			if(fsa != null){
				IDESWorkspace.instance().addFSAModel(fsa);			
			}
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
}