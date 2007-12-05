package presentation.fsa;

import java.awt.geom.Point2D.Float;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.Hub;
import presentation.Presentation;
import presentation.fsa.commands.GraphActions;
import presentation.fsa.commands.NodeActions;
import presentation.fsa.commands.UIActions;
import presentation.fsa.commands.NodeActions.SetInitialAction;
import presentation.fsa.commands.NodeActions.SetMarkedAction;


public class NodePopup extends JPopupMenu {

	private CircleNode node;
	private JMenuItem miSetMarked, miLabelNode, miSelfLoop, miSetInitial, miDeleteNode;
	private static GraphDrawingView view;

	// Using a singleton pattern (delayed instantiation) 
	// rather than initializing here since otherwise get java.lang.NoClassDefFoundError error
	private static NodePopup popup;

	private SetMarkedAction markedCmd;
	private SetInitialAction initialCmd;
//	private GraphCommands.TextCommand textCmd;
//	private SelfLoopAction selfLoopCmd;
	private Action deleteCmd;

	private static final long serialVersionUID = 6664241416811568136L;

	protected static void showPopup(GraphDrawingView context, CircleNode n){
		view = context;
		popup = new NodePopup(context, n);
		Float p = n.getLayout().getLocation();
		p=context.localToScreen(p);
		popup.show(context,(int)p.x, (int)p.y);
	}

	protected NodePopup(GraphDrawingView gdv, CircleNode n) {
		super("Node Properties");		
//		initialCmd = new SetInitialCommand();
//		selfLoopCmd = new SelfLoopCommand();
		deleteCmd = gdv.getDeleteAction();

		miSetMarked = new JCheckBoxMenuItem(new NodeActions.SetMarkedAction(n));
		miSetInitial = new JCheckBoxMenuItem(new NodeActions.SetInitialAction(n));
		JMenuItem miSelfLoop = new JMenuItem(new UIActions.SelfLoopAction(gdv.getGraphModel(),n));
		miLabelNode = new JMenuItem(new UIActions.TextAction(n));
//		miDeleteNode.addActionListener(deleteCmd);

		add(miLabelNode);
		add(miSetMarked);				
		add(miSetInitial);
		add(miSelfLoop);		
		add(new JPopupMenu.Separator());
		add(deleteCmd);
		addPopupMenuListener(new PopupListener());
		node = n;
		miSetMarked.setSelected(n.getState().isMarked());
		miSetInitial.setSelected(n.getState().isInitial());
	}

//	protected void setNode(CircleNode n){
//		node = n;
////		markedCmd.setNode(n);
////		initialCmd.setNode(n);
////		selfLoopCmd.setNode(n);
////		deleteCmd.setElement(n);
////		deleteCmd.setContext(view);
////		textCmd.setElement(n);
//		miSetMarked.setSelected(n.getState().isMarked());
//		miSetInitial.setSelected(n.getState().isInitial());
////		markedCmd..setSelected(node.getState().isMarked());		
////		initialCmd.setSelected(node.getState().isInitial());
//	}

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
				view.setAvoidNextDraw(false);
			}

		}
		public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
		{
			view.setAvoidNextDraw(true);
		}
		public void popupMenuCanceled(PopupMenuEvent arg0)
		{
			wasCanceled = true;
		}
	}		  
}
