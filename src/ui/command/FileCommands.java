package ui.command;

import io.IOUtilities;
import io.fsa.ver1.CommonTasks;
import io.fsa.ver1.FileOperations;

import java.awt.Cursor;
import java.io.File;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Hub;
import main.IDESWorkspace;
import main.IncompleteWorkspaceDescriptorException;
import main.Main;
import main.WorkspaceDescriptor;
import model.fsa.ver1.Automaton;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractFileOpenCommand;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;

//<<<<<<< FileCommands.java
import presentation.fsa.GraphExporter;
import presentation.fsa.GraphModel;
//=======
import services.latex.LatexPrerenderer;

//>>>>>>> 1.10

public class FileCommands {	
	
	public static class NewAutomatonCommand extends ActionCommand {
		
		/**
		 * used to create unique automaton names in a session
		 */
		private static int automatonCount=0;
		
		public NewAutomatonCommand(){
			super("new.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Automaton fsa = new Automaton(Hub.string("newAutomatonName")+"-"+automatonCount++);
			fsa.setId(Hub.getWorkspace().getRandomId());
			IDESWorkspace.instance().addFSAModel(fsa);
		}	
	}
	
	public static class OpenAutomatonCommand extends ActionCommand {
		
		public OpenAutomatonCommand() {
			super("open.automaton.command");			
		}

		@Override
		/**
		 * FIXME Don't add workspace files as if they were automata...
		 */
		protected void handleExecute() {
			JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(FileOperations.LAST_PATH_SETTING_NAME));
			fc.setDialogTitle(Hub.string("openModelTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, Hub.string("modelFileDescription")));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    	int retVal = fc.showOpenDialog(Hub.getMainWindow());
	    	if(retVal == JFileChooser.APPROVE_OPTION){
				Cursor cursor = Hub.getMainWindow().getCursor();
				Hub.getMainWindow().setCursor(Cursor.WAIT_CURSOR);
	    		Automaton fsa = (Automaton)FileOperations.openAutomaton(fc.getSelectedFile());
	    		if(fsa != null){
	    			Hub.getWorkspace().addFSAModel(fsa);
	    		}
				Hub.getMainWindow().setCursor(cursor);
			}
		}
	}	
	
	public static class SaveAllAutomataCommand extends ActionCommand {
		
		public SaveAllAutomataCommand(){
			super("saveall.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.WAIT_CURSOR);
			for(Iterator<GraphModel> i=Hub.getWorkspace().getGraphModels();i.hasNext();)
			{
				GraphModel gm=i.next();
				Automaton fsa=gm.getAutomaton();
				if(fsa!=null)
					if(FileOperations.saveAutomaton(fsa,fsa.getFile()))
						gm.setDirty(false);	
			}
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	public static class SaveAutomatonCommand extends ActionCommand {
		
		public SaveAutomatonCommand(){
			super("save.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Automaton fsa = (Automaton)Hub.getWorkspace().getActiveModel();
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.WAIT_CURSOR);
			if(fsa!=null)
				if(FileOperations.saveAutomaton(fsa,fsa.getFile()))
					Hub.getWorkspace().getActiveGraphModel().setDirty(false);				
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	public static class SaveAutomatonAsCommand extends ActionCommand {
		
		public SaveAutomatonAsCommand(){
			super("saveas.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Automaton fsa = (Automaton)Hub.getWorkspace().getActiveModel();			
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.WAIT_CURSOR);
			if(fsa!=null)
				if(FileOperations.saveAutomatonAs(fsa))
					Hub.getWorkspace().getActiveGraphModel().setDirty(false);				
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	
	public static class CloseAutomatonCommand extends ActionCommand {
		
		public CloseAutomatonCommand(){
			super("close.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Hub.getWorkspace().removeFSAModel(Hub.getWorkspace().getActiveModelName());
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
	
	public static class OpenWorkspaceCommand extends ActionCommand {
		
		public OpenWorkspaceCommand(){
			super("open.workspace.command");
		}
		
		@Override
		protected void handleExecute() {
			if(Hub.getWorkspace().isDirty())
				if(!CommonTasks.handleUnsavedWorkspace())
					return;
			JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(FileOperations.LAST_PATH_SETTING_NAME));
			fc.setDialogTitle(Hub.string("openWorkspaceTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.WORKSPACE_FILE_EXT, Hub.string("workspaceFileDescription")));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    	int retVal = fc.showOpenDialog(Hub.getMainWindow());
	    	if(retVal == JFileChooser.APPROVE_OPTION){
				Cursor cursor = Hub.getMainWindow().getCursor();
				Hub.getMainWindow().setCursor(Cursor.WAIT_CURSOR);
	    		WorkspaceDescriptor wd = FileOperations.openWorkspace(fc.getSelectedFile());
	    		if(wd != null){
	    			Hub.getWorkspace().replaceWorkspace(wd);
	    		}
				Hub.getMainWindow().setCursor(cursor);
	    	}
		}	
	}
	
	public static class SaveWorkspaceCommand extends ActionCommand {

		public SaveWorkspaceCommand(){
			super("save.workspace.command");
		}
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.WAIT_CURSOR);
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(FileOperations.saveWorkspace(wd,wd.getFile()))
					Hub.getWorkspace().setDirty(false);
			}catch(IncompleteWorkspaceDescriptorException e){}
			Hub.getMainWindow().setCursor(cursor);
		}
	}
	
	public static class SaveWorkspaceAsCommand extends ActionCommand {

		public SaveWorkspaceAsCommand(){
			super("saveas.workspace.command");
		}
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.WAIT_CURSOR);
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(FileOperations.saveWorkspaceAs(wd))
					Hub.getWorkspace().setDirty(false);
			}catch(IncompleteWorkspaceDescriptorException e){}
			Hub.getMainWindow().setCursor(cursor);
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
			// Modified: June 16, 2006
			// Modifier: Sarah-Jane Whittaker
			String fileContents = GraphExporter.createPSTricksFileContents();
			if (fileContents == null)
			{
				System.out.println("ERROR: File contents are null in FileCommands.performSave!!!");							
			}
			
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
			//int retVal = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit IDES 2.1?");
			//if(retVal == JOptionPane.OK_OPTION){
				//SystemVariables.instance().saveSettings();
				Main.onExit();
			//}else{
				// TODO
			//}
		}	
	}
}