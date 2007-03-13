package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import observer.FSAGraphMessage;
import observer.FSAGraphSubscriber;
import observer.WorkspaceMessage;
import observer.WorkspaceSubscriber;

import main.Hub;
import presentation.ModelWrap;
import presentation.Presentation;
import presentation.PresentationManager;
import presentation.fsa.FSAGraph;
//import presentation.fsa.GraphView;

/**
 * A panel of graph thumbnail views to navigate among multiple automata 
 * and highlight the border of the currently active graph.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
@SuppressWarnings("serial")
public class FilmStrip extends JPanel implements WorkspaceSubscriber, FSAGraphSubscriber, MouseListener, MouseMotionListener {
	
	private HashMap<ModelWrap, Thumbnail> graphPanels = new HashMap<ModelWrap, Thumbnail>();
	private Vector<Presentation> graphViews=new Vector<Presentation>();
	private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.borderDarkShadow"), 3);
	private static final Border PLAIN_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.inactiveBorderColor"), 1);
	
	private static Thumbnail underMouse; // the last Thumbnail that we had a mouseMove event above
	public static final int THUMBNAIL_SIZE = 100;
	public static final int SPACER_SIZE = 5;
	
	protected Box thumbnailBox;	
	
	public FilmStrip() {
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		Hub.getWorkspace().addSubscriber(this);
		thumbnailBox=Box.createHorizontalBox();
		add(thumbnailBox);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	protected String getDecoratedName(ModelWrap mw)
	{
		if(mw.needsSave())
		{
			return "* "+mw.getModel().getName();
		}
		else
		{
			return mw.getModel().getName();
		}
	}
	
	/**
	 * Packs each graph view into it's own panel with titled border.
	 * The current graph model is rendered with a highlighted border.
	 * Dirty models are shown with name decorated by an asterisk. 
	 */
	private void buildThumbnailBox() {
		thumbnailBox.removeAll();
		graphPanels.clear();
		for( int i=0;i<graphViews.size();++i ) {
			JComponent gv=graphViews.get(i).getGUI();
			Thumbnail p=new Thumbnail(new BorderLayout());
			p.setPreferredSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.setMinimumSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.setMaximumSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.add(gv);
			
			if(graphViews.get(i).getModelWrap().getModel().equals(Hub.getWorkspace().getActiveModel())) {
				p.setBorder(new TitledBorder(SELECTED_BORDER," "+getDecoratedName(graphViews.get(i).getModelWrap())));
			} else {
				p.setBorder(new TitledBorder(PLAIN_BORDER," "+getDecoratedName(graphViews.get(i).getModelWrap())));
			}
			graphPanels.put(graphViews.get(i).getModelWrap(), p);
			thumbnailBox.add(p);
			thumbnailBox.add(Box.createRigidArea(new Dimension(SPACER_SIZE,0)));		
		}
	}
	
	/**
	 * Gets all graph models from the workspace and renders 
	 * each in its own view. 
	 */
	private void refreshGraphViews() {
		Vector<ModelWrap> currentModels = new Vector<ModelWrap>();
		Iterator<ModelWrap> iter = Hub.getWorkspace().getModelWraps();
		while( iter.hasNext() ) {
			ModelWrap gm = iter.next();
			currentModels.add(gm);
		}		

		HashSet<Presentation> toRemove=new HashSet<Presentation>();
		for( Presentation gv : graphViews ) {
			if(!currentModels.contains(gv.getModelWrap())) {
				toRemove.add(gv);
			}
		}
		
		for( Presentation gv : toRemove ) {
			((FSAGraph)gv.getModelWrap()).removeSubscriber((FSAGraphSubscriber)gv.getGUI());
			graphViews.remove(gv);
		}
		
		for( int i=0; i < currentModels.size(); ++i ) {
			ModelWrap gm = currentModels.elementAt(i);
			if( graphViews.size() <=i || !graphViews.elementAt(i).getModelWrap().equals(gm) ) {
				Presentation gv = PresentationManager.getToolset(gm.getModelInterface()).getModelThumbnail(gm,10,10);				
				((FSAGraph)gm).addSubscriber(this);
				gv.getGUI().addMouseListener(this);
				gv.getGUI().addMouseMotionListener(this);
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
		if( !(arg0.getSource() instanceof Presentation) ) {
			return;
		}
		Presentation gv=(Presentation)arg0.getSource();
		Hub.getWorkspace().setActiveModel(gv.getModelWrap().getModel().getName());		
	}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {
		if (!this.getBounds().contains(arg0.getPoint())) {
			underMouse.handleMouseExited(arg0);
		}
	}
	
	public void mouseDragged(MouseEvent arg0) {}
	

	/*
	 * (non-Javadoc)
	 * The FilmStrip class acts as a dispatcher for mouse motion events to distribute
	 * the events to the closest Thumbnail object.  This activates the closeButton
	 * in the corner of the thumbnail, and ensures that at most one closeButton is visible
	 * at any given time.
	 * 
	 * We're still having issues with some of the mouse motion events being lost to
	 * subcomponents (the graphview contained in the thumbnail, to be specific), but it's
	 * starting to look as though using a glasspane is the only way to catch them all.
	 * --(CLM)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		Thumbnail current = null;
		if (arg0.getSource() != this) {
			current = graphPanels.get(( (Presentation)arg0.getSource() ).getModelWrap());
		} else {
			int thumbnailIndex = arg0.getPoint().x / (THUMBNAIL_SIZE+SPACER_SIZE);
			arg0.getPoint().x -= (THUMBNAIL_SIZE+SPACER_SIZE) * thumbnailIndex;
			current = graphPanels.get(graphViews.elementAt(thumbnailIndex).getModelWrap());
		}
		current.handleMouseEntered(arg0);
		if (!(current.equals(underMouse))) {
			underMouse = current;
			for (Thumbnail t : graphPanels.values()) {
				if (!t.equals(current)) {
					t.handleMouseExited(arg0);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage)
	 */
	public void modelCollectionChanged(WorkspaceMessage message) {
		if(message.getEventType() == WorkspaceMessage.ADD 
				|| message.getEventType() == WorkspaceMessage.REMOVE) {
			refreshGraphViews();
			buildThumbnailBox();
			invalidate();
			Hub.getMainWindow().validate();
		}
	}

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
	 */
	public void modelSwitched(WorkspaceMessage message) {
		if(message.getEventType() != WorkspaceMessage.REMOVE) {
			buildThumbnailBox();
			invalidate();
			Hub.getMainWindow().validate();
		}
	}
	

	/* (non-Javadoc)
	 * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
	 */
	public void repaintRequired(WorkspaceMessage message) {		
		for(Presentation gv : graphViews) {
			gv.getGUI().repaint();
		}
	}

	/**
	 * Refresh the title on the current graph view.
	 * 
	 * @see observer.FSAGraphSubscriber#fsmGraphChanged(observer.FSAGraphMessage)
	 */
	public void fsmGraphChanged(FSAGraphMessage message) {	
		buildThumbnailBox();
		/* A more efficient way
		JPanel panel = graphPanels.get(message.getSource());
		if(panel != null){
			TitledBorder border = (TitledBorder)panel.getBorder();
			border.setTitle(" "+message.getSource().getDecoratedName());	
			panel.invalidate();
			panel.repaint();
			invalidate();
			Hub.getMainWindow().validate();
		}*/
	}

	/* (non-Javadoc)
	 * NOTE Thumbnails don't need to respond to selection events. 
	 * @see observer.FSAGraphSubscriber#fsmGraphSelectionChanged(observer.FSAGraphMessage)
	 */
	public void fsmGraphSelectionChanged(FSAGraphMessage message) {}

	
}