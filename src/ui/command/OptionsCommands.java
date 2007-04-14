package ui.command;

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
	public static class MoreOptionsCommand extends ActionCommand {

		/**
		 * Default constructor; handy for exporting this command for group setup.
		 */
		public MoreOptionsCommand(){
			super("moreoptions.command");
		}
		
		/**
		 * Executes the command.
		 */
		public void handleExecute() {
			new OptionsWindow();		
		}
	}
	
	/**
	 * The class for that toggles grid display.
	 * 
	 * @author Lenko Grigorov
	 */
	public static class ShowGridCommand extends ToggleCommand {

		/**
		 * Default constructor; handy for exporting this command for group setup.
		 */
		public ShowGridCommand(){
			super("showgrid.command");
		}
		
		/**
		 * Changes the property state.
		 */
		public void handleSelection(boolean state) {
			MainWindow mw=(MainWindow)Hub.getMainWindow();
			if(!isSelected())
			{
				mw.getZoomControl().setZoom(1);
			}
			for(Presentation p:Hub.getWorkspace().getPresentationsOfType(GraphDrawingView.class))
				((GraphDrawingView)p).setShowGrid(!isSelected());
			FSAToolset.gridState=!isSelected();
		}

	}
}
