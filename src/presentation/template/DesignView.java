package presentation.template;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Iterator;

import javax.swing.JComponent;

import main.Hub;
import model.template.TemplateModel;
import model.template.TemplateModule;

import presentation.GraphicalLayout;
import presentation.LayoutShell;
import presentation.Presentation;
import presentation.fsa.FSAGraphSubscriber;

public class DesignView extends JComponent implements Presentation, TemplateGraphSubscriber {

	protected static final int GRAPH_BORDER_THICKNESS=10;

	protected TemplateGraph graph=null;
	
	protected Rectangle graphBounds=new Rectangle();

	protected float scaleFactor = 0.25f;
	/**
	 * if true, refreshView() will set the scale factor so that the whole model fits in the view
	 */
	protected boolean scaleToFit = true;

	public DesignView(TemplateGraph graph)
	{
		this.graph=graph;
		graph.addSubscriber(this);
	}
	
	public Dimension getPreferredSize()	{
		return Hub.getMainWindow().getSize();
//		return new Dimension((int)((graphBounds.width+GRAPH_BORDER_THICKNESS)*scaleFactor),(int)((graphBounds.height+GRAPH_BORDER_THICKNESS)*scaleFactor));
	}
	
	public JComponent getGUI()
	{
		return this;
	}
	
	public LayoutShell getLayoutShell()
	{
		return graph;
	}
	
	public void setTrackModel(boolean b)
	{
		if(b)
		{
			TemplateGraphSubscriber[] listeners=graph.getTemplateGraphSubscribers();
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
				graph.addSubscriber(this);
			}
		}
		else
		{
			graph.removeSubscriber(this);
		}
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2D = (Graphics2D) g;			
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);	    
	    g2D.setStroke(GraphicalLayout.WIDE_STROKE);

	    Rectangle r=getBounds();
	    if(true)
	    {
	    	g2D.setColor(Color.WHITE);
	    	g2D.fillRect(0,0,r.width,r.height);
	    }
	    
	    g2D.scale(scaleFactor, scaleFactor);
	    if(graph != null)
	    {
	    	graph.draw(g2D);
	    }
	}
	
	public void release()
	{
		
	}
	
	protected void refreshView()
	{
		if(graph!=null)  
		{
			graphBounds=graph.getBounds(true);
			if(graphBounds.x<0||graphBounds.y<0)
			{
				graph.translate(-graphBounds.x+GRAPH_BORDER_THICKNESS,-graphBounds.y+GRAPH_BORDER_THICKNESS);
			}
			if(scaleToFit&&getParent()!=null)
			{
				Insets ins=getParent().getInsets();
				float xScale=(float)(getParent().getWidth()-ins.left-ins.right)/(float)(graphBounds.width+graphBounds.x+GRAPH_BORDER_THICKNESS);
				float yScale=(float)(getParent().getHeight()-ins.top-ins.bottom)/(float)(graphBounds.height+graphBounds.y+GRAPH_BORDER_THICKNESS);
				setScaleFactor(Math.min(xScale,yScale));
			}
			repaint();
			revalidate();
//			invalidate();
//			Hub.getWorkspace().fireRepaintRequired();
		}
	}

	public void setScaleFactor(float sf) {
		scaleFactor=sf;
	}

	public void templateGraphChanged(TemplateGraphMessage message)
	{
		refreshView();
	}

	public void templateGraphSelectionChanged(TemplateGraphMessage message)
	{
		refreshView();
	}
}
