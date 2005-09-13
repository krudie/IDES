/*
 * Created on Jan 25, 2005
 */
package com.aggressivesoftware.ides.graphcontrol.graphparts;

import java.awt.image.BufferedImage;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.swt.widgets.TableItem;

import com.aggressivesoftware.general.Ascii;
import com.aggressivesoftware.geometric.Box;
import com.aggressivesoftware.geometric.Geometric;
import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.Drawer;
import com.aggressivesoftware.ides.graphcontrol.LatexPrinter;
import com.aggressivesoftware.ides.graphcontrol.TransitionData;

/**
 * @author Michael Wood
 */
public class LatexLabel extends Label
{
	/**
     * The rendered image of the latex code.
     */
	private BufferedImage rendered_latex = null;
	
	/**
     * This switch allows the label to draw a set of input BufferedImages, instead of rendering them itself.
     */
	private boolean never_render = false;

	/**
     * This records the bounding box of the displayed content when never_render is used.
     * It is populated by the drawData() method.
     */
	private Box bounding_box = null;
	
	/** 
	 * The number of commas processed when creating the bounding box.
	 */
	private int bounding_box_commas = 0;
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// LatexLabel construction ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
     * Construct the LatexLabel.
     * 
     * @param	gp				The GraphingPlatform in which this Label will exist.
     * @param	parent			The GraphObject which this Label serves.
	 * @param	never_render	Switch optionally allows the label to never render it's own images.
     */
	public LatexLabel(GraphingPlatform gp, GraphObject parent, boolean never_render)
	{ 
		super(gp, parent); 
		this.never_render = never_render;
	}

	/**
     * Construct the LatexLabel.
     * 
     * @param	gp						The GraphingPlatform in which this Label will exist.
     * @param	parent					The GraphObject which this Label serves.
     * @param	string_representation	The pre-rendered representation of this Label.
	 * @param	anchor					Positioning information for the rendered Label.
	 * @param	anchor_type				Determines how the anchor should be interpreted.
	 * @param	never_render			Switch optionally allows the label to never render it's own images.
     */
	public LatexLabel(GraphingPlatform gp, GraphObject parent, String string_representation, Point anchor, int anchor_type, boolean never_render)
	{ 
		super(gp, parent, string_representation, anchor, anchor_type, MINIMUM_RADIUS); 
		this.never_render = never_render;
	}
	
	/**
     * Construct a clone of a LatexLabel.
     * 
     * @param	gp				The GraphingPlatform in which this Label will exist.
     * @param	parent			The GraphObject which this Label serves.
	 * @param	source			A source LatexLabel from which to clone this one.
     */
	public LatexLabel(GraphingPlatform gp, GraphObject parent, LatexLabel source) 
	{ 
		super(gp, parent, source.string_representation, source.anchor, source.anchor_type, source.rendered_radius); 
		this.never_render = source.never_render;
		if (!never_render) { rendered_latex = source.cloneRenderedLatex(); }
	}

