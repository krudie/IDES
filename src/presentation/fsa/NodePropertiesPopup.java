package presentation.fsa;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import model.fsa.ver1.State;

import ui.command.EditCommands.SetAttributeCommand; 

public class NodePropertiesPopup extends JPopupMenu {

	private Node node;
	private JMenuItem miLabelNode, miSelfLoop;
	private JCheckBoxMenuItem miSetMarked, miSetInitial;
	
	// Using a singleton pattern (delayed instantiation) 
	// rather than initializing here since otherwise get java.lang.NoClassDefFoundError error
	private static NodePropertiesPopup popup;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6664241416811568136L;

	protected static void showPopup(Component context, Node n){
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
		node = n;
	
		ActionListener menuListener = new Listener();
		
		// TODO change to undoable commands
		miSetMarked = new JCheckBoxMenuItem("Marked");
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
	}

	protected void setNode(Node n){
		node = n;
		miSetMarked.setSelected(node.getState().isMarked());
		miSetInitial.setSelected(node.getState().isInitial());
	}

	private class Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			Object o = e.getSource();
			
			// FIXME Publisher (Automaton that holds the state) must be notified of update !!!
			// Should be done through GraphModel object which then notifies the FSA 
			
			if(o.equals(miSetMarked)){
				// construct and execute a SetAttributeCommand
	// 			DEBUG
//				JOptionPane.showMessageDialog(null, "mark");
				new SetAttributeCommand(node.getState(), State.ATTR_MARKED, Boolean.toString(miSetMarked.isSelected())).execute();
				node.update();
			}else if(o.equals(miSetInitial)){
				// construct and execute a SetAttributeCommand
	//			 DEBUG
				// JOptionPane.showMessageDialog(null, "initial");
				new SetAttributeCommand(node.getState(), State.ATTR_MARKED, Boolean.toString(miSetMarked.isSelected())).execute();
				node.update();
			}else if(o.equals(miLabelNode)){
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
