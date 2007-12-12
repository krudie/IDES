package ui.actions;

import io.CommonFileActions;
import io.IOUtilities;
import io.ParsingToolbox;
import io.ctct.CTCTException;
import io.ctct.LL_CTCT_Command;
import io.fsa.ver2_1.GraphExporter;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
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
public class FileActions {	

	public static class NewAction extends AbstractAction{
		/**
		 * used to create unique  names in a session
		 */
		private static int Count=0;
		
		private static ImageIcon icon = new ImageIcon();
		
		public NewAction()
		{
			super(Hub.string("comNewModel"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_new_automaton.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintNewModel"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			DESModel des= ModelManager.createModel(FSAModel.class);
			des.setName(Hub.string("newModelName")+"-"+Count++);
			Hub.getWorkspace().addModel(des);
			Hub.getWorkspace().setActiveModel(des.getName());
		}
	}

	public static class OpenAction extends AbstractAction{
		
		private static ImageIcon icon = new ImageIcon();
		
		public OpenAction()
		{
			super(Hub.string("comOpenModel"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_open_automaton.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintOpenModel"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
//			Open a window for the user to choose the file to open:
			io.CommonFileActions.open();
		}
	}
	public static class SaveAction extends AbstractAction{
		
		private static ImageIcon icon = new ImageIcon();
		
		public SaveAction()
		{
			super(Hub.string("comSaveModel"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_save_automaton.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveModel"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			io.CommonFileActions.save(Hub.getWorkspace().getActiveModel(), (File)Hub.getWorkspace().getActiveModel().getAnnotation(Annotable.FILE));
		}
	}
	
	public static class SaveAsAction extends AbstractAction{

		private static ImageIcon icon = new ImageIcon();
		
		public SaveAsAction()
		{
			super(Hub.string("comSaveAsModel"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_saveas_automaton.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveAsModel"));
		}
		public void actionPerformed(ActionEvent e)
		{
			io.CommonFileActions.saveAs(Hub.getWorkspace().getActiveModel());
		}
	}
	
	public static class SaveAllAction extends AbstractAction{

		private static ImageIcon icon = new ImageIcon();
		
		public SaveAllAction()
		{
			super(Hub.string("comSaveAllModels"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_saveall_automata.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveAllModels"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Iterator<LayoutShell> iterator = Hub.getWorkspace().getLayoutShells();			
			while(iterator.hasNext())
			{
				LayoutShell gm=iterator.next();
				DESModel model=gm.getModel();
				if( model != null)
				{
					io.CommonFileActions.save(model, (File)model.getAnnotation(Annotable.FILE));
//					Hub.getWorkspace().fireRepaintRequired();
				}
			}
			Hub.getMainWindow().setCursor(cursor);
		}
	}

	public static class SaveWorkspaceAction extends AbstractAction{
		
		private static ImageIcon icon = new ImageIcon();
		
		public SaveWorkspaceAction()
		{
			super(Hub.string("comSaveWorkspace"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_save_workspace.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveWorkspace"));
		}
		
		public void actionPerformed(ActionEvent event)
		{
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(io.CommonFileActions.saveWorkspace(wd,wd.getFile()))
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

	public static class SaveWorkspaceAsAction extends AbstractAction{
		
		private static ImageIcon icon = new ImageIcon();
		
		public SaveWorkspaceAsAction()
		{
			super(Hub.string("comSaveAsWorkspace"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_saveas_workspace.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveAsWorkspace"));
		}
		
		public void actionPerformed(ActionEvent event)
		{
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(io.CommonFileActions.saveWorkspaceAs(wd))
					Hub.getWorkspace().setDirty(false);
			}catch(IncompleteWorkspaceDescriptorException e){}
			Hub.getMainWindow().setCursor(cursor);
		}
	}

	public static class ImportAction extends AbstractAction{
		
		public ImportAction()
		{
			super(Hub.string("comImport"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintImport"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			io.CommonFileActions.importFile();
		}
	}

	public static class ExportAction extends AbstractAction{
		
		public ExportAction()
		{
			super(Hub.string("comExport"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintExport"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			io.CommonFileActions.export(Hub.getWorkspace().getActiveModel());	
		}
	}

	public static class CloseAction extends AbstractAction{

		private static ImageIcon icon = new ImageIcon();
		
		public CloseAction()
		{
			super(Hub.string("comCloseModel"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_close_automaton.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintCloseModel"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			Hub.getWorkspace().removeModel(Hub.getWorkspace().getActiveModelName());
		}
	}

	public static class OpenWorkspaceAction extends AbstractAction{

		private static ImageIcon icon = new ImageIcon();
		
		public OpenWorkspaceAction()
		{
			super(Hub.string("comOpenWorkspace"),icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/file_open_workspace.gif")));	
			putValue(SHORT_DESCRIPTION, Hub.string("comHintOpenWorkspace"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(Hub.getWorkspace().isDirty())
				if(!io.CommonFileActions.handleUnsavedWorkspace())
					return;
			JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(CommonFileActions.LAST_PATH_SETTING_NAME));
			fc.setDialogTitle(Hub.string("openWorkspaceTitle"));
			fc.setFileFilter(new IOUtilities.ExtensionFilter(new String[]{IOUtilities.WORKSPACE_FILE_EXT}, Hub.string("workspaceFileDescription")));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int retVal = fc.showOpenDialog(Hub.getMainWindow());
			if(retVal == JFileChooser.APPROVE_OPTION){
				Cursor cursor = Hub.getMainWindow().getCursor();
				Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				WorkspaceDescriptor wd = io.CommonFileActions.openWorkspace(fc.getSelectedFile());
				if(wd != null){
					Hub.getWorkspace().replaceWorkspace(wd);
				}
				Hub.getMainWindow().setCursor(cursor);
			}
		}	
	}

	
	
	public static class ExitAction extends AbstractAction{

		public ExitAction()
		{
			super(Hub.string("comExit"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintExit"));
		}
		
		public void actionPerformed(ActionEvent e)
		{
			Main.onExit(); 
		}
	}
}