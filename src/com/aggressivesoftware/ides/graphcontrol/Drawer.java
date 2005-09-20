/*
 * Created on Dec 17, 2004
 */
package com.aggressivesoftware.ides.graphcontrol;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.graphparts.CubicCurve2Dex;

/**
 * This subclass of Graphics2D allows leaf objects Nodes and Edges and ArrowHeads to draw
 * themselves, while being ignorant of color, stroke and graphics classes.
 * Their options in this regard are limited to the constants provided to them by their GraphModel.
 * This also keeps AWT out of most of the classes.
 * 
 * @author Michael Wood
 */
public class Drawer
{
	/**
     * Optional line styles.
     */
	public static final int SOLID = 0,
						    DASHED = 1,
							SMALL_SOLID = 2,
							SMALL_DASHED = 3,
							TINY_DASHED = 4;

	/**
     * The GraphingPlatform which this Drawer will service.
     */
	private GraphingPlatform gp = null;

	/**
     * The Graphics2D which will actually do the drawing.
     */
	private Graphics2D g2d = null;

	/**
     * The scale at which drawing should be done.
     */
	private float scale = 1;

	/**
     * The scale at which drawing should be done.
     */
	private BasicStroke	solid_stroke = null,
					    dashed_stroke[] = null,
						small_solid_stroke = null,
						small_dashed_stroke[] = null,
						tiny_dashed_stroke[] = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Drawer Construction ////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Construct the Drawer.
     *
     * @param	gp		The GraphingPlatform which this Drawer will service.
     * @param	g2d		The Graphics2D which will actually do the drawing.
     * @param	scale	The scale at which drawing should be done.
     */
	public Drawer(GraphingPlatform gp, Graphics2D g2d, float scale)
	{
		this.gp = gp;
		this.g2d = g2d;
		this.scale = scale;
		
		float stroke_width = scale*2;
		if (stroke_width < 0.25) { stroke_width = (float)0.25; }
				
		solid_stroke = new BasicStroke(stroke_width);
		small_solid_stroke = new BasicStroke(stroke_width/2);

		dashed_stroke = new BasicStroke[6];
		small_dashed_stroke = new BasicStroke[6];
		tiny_dashed_stroke = new BasicStroke[6];
		
		dashed_stroke[0] = new BasicStroke(stroke_width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {0.5f,0,1},1);
		dashed_stroke[1] = new BasicStroke(stroke_width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {1,0,1},1);
		dashed_stroke[2] = new BasicStroke(stroke_width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {2,0,1},1);
		dashed_stroke[3] = new BasicStroke(stroke_width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {4,0,1},1);
		dashed_stroke[4] = new BasicStroke(stroke_width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {8,0,1},1);
		dashed_stroke[5] = new BasicStroke(stroke_width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {16,0,1},1);
		small_dashed_stroke[0] = new BasicStroke(stroke_width/2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {0.5f,0,1},1);
		small_dashed_stroke[1] = new BasicStroke(stroke_width/2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {1,0,1},1);
		small_dashed_stroke[2] = new BasicStroke(stroke_width/2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {2,0,1},1);
		small_dashed_stroke[3] = new BasicStroke(stroke_width/2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {4,0,1},1);
		small_dashed_stroke[4] = new BasicStroke(stroke_width/2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {8,0,1},1);
		small_dashed_stroke[5] = new BasicStroke(stroke_width/2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {16,0,1},1);
		tiny_dashed_stroke[0] = new BasicStroke(stroke_width/8,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {0.5f,0,1},1);
		tiny_dashed_stroke[1] = new BasicStroke(stroke_width/8,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {1,0,1},1);
		tiny_dashed_stroke[2] = new BasicStroke(stroke_width/8,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {2,0,1},1);
		tiny_dashed_stroke[3] = new BasicStroke(stroke_width/8,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {4,0,1},1);
		tiny_dashed_stroke[4] = new BasicStroke(stroke_width/8,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {8,0,1},1);
		tiny_dashed_stroke[5] = new BasicStroke(stroke_width/8,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[] {16,0,1},1);
		
		g2d.setStroke(solid_stroke);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Settings ///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
	/**
	 * Change the drawing colour.
	 * 
	 * @param	color_handle	An index to the GraphController's color_set from the constants in the GraphModel.
	 */
	public void setColor(int color_handle)
	{
		g2d.setColor(gp.gc.color_set[color_handle]);	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Access /////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
	 * Access the FontRenderContext of the Graphics2D object.
	 * 
	 * @return	The FontRenderContext of the Graphics2D object.
	 */
	public FontRenderContext getFontRenderContext() { return g2d.getFontRenderContext(); }
	
	private BasicStroke dashedStroke()
	{
		if (scale < 1) { return dashed_stroke[0]; }
		else if (scale == 1) { return dashed_stroke[1]; }
		else if (scale == 2) { return dashed_stroke[2]; }
		else if (scale == 4) { return dashed_stroke[3]; }
		else if (scale == 8) { return dashed_stroke[4]; }
		else { return dashed_stroke[5]; }
	}

	private BasicStroke smallDashedStroke()
	{
		if (scale < 1) { return small_dashed_stroke[0]; }
		else if (scale == 1) { return small_dashed_stroke[1]; }
		else if (scale == 2) { return small_dashed_stroke[2]; }
		else if (scale == 4) { return small_dashed_stroke[3]; }
		else if (scale == 8) { return small_dashed_stroke[4]; }
		else { return small_dashed_stroke[5]; }
	}

	private BasicStroke tinyDashedStroke()
	{
		if (scale < 1) { return tiny_dashed_stroke[0]; }
		else if (scale == 1) { return tiny_dashed_stroke[1]; }
		else if (scale == 2) { return tiny_dashed_stroke[2]; }
		else if (scale == 4) { return tiny_dashed_stroke[3]; }
		else if (scale == 8) { return tiny_dashed_stroke[4]; }
		else { return tiny_dashed_stroke[5]; }
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Drawing ////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
     * Draw a circle.
     * 
     * @param	origin	The origin of the circle.
     * @param	r		The radius of the circle.
     * @param	style	Optional styles, such as solid or dashed.
     */
	public void drawCircle(Point origin, int r, int style)
	{ drawCircle(origin.x,origin.y,r,style); }		
	
	/**
     * Draw a circle.
     * 
     * @param	x		The x co-ordinate of the origin of the circle.
     * @param	y		The y co-ordinate of the origin of the circle.
     * @param	r		The radius of the circle.
     * @param	style	Optional styles, such as solid or dashed.
     */
	public void drawCircle(int x, int y, int r, int style)
	{
		if (style == SMALL_SOLID) { g2d.setStroke(small_solid_stroke); }
		if (scale == 1) { g2d.drawOval(x-r, y-r, 2*r, 2*r); }
		else { g2d.draw(new Ellipse2D.Float((x-r)*scale, (y-r)*scale, 2*r*scale, 2*r*scale)); }
		if (style == SMALL_SOLID) { g2d.setStroke(solid_stroke); }
	}	
		
	/**
     * Draw a line.
     * 
     * @param	x1		The x co-ordinate of the start of the line.
     * @param	y1		The y co-ordinate of the start of the line.
     * @param	x2		The x co-ordinate of the end of the line.
     * @param	y2		The y co-ordinate of the end of the line.
     * @param	style	Optional styles, such as solid or dashed.
     */
	public void drawLine(int x1, int y1, int x2, int y2, int style)
	{
		if (style == DASHED) { g2d.setStroke(dashedStroke()); }
		else if (style == SMALL_DASHED) { g2d.setStroke(smallDashedStroke()); }
		else if (style == TINY_DASHED) { g2d.setStroke(tinyDashedStroke()); }
		if (scale == 1) { g2d.drawLine(x1,y1,x2,y2); }
		else { g2d.draw(new Line2D.Float(x1*scale,y1*scale,x2*scale,y2*scale));  }
		if (style != SOLID) { g2d.setStroke(solid_stroke); }
	}	
	
	/**
     * Draw a line.
     * 
     * @param	p1		The start point.
     * @param	p2		The end point.
     * @param	style	Optional styles, such as solid or dashed.
     */
	public void drawLine(Point p1, Point p2, int style)
	{ drawLine(p1.x,p1.y,p2.x,p2.y,style); }

	/**
     * Draw a box.
     * 
     * @param	x		The x co-ordinate of the top left corner of the box.
     * @param	y		The y co-ordinate of the top left corner of the box.
     * @param	width	The width of the box.
     * @param	height	The height of the box.
     * @param	style	Optional styles, such as solid or dashed.
     */
	public void drawBoxWH(int x, int y, int width, int height, int style)
	{
		if (style == DASHED) { g2d.setStroke(dashedStroke()); }
		else if (style == SMALL_DASHED) { g2d.setStroke(smallDashedStroke()); }
		if (scale == 1) { g2d.drawRect(x,y,width,height); }
		else { g2d.draw(new Rectangle2D.Float(x*scale,y*scale,width*scale,height*scale)); }
		if (style != SOLID) { g2d.setStroke(solid_stroke); }
	}	
	
	/**
     * Draw a box.
     * 
     * @param	x1		The x co-ordinate of the top left corner of the box.
     * @param	y1		The y co-ordinate of the top left corner of the box.
     * @param	x2		The x co-ordinate of the bottom right corner of the box.
     * @param	y2		The y co-ordinate of the bottom right corner of the box.
     * @param	style	Optional styles, such as solid or dashed.
     */
	public void drawBoxXY(int x1, int y1, int x2, int y2, int style)
	{ drawBoxWH(x1,y1,x2-x1,y2-y1,style); }
		
	
    /**
     * Draw a curve.
     *
     * @param	curve	The curve to be drawn.
     * @param	style	The style for the Drawer (i.e. solid/dashed).
     */
	public void drawCurve(CubicCurve2Dex curve, int style)
	{	
		if (style == DASHED) { g2d.setStroke(dashedStroke()); }
		if (scale == 1) { g2d.draw(curve); }
		else { g2d.draw(new CubicCurve2Dex(scale*curve.x1,scale*curve.y1,scale*curve.ctrlx1,scale*curve.ctrly1,scale*curve.ctrlx2,scale*curve.ctrly2,scale*curve.x2,scale*curve.y2)); }		
		if (style == DASHED) { g2d.setStroke(solid_stroke); }
	}

	/**
	 * Draw a filled in Shape.
	 * 
	 * @param	shape	The Shape to be drawn.
	 */
	public void drawShape(Shape shape)
	{
		if (scale != 1) { g2d.scale(scale,scale); }
		g2d.fill(shape); 
		if (scale != 1) { g2d.scale(1/scale,1/scale); }
	}
	
	/**
	 * Draw a filled Polygon.
	 * 
	 * @param xcoords	The x co-ordinates of the points in the polygon.
	 * @param ycoords	The y co-ordinates of the points in the polygon.
	 * @param size		The number of points in the polygon.
	 */
	public void drawPolygon(int[] xcoords, int[] ycoords, int size)
	{
		if (scale == 1) { g2d.fillPolygon(xcoords, ycoords, 4); }
		else 
		{ 
			GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD,4);
			p.moveTo(xcoords[0]*scale,ycoords[0]*scale);
			p.lineTo(xcoords[1]*scale,ycoords[1]*scale);
			p.lineTo(xcoords[2]*scale,ycoords[2]*scale);
			p.lineTo(xcoords[3]*scale,ycoords[3]*scale);
			p.closePath();
			g2d.fill(p);
		}
	}
	
	/**
	 * Draw a BufferedImage
	 * 
	 * @param buffered_image	The BufferedImage to be drawn
	 * @param x					Important, this is the scaled location, no modification will be made to this co-ordinate.
	 * @param y					Important, this is the scaled location, no modification will be made to this co-ordinate.
	 */
	public void draw(BufferedImage buffered_image, int x, int y)
	{
		if (scale == 1)
		{ g2d.drawImage(buffered_image, null, x, y); }
		else
		{
			if (Math.round(buffered_image.getWidth()*scale) != 0 && Math.round(buffered_image.getHeight()*scale) != 0)
			{
				Image image = buffered_image.getScaledInstance(Math.round(buffered_image.getWidth()*scale),Math.round(buffered_image.getHeight()*scale),0);
				g2d.drawImage(image,Math.round(x*scale),Math.round(y*scale),null);
			}
		}
	}

	public void drawString(String s, int x, int y)
	{
		if (scale != 1) { g2d.scale(scale,scale); }
		g2d.drawString(s,x,y);
		if (scale != 1) { g2d.scale(1/scale,1/scale); }
	}
}