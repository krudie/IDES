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
 * @author Squirrel
 *
 */
public class EdgePopup extends JPopupMenu {

	private Edge edge;
	private DeleteCommand deleteCmd;
	private JMenuItem miModify, miEditEvents, miStraighten, miDeleteEdge;
	private static GraphDrawingView view;
	
	// Using a singleton pattern (delayed instantiation) 
	// rather than initializing here since otherwise get java.lang.NoClassDefFoundError error
	private static EdgePopup popup;
	
	/**
	 * @param e
	 */
	protected EdgePopup(Edge e) {
		// TODO Auto-generated constructor stub
		miModify = new JMenuItem("Modify curve", new ImageIcon("images/icons/graphic_alledges.gif"));
		MenuListener listener = new MenuListener();
		miModify.addActionListener(listener);
		add(miModify);
		miEditEvents = new JMenuItem("Label with Events");
		miEditEvents.addActionListener(listener);
		add(miEditEvents);
		deleteCmd = new DeleteCommand(view);
		miDeleteEdge = deleteCmd.createMenuItem();
		add(miDeleteEdge);		
		
		add(new JPopupMenu.Separator());
		miStraighten = new JMenuItem("Straighten");
		miStraighten.setEnabled(false);
		add(miStraighten);
		// TODO arc more
		// TODO arc less
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
		popup.show(context, (int)p.x, (int)p.y);
	}
			
	public void setEdge(Edge edge){
		this.edge = edge;
		deleteCmd.setElement(edge);
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
