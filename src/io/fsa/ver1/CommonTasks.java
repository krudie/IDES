/**
 * 
 */
package io.fsa.ver1;

import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import main.Hub;
import main.IncompleteWorkspaceDescriptorException;
import main.WorkspaceDescriptor;

/**
 *
 * @author Lenko Grigorov
 */
public class CommonTasks {

	private CommonTasks(){}
	
	/**
	 * Asks the user if they want to save the workspace
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
	 * @param gm the GraphModel that needs to be saved
	 * @return false if the process was cancelled
	 */
	public static boolean handleUnsavedModel(presentation.fsa.FSMGraph gm)
	{
		int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
				Hub.string("saveChangesAskModel")+"\""+gm.getName()+"\"?",
				Hub.string("saveChangesModelTitle"),
				JOptionPane.YES_NO_CANCEL_OPTION);
		if(choice!=JOptionPane.YES_OPTION&&choice!=JOptionPane.NO_OPTION)
			return false;
		if(choice==JOptionPane.YES_OPTION)
		{
			if(FileOperations.saveAutomaton(gm.getAutomaton(),gm.getAutomaton().getFile()))
			{
				gm.setDirty(false);
				gm.notifyAllSubscribers();
			}
			else
				return false;
		}
		return true;
	}
}
