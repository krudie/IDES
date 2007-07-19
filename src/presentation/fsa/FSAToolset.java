package presentation.fsa;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
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

/**
 * The toolset for {@link FSAModel}s.
 * @see Toolset
 * 
 * @author Lenko Grigorov
 */
public class FSAToolset implements Toolset {
	
//	/**
//	 * Keeps track of which {@link LayoutShell} the {@link GraphDrawingView}
//	 * is attached to.
//	 */
//	FSAGraph attachedTo=null;
	
	//FIXME eventually make a better design for the grid
	public static boolean gridState=false;

	protected class FSAUIDescriptor implements UIDescriptor
	{
		protected FSAGraph shell;
		protected Presentation[] views;
		
		public FSAUIDescriptor(FSAGraph ls)
		{
			shell=ls;
			views=new Presentation[2];
			GraphDrawingView drawingBoard=new GraphDrawingView();
			drawingBoard.setShowGrid(gridState);
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

		public JMenu[] getMenus()
		{
			return null;
		}

		public JToolBar getToolbar()
		{
			return null;
		}

		public JComponent getStatusBar()
		{
			return null;
		}

		public JMenu getPopupMenu()
		{
			return null;
		}

	}
	
	
	public FSAToolset()
	{
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
		LayoutShell w;
		//TODO - remove this MetaData bullshit
//		if(model.hasAnnotation("metadata"))
//			w=new FSAGraph((FSAModel)model,(MetaData)model.getAnnotation("metadata"));
//		else
//			w=new FSAGraph((FSAModel)model);
		//Christian - Trying to remove this MetaData:
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
	
//	/**
//	 * Called when a model collection change 
//	 * (a DES model is created or opened (added), closed (removed) 
//	 * or renamed) has occurred in a <code>WorkspacePublisher</code> 
//	 * to which I have subscribed.
//	 *  
//	 * @param message details of the change notification
//	 */
//	public void modelCollectionChanged(WorkspaceMessage message)
//	{
//		if(attachedTo!=null)
//		{
//			attachedTo.removeSubscriber(Hub.getWorkspace().getDrawingBoard());
//		}
//		LayoutShell activew=Hub.getWorkspace().getActiveLayoutShell();
//		if(activew!=null&&activew instanceof FSAGraph)
//		{
//			attachedTo=(FSAGraph)activew;
//			Hub.getWorkspace().getDrawingBoard().setGraphModel(attachedTo);
//		}
//		else
//		{
//			attachedTo=null;
//		}
//		Hub.getWorkspace().getDrawingBoard().repaint();
//	}
//	
//
//	/**
//	 * Called when a change requiring a repaint has
//	 * occurred in a <code>WorkspacePublisher</code> to which I have
//	 * subscribed.
//	 *  
//	 * @param message details of the change notification
//	 */
//	/* NOTE ignore param except possibly for the source field */
//	public void repaintRequired(WorkspaceMessage message)
//	{
//		if(attachedTo!=null)
//			Hub.getWorkspace().getDrawingBoard().repaint();			
//	}
//	
//
//	/**
//	 * Called when a the model type has been switched 
//	 * (the type of active model has changed e.g. from FSA to petri net) 
//	 * in a <code>WorkspacePublisher</code> to which I have subscribed. 
//	 *  
//	 * @param message details of the change notification
//	 */
//	public void modelSwitched(WorkspaceMessage message)
//	{
//		if(attachedTo!=null)
//		{
//			attachedTo.removeSubscriber(Hub.getWorkspace().getDrawingBoard());
//		}
//		LayoutShell activew=Hub.getWorkspace().getActiveLayoutShell();
//		if(activew!=null&&activew instanceof FSAGraph)
//		{
//			attachedTo=(FSAGraph)activew;
//			Hub.getWorkspace().getDrawingBoard().setGraphModel(attachedTo);
//		}
//		else
//		{
//			attachedTo=null;
//		}
//		Hub.getWorkspace().getDrawingBoard().repaint();
//	}
}