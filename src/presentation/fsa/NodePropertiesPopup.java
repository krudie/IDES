package presentation.fsa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D.Float;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ui.command.EditCommands.SetBooleanAttributeCommand;

public class NodePropertiesPopup extends JPopupMenu {

	private Node node;
	private JMenuItem miSetMarked, miLabelNode, miSelfLoop, miSetInitial;
	private static GraphDrawingView view;
	
	// Using a singleton pattern (delayed instantiation) 
	// rather than initializing here since otherwise get java.lang.NoClassDefFoundError error
	private static NodePropertiesPopup popup;
	
	private SetBooleanAttributeCommand booleanCmd;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6664241416811568136L;

	protected static void showPopup(GraphDrawingView context, Node n){
		view = context;
		if(popup == null) {
			popup = new NodePropertiesPopup(n);
		}else{		
			popup.setNode(n);
		}
		// TODO change sign of x or y as required
		Float p = n.getLayout().getLocation();
		float r = n.getLayout().getRadius();		
		popup.show(context, (int)(p.x + r), (int)(p.y + r));
	}
		
	protected NodePropertiesPopup(Node n) {
		super("State Properties");		
		booleanCmd = new SetBooleanAttributeCommand();
		ActionListener menuListener = new Listener();
		
		// TODO change to undoable commands
		miSetMarked = booleanCmd.createMenuItem();
		miSetMarked.setText("Marked");
		miSetInitial = new JCheckBoxMenuItem("Initial");
		miLabelNode = new JMenuItem("Label");
		miSelfLoop = new JMenuItem("Self Loop");		
		
		miSetMarked.addActionListener(menuListener);
		miSetInitial.addActionListener(menuListener);
		miLabelNode.addActionListener(menuListener);
		miSelfLoop.addActionListener(menuListener);
				
		add(miSetMarked);
		add(miSetInitial);		
		add(new JPopupMenu.Separator());
		add(miLabelNode);
		
		// TODO should be checkbox to add or remove self loop
		add(miSelfLoop);
		
		setNode(n);
	}

	protected void setNode(Node n){
		node = n;
		
		booleanCmd.setAttributeName("marked");
		booleanCmd.setElement(n.getState());
		
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
			}else if(o.equals(miSelfLoop)){
				// Create and elecute a create edge command
	//			 DEBUG
				JOptionPane.showMessageDialog(null, "TODO add or remove self loop");
			}else{
				JOptionPane.showMessageDialog(null, "Can't figure out which item selected...", "Node Properties Dialog", JOptionPane.ERROR_MESSAGE);
			}
		}		    			
	}
}
