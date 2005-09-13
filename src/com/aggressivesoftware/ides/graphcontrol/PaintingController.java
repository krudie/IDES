/*
 * Created on Jul 7, 2004
 */
package com.aggressivesoftware.ides.graphcontrol;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.holongate.j2d.IPaintable;

/**
 * This handles the Holongate intervase, allowing us to use Java2D in SWT
 * 
 * @author Michael Wood
 */
public class PaintingController implements IPaintable 
{
	private GraphController controller = null;
	
	public PaintingController(GraphController graph_controller) 
	{ controller = graph_controller; }
	
	public void redraw(Control control, GC gc) { }
	
	public Rectangle2D getBounds(Control control) { return null; }

	public void paint(Control control, Graphics2D g2d) 
	{
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.setStroke(new BasicStroke(controller.line_width));
	    controller.draw(g2d);
	}
}