package ui.command;

import main.Hub;

import org.pietschy.command.CommandManager;
import org.pietschy.command.ToggleCommand;
import org.pietschy.command.ToggleVetoException;

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
//		if(Hub.getWorkspace().getActiveGraphModel()!=null)
//			Hub.getWorkspace().getActiveGraphModel().update();
		
		// set FSMGraph (the activeGraphModel()) to dirty
		// so when repaint happens, graph will recompute its layout.
		// NOTE depends on FSMGraph extending GraphElement
		// ??? will FSMGraph fire any notifications ?
		if(Hub.getWorkspace().getActiveLayoutShell()!= null &&
				Hub.getWorkspace().getActiveLayoutShell() instanceof FSAGraph){
			((FSAGraph)Hub.getWorkspace().getActiveLayoutShell()).setNeedsRefresh(true);
		// gm.setDirty(true);
			Hub.getWorkspace().fireRepaintRequired();
		}
	}

}
