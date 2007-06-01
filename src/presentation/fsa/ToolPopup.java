package presentation.fsa;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import presentation.fsa.NodePopup.PopupListener;

import main.Hub;
import ui.command.GraphCommands.AlignCommand;
import ui.command.OptionsCommands.ShowGridCommand;

/**
 * A default context menu which allows the user to switch drawing tools and
 * perform other operations on the DES as a whole.
 * @author chris mcaloney
 */
@SuppressWarnings("serial")
public class ToolPopup extends JPopupMenu {
	
	private static ToolPopup popup;
	private static GraphDrawingView view;
	
	private JMenuItem miSelect, miCreate, miMove, miAlign, miShowGrid;
	
	private AlignCommand alignCmd;
	private ShowGridCommand showGridCmd;

	public static void showPopup(GraphDrawingView context, MouseEvent m) {
		view = context;
		if (popup == null) {
			popup = new ToolPopup();
		}
		Point p = m.getPoint();
		popup.show(context, (int)p.x, (int)p.y);
	}
	
	protected ToolPopup() {
		super("Graph Operations");
		
		alignCmd = new AlignCommand();
		showGridCmd = new ShowGridCommand();
		
		miSelect = new JMenuItem("Select nodes", new ImageIcon(Hub.getResource("images/icons/graphic_modify.gif")));
		miSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setTool(GraphDrawingView.SELECT);
				view.setPreferredTool(GraphDrawingView.SELECT);
			}
		});
		miCreate = new JMenuItem("Create nodes and edges", new ImageIcon(Hub.getResource("images/icons/graphic_create.gif")));
		miCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setTool(GraphDrawingView.CREATE);
				view.setPreferredTool(GraphDrawingView.CREATE);
			}
		});
		miMove = new JMenuItem("Move nodes and edges", new ImageIcon(Hub.getResource("images/icons/graphic_move.gif")));
		miMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setTool(GraphDrawingView.MOVE);
				view.setPreferredTool(GraphDrawingView.SELECT);
			}
		});
		
		miAlign = alignCmd.createMenuItem();
		miShowGrid = showGridCmd.createMenuItem();
		
		add(miSelect);
		add(miCreate);
		add(miMove);
		add(new JPopupMenu.Separator());
		add(miAlign);
		add(miShowGrid);
		addPopupMenuListener(new PopupListener());
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
