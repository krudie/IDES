package ui.command;

import main.Hub;

import org.pietschy.command.CommandManager;
import org.pietschy.command.ToggleCommand;
import org.pietschy.command.ToggleVetoException;

import services.latex.LatexManager;

public class UniformNodesCommand extends ToggleCommand {

	protected static final String PROPERTY_NAME="uniformNodeSize";
	
	public UniformNodesCommand() {
		super("uniformnodes.command");
		setSelected(Hub.persistentData.getBoolean(PROPERTY_NAME));
	}

	protected void handleSelection(boolean arg0) throws ToggleVetoException {
		Hub.persistentData.setBoolean(PROPERTY_NAME,!isSelected());
		if(Hub.getMainWindow()!=null)
			((ui.MainWindow)Hub.getMainWindow()).getDrawingBoard().update();
	}

}
