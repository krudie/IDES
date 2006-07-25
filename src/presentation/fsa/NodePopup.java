package presentation.fsa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D.Float;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import ui.command.GraphCommands.DeleteCommand;
import ui.command.GraphCommands.TextCommand;
import ui.command.NodeCommands.SelfLoopCommand;
import ui.command.NodeCommands.SetInitialCommand;
import ui.command.NodeCommands.SetMarkedCommand;

public class NodePopup extends JPopupMenu {

	private Node node;
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
	 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6664241416811568136L;

	protected static void showPopup(GraphDrawingView context, Node n){
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
		p=((ui.MainWindow)main.Hub.getMainWindow()).getDrawingBoard().localToScreen(p);
		popup.show(context, (int)p.x, (int)p.y);
	}
		
	protected NodePopup(Node n) {
		super("Node Properties");		
		markedCmd = new SetMarkedCommand();
		initialCmd = new SetInitialCommand();
		selfLoopCmd = new SelfLoopCommand();
		textCmd = new TextCommand(view);
		deleteCmd = new DeleteCommand(view);
				
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

	protected void setNode(Node n){
		node = n;
		
		markedCmd.setNode(n);
		initialCmd.setNode(n);
		selfLoopCmd.setNode(n);
		deleteCmd.setElement(n);
		textCmd.setElement(n);
		
		//TODO this is bad since setSelected actually calls the selection handling
		//miSetMarked.setSelected(node.getState().isMarked());
		markedCmd.setSelected(node.getState().isMarked());
		//miSetInitial.setSelected(node.getState().isInitial());
		initialCmd.setSelected(node.getState().isInitial());
		//miSelfLoop.setSelected(node.hasSelfLoop());
	}
	
	
	  class PopupListener implements PopupMenuListener {

		/* (non-Javadoc)
		 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			view.repaint();
		}

		/* (non-Javadoc)
		 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {}

		/* (non-Javadoc)
		 * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuCanceled(PopupMenuEvent arg0) {}
	  }	  
}
