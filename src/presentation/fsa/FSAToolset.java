package presentation.fsa;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.Toolset;
import ides.api.plugin.presentation.UIDescriptor;
import ides.api.plugin.presentation.UnsupportedModelException;

import java.util.Collection;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import presentation.fsa.actions.GraphActions;
import presentation.fsa.actions.UIActions;
import util.BooleanUIBinder;

/**
 * The toolset for {@link FSAModel}s.
 * 
 * @see Toolset
 * @author Lenko Grigorov
 */
public class FSAToolset implements Toolset
{
	protected static class FSAUIDescriptor implements UIDescriptor
	{
		protected FSAGraph shell;

		protected Presentation[] views;

		protected static JToolBar toolbar = null;

		protected static JMenu graphMenu = null;

		private static Action selectAction = null;

		private static Action createAction = null;

		private static Action moveAction = null;

		private Action alignAction = null;

		private Action gridAction = null;

		private static BooleanUIBinder gridBinder = new BooleanUIBinder();

		private static JToggleButton gridButton = null;

		private static JMenuItem alignMenuItem = new JMenuItem();

		private static JButton alignButton = new JButton();

		public FSAUIDescriptor(FSAModel model)
		{
			views = new Presentation[2];
			GraphDrawingView drawingBoard = new GraphDrawingView(
					model,
					gridBinder);
			gridAction = new UIActions.ShowGridAction(drawingBoard);
			alignAction = drawingBoard.getAlignAction();
			drawingBoard.setName(Hub.string("graph"));
			shell = drawingBoard.getGraphModel();
			views[0] = drawingBoard;
			views[1] = new EventView(shell);
			((EventView)views[1]).setName(Hub.string("events"));
		}

		public Presentation[] getMainPanePresentations()
		{
			return views;
		}

		public Presentation[] getLeftPanePresentations()
		{
			return new Presentation[0];
		}

		public Presentation[] getRightPanePresentations()
		{
			return new Presentation[0];
		}

		protected void setupActions()
		{
			if (selectAction == null)
			{
				selectAction = new UIActions.SelectTool();
			}
			if (createAction == null)
			{
				createAction = new UIActions.CreateTool();
			}
			if (moveAction == null)
			{
				moveAction = new UIActions.MoveTool();
			}
		}

		public JMenu[] getMenus()
		{
			if (graphMenu == null)
			{
				setupActions();
				graphMenu = new JMenu(Hub.string("menuGraph"));
				// Initializing the menu items for the "graphMenu"
				JMenuItem select = new JMenuItem(selectAction);
				JMenuItem create = new JMenuItem(createAction);
				JMenuItem move = new JMenuItem(moveAction);
				JMenuItem showGrid = new JCheckBoxMenuItem(gridAction);
				gridBinder.bind(showGrid);
				// this is a dummy menu item since it'll be replaced
				JCheckBoxMenuItem uniformNodeSize = new JCheckBoxMenuItem();
				// Adding the menu items to the "graphMenu"
				graphMenu.add(select);
				graphMenu.add(create);
				graphMenu.add(move);
				graphMenu.addSeparator();
				graphMenu.add(alignMenuItem);
				graphMenu.add(showGrid);
				graphMenu.add(uniformNodeSize);
			}
			alignMenuItem.setAction(alignAction);
			// get the "use uniform node size" menu item for the current shell
			String MENU_ITEM = "useUniformNodeSizeMenuItem";
			JMenuItem useUniformNodeSizeMenu = (JMenuItem)shell
					.getAnnotation(MENU_ITEM);
			if (useUniformNodeSizeMenu == null)
			{
				useUniformNodeSizeMenu = new JCheckBoxMenuItem(
						new GraphActions.UniformNodesAction(shell));
				shell.getUseUniformRadiusBinder().bind(useUniformNodeSizeMenu);
				shell.setAnnotation(MENU_ITEM, useUniformNodeSizeMenu);
			}
			// update menu with current layout shell's uniform node size menu
			// item
			graphMenu.remove(graphMenu.getMenuComponentCount() - 1);
			graphMenu.add(useUniformNodeSizeMenu);
			return new JMenu[] { graphMenu };
		}

		public JToolBar getToolbar()
		{
			if (toolbar == null)
			{
				setupActions();
				toolbar = new JToolBar();
			}
			if (toolbar.getComponentCount() == 0)
			{
				toolbar.add(selectAction);
				toolbar.add(createAction);
				toolbar.add(moveAction);
				toolbar.addSeparator();
				alignButton.setAction(alignAction);
				alignButton.setText("");
				toolbar.add(alignButton);
				gridBinder.unbind(gridButton);
				gridButton = new JToggleButton(gridAction);
				gridButton.setText("");
				gridBinder.bind(gridButton);
				toolbar.add(gridButton);
			}
			return toolbar;
		}

		public JComponent getStatusBar()
		{
			return null;
		}

		public JMenu getPopupMenu()
		{
			return null;
		}

		public boolean supportsZoom()
		{
			return true;
		}
	}

	public UIDescriptor getUIElements(DESModel model)
	{
		if (!(model instanceof FSAModel))
		{
			throw new UnsupportedModelException();
		}
		return new FSAUIDescriptor((FSAModel)model);
	}

	public Presentation getModelThumbnail(DESModel model, int width, int height)
			throws UnsupportedModelException
	{
		if (!(model instanceof FSAModel))
		{
			throw new UnsupportedModelException();
		}
		GraphView gv = new GraphView((FSAModel)model);
		return gv;
	}

	// public FSAGraph retrieveGraph(FSAModel model)
	// {
	// FSAGraph graph;
	// if(model.hasAnnotation(FSA_LAYOUT))
	// {
	// graph=(FSAGraph)model.getAnnotation(FSA_LAYOUT);
	// }
	// else
	// {
	// graph = new FSAGraph(model);
	// model.setAnnotation(FSA_LAYOUT, graph);
	// }
	// return graph;
	// }

	// /**
	// * If the parameter is a {@link FSAModel}, wraps it inside a
	// * {@link FSAGraph}.
	// */
	// public LayoutShell wrapModel(DESModel model)
	// throws UnsupportedModelException
	// {
	// if (!(model instanceof FSAModel))
	// {
	// throw new UnsupportedModelException();
	// }
	// FSAGraph graph = new FSAGraph((FSAModel)model);
	// return graph;
	// }

	/**
	 * Gets the current graph drawing view. FIXME This method is a quick-fix and
	 * needs to be removed altogether with the required modifications elsewhere
	 * in the code.
	 * 
	 * @return current graph drawing view if any, else null
	 */
	public static GraphDrawingView getCurrentBoard()
	{
		Collection<GraphDrawingView> ps = Hub
				.getWorkspace().getPresentationsOfType(GraphDrawingView.class);
		if (ps.size() < 1)
		{
			return null;
		}
		else
		{
			return ps.iterator().next();
		}
	}
}