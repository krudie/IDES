package ui.listeners;

import io.FileOperations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import main.SystemVariables;
import model.DESModel;

import ui.UIStateModel;
import ui.command.Command;
import ui.command.CommandHistory;
import ui.command.CopyCommand;
import ui.command.CutCommand;
import ui.command.DeleteCommand;

public class MenuListenerFactory {

  public static ActionListener getCopyListener () {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  			
			  Command cmd = new CopyCommand("selected item", "drawing area"); 
			  UIStateModel.instance().getCommandHistory().add(cmd);
			  cmd.execute();	  
		  }
	  };
  }

  public static ActionListener getDeleteListener () {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  	
			  Command cmd = new DeleteCommand("selected item", "drawing area"); 
			  UIStateModel.instance().getCommandHistory().add(cmd);
			  cmd.execute();	  
		  }
	  };
  }
  
  public static ActionListener getCutListener () {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  			
			  Command cmd = new CutCommand("selected item", "drawing area"); 
			  UIStateModel.instance().getCommandHistory().add(cmd);
			  cmd.execute();
		  }
	  };
  }

  /**
   * Note that file operations do not go into the command history
   * for undoing etc.
   * 
   * @return a listener for all file menu items
   */
  public static ActionListener getFileMenuListener() {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  			
			  //JMenuItem item = (JMenuItem)arg0.getSource();			  
			  // figure out which file menu item was selected
			  //if(item.getName().equals(""))
			  // For now just open an existing system
			  DESModel des = FileOperations.openSystem();
			  if(des != null){
				  UIStateModel.instance().setDESModel(des);			  			  
				  // refresh the views
				  UIStateModel.instance().refresh();
			  }
		  }
	  };
	  
  }
  
}

