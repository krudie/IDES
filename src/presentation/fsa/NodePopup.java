package presentation.fsa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D.Float;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import model.fsa.ver1.State;

import ui.command.GraphCommands.DeleteCommand;
import ui.command.GraphCommands.TextCommand;
import ui.command.NodeCommands.*;

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
		if(popup == null) {
			popup = new NodePopup(n);
		}else{		
			popup.setNode(n);
		}
		// TODO change sign of x or y as required
		Float p = n.getLayout().getLocation();
		float r = n.getLayout().getRadius();		
		popup.show(context, (int)(p.x + r), (int)(p.y + r));
	}
		
	protected NodePopup(Node n) {
		super("Node Properties");		
		markedCmd = new SetMarkedCommand();
		initialCmd = new SetInitialCommand();
		selfLoopCmd = new SelfLoopCommand();
		textCmd = new TextCommand(view);
		deleteCmd = new DeleteCommand(view);
		
		ActionListener menuListener = new Listener();		
		
		miSetMarked = markedCmd.createMenuItem();
		miSetInitial = initialCmd.createMenuItem();
		miSelfLoop = selfLoopCmd.createMenuItem();
		miLabelNode = textCmd.createMenuItem();
		miDeleteNode = deleteCmd.createMenuItem();
						
		add(miSetInitial);
		add(miSetMarked);				
		add(miSelfLoop);		
		add(miLabelNode);
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
		
		miSetMarked.setSelected(node.getState().isMarked());
		miSetInitial.setSelected(node.getState().isInitial());
	}

	private class Listener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			Object o = e.getSource();			
			if(o.equals(miLabelNode)){
				// create and execute a Text command
				// open labeling input box as in text tool
	//			 DEBUG
				JOptionPane.showMessageDialog(null, "TODO open labeling dialog");
			
			}else{
				//OptionPane.showMessageDialog(null, "Can't figure out which item selected...", "Node Properties Dialog", JOptionPane.ERROR_MESSAGE);
			}		
		}		    			
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
