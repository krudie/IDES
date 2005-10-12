/*
 * Created on Jan 25, 2005
 */
package com.aggressivesoftware.ides.graphcontrol.graphparts;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.StringTokenizer;
import java.util.Vector;

import com.aggressivesoftware.general.Ascii;
import com.aggressivesoftware.geometric.Geometric;
import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.Drawer;
import com.aggressivesoftware.ides.graphcontrol.LatexPrinter;

/**
 * @author Michael Wood
 */
public class GlyphLabel extends Label
{
	/**
     * The Font used when rendering.
     */
	private static final Font FONT = new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 12);
	
	/**
     * The FontRenderContext used when rendering.
     */
	private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(null,true,false);

	/**
     * Constants based on font size.
     */
	private static final int GLYPH_HEIGHT = 10,
							 LINE_SPACE = 3;
		
	/**
     * The bounding region of the label.
     */
	private Rectangle glyph_bounds = null;

	/**
     * A vector of glyph shapes and vectors to be rendered.
     */
	private Vector glyph_vector_vector = null,
	               glyph_shape_vector = null;
	
	/**
     * The unpadded box height used in rendering.
     */
	private int unpadded_box_height = 0;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// GlyphLabel construction ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
     * Construct the GlyphLabel.
     * 
     * @param	gp		The GraphingPlatform in which this Label will exist.
     * @param	parent	The GraphObject which this Label serves.
     */
	public GlyphLabel(GraphingPlatform gp, GraphObject parent) { super(gp, parent); }

	/**
     * Construct the GlyphLabel.
     * 
     * @param	gp						The GraphingPlatform in which this Label will exist.
     * @param	parent					The GraphObject which this Label serves.
     * @param	string_representation	The pre-rendered representation of this Label.
	 * @param	anchor					Positioning information for the rendered Label.
	 * @param	anchor_type				Determines how the anchor should be interpreted.
     */
	public GlyphLabel(GraphingPlatform gp, GraphObject parent, String string_representation, Point anchor, int anchor_type)
	{ 
		super(gp, parent, string_representation, anchor, anchor_type, MINIMUM_RADIUS); 
	}
	
	/**
     * Construct a clone of a GlyphLabel.
     * 
     * @param	gp			The GraphingPlatform in which this Label will exist.
     * @param	parent		The GraphObject which this Label serves.
	 * @param	source		A source GlyphLabel from which to clone this one.
     */
	public GlyphLabel(GraphingPlatform gp, GraphObject parent, GlyphLabel source) 
	{ 
		super(gp, parent, source.string_representation, source.anchor, source.anchor_type, source.rendered_radius); 
		glyph_vector_vector = source.cloneRenderedGlyphVectors();
		glyph_bounds = source.cloneBoundingBox();
		unpadded_box_height = source.cloneBoxHeight();
	}
	
	/**
     * Access a copy of the unpadded_box_height.
     * 
	 * @return	A copy of the unpadded_box_height.
     */
	public int cloneBoxHeight() 
	{ 
		return unpadded_box_height;
	}
	
	/**
     * Clone the bounding box.
     * 
	 * @return	A clone of the bounding box.
     */
	public Rectangle cloneBoundingBox() 
	{ 
		if (glyph_bounds != null) { return (Rectangle)glyph_bounds.clone(); }
		else { return null; }
	}

	/**
     * Clone the rendered glyph vectors.
     * 
	 * @return	A clone of the Vector of GlyphVectors.
     */
	public Vector cloneRenderedGlyphVectors() 
	{ 
		// the shapes are derived from the vectors without modifying the vectors
		// it is therefore okay to have both clones pointint to the same vectors
		// if ever either change their string representation, they will drop the pointer to the Vector of GlyphVectors and create a new Vector.
		return glyph_vector_vector;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// rendering ////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	
	/**
     * Create a displayable representation of the label.
     * If no anchor has been specified, then no rendering takes place.
     * You must first explicitly specify an anchor.
     */
	public void render()
	{
		if (string_representation.length() > 0)
		{
			try
			{ 	
				gp.shell.setEnabled(false);
				gp.shell.setCursor(gp.rm.getCursor(gp.rm.WAIT_CURSOR));
				
				if (anchor_type == CENTER) { renderAtCenter(); }
				else { renderAtCorner(); }
				
				rendered_radius = Math.round(Geometric.magnitude(glyph_bounds.height, glyph_bounds.width)/2) + RENDERED_RADIUS_FACTOR;
				rendered_radius = Math.max(rendered_radius, MINIMUM_RADIUS);
				parent.accomodateLabel();
	
				gp.gc.j2dcanvas.repaint();
				gp.shell.setEnabled(true);
				gp.shell.setCursor(gp.rm.getCursor(gp.rm.ARROW_CURSOR));
			}
			catch (Exception e) { e.printStackTrace(); }	
		}
		else { glyph_shape_vector = null; }
	}
	
	/**
     * Render if previous rendering failed, but rendering is now possible.
     * If no anchor has been specified, then no rendering takes place.
     * You must first explicitly specify an anchor.
     */
	public void renderIfNeeded()
	{
		if (glyph_shape_vector == null && string_representation.length() > 0)
		{ render(); }
	}
	
	/**
     * Create a displayable representation of the label, centered at the anchor.
     */
	private void renderAtCenter()
	{
		String glyph_string = string_representation;
		int num_lines = Ascii.occurrances(glyph_string,Ascii.RETURN)+1;
		int line_number = 0;
		int box_width = (int)Math.round(2*MINIMUM_RADIUS/Math.sqrt(2));
		int box_height = 0;
		GlyphVector glyph_vector = null;
		Shape glyph_shape = null;
		glyph_shape_vector = new Vector();
		glyph_vector_vector = new Vector();
		
		StringTokenizer s = new StringTokenizer(glyph_string,Ascii.STANDARD_RETURN);

		//build the glyphs
		while (s.hasMoreTokens()) 
		{ 
			line_number++;
			glyph_string = s.nextToken(); 
			glyph_vector = FONT.createGlyphVector(FONT_RENDER_CONTEXT,glyph_string);
			glyph_vector_vector.addElement(glyph_vector);
			glyph_bounds = glyph_vector.getPixelBounds(glyph_vector.getFontRenderContext(),0,0);
			if (glyph_bounds.width > box_width ) { box_width = glyph_bounds.width; }
			box_height = box_height + GLYPH_HEIGHT;
			if (num_lines > 1 && line_number != 1) { box_height = box_height + LINE_SPACE; }
		}
				
		//build the glyph shapes in the proper positions
		for (int i=0; i<glyph_vector_vector.size(); i++)
		{
			glyph_bounds = ((GlyphVector)glyph_vector_vector.elementAt(i)).getPixelBounds(glyph_vector.getFontRenderContext(),0,0);
			glyph_shape = ((GlyphVector)glyph_vector_vector.elementAt(i)).getOutline
			              (anchor.x - glyph_bounds.x - glyph_bounds.width/2, 
			               anchor.y - glyph_bounds.y - box_height/2 + i*(GLYPH_HEIGHT + LINE_SPACE));
			glyph_shape_vector.addElement(glyph_shape);
		}
		unpadded_box_height = box_height;

		// build the bounding box
		glyph_bounds = new Rectangle(anchor.x - box_width/2, anchor.y - box_height/2, box_width, box_height);
	}
	
	/**
     * Create a displayable representation of the label, with its top left corner at the anchor.
     */
	private void renderAtCorner()
	{
		Point modified_anchor = anchor.plus(new Point(0,10));
		GlyphVector glyph_vector = FONT.createGlyphVector(FONT_RENDER_CONTEXT,string_representation);
		Shape glyph_shape = glyph_vector.getOutline(modified_anchor.x,modified_anchor.y);
		glyph_bounds = glyph_vector.getPixelBounds(glyph_vector.getFontRenderContext(),modified_anchor.x,modified_anchor.y);
		glyph_bounds.grow(BOUNDING_BOX_FACTOR,BOUNDING_BOX_FACTOR);
		glyph_shape_vector = new Vector();
		glyph_shape_vector.addElement(glyph_shape);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// drawing ////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
	/**
     * Draw the displayable representation of this Label.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     */
	public void drawLabel(Drawer drawer)
	{
		if (glyph_shape_vector != null) 
		{
			for (int i=0; i<glyph_shape_vector.size(); i++)
			{ drawer.drawShape((Shape)glyph_shape_vector.elementAt(i)); }
		}
	}
	
	/**
     * Simply recalls drawLabel()
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     * @param	data	Some data.
     */
	public void drawData(Drawer drawer, Vector data) { drawLabel(drawer); }	
	
	/**
     * Draw a bounding box around this Label.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     */
	public void drawBox(Drawer drawer)
	{

		drawer.drawBoxWH(glyph_bounds.x,glyph_bounds.y,glyph_bounds.width,glyph_bounds.height,Drawer.SMALL_DASHED);		
	}
	
	/**
     * Draw a line from the top left corner of the bounding box of this Label to a specified location.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     * @param	anchor	The destination point for the line from the bounding box of this Label. 
     */
	public void drawTether(Drawer drawer, Point anchor)
	{
		drawer.drawLine(anchor.x,anchor.y,glyph_bounds.x,glyph_bounds.y,Drawer.SMALL_DASHED);		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// data ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	
	/**
     * Specify a new anchor for the displayable representation of this Label.
     * 
	 * @param	anchor			Positioning information for the rendered Label.
	 * @param	anchor_type		Determines how the anchor should be interpreted.
     */
	public void setAnchor(Point anchor, int anchor_type)
	{
		boolean doit = true;
		if (this.anchor != null) { if (anchor.isSameAs(this.anchor) && anchor_type == this.anchor_type) { doit = false; } }
		if (doit)
		{
			if (this.anchor == null) { this.anchor = anchor.getCopy(); }
			else { this.anchor.copy(anchor); }
			this.anchor_type = anchor_type;
						
			if (glyph_bounds != null && glyph_vector_vector != null && anchor_type == CENTER)
			{
				glyph_bounds.x = anchor.x - glyph_bounds.width/2;
				glyph_bounds.y = anchor.y - glyph_bounds.height/2;
				
				//build the glyph shapes in the proper positions
				glyph_shape_vector = new Vector();
				Rectangle these_bounds = null;
				Shape glyph_shape = null;
				for (int i=0; i<glyph_vector_vector.size(); i++)
				{
					these_bounds = ((GlyphVector)glyph_vector_vector.elementAt(i)).getPixelBounds(FONT_RENDER_CONTEXT,0,0);
					glyph_shape = ((GlyphVector)glyph_vector_vector.elementAt(i)).getOutline
		            			  (anchor.x - these_bounds.x - these_bounds.width/2, 
		            			  		anchor.y - these_bounds.y - unpadded_box_height/2 + i*(GLYPH_HEIGHT + LINE_SPACE));
					glyph_shape_vector.addElement(glyph_shape);
				}
			}
			else if (anchor_type == CORNER && string_representation.length() > 0) { renderAtCorner(); }
		}
	}

	/**
     * Test if a mouse-click should select this Label
     * 
     * @param	mouse	The Point to be tested.
     * @return	true if the mouse-click should select this label.
     */
	public boolean isLocated(Point mouse)
	{
		if ( mouse.x > glyph_bounds.x + BOUNDING_BOX_FACTOR/2 &&
			 mouse.y > glyph_bounds.y + BOUNDING_BOX_FACTOR/2 &&
			(mouse.x-glyph_bounds.x) < (glyph_bounds.width-BOUNDING_BOX_FACTOR) &&
			(mouse.y-glyph_bounds.y) < (glyph_bounds.height-BOUNDING_BOX_FACTOR) )
		{ return true; } else { return false; }
	}
	
	/**
     * Produce formatted LaTeX output.
     * 
     * @param	latex_printer	The object that stores and orders the generated output.
     * @param	x1				The x co-ordinate of the top left corner of the selection area (in SWT perspective).
     * @param	y2				The y co-ordinate of the bottom right corner of the selection area (in SWT perspective).
     * @return	A string for inclusion in the LaTeX markup code.
     */
	public void exportLatex(LatexPrinter latex_printer, int x1, int y2)
	{
		if (string_representation.length() > 0)
		{
			if (anchor_type == CENTER)
			{
				// this is used by nodes

				// this is used by nodes
				if (gp.sv.use_pstricks)
				{
					// just to be safe, should always be a node anyway
					if (parent.isNode())
					{
						// draws the label centered about the given point
						// point is node origin translated into latex coords
						// need parbox so that things like CR will translate
						String safe_representation = LatexPrinter.escapeLaTeX(string_representation);
						safe_representation = Ascii.replaceAll(safe_representation,Ascii.STANDARD_RETURN+Ascii.STANDARD_RETURN,Ascii.STANDARD_RETURN);
						safe_representation = Ascii.replaceAll(safe_representation,Ascii.STANDARD_RETURN,"\\\\ ");
						latex_printer.addLaTeX("  \\rput(" + (((Node)parent).origin().x-x1) + "," + (y2-((Node)parent).origin().y) + "){\\parbox{" + glyph_bounds.width + "pt}{\\begin{center}" + safe_representation + "\\end{center}}}");
					}
				}
				else
				{
					// it may be that this can be improved using some of the techinques from the pstricks code above.
	
					StringTokenizer s = new StringTokenizer(string_representation,Ascii.STANDARD_RETURN);
		
					Rectangle these_bounds;
					int i=0;
					String output = "";
					String new_line = "";
					
					while (s.hasMoreTokens()) 
					{ 
						these_bounds = ((Shape)glyph_shape_vector.elementAt(i)).getBounds();
						output = output + new_line + "  \\put(" + (these_bounds.x-x1) + "," + (y2-(these_bounds.y+these_bounds.height)) + "){" + LatexPrinter.escapeLaTeX(s.nextToken()) + "}";
						i++;
						new_line = "\n";
					}
					latex_printer.addLaTeX(output);
				}
			}
			else
			{
				// this is used by edges
				// edges seem to render down too low, so we force them up a bit
				int move_up = 3;
				latex_printer.addLaTeX("  \\put(" + ((glyph_bounds.x+BOUNDING_BOX_FACTOR/2)-x1) + "," + (y2-(glyph_bounds.y+glyph_bounds.height-BOUNDING_BOX_FACTOR/2-move_up)) + "){" + LatexPrinter.escapeLaTeX(string_representation) + "}");		
			}
		}
	}	
}