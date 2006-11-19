package presentation.fsa;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

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
		
		alignCmd = new AlignCommand(view);
		showGridCmd = new ShowGridCommand();
		
		miSelect = new JMenuItem("Select nodes", new ImageIcon(Hub.getResource("images/icons/graphic_modify.gif")));
		miSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setTool(GraphDrawingView.SELECT);
			}
		});
		miCreate = new JMenuItem("Create nodes and edges", new ImageIcon(Hub.getResource("images/icons/graphic_create.gif")));
		miCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setTool(GraphDrawingView.CREATE);
			}
		});
		miMove = new JMenuItem("Move nodes and edges", new ImageIcon(Hub.getResource("images/icons/graphic_move.gif")));
		miMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setTool(GraphDrawingView.MOVE);
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
	}
}
