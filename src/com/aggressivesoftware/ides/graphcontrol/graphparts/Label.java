/*
 * Created on Oct 15, 2004
 */
package com.aggressivesoftware.ides.graphcontrol.graphparts;

import java.util.Vector;

import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.Drawer;
import com.aggressivesoftware.ides.graphcontrol.LatexPrinter;

/**
 * Displayable information to be associated with graph objects.
 * 
 * @author Michael Wood
 */
public abstract class Label 
{
	/**
     * Various anchor types
     */
	public static final int CENTER = 0,
							CORNER = 1;

	/**
     * The factor by which the bounding box of the label is grown in order for drawing to look nice.
     */
	public static final int BOUNDING_BOX_FACTOR = 4;

	/**
     * The factor by which the rendered radius of the label is grown in order for drawing to look nice.
     */
	public static final int RENDERED_RADIUS_FACTOR = 3;

	/**
     * The factor by which the rendered radius of the label is grown in order for drawing to look nice.
     */
	public static final int MINIMUM_RADIUS = 15;
	
	/**
     * Class names for various Labels.
     */
	public static final String LATEX_CLASS = "com.aggressivesoftware.ides.graphcontrol.graphparts.LatexLabel",
							   GLYPH_CLASS = "com.aggressivesoftware.ides.graphcontrol.graphparts.GlyphLabel";

	/**
     * The platform in which this Label will exist.
     */
	protected GraphingPlatform gp = null;

	/**
     * The parent of this Label in the GraphModel.
     */
	protected GraphObject parent = null;

	/**
     * The Point that anchors the rendered Label.
     * If anchor_type = CENTER, then the rendered Label should be centered at the anchor.
     * If anchor_type = CORNER, then the rendered Label should have the point at it's top left corner.
     */
	protected Point anchor = null;

	/**
     * Determines how the anchor should be interpreted.
     */
	protected int anchor_type = CENTER;

	/**
     * The pre-rendered representation of this Label.
     */
	public String string_representation = "";
	
	/**
     * The actual radius of the bounding box of this rendered Label.
     */
	public int rendered_radius = MINIMUM_RADIUS;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Label construction /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
     * Construct the Label.
     * 
     * @param	gp		The GraphingPlatform in which this Label will exist.
     * @param	parent	The GraphObject which this Label serves.
     */
	public Label(GraphingPlatform gp, GraphObject parent) 
	{
		this.gp = gp;
		this.parent = parent; 
	}

	/**
     * Construct the Label.
     * 
     * @param	gp						The GraphingPlatform in which this Label will exist.
     * @param	parent					The GraphObject which this Label serves.
     * @param	string_representation	The pre-rendered representation of this Label.
	 * @param	anchor					Positioning information for the rendered Label.
	 * @param	anchor_type				Determines how the anchor should be interpreted.
	 * @param	rendered_radius			The rendered radius if known (as in cloning). Should be set to MINIMUM_RADIUS otherwise.
     */
	public Label(GraphingPlatform gp, GraphObject parent, String string_representation, Point anchor, int anchor_type, int rendered_radius) 
	{ 
		this.gp = gp;
		this.parent = parent; 
		this.string_representation = string_representation;
		if (anchor != null) { this.anchor = anchor.getCopy(); }
		this.anchor_type = anchor_type;
		this.rendered_radius = rendered_radius;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// type testing ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
     * Test if this Label is a LatexLabel.
     * 
     * @return	true if it is a LatexLabel.
     */
	public boolean isLatexLabel() { return (this.getClass().getName() == LATEX_CLASS); }

	/**
     * Test if this Label is a GlyphLabel.
     * 
     * @return	true if it is a GlyphLabel.
     */
	public boolean isGlyphLabel() { return (this.getClass().getName() == GLYPH_CLASS); }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// display ////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	
	/**
     * Create a displayable representation of the label.
     * If no anchor has been specified, then no rendering takes place.
     * You must first explicitly specify an anchor.
     */
	public abstract void render();
	
	/**
     * Render if previous rendering failed, but rendering is now possible.
     * If no anchor has been specified, then no rendering takes place.
     * You must first explicitly specify an anchor.
     */
	public abstract void renderIfNeeded();
	
	/**
     * Draw the displayable representation of this Label.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     */
	public abstract void drawLabel(Drawer drawer);

	/**
     * An optional drawing method.
     * Implementations without any special drawing needs should use this method to recall drawLabel.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     * @param	data	Some data.
     */
	public abstract void drawData(Drawer drawer, Vector data);	
	
	/**
     * Draw a bounding box around this Label.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     */
	public abstract void drawBox(Drawer drawer);
	
	/**
     * Draw a line from the top left corner of the bounding box of this Label to a specified location.
     * 
     * @param	drawer		The Drawer that will handle the drawing.
     * @param	destination	The destination point for the line from the bounding box of this Label. 
     */
	public abstract void drawTether(Drawer drawer, Point destination);
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// data ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
     * Test if this Label actually draws anything.
     * 
     * @return	true If this label actually draws something.
     */
	public boolean isntEmpty() { return (string_representation.length() > 0); }

	/**
     * Test if this Label's anchor is at the given Point.
     * 
     * @param	origin	The point to test.
     * @return	true If this Label is centered at the given Point.
     */
	public boolean isAnchoredAt(Point origin) { return (origin.isSameAs(anchor)); }

	/**
     * Specify a new anchor for the displayable representation of this Label.
     * 
	 * @param	anchor			Positioning information for the rendered Label.
	 * @param	anchor_type		Determines how the anchor should be interpreted.
     */
	public abstract void setAnchor(Point anchor, int anchor_type);
	
	/**
     * Test if a mouse-click should select this Label
     * 
     * @param	mouse	The Point to be tested.
     * @return	true if the mouse-click should select this label.
     */
	public abstract boolean isLocated(Point mouse);
	
	/**
     * Produce formatted LaTeX output.
     * 
     * @param	latex_printer	The object that stores and orders the generated output.
     * @param	x1				The x co-ordinate of the top left corner of the selection area (in SWT perspective).
     * @param	y2				The y co-ordinate of the bottom right corner of the selection area (in SWT perspective).
     * @return	A string for inclusion in the LaTeX markup code.
     */
	public abstract void exportLatex(LatexPrinter latex_printer, int x1, int y2);
}
