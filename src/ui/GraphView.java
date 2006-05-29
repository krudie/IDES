package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import presentation.fsa.GraphElement;

import model.Subscriber;

public class GraphView extends JComponent implements Subscriber {

	protected float scaleFactor = 0.25f;
	
	/**
	 * An object to handle synchronizing FSA model with the displayed graph.
	 */
	protected GraphModel graphModel;
	/**
	 * Presentation model (the composite structure that represents the DES model.)
	 */
	protected GraphElement graph;

	public void paint(Graphics g) {
				
			Graphics2D g2D = (Graphics2D) g; // cast to 2D	
				
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                         RenderingHints.VALUE_ANTIALIAS_ON);		
		    g2D.setBackground(Color.white);  // FIXME THIS DOESN'T WORK
		    
		    // TODO what happens to stroke when scaled?
		    g2D.setStroke(GUISettings.instance().getWideStroke());
			
			// Warning: scales distance from origin as well as size of nodes
			// we want to scale everything from the centre of the user's view.
			// Solution: translate origin before scaling and beware of op precendence.
		    // FIXME: this is not working
	//	    g2D.translate(-(getWidth()/scaleFactor), -(getHeight()/scaleFactor));
		    g2D.scale(scaleFactor, scaleFactor);		
		    //	TODO other transformation?
	
		    graph.draw(g2D);
		    g2D.dispose();
	}

	/**
	 * Refresh my visual model from GraphModel.
	 */
	public void update() {		
		graph = graphModel.getGraph();
		repaint();
	}

	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
		this.setName(graphModel.getName());
	}

	public GraphModel getGraphModel() {
		return graphModel;
	}

}
