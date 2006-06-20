package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import main.IDESWorkspace;
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
	
	private GraphView activeView;	
	private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.BLUE, 2);
	private static final Border PLAIN_BORDER = BorderFactory.createLineBorder(Color.BLACK, 2);
	
	public FilmStrip(){
		IDESWorkspace.instance().attach(this);
		setLayout(new GridLayout(1,6));
		setAlignmentY(Component.LEFT_ALIGNMENT);
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
		int panelWidth = getWidth()/6;
		
		// IDEA pad the empty spaces so frames don't expand to fill the filmstrip
		
		// Detach each GraphView from its model before attaching to the new model.		
		Component[] panels = this.getComponents();
		int n= this.getComponentCount();
		for(int i = 0; i < n; i++){		
				JPanel p = (JPanel)panels[i];
				GraphView gv = (GraphView)p.getComponent(0);
				gv.setGraphModel(null);		
		}		
		
		/*
		 * Update the graphmodel for each view
		 */
		Iterator graphs = IDESWorkspace.instance().getGraphModels();
		panels = this.getComponents();		
		int i=0;
		while(graphs.hasNext() && i < n) {
			JPanel p = (JPanel)panels[i];
			GraphView gv = (GraphView)p.getComponent(0);
			GraphModel gm = (GraphModel)graphs.next();			
			gv.setGraphModel(gm);			
			if(gm == IDESWorkspace.instance().getActiveGraphModel()){
				activeView = gv;
				p.setBorder(SELECTED_BORDER);
			}else{
				p.setBorder(PLAIN_BORDER);
			}			
			i++;
			p.validate();
		}
		
		/*
		 * More graphs than views
		 */
		while(graphs.hasNext()){
			GraphModel gm = (GraphModel)graphs.next();
			GraphView gv = new GraphView(gm);
			JPanel p = new JPanel();
			p.add(gv);
			p.setPreferredSize(new Dimension(panelWidth, panelWidth));
			gv.setPreferredSize(new Dimension(panelWidth, panelWidth));
			gv.setVisible(true);
			p.setPreferredSize(gv.getPreferredSize());
			if(gm == IDESWorkspace.instance().getActiveGraphModel()){
				activeView = gv;
				p.setBorder(SELECTED_BORDER);
			}else{
				p.setBorder(PLAIN_BORDER);
			}
			add(p);
			p.validate();
		}
		
		/*
		 * More views than graphs so remove them.
		 */		
		while(i < n){
			remove(panels[i]);			
			i++;
		}
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
			JPanel p = (JPanel)arg0.getComponent().getComponentAt(arg0.getPoint());// (GraphView)arg0.getSource();
			activeView = (GraphView)p.getComponent(0);
			IDESWorkspace.instance().setActiveModel(activeView.getName());
			IDESWorkspace.instance().notifyAllBut(this);
			p.setBorder(SELECTED_BORDER);
		}catch(Exception e){
			JOptionPane.showMessageDialog(activeView, "Unable to select and highlight graph: " + e.getStackTrace(), "FilmStrip Error", JOptionPane.ERROR_MESSAGE);
		}	
	}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}
}