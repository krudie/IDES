package presentation.fsa;

import javax.swing.JComponent;

import observer.WorkspaceMessage;
import observer.WorkspaceSubscriber;

import main.Hub;
import model.DESModel;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.MetaData;

import pluggable.ui.Toolset;
import pluggable.ui.UnsupportedModelException;
import presentation.ModelWrap;
import presentation.Presentation;

/**
 * The toolset for {@link FSAModel}s.
 * @see Toolset
 * 
 * @author Lenko Grigorov
 */
public class FSAToolset implements Toolset, WorkspaceSubscriber {
	
	/**
	 * Keeps track of which {@link ModelWrap} the {@link GraphDrawingView}
	 * is attached to.
	 */
	FSAGraph attachedTo=null;
	
	public FSAToolset()
	{
		Hub.getWorkspace().addSubscriber(this);
	}

	public JComponent[] getEditPanes(ModelWrap mw) throws UnsupportedModelException {
		if(!(mw instanceof FSAGraph))
			throw new UnsupportedModelException();
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getEditPanesCaptions(ModelWrap mw) throws UnsupportedModelException {
		if(!(mw instanceof FSAGraph))
			throw new UnsupportedModelException();
		// TODO Auto-generated method stub
		return null;
	}

	public Presentation getModelThumbnail(ModelWrap mw, int width, int height) throws UnsupportedModelException {
		if(!(mw instanceof FSAGraph))
			throw new UnsupportedModelException();
		GraphView gv=new GraphView((FSAGraph)mw);
		return gv;
	}

	/**
	 * If the parameter is a {@link FSAModel}, wraps it inside
	 * a {@link FSAGraph}.
	 */
	public ModelWrap wrapModel(DESModel model) throws UnsupportedModelException {
		if(!(model instanceof FSAModel))
			throw new UnsupportedModelException();
		ModelWrap w;
		//TODO - remove this MetaData bullshit
		if(model.hasAnnotation("metadata"))
			w=new FSAGraph((FSAModel)model,(MetaData)model.getAnnotation("metadata"));
		else
			w=new FSAGraph((FSAModel)model);
		return w;
	}

	/**
	 * Called when a model collection change 
	 * (a DES model is created or opened (added), closed (removed) 
	 * or renamed) has occurred in a <code>WorkspacePublisher</code> 
	 * to which I have subscribed.
	 *  
	 * @param message details of the change notification
	 */
	public void modelCollectionChanged(WorkspaceMessage message)
	{
		if(attachedTo!=null)
			attachedTo.removeSubscriber(Hub.getWorkspace().getDrawingBoard());
		ModelWrap activew=Hub.getWorkspace().getActiveModelWrap();
		if(activew!=null&&activew instanceof FSAGraph)
		{
			attachedTo=(FSAGraph)activew;
			attachedTo.addSubscriber(Hub.getWorkspace().getDrawingBoard());
		}
		else
		{
			attachedTo=null;
		}
	}
	

	/**
	 * Called when a change requiring a repaint has
	 * occurred in a <code>WorkspacePublisher</code> to which I have
	 * subscribed.
	 *  
	 * @param message details of the change notification
	 */
	/* NOTE ignore param except possibly for the source field */
	public void repaintRequired(WorkspaceMessage message)
	{
	}
	

	/**
	 * Called when a the model type has been switched 
	 * (the type of active model has changed e.g. from FSA to petri net) 
	 * in a <code>WorkspacePublisher</code> to which I have subscribed. 
	 *  
	 * @param message details of the change notification
	 */
	public void modelSwitched(WorkspaceMessage message)
	{
		if(attachedTo!=null)
			attachedTo.removeSubscriber(Hub.getWorkspace().getDrawingBoard());
		ModelWrap activew=Hub.getWorkspace().getActiveModelWrap();
		if(activew!=null&&activew instanceof FSAGraph)
		{
			attachedTo=(FSAGraph)activew;
			attachedTo.addSubscriber(Hub.getWorkspace().getDrawingBoard());
		}
		else
		{
			attachedTo=null;
		}
	}
}