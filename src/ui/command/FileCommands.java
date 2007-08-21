package ui.command;

import io.IOUtilities;
import io.ParsingToolbox;
import io.ctct.CTCTException;
import io.ctct.LL_CTCT_Command;
import io.fsa.ver2_1.FileOperations;
import io.fsa.ver2_1.GraphExporter;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Annotable;
import main.Hub;
import main.IncompleteWorkspaceDescriptorException;
import main.Main;
import main.WorkspaceDescriptor;
import model.DESModel;
import model.ModelDescriptor;
import model.ModelManager;
import model.fsa.FSAModel;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;

import presentation.LayoutShell;
import services.General;
import services.latex.LatexManager;
import services.latex.LatexRenderException;
import ui.NewModelDialog;
import ui.OperationDialog;
import ui.SaveDialog;
import pluggable.io.IOCoordinator;
import ui.ImportExportDialog;
;/**
 * @author Lenko Grigorov
 */
public class FileCommands {	

	public static class NewCommand extends ActionCommand {

		/**
		 * used to create unique  names in a session
		 */
		private static int Count=0;

		public NewCommand(){
			super("new.command");
		}

		@Override
		protected void handleExecute() {
//			ModelDescriptor md=new NewModelDialog().selectModel();
//			if(md==null)
//			return;
//			DESModel des=md.createModel(Hub.string("newModelName")+"-"+Count++);
			//This version of IDES just support FSAModel:
			DESModel des= ModelManager.createModel(FSAModel.class);
			des.setName(Hub.string("newModelName")+"-"+Count++);
			Hub.getWorkspace().addModel(des);
			Hub.getWorkspace().setActiveModel(des.getName());
		}	
	}

	public static class OpenCommand extends ActionCommand {

		public OpenCommand() {
			super("open.command");			
		}
		@Override
		/**
		 * FIXME Don't add workspace files as if they were automata...
		 */
		protected void handleExecute() {
			//Open a window for the user to choose the file to open:
			io.CommonActions.load();
		}
	}

	public static class SaveAllCommand extends ActionCommand {

		public SaveAllCommand(){
			super("saveall.command");
		}

		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Iterator<LayoutShell> iterator = Hub.getWorkspace().getLayoutShells();			
			while(iterator.hasNext())
			{
				LayoutShell gm=iterator.next();
				DESModel model=gm.getModel();
				if( model != null)
				{
					try
					{
						IOCoordinator.getInstance().save(model, (File)model.getAnnotation(Annotable.FILE));
					}catch(IOException e)
					{
						Hub.displayAlert(e.getMessage());
					}
					Hub.getWorkspace().fireRepaintRequired();
				}
			}
			Hub.getMainWindow().setCursor(cursor);
		}	
	}

	public static class SaveCommand extends ActionCommand {

		public SaveCommand() {
			super("save.command");
		}

		@Override
		protected void handleExecute() {
//			Vector<DESModel> models=new Vector<DESModel>();
//			for(Iterator<DESModel> i=Hub.getWorkspace().getModels();i.hasNext();)
//			{
//				models.add(i.next());
//			}
//			new SaveDialog(models).selectModels();
			io.CommonActions.save(Hub.getWorkspace().getActiveModel(), null);
		}
	}

	public static class SaveAsCommand extends ActionCommand {

		public SaveAsCommand(){
			super("saveas.command");
		}

		@Override
		protected void handleExecute() {
			io.CommonActions.saveAs(Hub.getWorkspace().getActiveModel());
		}	

	}



	public static class CloseCommand extends ActionCommand {

		public CloseCommand(){
			super("close.command");
		}

		@Override
		protected void handleExecute() {
			Hub.getWorkspace().removeModel(Hub.getWorkspace().getActiveModelName());
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
				if(!io.CommonActions.handleUnsavedWorkspace())
					return;
			JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME"));
			fc.setDialogTitle(Hub.string("openWorkspaceTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.WORKSPACE_FILE_EXT, Hub.string("workspaceFileDescription")));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int retVal = fc.showOpenDialog(Hub.getMainWindow());
			if(retVal == JFileChooser.APPROVE_OPTION){
				Cursor cursor = Hub.getMainWindow().getCursor();
				Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				WorkspaceDescriptor wd = io.CommonActions.openWorkspace(fc.getSelectedFile());
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
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(io.CommonActions.saveWorkspace(wd,wd.getFile()))
					Hub.getWorkspace().setDirty(false);
			}catch(IncompleteWorkspaceDescriptorException e){}
			catch(NullPointerException e)
			{
				Hub.getMainWindow().setCursor(cursor);
				return;
			}
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
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(io.CommonActions.saveWorkspaceAs(wd))
					Hub.getWorkspace().setDirty(false);
			}catch(IncompleteWorkspaceDescriptorException e){}
			Hub.getMainWindow().setCursor(cursor);
		}
	}

	public static class ImportCommand extends ActionCommand {

		public ImportCommand() {
			super("import.command");			
		}

		@Override
		protected void handleExecute() {
			io.CommonActions.importModel();
		}

	}

	public static class ExportCommand extends ActionCommand {

		public ExportCommand() {
			super("export.command");			
		}

		@Override
		protected void handleExecute() {
//			try{
			io.CommonActions.exportModel();	
//			}catch(IOException e)
//			{
//			Hub.displayAlert(e.getMessage());
//			}
		}

	}



	public static class ExitCommand extends ActionCommand {

		public ExitCommand(){
			super("exit.command");
		}

		@Override
		protected void handleExecute() {			
			Main.onExit();
		}	
	}
}