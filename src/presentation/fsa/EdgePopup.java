/**
 * 
 */
package presentation.fsa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D.Float;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.Hub;
import ui.command.GraphCommands.DeleteCommand;

/**
 * @author Helen Bretzke 2006
 *
 */
public class EdgePopup extends JPopupMenu {

	private Edge edge;
	private DeleteCommand deleteCmd;
	private JMenuItem miModify, miEditEvents, miStraighten, miDeleteEdge, miSymmetrize, miArcMore, miArcLess;
	private static GraphDrawingView view;
	
	// Using a singleton pattern (delayed instantiation) 
	// rather than initializing here since otherwise get java.lang.NoClassDefFoundError error
	private static EdgePopup popup;
	
	/**
	 * @param e
	 */
	protected EdgePopup(Edge e) {
		// TODO Auto-generated constructor stub
//		miModify = new JMenuItem("Modify curve", new ImageIcon(Hub.getResource("images/icons/graphic_alledges.gif")));
		MenuListener listener = new MenuListener();
//		miModify.addActionListener(listener);
//		add(miModify);
		miEditEvents = new JMenuItem("Label with events", new ImageIcon(Hub.getResource("images/icons/machine_alpha.gif")));
		miEditEvents.addActionListener(listener);
		add(miEditEvents);

		miSymmetrize = new JMenuItem(Hub.string("symmetrize"));
		//TODO symmetrize should be an undoable command
		miSymmetrize.addActionListener(listener);
		add(miSymmetrize);
		
//		miStraighten = new JMenuItem("Straighten");
//		miStraighten.setEnabled(false);
//		add(miStraighten);

		add(new JPopupMenu.Separator());		
		deleteCmd = new DeleteCommand(view);
		miDeleteEdge = deleteCmd.createMenuItem();
		add(miDeleteEdge);
		
		// TODO arc more
		miArcMore = new JMenuItem(Hub.string("arcmore"));
		miArcMore.addActionListener(listener);
		add(miArcMore);
		
		// TODO arc less
		miArcLess = new JMenuItem(Hub.string("arcless"));
		miArcLess.addActionListener(listener);
		add(miArcLess);
		// TODO reverse
		
		addPopupMenuListener(new PopupListener());
		setEdge(e);
	}

	protected static void showPopup(GraphDrawingView context, Edge e){
		view = context;
		if(popup == null) {
			popup = new EdgePopup(e);
		}else{		
			popup.setEdge(e);
		}
		Float p = e.getLayout().getLocation();
		p=((ui.MainWindow)Hub.getMainWindow()).getDrawingBoard().localToScreen(p);
		popup.show(context, (int)p.x,
				(int)p.y);
	}
			
	public void setEdge(Edge edge){		
		this.edge = edge;
		deleteCmd.setElement(edge);
		if(edge != null){
			miArcLess.setEnabled(!edge.isStraight());
		}
	}

	class MenuListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			Object source = arg0.getSource();
			if(source.equals(miModify)){				
				edge.getHandler().setVisible(true);
				view.setTool(GraphDrawingView.MODIFY);
			}else if(source.equals(miEditEvents)){				
				EdgeLabellingDialog.showDialog(view, edge);
				
			// NOTE these last three cases do not apply to reflexive edges
			// and they should be UNDOABLE graph commands
			}else if(source.equals(miSymmetrize)){				
				Hub.getWorkspace().getActiveGraphModel().symmetrize(edge);
			}else if(source.equals(miArcMore)){
				Hub.getWorkspace().getActiveGraphModel().arcMore(edge);
			}else if(source.equals(miArcLess)){
				Hub.getWorkspace().getActiveGraphModel().arcLess(edge);			
			}else{
				Hub.displayAlert("Edge popup: " + source.toString());
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
		public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {}
		public void popupMenuCanceled(PopupMenuEvent arg0) {}
	  }	  
}
