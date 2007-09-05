package ui.command;

import java.util.Iterator;

import main.Hub;

import org.pietschy.command.CommandManager;
import org.pietschy.command.ToggleCommand;
import org.pietschy.command.ToggleVetoException;

import presentation.LayoutShell;
import presentation.fsa.FSAGraph;

import services.latex.LatexManager;

public class UniformNodesCommand extends ToggleCommand {

	protected static final String PROPERTY_NAME="uniformNodeSize";
	
	public UniformNodesCommand() {
		super("uniformnodes.command");
		setSelected(Hub.persistentData.getBoolean(PROPERTY_NAME));
	}

	protected void handleSelection(boolean arg0) throws ToggleVetoException {
		Hub.persistentData.setBoolean(PROPERTY_NAME,!isSelected());
		
		// set all FSMGraphs to dirty
		// so when repaint happens, graph will recompute its layout.
		// NOTE depends on FSMGraph extending GraphElement
		// ??? will FSMGraph fire any notifications ?
		for(Iterator<LayoutShell> i=Hub.getWorkspace().getLayoutShells();i.hasNext();)
		{
			LayoutShell shell=i.next();
			if(shell instanceof FSAGraph)
			{
				((FSAGraph)shell).setNeedsRefresh(true);
			}
		}

			Hub.getWorkspace().fireRepaintRequired();
	}

}
