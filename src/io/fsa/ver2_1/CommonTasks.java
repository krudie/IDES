/**
 * 
 */
package io.fsa.ver2_1;

import model.DESModel;
import io.IOUtilities;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.pietschy.command.file.ExtensionFileFilter;

import presentation.LayoutShell;
import presentation.fsa.FSAGraph;

import main.Annotable;
import main.Hub;
import main.IncompleteWorkspaceDescriptorException;
import main.WorkspaceDescriptor;
import model.fsa.FSAModel;

import pluggable.io.IOCoordinator;
/**
 *
 * @author Lenko Grigorov
 */
public class CommonTasks {

	private CommonTasks(){}
	
	/**
	 * Asks the user if they want to save the workspace
	 * 
	 * @return false if the process was cancelled
	 */
	public static boolean handleUnsavedWorkspace()
	{
		int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
				Hub.string("saveChangesAskWorkspace")+"\""+Hub.getWorkspace().getName()+"\"?",
				Hub.string("saveChangesWorkspaceTitle"),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/save_workspace.gif"))));
		if(choice!=JOptionPane.YES_OPTION&&choice!=JOptionPane.NO_OPTION)
			return false;
		if(choice==JOptionPane.YES_OPTION)
		{
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(FileOperations.saveWorkspace(wd,wd.getFile()))
					Hub.getWorkspace().setDirty(false);
				else
					return false;
			}catch(IncompleteWorkspaceDescriptorException e)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Asks the user if they want to save the model
	 * 
	 * @param gm the GraphModel that needs to be saved
	 * @return false if the process was cancelled
	 */
	public static boolean handleUnsavedModel(DESModel m)
	{
		int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
				Hub.string("saveChangesAskModel")+"\""+m.getName()+"\"?",
				Hub.string("saveChangesModelTitle"),
				JOptionPane.YES_NO_CANCEL_OPTION);
		if(choice!=JOptionPane.YES_OPTION&&choice!=JOptionPane.NO_OPTION)
			return false;
		if(choice==JOptionPane.YES_OPTION)
		{
			if((File)m.getAnnotation(Annotable.FILE) != null)
			{
				try{
				IOCoordinator.getInstance().save(m, (File)m.getAnnotation(Annotable.FILE));
				}catch(IOException e)
				{
					Hub.displayAlert(e.getMessage());
				}
				}
			else{
				JFileChooser fc;
				String path = Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME");
				if(path == null)
				{
					fc=new JFileChooser();
				}else
				{
					fc=new JFileChooser(path);	
				}
		        fc.setDialogTitle(Hub.string("saveModelTitle"));
				fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, 
						Hub.string("modelFileDescription")));
				
				if((File)m.getAnnotation(Annotable.FILE)!=null){
					fc.setSelectedFile((File)m.getAnnotation(Annotable.FILE));
				}else{
					fc.setSelectedFile(new File(m.getName()));
				}
				
				int retVal;
				boolean fcDone=true;
				File file=null;
				do
				{
					retVal = fc.showSaveDialog(Hub.getMainWindow());
					if(retVal != JFileChooser.APPROVE_OPTION)
						break;
					file=fc.getSelectedFile();
		    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.MODEL_FILE_EXT))
		    			file=new File(file.getPath()+"."+IOUtilities.MODEL_FILE_EXT);
					if(file.exists())
					{
						int _choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
							Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
							Hub.string("saveModelTitle"),
							JOptionPane.YES_NO_CANCEL_OPTION);
						fcDone=_choice!=JOptionPane.NO_OPTION;
						if(_choice!=JOptionPane.YES_OPTION)
							retVal=JFileChooser.CANCEL_OPTION;
					}
				} while(!fcDone);					
			
				if(retVal != JFileChooser.CANCEL_OPTION)
				{
					m.setAnnotation(Annotable.FILE, file);
					try
					{
						IOCoordinator.getInstance().save(m, (File)m.getAnnotation(Annotable.FILE));
					}catch(Exception e)
					{
						Hub.displayAlert(e.getMessage());
					}
					Hub.getWorkspace().fireRepaintRequired();
				}
				return false;
			}

		}
		return true;
	}
}
