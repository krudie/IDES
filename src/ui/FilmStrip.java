package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import presentation.fsa.GraphModel;
import presentation.fsa.GraphView;

import main.IDESWorkspace;
import model.Subscriber;

/**
 * A panel of graph thumbnail views that highlights the border
 * on the graph that has focus.
 * 
 * @author Helen Bretzke
 *
 */
@SuppressWarnings("serial")
public class FilmStrip extends JPanel implements Subscriber, MouseListener {
	
	private GraphView activeView;
	private Box box;
	
	public FilmStrip(){
		IDESWorkspace.instance().attach(this);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setAlignmentY(Component.LEFT_ALIGNMENT);
		
		// Create a Box with horizontal alignment
//	    box = Box.createHorizontalBox ();
//		add(Box.createHorizontalGlue());
//		add(box);

	    // Get the screen dimensions.
	    Toolkit tk = Toolkit.getDefaultToolkit ();
	    Dimension screen = tk.getScreenSize ();    
	    setSize (screen.width, screen.height/4);
		setPreferredSize(this.getSize());
		//setBackground(Color.BLACK);
		
		addMouseListener(this);
	}
	
	public void remove(GraphView gv){
		super.remove(gv);
		if(activeView == gv){
			activeView = null;
		}
	}
	
	public GraphView getActiveView() {
		return activeView;
	}

	public void update() {
		// Get all graph models from the workspace and render them here,
		// each in its own GraphView object.	
		
		// Detach each GraphView from its model before attaching to the new model.		
		Component[] views = this.getComponents();
		int n= this.getComponentCount();
		for(int i = 0; i < n; i++){			
				GraphView gv = (GraphView)views[i];
				gv.setGraphModel(null);		
		}		
		
		/*
		 * Update the graphmodel for each view
		 */
		Iterator graphs = IDESWorkspace.instance().getGraphModels();
		views = this.getComponents();		
		int i=0;
		while(graphs.hasNext() && i < n) {
			GraphView gv = (GraphView)views[i];
			GraphModel gm = (GraphModel)graphs.next();			
			gv.setGraphModel(gm);		
			if(gm == IDESWorkspace.instance().getActiveGraphModel()){
				activeView = gv;
			}
			i++;
		}
		
		/*
		 * More graphs than views
		 */
		while(graphs.hasNext()){
			GraphModel gm = (GraphModel)graphs.next();
			GraphView gv = new GraphView(gm);
			gv.setPreferredSize(new Dimension(this.getSize().height, this.getSize().height));
			add(gv);			
			if(gm == IDESWorkspace.instance().getActiveGraphModel()){
				activeView = gv;
			}
		}
		
		/*
		 * More views than graphs so remove them.
		 */		
		while(i < n){
			remove(views[i]);			
			i++;
		}

		// FIXME why is the border not visible?
		activeView.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
		// box.validate();
		validate();
	}

	/**
	 * Figure out which graph was selected, toggle it's border (clear all others)
	 * and make it known to the UIStateModel as the currently active graph. 
	 * 
	 * @param arg0
	 */
	public void mouseClicked(MouseEvent arg0) {
		try{
			Component[] components = getComponents();			
			if(components != null){
				int n = components.length;
				for(int i=0; i<n; i++){
					((JComponent)components[i]).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				}				
			}
			
			activeView = (GraphView)arg0.getComponent().getComponentAt(arg0.getPoint());// (GraphView)arg0.getSource();
			IDESWorkspace.instance().setActiveModel(activeView.getName());
			IDESWorkspace.instance().notifyAllBut(this);
			activeView.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		}catch(Exception e){
			JOptionPane.showMessageDialog(activeView, "Unable to select and highlight graph: " + e.getStackTrace(), "FilmStrip Error", JOptionPane.ERROR_MESSAGE);
		}	
	}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}
}