package ui;

import java.awt.BorderLayout;
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

import main.Hub;
import model.Subscriber;
import presentation.fsa.GraphModel;
import presentation.fsa.GraphView;

/**
 * A panel of graph thumbnail views to navigate among multiple automata 
 * and highlight the border of the currently active graph.
 * 
 * @author Helen Bretzke
 *
 */
@SuppressWarnings("serial")
public class FilmStrip extends JPanel implements Subscriber, MouseListener {
	
//	private GraphView activeView;
	private Vector<GraphView> graphViews=new Vector<GraphView>();
	private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.borderDarkShadow"), 2);
	private static final Border PLAIN_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.inactiveBorderColor"), 2);
	public static final int THUMBNAIL_SIZE = 100;
	
	protected Box thumbnailBox;
	
	
	public FilmStrip(){
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		Hub.getWorkspace().attach(this);
		thumbnailBox=Box.createHorizontalBox();
		add(thumbnailBox);
		addMouseListener(this);
	}
	
	public void remove(GraphView gv){
		thumbnailBox.remove(gv);
		graphViews.remove(gv);
	}
	
//	public GraphView getActiveView() {
//		return activeView;
//	}

	public void update() {

		// Get all graph models from the workspace and render them here,
		// each in its own GraphView object.
		
		Vector<GraphModel> currentModels=new Vector<GraphModel>();
		for(Iterator<GraphModel> i=Hub.getWorkspace().getGraphModels();i.hasNext();)
		{
			GraphModel gm=i.next();
			currentModels.add(gm);
		}
		GraphModel activeModel=Hub.getWorkspace().getActiveGraphModel();

		HashSet<GraphView> toRemove=new HashSet<GraphView>();
		for(GraphView gv:graphViews)
		{
			if(!currentModels.contains(gv.getGraphModel()))
				toRemove.add(gv);
		}
		for(GraphView gv:toRemove)
		{
			gv.getGraphModel().detach(this);
			graphViews.remove(gv);
		}
		
		for(int i=0;i<currentModels.size();++i)
		{
			GraphModel gm=currentModels.elementAt(i);
			if(graphViews.size()<=i||!graphViews.elementAt(i).getGraphModel().equals(gm))
			{
				GraphView gv = new GraphView(gm);
				gm.attach(gv);
				gm.attach(this);
				gv.addMouseListener(this);
				graphViews.insertElementAt(gv,i);
			}
		}
		
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
		invalidate();
		Hub.getMainWindow().validate();
	}

	/**
	 * Figure out which graph was selected, toggle it's border (clear all others)
	 * and make it known to the UIStateModel as the currently active graph. 
	 * 
	 * @param arg0
	 */
	public void mouseClicked(MouseEvent arg0) {
		if(!(arg0.getSource() instanceof GraphView))
			return;
		GraphView gv=(GraphView)arg0.getSource();
		Hub.getWorkspace().setActiveModel(gv.getGraphModel().getName());
		Hub.getWorkspace().notifyAllSubscribers();
	}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}
}