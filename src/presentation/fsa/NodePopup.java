package presentation.fsa;

import java.awt.geom.Point2D.Float;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.Hub;
import presentation.Presentation;

import ui.command.GraphCommands.DeleteCommand;
import ui.command.GraphCommands.TextCommand;
import ui.command.NodeCommands.SelfLoopCommand;
import ui.command.NodeCommands.SetInitialCommand;
import ui.command.NodeCommands.SetMarkedCommand;

public class NodePopup extends JPopupMenu {

	private CircleNode node;
	private JMenuItem miSetMarked, miLabelNode, miSelfLoop, miSetInitial, miDeleteNode;
	private static GraphDrawingView view;
	
	// Using a singleton pattern (delayed instantiation) 
	// rather than initializing here since otherwise get java.lang.NoClassDefFoundError error
	private static NodePopup popup;
	
	private SetMarkedCommand markedCmd;
	private SetInitialCommand initialCmd;
	private TextCommand textCmd;
	private SelfLoopCommand selfLoopCmd;
	private DeleteCommand deleteCmd;	 
	
	private static final long serialVersionUID = 6664241416811568136L;

	protected static void showPopup(GraphDrawingView context, CircleNode n){
		view = context;
		if(popup == null)
		{
			popup = new NodePopup(n);
		}
//		else
		{		
			popup.setNode(n);
		}
		// TODO change sign of x or y as required
		Float p = n.getLayout().getLocation();
		//float r = n.getLayout().getRadius();
		
		// FIXME rework to eliminate call to getcurrentboard
		GraphDrawingView gdv=FSAToolset.getCurrentBoard();
		if(gdv!=null)
		{
			p=gdv.localToScreen(p);
			popup.show(context, (int)p.x, (int)p.y);
		}
	}
		
	protected NodePopup(CircleNode n) {
		super("Node Properties");		
		markedCmd = new SetMarkedCommand();
		initialCmd = new SetInitialCommand();
		selfLoopCmd = new SelfLoopCommand();
		textCmd = new TextCommand();
		deleteCmd = new DeleteCommand();
				
		miSetMarked = markedCmd.createMenuItem();
		miSetInitial = initialCmd.createMenuItem();
		miSelfLoop = selfLoopCmd.createMenuItem();
		miLabelNode = textCmd.createMenuItem();
		miDeleteNode = deleteCmd.createMenuItem();
						
		add(miLabelNode);
		add(miSetMarked);				
		add(miSetInitial);
		add(miSelfLoop);		
		add(new JPopupMenu.Separator());
		add(miDeleteNode);
		addPopupMenuListener(new PopupListener());
		setNode(n);
	}

	protected void setNode(CircleNode n){
		node = n;
		
		markedCmd.setNode(n);
		initialCmd.setNode(n);
		selfLoopCmd.setNode(n);
		deleteCmd.setElement(n);
		textCmd.setElement(n);
				
		markedCmd.setSelected(node.getState().isMarked());		
		initialCmd.setSelected(node.getState().isInitial());
	}
	
	class PopupListener implements PopupMenuListener {

		boolean wasCanceled = false;
		boolean becomeInvisible = false;
		/* (non-Javadoc)
		 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			view.repaint();
			if(wasCanceled == true)
			{
				wasCanceled = false;
			}else
			{
				view.setInterfaceInterruptionStatus(false);
				view.setAvoidNextDraw(false);
			}
			
		}
		public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
		{
			view.setInterfaceInterruptionStatus(true);
		}
		public void popupMenuCanceled(PopupMenuEvent arg0)
		{
			wasCanceled = true;
		}
	  }		  
}
