package ui.command;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;

import main.Hub;

import org.pietschy.command.CommandManager;
import org.pietschy.command.ToggleCommand;
import org.pietschy.command.ToggleVetoException;

import presentation.LayoutShell;
import presentation.fsa.FSAGraph;

import services.latex.LatexManager;

public class UniformNodesAction extends AbstractAction {

	protected static final String PROPERTY_NAME="uniformNodeSize";
	private boolean state;
	
	public UniformNodesAction() {
		super(Hub.string("uniformNodeSize"));
		setSelected(Hub.persistentData.getBoolean(PROPERTY_NAME));
	}

	public void setSelected(boolean b)
	{
		state = b;
	}
	
	public static boolean isSelected()
	{
		return Hub.persistentData.getBoolean("uniformNodeSize");
	}
	
	public void actionPerformed(ActionEvent e) {
		state = !state;	
		Hub.persistentData.setBoolean(PROPERTY_NAME,state);
			
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
