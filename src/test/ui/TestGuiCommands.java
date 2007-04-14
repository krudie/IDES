package test.ui;

import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import org.pietschy.command.CommandManager;
import org.pietschy.command.HoverEvent;
import org.pietschy.command.HoverListener;
import org.pietschy.command.LoadException;

import ui.command.GraphCommands.CreateCommand;
import ui.command.GraphCommands.SelectCommand;


public class TestGuiCommands {
	
	public static void main(String[] args){
		
		//	load the xml command definition and initialize the manager.
		File myCommandFile = new File("commands.xml");
		try 
		{
		   CommandManager.defaultInstance().load(myCommandFile);
		   //	create a new instance of the command.
		   SelectCommand editCommand = new SelectCommand();		   
		   
//		    and use it!
		   AbstractButton button = editCommand.createButton();
		   JMenuItem menu = editCommand.createMenuItem();
		   
		   
		   CreateCommand createCommand = new CreateCommand();
		   menu = createCommand.createMenuItem();		   
		   
		   JFrame window = new JFrame();		   
		   window.getContentPane().add(button);		   
		   window.pack();
		   
		   CommandManager.defaultInstance().addHoverListener(new HoverListener() 
				   {
				      public void hoverStarted(HoverEvent e)
				      {
				         String text = e.getFace().getLongDescription();
				         if (text == null)
				            text = e.getFace().getDescription();
				         	System.out.println(text);
				      }

				      public void hoverEnded(HoverEvent e)
				      {
				    	  System.out.println("Hover ended.");
				      }
				   });

		   window.setVisible(true);
		}
		catch (LoadException e)
		{
		   // oops
		   e.printStackTrace();
		   System.exit(1);	
		}
		
	}
}
