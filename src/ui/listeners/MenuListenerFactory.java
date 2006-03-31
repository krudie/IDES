package ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import main.SystemVariables;

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

}
