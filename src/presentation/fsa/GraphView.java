package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import ui.GUISettings;

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
	
	public GraphView(){
		setGraphModel(null);		
	}
	
	public GraphView(GraphModel graphModel){
		setGraphModel(graphModel);
	}

	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;			
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);	    
	    g2D.setStroke(GUISettings.instance().getWideStroke());

	    Rectangle r=this.getBounds();
	    g2D.setColor(Color.WHITE);
	    g2D.fillRect(r.x,r.y,r.width,r.height);
	    g2D.scale(scaleFactor, scaleFactor);		
	    //	TODO other transformation?

	    graph.draw(g2D);	    
	}

	/**
	 * Refresh my visual model from GraphModel.
	 */
	public void update() {	
		if(graphModel != null){
			graph = graphModel.getGraph();
		}else{
			graph = new GraphElement();
		}
		repaint();
	}

	public void setGraphModel(GraphModel graphModel) {						
		this.graphModel = graphModel;
		if(graphModel != null){
			this.graphModel.detach(this);
			graphModel.attach(this);
			this.setName(graphModel.getName());		
			update();
		}else{
			this.setName("No automaton");
		}
	}

	public GraphModel getGraphModel() {
		return graphModel;
	}

}
