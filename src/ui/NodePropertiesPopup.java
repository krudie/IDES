package ui;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import presentation.fsa.Node;

public class NodePropertiesPopup extends JPopupMenu {

	private Node node;
	private JMenuItem miLabelNode, miSelfLoop;
	private JCheckBoxMenuItem miSetMarked, miSetInitial;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6664241416811568136L;

	public NodePropertiesPopup(Node node) {
		super();		
		this.node = node;
	
		// TODO change to undoable commands
		miSetMarked = new JCheckBoxMenuItem("Marked");
		miSetInitial = new JCheckBoxMenuItem("Initial");
		miLabelNode = new JMenuItem("Label");
		miSelfLoop = new JMenuItem("Self Loop");
		
		// miSetMarked.setSelected(node.???);
		// miSetInitial.setSelected(node.???);
		
		add(miSetMarked);
		add(miSetInitial);
		// TODO add(separator);
		add(miLabelNode);
	}
		
}
