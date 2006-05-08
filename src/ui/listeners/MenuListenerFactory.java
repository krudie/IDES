package ui.listeners;

import io.fsa.ver1.FileOperations;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import main.SystemVariables;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;

import ui.GraphModel;
import ui.Publisher;
import ui.UIStateModel;
import ui.command.Command;
import ui.command.CommandHistory;
import ui.command.CopyCommand;
import ui.command.CutCommand;
import ui.command.DeleteCommand;

/**
 * FIXME will need to wire these listeners up to both the menu and the toolbar.
 * Don't want to make duplicates of each object by calling these methods twice.
 * 
 * @author helen
 *
 */
public class MenuListenerFactory {

	/**
	 * STUB 
	 * 
	 * @return
	 */
  public static ActionListener makeCopyListener () {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  			
			  Command cmd = new CopyCommand("copied item", "drawing area"); 
			  UIStateModel.instance().getCommandHistory().add(cmd);
			  cmd.execute();	  
		  }
	  };
  }

	/**
	 * STUB 
	 * 
	 * @return
	 */
  public static ActionListener makeDeleteListener () {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  	
			  Command cmd = new DeleteCommand("deleted item", "drawing area"); 
			  UIStateModel.instance().getCommandHistory().add(cmd);
			  cmd.execute();	  
		  }
	  };
  }
  
	/**
	 * STUB 
	 * 
	 * @return
	 */
  public static ActionListener makeCutListener () {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  			
			  Command cmd = new CutCommand("cut item", "drawing area"); 
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
  public static ActionListener makeFileMenuListener() {
	  return new ActionListener() {
		  public void actionPerformed(ActionEvent arg0) {			  			
			  JMenuItem item = (JMenuItem)arg0.getSource();			  
			  Container c = item.getParent();			  
			  JFileChooser chooser = new JFileChooser(FileOperations.DEFAULT_DIRECTORY);
			  Automaton fsa = null;
			  int returnVal = chooser.showOpenDialog(c);
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
	  };
	  
  }
  
}

