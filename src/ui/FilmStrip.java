package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.OverlayLayout;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import observer.FSAGraphMessage;
import observer.FSAGraphSubscriber;
import observer.WorkspaceMessage;
import observer.WorkspaceSubscriber;

import main.Hub;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphView;

/**
 * A panel of graph thumbnail views to navigate among multiple automata 
 * and highlight the border of the currently active graph.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
@SuppressWarnings("serial")
public class FilmStrip extends JPanel implements WorkspaceSubscriber, FSAGraphSubscriber, MouseListener {

	private HashMap<FSAGraph, JPanel> graphPanels = new HashMap<FSAGraph, JPanel>();
	private Vector<GraphView> graphViews=new Vector<GraphView>();
	private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.borderDarkShadow"), 3);
	private static final Border PLAIN_BORDER = BorderFactory.createLineBorder(UIManager.getColor("InternalFrame.inactiveBorderColor"), 1);
	public static final int THUMBNAIL_SIZE = 100;
	private static final int BUTTON_SIZE = 18;
	private static final JButton closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(BUTTON_SIZE - 2));
	private static final PopupFactory popupFactory = PopupFactory.getSharedInstance();
	private Popup closePopup;
	
	protected Box thumbnailBox;	
	
	public FilmStrip() {
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		Hub.getWorkspace().addSubscriber(this);
		thumbnailBox=Box.createHorizontalBox();
		add(thumbnailBox);
		closeButton.setPreferredSize(new Dimension(BUTTON_SIZE,BUTTON_SIZE));
		addMouseListener(this);
	}
	
	/**
	 * Packs each graph view into it's own panel with titled border.
	 * The current graph model is rendered with a highlighted border.
	 * Dirty models are shown with name decorated by an asterisk. 
	 */
	private void buildThumbnailBox() {
		FSAGraph activeModel=Hub.getWorkspace().getActiveGraphModel();
		thumbnailBox.removeAll();
		graphPanels.clear();
		for( GraphView gv : graphViews ) {
			JPanel p = new JPanel();
			LayoutManager overlay = new OverlayLayout(p);
			p.setLayout(overlay);
			p.setPreferredSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.setMinimumSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.setMaximumSize(new Dimension(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
			p.add(gv);
			//p.add(closeButton, GridBagConstraints.NORTHEAST);
			
			if(gv.getGraphModel().equals(activeModel)) {
				p.setBorder(new TitledBorder(SELECTED_BORDER," "+gv.getGraphModel().getDecoratedName()));				
			} else {
				p.setBorder(new TitledBorder(PLAIN_BORDER," "+gv.getGraphModel().getDecoratedName()));
			}
//			if (gv.getGraphModel().equals(activeModel)) {
//				p.setBorder(SELECTED_BORDER);
//			} else {
//				p.setBorder(PLAIN_BORDER);
//			}
			graphPanels.put(gv.getGraphModel(), p);
			thumbnailBox.add(p);
			thumbnailBox.add(Box.createRigidArea(new Dimension(5,0)));		
		}
	}
	
	/**
	 * Gets all graph models from the workspace and renders 
	 * each in its own view. 
	 */
	private void refreshGraphViews() {		
		Vector<FSAGraph> currentModels = new Vector<FSAGraph>();
		Iterator<FSAGraph> iter = Hub.getWorkspace().getGraphModels();
		while( iter.hasNext() ) {
			FSAGraph gm = iter.next();
			currentModels.add(gm);
		}		

		HashSet<GraphView> toRemove=new HashSet<GraphView>();
		for( GraphView gv : graphViews ) {
			if(!currentModels.contains(gv.getGraphModel())) {
				toRemove.add(gv);
			}
		}
		
		for( GraphView gv : toRemove ) {
			gv.getGraphModel().removeSubscriber(gv);
			graphViews.remove(gv);
		}
		
		for( int i=0; i < currentModels.size(); ++i ) {
			FSAGraph gm = currentModels.elementAt(i);
			if( graphViews.size() <=i || !graphViews.elementAt(i).getGraphModel().equals(gm) ) {
				GraphView gv = new GraphView(gm);				
				gm.addSubscriber(this);
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
		if( !(arg0.getSource() instanceof GraphView) ) {
			return;
		}
		GraphView gv=(GraphView)arg0.getSource();
		Hub.getWorkspace().setActiveModel(gv.getGraphModel().getName());		
	}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {
//		if (arg0.getComponent() != this) {
//			Point p = arg0.getComponent().getParent().getLocationOnScreen();
//			System.out.println(arg0.getComponent().getParent().toString());
//			closePopup = popupFactory.getPopup(null, closeButton,
//					p.x + (THUMBNAIL_SIZE-BUTTON_SIZE), p.y);
//			closePopup.show();
//		}
	}

	public void mouseExited(MouseEvent arg0) {
//		if (closePopup != null) {
//			closePopup.hide();
//		}
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
		for(GraphView gv : graphViews) {
			gv.repaint();
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