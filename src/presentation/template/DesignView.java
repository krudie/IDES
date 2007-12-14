package presentation.template;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import main.Hub;
import presentation.Geometry;
import presentation.GraphicalLayout;
import presentation.LayoutShell;
import presentation.Presentation;

public class DesignView extends JComponent implements Presentation,
		TemplateGraphSubscriber
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7211452787823947507L;

	protected static final int GRAPH_BORDER_THICKNESS = 10;

	protected TemplateGraph graph = null;

	protected Rectangle graphBounds = new Rectangle();

	protected float scaleFactor = 0.25f;

	/**
	 * if true, refreshView() will set the scale factor so that the whole model
	 * fits in the view
	 */
	protected boolean scaleToFit = true;

	public DesignView(TemplateGraph graph)
	{
		this.graph = graph;
		graph.addSubscriber(this);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return Hub.getMainWindow().getSize();
		// return new
		// Dimension((int)((graphBounds.width+GRAPH_BORDER_THICKNESS)*scaleFactor),(int)((graphBounds.height+GRAPH_BORDER_THICKNESS)*scaleFactor));
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
		if (b)
		{
			TemplateGraphSubscriber[] listeners = graph
					.getTemplateGraphSubscribers();
			boolean found = false;
			for (int i = 0; i < listeners.length; ++i)
			{
				if (listeners[i] == this)
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				graph.addSubscriber(this);
			}
		}
		else
		{
			graph.removeSubscriber(this);
		}
	}

	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setStroke(GraphicalLayout.WIDE_STROKE);

		Rectangle r = getBounds();
		if (true)
		{
			g2D.setColor(Color.WHITE);
			g2D.fillRect(0, 0, r.width, r.height);
		}

		g2D.scale(scaleFactor, scaleFactor);
		if (graph != null)
		{
			graph.draw(g2D);
		}
	}

	public void release()
	{
		setTrackModel(false);
	}

	protected void refreshView()
	{
		if (graph != null)
		{
			graphBounds = graph.getBounds(true);
			if (graphBounds.x < 0 || graphBounds.y < 0)
			{
				graph.translate(-graphBounds.x + GRAPH_BORDER_THICKNESS,
						-graphBounds.y + GRAPH_BORDER_THICKNESS);
			}
			if (scaleToFit && getParent() != null)
			{
				Insets ins = getParent().getInsets();
				float xScale = (float)(getParent().getWidth() - ins.left - ins.right)
						/ (float)(graphBounds.width + graphBounds.x + GRAPH_BORDER_THICKNESS);
				float yScale = (float)(getParent().getHeight() - ins.top - ins.bottom)
						/ (float)(graphBounds.height + graphBounds.y + GRAPH_BORDER_THICKNESS);
				setScaleFactor(Math.min(xScale, yScale));
			}
			repaint();
			revalidate();
			// invalidate();
			// Hub.getWorkspace().fireRepaintRequired();
		}
	}

	public void setScaleFactor(float sf)
	{
		scaleFactor = sf;
	}

	public void templateGraphChanged(TemplateGraphMessage message)
	{
		refreshView();
	}

	public void templateGraphSelectionChanged(TemplateGraphMessage message)
	{
		refreshView();
	}

	public GraphBlock getBlockAt(Point2D p)
	{
		GraphBlock ret = null;
		for (GraphBlock b : graph.getBlocks())
		{
			if (b.bounds().contains(p))
			{
				ret = b;
				break;
			}
		}
		return ret;
	}

	public GraphLink getLinkAt(Point2D p)
	{
		GraphLink ret = null;
		Rectangle2D.Float r = new Rectangle2D.Float(
				(float)p.getX() - 3,
				(float)p.getY() - 3,
				6,
				6);
		for (GraphLink l : graph.getLinks())
		{
			Point2D loc1 = graph
					.getLayout(l.getLink().getBlockLeft()).getLocation();
			Point2D loc2 = graph
					.getLayout(l.getLink().getBlockRight()).getLocation();
			float slope = (float)(loc1.getX() < loc2.getX() ? Geometry
					.slope(loc1, loc2) : Geometry.slope(loc2, loc1));
			float disp = (float)(loc1.getY() - slope * loc1.getX());
			// System.out.println(slope);
			// System.out.println(disp);
			// System.out.println(r);
			// System.out.println(graph.getLayout(l.getLink().getBlockLeft()).getLocation());
			// System.out.println(graph.getLayout(l.getLink().getBlockRight()).getLocation());
			// System.out.println(
			// ""+r.getMinX()+","+(r.getMinX()*slope+disp)+"||"+
			// r.getMaxX()+","+(r.getMaxX()*slope+disp)+"||"+
			// ((r.getMinY()-disp)/slope)+","+r.getMinY()+"||"+
			// ((r.getMaxY()-disp)/slope)+","+r.getMaxY()
			// );
			if (r.contains(r.getMinX(), r.getMinX() * slope + disp)
					|| r.contains(r.getMaxX(), r.getMaxX() * slope + disp)
					|| r.contains((r.getMinY() - disp) / slope, r.getMinY())
					|| r.contains((r.getMaxY() - disp) / slope, r.getMaxY()))
			{
				ret = l;
				break;
			}
		}
		return ret;
	}

	public void forceRepaint()
	{
	}
}
