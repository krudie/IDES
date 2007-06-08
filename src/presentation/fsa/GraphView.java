package presentation.fsa;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;


import presentation.GraphicalLayout;
import presentation.LayoutShell;
import presentation.Presentation;

import main.Hub;

/**
 * The visual display of an FSAGraph.  Subscribes and response to change
 * notifications from the underlying Publisher portion of the underlying graph
 * layout model.  Canvas can be scaled to display the graph at any size from full 
 * to thumbnail representation.
 * @see Presentation
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
@SuppressWarnings("serial")
public class GraphView extends JComponent implements FSAGraphSubscriber,Presentation {

	protected static final int GRAPH_BORDER_THICKNESS=10;
	
	protected float scaleFactor = 0.25f;
	protected Rectangle graphBounds=new Rectangle();
	
	/**
	 * if true, refreshView() will set the scale factor so that the whole model fits in the view
	 */
	protected boolean scaleToFit = true;
	
	/**
	 * Presentation model (the composite structure that represents the DES model.)
	 * which handles synchronizing FSA model with the displayed graph.
	 */
	protected FSAGraph graphModel;
		
	public GraphView(){
		setGraphModel(null);
	}
	
	public GraphView(FSAGraph graphModel){
		setGraphModel(graphModel);
	}

	public JComponent getGUI()
	{
		return this;
	}
	
	public LayoutShell getLayoutShell()
	{
		return graphModel;
	}
	
	public void setTrackModel(boolean b)
	{
		if(b)
		{
			FSAGraphSubscriber[] listeners=graphModel.getFSAGraphSubscribers();
			boolean found=false;
			for(int i=0;i<listeners.length;++i)
			{
				if(listeners[i]==this)
				{
					found=true;
					break;
				}
			}
			if(!found)
			{
				graphModel.addSubscriber(this);
			}
		}
		else
		{
			graphModel.removeSubscriber(this);
		}
	}
	
	public void release()
	{
		setTrackModel(false);
	}

	public void paint(Graphics g) {
		paint(g,true);
	}

	public void paint(Graphics g, boolean doFill) {
		Graphics2D g2D = (Graphics2D) g;			
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);	    
	    g2D.setStroke(GraphicalLayout.WIDE_STROKE);

	    Rectangle r=getBounds();
	    if(doFill)
	    {
	    	//FIXME remove the condition on avoid layout
	    	if(graphModel.isAvoidLayoutDrawing())
	    	{
		    	g2D.setColor(Color.ORANGE);
	    	}
	    	else
	    	{
	    		g2D.setColor(Color.WHITE);
	    	}
	    	g2D.fillRect(0,0,r.width,r.height);
	    }
	    
	    g2D.scale(scaleFactor, scaleFactor);
	    if(graphModel != null)	graphModel.draw(g2D);	    
	}

	public void setGraphModel(FSAGraph graphModel) {
		if(this.graphModel != null) {
			this.graphModel.removeSubscriber(this);
		}		
		this.graphModel = graphModel;
		
		if(graphModel != null) {					
			graphModel.addSubscriber(this);
			this.setName(graphModel.getName());		
			refreshView();
		} else {
			this.setName("No automaton");
		}
	}

	public FSAGraph getGraphModel() {
		return graphModel;
	}

	public float getScaleFactor() {
		return scaleFactor;
	}
	
	public void setScaleFactor(float sf) {
		scaleFactor=sf;
	}

	public Dimension getPreferredSize()	{
		return new Dimension((int)((graphBounds.width+GRAPH_BORDER_THICKNESS)*scaleFactor),(int)((graphBounds.height+GRAPH_BORDER_THICKNESS)*scaleFactor));
	}

	/**
	 * Respond to change notification from underlying graph model.
	 *  
	 * @see presentation.fsa.FSAGraphSubscriber#fsaGraphChanged(presentation.fsa.FSAGraphMessage)
	 */
	public void fsaGraphChanged(FSAGraphMessage message) {
		// TODO check contents of message to determine minimal response required
		refreshView();			
	}

	
	protected void refreshView(){
		
		if(getGraphModel()!=null)  
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
				
			
			Hub.getWorkspace().fireRepaintRequired();
		}
//		repaint();
	}
	
	/* Don't need to respond to selection changes.
	 * 
	 * (non-Javadoc)
	 * @see observer.FSMGraphSubscriber#fsmGraphSelectionChanged(observer.FSMGraphMessage)
	 */
	public void fsaGraphSelectionChanged(FSAGraphMessage message) {	}
	
	public void fsaGraphSaveStatusChanged(FSAGraphMessage message) { }
}
