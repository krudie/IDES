package presentation.fsa;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import presentation.fsa.NodePopup.PopupListener;
import presentation.fsa.actions.GraphActions;
import presentation.fsa.actions.UIActions;

import main.Hub;
import ui.actions.OptionsActions;

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
	
		miSelect = new JMenuItem(new UIActions.SelectTool());
		miCreate = new JMenuItem(new UIActions.CreateTool());
		miMove = new JMenuItem(new UIActions.MoveTool());
		miAlign = new JMenuItem(ContextAdaptorHack.context.getAlignAction());
//		miShowGrid = new JMenuItem(new OptionsCommands.ShowGridAction());

		add(miSelect);
		add(miCreate);
		add(miMove);
		add(new JPopupMenu.Separator());
		add(miAlign);
//		add(miShowGrid);
		PopupListener popListener = new PopupListener();
		addPopupMenuListener(popListener);
	}
	class PopupListener implements PopupMenuListener{
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

	public void processMouseEvent(MouseEvent event,
			MenuElement[] path,
			MenuSelectionManager manager)
	{
	}


}
