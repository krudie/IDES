package ui.command;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Hub;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.ToggleCommand;

import presentation.Presentation;
import presentation.fsa.FSAToolset;
import presentation.fsa.GraphDrawingView;

import services.latex.LatexManager;
import ui.MainWindow;
import ui.OptionsWindow;

/**
 * The class with the commands from the "Options" menu.
 *  
 * @author Lenko Grigorov
 */
public class OptionsCommands {

// NOTE: migrated to services.latex
//-----------------------------------
//	/**
//	 * The class for the "Use LaTeX rendering" menu item.
//	 * 
//	 * @author Lenko Grigorov
//	 */
//	public static class UseLatexCommand extends org.pietschy.command.ToggleCommand
//	{
//		/**
//		 * Default constructor; handy for exporting this command for group setup.
//		 */
//		public UseLatexCommand(){
//			super("uselatex.command");
//			setSelected(LatexManager.isLatexEnabled());
//		}
//		
//		/**
//		 * Changes the property state.
//		 */
//		public void handleSelection(boolean state) {
//			LatexManager.setLatexEnabled(state);		
//		}
//		
//	}
	
	/**
	 * The class for the "More options..." menu item.
	 * 
	 * @author Lenko Grigorov
	 */
	public static class MoreOptionsAction extends AbstractAction {
		private static String text = "More options...";
		private static ImageIcon icon = new ImageIcon();
		
		/**
		 * Default constructor; handy for exporting this command for group setup.
		 */
		public MoreOptionsAction(){
			super(text,icon);
		}
		
		/**
		 * Executes the command.
		 */
		public void actionPerformed(ActionEvent e) {
			new OptionsWindow();		
		}
	}
	
	/**
	 * The class for that toggles grid display.
	 * 
	 * @author Lenko Grigorov
	 */
	public static class ShowGridAction extends AbstractAction{

		private static String text = "Toggle grid display";
		private static ImageIcon icon = new ImageIcon();
		public boolean state = 	false;
		
		public ShowGridAction()
		{
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/view_grid.gif")));
		}	

				
		public void disableGrid()
		{
			state = false;
			for(Presentation p:Hub.getWorkspace().getPresentationsOfType(GraphDrawingView.class))
				((GraphDrawingView)p).setShowGrid(state);
			FSAToolset.gridState=state;
			CommandManager_new.getInstance().updateToggleButton(CommandManager_new.TOGGLE_BUTTON, CommandManager_new.GRID_BUTTON,state);
		}
		
		/**
		 * Changes the property state.
		 */
		public void actionPerformed(ActionEvent e) {
			state=(FSAToolset.gridState==true?false:true);
			MainWindow mw=(MainWindow)Hub.getMainWindow();
			if(state)
			{
				mw.getZoomControl().setZoom(1);
			}
			for(Presentation p:Hub.getWorkspace().getPresentationsOfType(GraphDrawingView.class))
				((GraphDrawingView)p).setShowGrid(state);
			FSAToolset.gridState=state;
			CommandManager_new.getInstance().updateToggleButton(CommandManager_new.TOGGLE_BUTTON, CommandManager_new.GRID_BUTTON,state);
		}
	}

}
