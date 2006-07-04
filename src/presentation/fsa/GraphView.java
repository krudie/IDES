package presentation.fsa;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import ui.GUISettings;

import main.Hub;
import model.Subscriber;

public class GraphView extends JComponent implements Subscriber {

	protected static final int GRAPH_BORDER_TICKNESS=10;
	
	protected float scaleFactor = 0.25f;
	protected Rectangle graphBounds=new Rectangle();
	
	/**
	 * if true, update() will set the scale factor so that the whole model fits in the view
	 */
	protected boolean scaleToFit = true;
	
	protected int width=100,height=100;
	
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

	    Rectangle r=getBounds();
	    g2D.setColor(Color.WHITE);
	    g2D.fillRect(0,0,r.width,r.height);
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
		if(getGraphModel()!=null)
		{
			graphBounds=getGraphModel().getBounds(true);
			if(scaleToFit&&getParent()!=null)
			{
				float xScale=(float)(getParent().getBounds().getWidth()-getParent().insets().left-getParent().insets().right-30)/(float)(graphBounds.width+graphBounds.x);
				float yScale=(float)(getParent().getBounds().getHeight()-getParent().insets().top-getParent().insets().bottom-30)/(float)(graphBounds.height+graphBounds.y);
				setScaleFactor(Math.min(xScale,yScale));
			}
			if(graphBounds.x<0||graphBounds.y<0)
			{
				graphModel.translate(-graphBounds.x+GRAPH_BORDER_TICKNESS,-graphBounds.y+GRAPH_BORDER_TICKNESS);
			}
			invalidate();
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
		return new Dimension((int)((graphBounds.width+GRAPH_BORDER_TICKNESS)*scaleFactor),(int)((graphBounds.height+GRAPH_BORDER_TICKNESS)*scaleFactor));
	}
}
