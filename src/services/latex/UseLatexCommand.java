package services.latex;

import org.pietschy.command.ToggleCommand;

/**
 * The class for the "Use LaTeX rendering" menu item.
 * 
 * @author Lenko Grigorov
 */
public class UseLatexCommand extends ToggleCommand {

	/**
	 * Default constructor; handy for exporting this command for group setup.
	 */
	public UseLatexCommand(){
		super("uselatex.command");
		setSelected(LatexManager.isLatexEnabled());
	}
	
	/**
	 * Changes the property state.
	 */
	public void handleSelection(boolean state) {
		LatexManager.setLatexEnabledFromMenu(state);
	}

}
