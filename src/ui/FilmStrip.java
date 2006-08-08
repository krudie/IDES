package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import observer.FSMGraphMessage;
import observer.FSMGraphSubscriber;
import observer.Subscriber;
import observer.WorkspaceMessage;
import observer.WorkspaceSubscriber;

import main.Hub;
import presentation.fsa.FSMGraph;
import presentation.fsa.GraphView;

/**
 * A panel of graph thumbnail views to navigate among multiple automata 
 * and highlight the border of the currently active graph.
 * 
 * @author Helen Bretzke
 *
 */
@SuppressWarnings("serial")
public class FilmStrip extends JPanel implements WorkspaceSubscriber, Subscriber, MouseListener {
	
//	private GraphView activeView;
	private Vector<GraphView> graphViews=new Vector<GraphView>();
	private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.borderDarkShadow"), 2);
	private static final Border PLAIN_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.inactiveBorderColor"), 2);
	public static final int THUMBNAIL_SIZE = 100;
	
	protected Box thumbnailBox;
	
	
	public FilmStrip(){
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		Hub.getWorkspace().addSubscriber(this);
		thumbnailBox=Box.createHorizontalBox();
		add(thumbnailBox);
		addMouseListener(this);
	}
	
	public void update() {
		refreshGraphViews();
		buildThumbnailBox();
		invalidate();
		Hub.getMainWindow().validate();
	}

	private void buildThumbnailBox()
	{
		FSMGraph activeModel=Hub.getWorkspace().getActiveGraphModel();
		thumbnailBox.removeAll();
		for(GraphView gv:graphViews)
		{
			JPanel p=new JPanel(new BorderLayout());
			p.setPreferredSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.setMinimumSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.setMaximumSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.add(gv);
			if(gv.getGraphModel().equals(activeModel))
				p.setBorder(new TitledBorder(SELECTED_BORDER," "+gv.getGraphModel().getDecoratedName()));
			else
				p.setBorder(new TitledBorder(PLAIN_BORDER," "+gv.getGraphModel().getDecoratedName()));
			thumbnailBox.add(p);
			thumbnailBox.add(Box.createRigidArea(new Dimension(5,0)));
		}
	}
	
	private void refreshGraphViews()
	{
//		 Get all graph models from the workspace and render them here,
		// each in its own GraphView object.
		
		Vector<FSMGraph> currentModels=new Vector<FSMGraph>();
		for(Iterator<FSMGraph> i=Hub.getWorkspace().getGraphModels();i.hasNext();)
		{
			FSMGraph gm=i.next();
			currentModels.add(gm);
		}
		

		HashSet<GraphView> toRemove=new HashSet<GraphView>();
		for(GraphView gv:graphViews)
		{
			if(!currentModels.contains(gv.getGraphModel()))
				toRemove.add(gv);
		}
		for(GraphView gv:toRemove)
		{
			gv.getGraphModel().removeSubscriber(gv);
			graphViews.remove(gv);
		}
		
		for(int i=0;i<currentModels.size();++i)
		{
			FSMGraph gm=currentModels.elementAt(i);
			if(graphViews.size()<=i||!graphViews.elementAt(i).getGraphModel().equals(gm))
			{
				GraphView gv = new GraphView(gm);				
				//gm.addSubscriber(this);
				gv.addMouseListener(this);
				graphViews.insertElementAt(gv,i);
			}
		}
	}
	
	/**
	 * Figure out which graph was selected, 
	 * and set it as the currently active graph in the workspace. 
	 * 
	 * @param arg0
	 */
	public void mouseClicked(MouseEvent arg0) {
		if(!(arg0.getSource() instanceof GraphView))
			return;
		GraphView gv=(GraphView)arg0.getSource();
		Hub.getWorkspace().setActiveModel(gv.getGraphModel().getName());
		// Hub.getWorkspace().notifyAllSubscribers();
	}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage)
	 */
	public void modelCollectionChanged(WorkspaceMessage message) {
		if(message.getEventType() == WorkspaceMessage.ADD || message.getEventType() == WorkspaceMessage.REMOVE)
		refreshGraphViews();
		buildThumbnailBox();
		invalidate();
		Hub.getMainWindow().validate();
	}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
	 */
	public void modelSwitched(WorkspaceMessage message) {
		if(message.getEventType() != WorkspaceMessage.REMOVE){
			buildThumbnailBox();
			invalidate();
			Hub.getMainWindow().validate();
		}
	}
	

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
	 */
	public void repaintRequired(WorkspaceMessage message) {	
		for(GraphView gv:graphViews){
			gv.repaint();
		}
	}

	
}