	/**
     * Clone the rendered_latex.
     * 
	 * @return	A copy of the rendered_latex BufferedImage.
     */
	private BufferedImage cloneRenderedLatex() 
	{ 
		if (rendered_latex != null) { return rendered_latex.getSubimage(0,0,rendered_latex.getWidth(),rendered_latex.getHeight()); }
		else { return null; }
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// rendering //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
	/**
     * Create a displayable representation of the label.
     * If no anchor has been specified, then no rendering takes place.
     * You must first explicitly specify an anchor.
     */
	public void render()
	{
		if (string_representation.length() > 0 && gp.gc.renderer != null && never_render == false)
		{
			try
			{ 
				gp.shell.setEnabled(false);
				gp.shell.setCursor(gp.rm.getCursor(gp.rm.WAIT_CURSOR));
				
				rendered_latex = gp.gc.renderer.renderString("\\begin{center}" + string_representation + "\\end{center}"); 
				
				rendered_radius = Math.round(Geometric.magnitude(rendered_latex.getHeight(), rendered_latex.getWidth())/2) + RENDERED_RADIUS_FACTOR;
				rendered_radius = Math.max(rendered_radius, MINIMUM_RADIUS);
				parent.accomodateLabel();
				
				gp.gc.j2dcanvas.repaint();
				gp.shell.setEnabled(true);
				gp.shell.setCursor(gp.rm.getCursor(gp.rm.ARROW_CURSOR));
			}
			catch (Exception e) { e.printStackTrace(); }	
		}
		else 
		{ 
			rendered_latex = null; 
			gp.gc.j2dcanvas.repaint();
		}
	}
	
	/**
     * Render if previous rendering failed, but rendering is now possible.
     * If no anchor has been specified, then no rendering takes place.
     * You must first explicitly specify an anchor.
     */
	public void renderIfNeeded()
	{
		if (rendered_latex == null && string_representation.length() > 0 && gp.gc.renderer != null && never_render == false)
		{ render(); }
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
		if (rendered_latex != null)
		{
			if (anchor_type == CENTER)
			{ drawer.draw(rendered_latex, (anchor.x - Math.round(rendered_latex.getWidth()/2)), (anchor.y - Math.round(rendered_latex.getHeight()/2))); } 
			else
			{ drawer.draw(rendered_latex, anchor.x, anchor.y); } 
		}
	}

	/**
     * Draw the displayable representation of this Label.
     * The displayable representation is given as an Vector of TableItems which contain BufferedImages in their getData() methods.
     * This should be displayed, left to right, comma seperated.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     * @param	data	The Vector of data to be displayed.
     */
	public void drawData(Drawer drawer, Vector data)
	{
		bounding_box_commas = 0;
		Box box = new Box(anchor.x,anchor.y,anchor.x,anchor.y);
		BufferedImage image = null;
		int comma_factor = 3;
		int max_height = 0;
		boolean first_access = true;
		string_representation = "";

		if (data != null)
		{
			for (int i=0; i<data.size(); i++)
			{
				image = (BufferedImage)((TableItem)data.elementAt(i)).getData("awt");
				if (image != null) { max_height = Math.max(max_height, image.getHeight()); }
			}
			box.y2(box.y1() + max_height);
			for (int i=0; i<data.size(); i++)
			{
				image = (BufferedImage)((TableItem)data.elementAt(i)).getData("awt");
				if (image != null)
				{
					if (!first_access)
					{
						string_representation = string_representation + "~,~";
						drawer.drawString(",",box.x2()+comma_factor,box.y1()+max_height);
						box.x2(box.x2() + 2*BOUNDING_BOX_FACTOR);
						bounding_box_commas++;
					}
					else { first_access = false; }
					string_representation = string_representation + ((TableItem)data.elementAt(i)).getText(TransitionData.SPEC_LATEX);
					drawer.draw(image, box.x2(), box.y1() + max_height - image.getHeight());
					box.x2(box.x2() + image.getWidth());
				}
			}
		}
		box.grow(BOUNDING_BOX_FACTOR);
		bounding_box = box;
	}
		
	/**
     * Draw a bounding box around this Label.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     */
	public void drawBox(Drawer drawer) 
	{ 
		if (bounding_box != null)
		{ drawer.drawBoxWH(bounding_box.x1(), bounding_box.y1(), bounding_box.w(), bounding_box.h(), Drawer.SMALL_DASHED); }			
	}
	
	/**
     * Draw a line from the top left corner of the bounding box of this Label to a specified location.
     * 
     * @param	drawer	The Drawer that will handle the drawing.
     * @param	anchor	The destination point for the line from the bounding box of this Label. 
     */
	public void drawTether(Drawer drawer, Point anchor) 
	{
		if (bounding_box != null)
		{ drawer.drawLine(anchor.x,anchor.y,bounding_box.x1(),bounding_box.y1(),Drawer.SMALL_DASHED); }
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
		if (this.anchor == null) { this.anchor = anchor.getCopy(); }
		else { this.anchor.copy(anchor); }
		this.anchor_type = anchor_type;
	}
	
	/**
     * Test if a mouse-click should select this Label
     * 
     * @param	mouse	The Point to be tested.
     * @return	true if the mouse-click should select this label.
     */
	public boolean isLocated(Point mouse)
	{
		if (rendered_latex != null)
		{
			int half_width  = Math.round(rendered_latex.getWidth()/2);
			int half_height = Math.round(rendered_latex.getHeight()/2);
			
			if (anchor_type == CENTER)
			{
				if ( mouse.x >= anchor.x - half_width &&
				     mouse.y >= anchor.y - half_height &&
					 mouse.x <= anchor.x + half_width &&
				     mouse.y <= anchor.y + half_height)	
				{ return true; } else { return false; }
			}
			else
			{
				if ( mouse.x >= anchor.x &&
					     mouse.y >= anchor.y &&
						 mouse.x <= anchor.x + rendered_latex.getWidth() &&
					     mouse.y <= anchor.y + rendered_latex.getHeight())	
					{ return true; } else { return false; }				
			}
		}
		else if (bounding_box != null)
		{
			if ( mouse.x > bounding_box.x1() + BOUNDING_BOX_FACTOR/2 &&
				 mouse.y > bounding_box.y1() + BOUNDING_BOX_FACTOR/2 &&
				(mouse.x-bounding_box.x1()) < (bounding_box.w()-BOUNDING_BOX_FACTOR) &&
				(mouse.y-bounding_box.y1()) < (bounding_box.h()-BOUNDING_BOX_FACTOR) )
				{ return true; } else { return false; }
		}
		else { return false; }
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
		String safe_representation = string_representation;
		safe_representation = Ascii.replaceAll(safe_representation,"\\\\"+Ascii.STANDARD_RETURN,"\\\\ ");
		safe_representation = Ascii.replaceAll(safe_representation,"\\\\ "+Ascii.STANDARD_RETURN,"\\\\ ");
		safe_representation = Ascii.replaceAll(safe_representation,Ascii.STANDARD_RETURN+Ascii.STANDARD_RETURN,"\\\\ ");
		safe_representation = Ascii.replaceAll(safe_representation,Ascii.STANDARD_RETURN," ");
		
		if (bounding_box != null)
		{
			// this is used by edges
			// renders a little wider in latex due to commas/spacing, hence must move left some degree.
			latex_printer.addLaTeX("  \\put(" + (((bounding_box.x1()-(bounding_box_commas))+BOUNDING_BOX_FACTOR)-x1) + "," + (y2-(bounding_box.y2()-BOUNDING_BOX_FACTOR)) + "){" + safe_representation + "}"); 
		}
		else if (rendered_latex != null && gp.gc.renderer != null)
		{ 
			// this is used by nodes
			if (gp.sv.use_pstricks)
			{
				// just to be safe, should always be a node anyway
				if (parent.isNode())
				{
					// draws the label centered about the given point
					// point is node origin translated into latex coords
					// need parbox so that things like CR will translate
					latex_printer.addLaTeX("  \\rput(" + (((Node)parent).origin().x-x1) + "," + (y2-((Node)parent).origin().y) + "){\\parbox{" + rendered_latex.getWidth() + "pt}{\\begin{center}" + safe_representation + "\\end{center}}}");
				}
			}
			else
			{
				// it may be that this can be improved using some of the techinques from the pstricks code above.
				// this method requires rendering
				gp.shell.setEnabled(false);
				gp.shell.setCursor(gp.rm.getCursor(gp.rm.WAIT_CURSOR));
				try
				{
					int fudge = 1;
					int line_height = 6;
					int x = anchor.x - Math.round(rendered_latex.getWidth()/2);  // top left corner
					int y = anchor.y - Math.round(rendered_latex.getHeight()/2); // top left corner
		
					// the reason this is exporting wrong is because we're specifying the bottom left corner, 
					// and the line breaks aren't occurring where they should, so the image isn't tall enough.
					//latex_printer.addLaTeX("  \\put(" + (fudge + (x-x1)) + "," + (fudge + (y2-y)) + "){" + string_representation + "}"); 		
		
					StringTokenizer s = new StringTokenizer(string_representation,"\\\\"+Ascii.STANDARD_RETURN);
					BufferedImage[] biary = new BufferedImage[s.countTokens()];
					int i=0;			
					String output = "";
					String new_line = "";
					String this_line = "";
					while (s.hasMoreTokens())
					{
						this_line = s.nextToken();
						biary[i] = gp.gc.renderer.renderString(this_line);
						// the x1, and y2 are for converting co-ordinate spaces
						// the x is the left edge of this image
						// the y+height is the bottom of this image
						output = output + new_line + "  \\put(" + (fudge + (x-x1)) + "," + (fudge + (y2-(y+biary[i].getHeight()))) + "){" + this_line + "}";				
						// increase y for the next line
						y = y + biary[i].getHeight() + line_height;
						i++;
						new_line = "\n";
					}						
					latex_printer.addLaTeX(output);
				}
				catch (Exception e) { e.printStackTrace(); }
				gp.shell.setEnabled(true);
				gp.shell.setCursor(gp.rm.getCursor(gp.rm.ARROW_CURSOR));	
			}
		}
	}
}