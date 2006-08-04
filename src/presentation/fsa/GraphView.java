package presentation.fsa;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import observer.Subscriber;

import ui.GUISettings;

import main.Hub;

public class GraphView extends JComponent implements Subscriber {

	protected static final int GRAPH_BORDER_THICKNESS=10;
	
	protected float scaleFactor = 0.25f;
	protected Rectangle graphBounds=new Rectangle();
	
	/**
	 * if true, update() will set the scale factor so that the whole model fits in the view
	 */
	protected boolean scaleToFit = true;
	
	/**
	 * An object to handle synchronizing FSA model with the displayed graph.
	 */
	protected FSMGraph graphModel;
	
	/**
	 * Presentation model (the composite structure that represents the DES model.)
	 */
	protected GraphElement graph;
	
	public GraphView(){
		setGraphModel(null);
	}
	
	public GraphView(FSMGraph graphModel){
		setGraphModel(graphModel);
	}


	public void paint(Graphics g) {
		paint(g,true);
	}

	public void paint(Graphics g, boolean doFill) {
		Graphics2D g2D = (Graphics2D) g;			
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);	    
	    g2D.setStroke(GUISettings.instance().getWideStroke());

	    Rectangle r=getBounds();
	    if(doFill)
	    {
	    	g2D.setColor(Color.WHITE);
	    	g2D.fillRect(0,0,r.width,r.height);
	    }
	    
	    g2D.scale(scaleFactor, scaleFactor);	    	    
	    graph.draw(g2D);	    
	}

	/**
	 * Refresh my visual model from GraphModel.
	 */
	public void update() {
		if(getGraphModel() != null){
			graph = getGraphModel().getGraph();
		}else{
			graph = new GraphElement();
		}
		if(getGraphModel()!=null)  // Why can't this be moved into first case above?
		{
			graphBounds=getGraphModel().getBounds(true);
			if(graphBounds.x<0||graphBounds.y<0)
			{
				graphModel.translate(-graphBounds.x+GRAPH_BORDER_THICKNESS,-graphBounds.y+GRAPH_BORDER_THICKNESS);
			}
			if(scaleToFit&&getParent()!=null)
			{
				Insets ins=getParent().getInsets();
				float xScale=(float)(getParent().getWidth()-ins.left-ins.right)/(float)(graphBounds.width+graphBounds.x+GRAPH_BORDER_THICKNESS);
				float yScale=(float)(getParent().getHeight()-ins.top-ins.bottom)/(float)(graphBounds.height+graphBounds.y+GRAPH_BORDER_THICKNESS);
				setScaleFactor(Math.min(xScale,yScale));
			}
			invalidate();
		}
		repaint();
	}

	public void setGraphModel(FSMGraph graphModel) {						
		this.graphModel = graphModel;
		if(graphModel != null){
			this.graphModel.removeSubscriber(this);
			graphModel.addSubscriber(this);
			this.setName(graphModel.getName());		
			update();
		}else{
			this.setName("No automaton");
		}
	}

	public FSMGraph getGraphModel() {
		return graphModel;
	}

	public float getScaleFactor()
	{
		return scaleFactor;
	}
	
	public void setScaleFactor(float sf)
	{
		scaleFactor=sf;
	}

	public Dimension getPreferredSize()
	{
		return new Dimension((int)((graphBounds.width+GRAPH_BORDER_THICKNESS)*scaleFactor),(int)((graphBounds.height+GRAPH_BORDER_THICKNESS)*scaleFactor));
	}
}
