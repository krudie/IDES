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

import presentation.Presentation;
import presentation.fsa.commands.GraphCommands;
import presentation.fsa.commands.GraphCommands.DeleteAction;
import ui.command.EdgeCommands;

import main.Hub;

/**
 * A popup menu providing operations to modify or delete an edge.
 * 
 * TODO enable Symmetrize command after the command has been debugged and tested. 
 * 
 * @author Helen Bretzke
 */
public class EdgePopup extends JPopupMenu {

	private Edge edge;
	private DeleteAction deleteCmd;
	private JMenuItem miModify, miEditEvents, miStraighten, miSymmetrize, miDeleteEdge, miArcMore, miArcLess; // , miSymmetrize;
	private static GraphDrawingView view;
	
	// Using a singleton pattern (delayed instantiation) 
	// rather than initializing here since otherwise get java.lang.NoClassDefFoundError error
	private static EdgePopup popup;
	
	/**
	 * Creates a popup menu to for displaying when user right-clicks on an edge.
	 * 
	 * @param e the edge to associate with this menu instance
	 */
	protected EdgePopup(Edge e) {		

		miEditEvents = new JMenuItem(new EdgeCommands.CreateEventCommand(view,e));
		add(miEditEvents);

		miSymmetrize = new JMenuItem(new EdgeCommands.SymmetrizeEdgeAction(view,e));		
		add(miSymmetrize);
		
		miStraighten = new JMenuItem("Straighten");
//		miStraighten.addActionListener(listener);
		add(miStraighten);
		
		miArcMore = new JMenuItem(Hub.string("arcmore"));
//		miArcMore.addActionListener(listener);
		add(miArcMore);
				
		miArcLess = new JMenuItem(Hub.string("arcless"));
//		miArcLess.addActionListener(listener);
		add(miArcLess);		

		add(new JPopupMenu.Separator());
		
		deleteCmd = new DeleteAction();
		add(deleteCmd);
		
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
		// FIXME rework to eliminate call to getcurrentboard
		GraphDrawingView gdv=FSAToolset.getCurrentBoard();
		if(gdv!=null)
		{
			p=gdv.localToScreen(p);
			popup.show(context, (int)p.x,
				(int)p.y);
		}
	}
			
	/**
	 * Associates the given edge with this menu instance. 
	 * 
	 * @param edge the edge to associate with this menu instance
	 */
	public void setEdge(Edge edge){		
		this.edge = edge;
		deleteCmd.setElement(edge);
		deleteCmd.setContext(view);
		if(edge != null){			
			miStraighten.setVisible(edge.canBeStraightened());
			// if the edge can't be straightened, then we assume we cannot 
			// otherwise tamper with its shape
			miArcLess.setVisible(edge.canBeStraightened());
			miArcMore.setVisible(edge.canBeStraightened());
			miSymmetrize.setVisible(edge.canBeStraightened());
			
			// Don't enable straightening, flattening or symmetrizing if edge is already straight
			// since there is nothing to to.  
			miArcLess.setEnabled(!edge.isStraight());
			miStraighten.setEnabled(!edge.isStraight());
			miSymmetrize.setEnabled(!edge.isStraight());
		}		
	}

//	/**
//	 * Listens to events on the EdgePopup menu.
//	 * 
//	 * @author helen bretzke
//	 */
//	class MenuListener implements ActionListener {
//
//		/* (non-Javadoc)
//		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//		 */
//		public void actionPerformed(ActionEvent arg0) {
//			Object source = arg0.getSource();
//			if(source.equals(miModify)){				
//				edge.getHandler().setVisible(true);
//				view.setTool(GraphDrawingView.MODIFY);
//			}else if(source.equals(miEditEvents)){				
//				EdgeLabellingDialog.showDialog(view, edge);
//				
//			// TODO should be UNDOABLE graph commands
//				
//			}else if(source.equals(miSymmetrize)){				
//				edge.getGraph().symmetrize(edge);
//			}else if(source.equals(miArcMore)){
//				edge.getGraph().arcMore(edge);
//			}else if(source.equals(miArcLess)){
//				edge.getGraph().arcLess(edge);
//			}else if(source.equals(miStraighten)){
//				edge.getGraph().straighten(edge);
//			}else{
//				Hub.displayAlert("Edge popup: " + source.toString());
//			}			
//		}		
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
