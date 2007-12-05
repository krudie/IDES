package presentation.fsa;

import java.util.Collection;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;


import main.Hub;
import main.WorkspaceMessage;
import main.WorkspaceSubscriber;
import model.DESModel;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;

import pluggable.ui.Toolset;
import pluggable.ui.UIDescriptor;
import pluggable.ui.UnsupportedModelException;
import presentation.LayoutShell;
import presentation.Presentation;
import presentation.fsa.commands.GraphActions;
import presentation.fsa.commands.UIActions;
import ui.command.OperationsCommands;
import ui.command.OptionsCommands;
import util.BooleanUIBinder;

/**
 * The toolset for {@link FSAModel}s.
 * @see Toolset
 * 
 * @author Lenko Grigorov
 */
public class FSAToolset implements Toolset {
	
	protected static class FSAUIDescriptor implements UIDescriptor
	{
		protected FSAGraph shell;
		protected Presentation[] views;
		protected static JToolBar toolbar=null;
		protected static JMenu graphMenu=null;
		private static Action selectAction=null;
		private static Action createAction=null;
		private static Action moveAction=null;
		private static Action alignAction=null;
		private Action gridAction=null;
		private static BooleanUIBinder gridBinder=new BooleanUIBinder();
		private static JToggleButton gridButton=null;

		public FSAUIDescriptor(FSAGraph ls)
		{
			shell=ls;
			views=new Presentation[2];
			GraphDrawingView drawingBoard=new GraphDrawingView(gridBinder);
			gridAction=new UIActions.ShowGridAction(drawingBoard);
			drawingBoard.setGraphModel(shell);
			drawingBoard.setName(Hub.string("graph"));
			views[0]=drawingBoard;
			views[1]=new EventView(shell);
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
			if(selectAction==null)
			{
				selectAction=new UIActions.SelectTool();
			}
			if(createAction==null)
			{
				createAction=new UIActions.CreateTool();
			}
			if(moveAction==null)
			{
				moveAction=new UIActions.MoveTool();
			}
			if(alignAction==null)
			{
				alignAction=new GraphActions.AlignToolAction();
			}
		}
		
		public JMenu[] getMenus()
		{
			if(graphMenu==null)
			{
				setupActions();
				graphMenu=new JMenu(Hub.string("menuGraph"));
				//Initializing the menu items for the "graphMenu"
				JMenuItem select = new JMenuItem(selectAction);
				JMenuItem create = new JMenuItem(createAction);
				JMenuItem move = new JMenuItem(moveAction);
				JMenuItem alignNodes = new JMenuItem(alignAction); 
				JMenuItem showGrid = new JCheckBoxMenuItem(gridAction);
				gridBinder.bind(showGrid);
				//this is a dummy menu item since it'll be replaced 
				JCheckBoxMenuItem uniformNodeSize = new JCheckBoxMenuItem();
				//Adding the menu items to the "graphMenu"
				graphMenu.add(select);
				graphMenu.add(create);
				graphMenu.add(move);
				graphMenu.addSeparator();
				graphMenu.add(alignNodes);
				graphMenu.add(showGrid);
				graphMenu.add(uniformNodeSize);
			}
			//get the "use uniform node size" menu item for the current shell
			String MENU_ITEM="useUniformNodeSizeMenuItem";
			JMenuItem useUniformNodeSizeMenu=(JMenuItem)shell.getAnnotation(MENU_ITEM);
			if(useUniformNodeSizeMenu==null)
			{
				useUniformNodeSizeMenu=new JCheckBoxMenuItem(new GraphActions.UniformNodesAction(shell));
				shell.getUseUniformRadiusBinder().bind(useUniformNodeSizeMenu);
				shell.setAnnotation(MENU_ITEM, useUniformNodeSizeMenu);
			}
			//update menu with current layout shell's uniform node size menu item 
			graphMenu.remove(graphMenu.getMenuComponentCount()-1);
			graphMenu.add(useUniformNodeSizeMenu);
			return new JMenu[]{graphMenu};
		}

		public JToolBar getToolbar()
		{
			if(toolbar==null)
			{
				setupActions();
				toolbar=new JToolBar();
			}
			if(toolbar.getComponentCount()==0)
			{
				toolbar.add(selectAction);
				toolbar.add(createAction);
				toolbar.add(moveAction);
				toolbar.addSeparator();
				toolbar.add(alignAction);
				gridBinder.unbind(gridButton);
				gridButton=new JToggleButton(gridAction);
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

		public boolean showZoomControl()
		{
			return true;
		}
	}
	
	public UIDescriptor getUIElements(LayoutShell mw)
	{
		if(!(mw instanceof FSAGraph))
			throw new UnsupportedModelException();
		return new FSAUIDescriptor((FSAGraph)mw);
	}

	public Presentation getModelThumbnail(LayoutShell mw, int width, int height) throws UnsupportedModelException {
		if(!(mw instanceof FSAGraph))
			throw new UnsupportedModelException();
		GraphView gv=new GraphView((FSAGraph)mw);
		return gv;
	}

	/**
	 * If the parameter is a {@link FSAModel}, wraps it inside
	 * a {@link FSAGraph}.
	 */
	public LayoutShell wrapModel(DESModel model) throws UnsupportedModelException {
		if(!(model instanceof FSAModel))
			throw new UnsupportedModelException();
		return new FSAGraph((FSAModel)model);
	}

	/**
	 * Gets the current graph drawing view.
	 * FIXME This method is a quick-fix and needs to be removed
	 * altogether with the required modifications elsewhere
	 * in the code.
	 * @return current graph drawing view if any, else null
	 */
	public static GraphDrawingView getCurrentBoard()
	{
		Collection<Presentation> ps=Hub.getWorkspace().getPresentationsOfType(GraphDrawingView.class);
		if(ps.size()<1)
			{
			 return null;
			}
		else
			{
			return (GraphDrawingView)ps.iterator().next();
			}
	}	
}