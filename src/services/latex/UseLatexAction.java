package services.latex;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import main.Hub;

import org.pietschy.command.ToggleCommand;

/**
 * The class for the "Use LaTeX rendering" menu item.
 * 
 * @author Lenko Grigorov
 */
public class UseLatexAction extends AbstractAction {

	private boolean state;
	/**
	 * Default constructor; handy for exporting this command for group setup.
	 */
	public UseLatexAction(){
		super(Hub.string("useLatex"));
		setSelected(LatexManager.isLatexEnabled());
	}
	
	/**
	 * Changes the property state.
	 */
	public void actionPerformed(ActionEvent evt) {
		state = !state;
		LatexManager.setLatexEnabledFromMenu(state);
	}

	public void setSelected(boolean b)
	{
		state = b;
	}
}