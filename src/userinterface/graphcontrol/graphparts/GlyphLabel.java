/*
 * Created on Jan 25, 2005
 */
package userinterface.graphcontrol.graphparts;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.StringTokenizer;
import java.util.Vector;

import userinterface.GraphingPlatform;
import userinterface.ResourceManager;
import userinterface.general.Ascii;
import userinterface.geometric.Geometric;
import userinterface.geometric.Point;

import userinterface.graphcontrol.Drawer;

/**
 * @author Michael Wood
 */
public class GlyphLabel extends Label{
    /**
     * The Font used when rendering.
     */
    private static final Font FONT = new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 12);

    /**
     * The FontRenderContext used when rendering.
     */
    private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(null, true,
            false);

    /**
     * Constants based on font size.
     */
    private static final int GLYPH_HEIGHT = 10, LINE_SPACE = 3;

    /**
     * The bounding region of the label.
     */
    private Rectangle glyph_bounds = null;

    /**
     * A vector of glyph shapes and vectors to be rendered.
     */
    private Vector<GlyphVector> glyph_vector_vector = null;

    private Vector<Shape> glyph_shape_vector = null;

    /**
     * The unpadded box height used in rendering.
     */
    private int unpadded_box_height = 0;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GlyphLabel construction
    // ////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the GlyphLabel.
     * 
     * @param gp
     *            The GraphingPlatform in which this Label will exist.
     * @param parent
     *            The GraphObject which this Label serves.
     */
    public GlyphLabel(GraphingPlatform gp, GraphObject parent){
        super(gp, parent);
    }

    /**
     * Construct the GlyphLabel.
     * 
     * @param gp
     *            The GraphingPlatform in which this Label will exist.
     * @param parent
     *            The GraphObject which this Label serves.
     * @param string_representation
     *            The pre-rendered representation of this Label.
     * @param anchor
     *            Positioning information for the rendered Label.
     * @param anchor_type
     *            Determines how the anchor should be interpreted.
     */
    public GlyphLabel(GraphingPlatform gp, GraphObject parent, String string_representation,
            Point anchor, int anchor_type){
        super(gp, parent, string_representation, anchor, anchor_type, MINIMUM_RADIUS);
    }

    /**
     * Construct a clone of a GlyphLabel.
     * 
     * @param gp
     *            The GraphingPlatform in which this Label will exist.
     * @param parent
     *            The GraphObject which this Label serves.
     * @param source
     *            A source GlyphLabel from which to clone this one.
     */
    public GlyphLabel(GraphingPlatform gp, GraphObject parent, GlyphLabel source){
        super(gp, parent, source.string_representation, source.anchor, source.anchor_type,
                source.rendered_radius);
        glyph_vector_vector = source.cloneRenderedGlyphVectors();
        glyph_bounds = source.cloneBoundingBox();
        unpadded_box_height = source.cloneBoxHeight();
    }

    /**
     * Access a copy of the unpadded_box_height.
     * 
     * @return A copy of the unpadded_box_height.
     */
    public int cloneBoxHeight(){
        return unpadded_box_height;
    }

    /**
     * Clone the bounding box.
     * 
     * @return A clone of the bounding box.
     */
    public Rectangle cloneBoundingBox(){
        return glyph_bounds != null ? (Rectangle) glyph_bounds.clone() : null;
    }

    /**
     * Clone the rendered glyph vectors.
     * 
     * @return A clone of the Vector of GlyphVectors.
     */
    public Vector<GlyphVector> cloneRenderedGlyphVectors(){
        // the shapes are derived from the vectors without modifying the vectors
        // it is therefore okay to have both clones pointint to the same vectors
        // if ever either change their string representation, they will drop the
        // pointer to the Vector of GlyphVectors and create a new Vector.
        return glyph_vector_vector;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // rendering
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Create a displayable representation of the label. If no anchor has been
     * specified, then no rendering takes place. You must first explicitly
     * specify an anchor.
     */
    public void render(){
        if(string_representation.length() <= 0){
            glyph_shape_vector = null;
            return;
        }
        try{
            gp.shell.setEnabled(false);
            gp.shell.setCursor(ResourceManager.getCursor(ResourceManager.WAIT_CURSOR));

            if(anchor_type == CENTER) renderAtCenter();
            else renderAtCorner();

            rendered_radius = Math.round(Geometric.magnitude(glyph_bounds.height,
                    glyph_bounds.width) / 2)
                    + RENDERED_RADIUS_FACTOR;
            rendered_radius = Math.max(rendered_radius, MINIMUM_RADIUS);
            parent.accomodateLabel();

            gp.gc.j2dcanvas.repaint();
            gp.shell.setEnabled(true);
            gp.shell.setCursor(ResourceManager.getCursor(ResourceManager.ARROW_CURSOR));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Render if previous rendering failed, but rendering is now possible. If no
     * anchor has been specified, then no rendering takes place. You must first
     * explicitly specify an anchor.
     */
    public void renderIfNeeded(){
        if(glyph_shape_vector == null && string_representation.length() > 0) render();
    }

    /**
     * Create a displayable representation of the label, centered at the anchor.
     */
    private void renderAtCenter(){
        String glyph_string = string_representation;
        int num_lines = Ascii.occurrances(glyph_string, Ascii.RETURN) + 1;
        int line_number = 0;
        int box_width = (int) Math.round(2 * MINIMUM_RADIUS / Math.sqrt(2));
        int box_height = 0;
        GlyphVector glyph_vector = null;
        Shape glyph_shape = null;
        glyph_shape_vector = new Vector<Shape>();
        glyph_vector_vector = new Vector<GlyphVector>();

        StringTokenizer s = new StringTokenizer(glyph_string, Ascii.STANDARD_RETURN);

        // build the glyphs
        while(s.hasMoreTokens()){
            line_number++;
            glyph_string = s.nextToken();
            glyph_vector = FONT.createGlyphVector(FONT_RENDER_CONTEXT, glyph_string);
            glyph_vector_vector.addElement(glyph_vector);
            glyph_bounds = glyph_vector.getPixelBounds(glyph_vector.getFontRenderContext(), 0, 0);
            if(glyph_bounds.width > box_width){
                box_width = glyph_bounds.width;
            }
            box_height = box_height + GLYPH_HEIGHT;
            if(num_lines > 1 && line_number != 1){
                box_height = box_height + LINE_SPACE;
            }
        }

        // build the glyph shapes in the proper positions
        for(int i = 0; i < glyph_vector_vector.size(); i++){
            glyph_bounds = ((GlyphVector) glyph_vector_vector.elementAt(i)).getPixelBounds(
                    glyph_vector.getFontRenderContext(), 0, 0);
            glyph_shape = ((GlyphVector) glyph_vector_vector.elementAt(i)).getOutline(anchor.getX()
                    - glyph_bounds.x - glyph_bounds.width / 2, anchor.getY() - glyph_bounds.y
                    - box_height / 2 + i * (GLYPH_HEIGHT + LINE_SPACE));
            glyph_shape_vector.addElement(glyph_shape);
        }
        unpadded_box_height = box_height;

        // build the bounding box
        glyph_bounds = new Rectangle(anchor.getX() - box_width / 2, anchor.getY() - box_height / 2,
                box_width, box_height);
    }

    /**
     * Create a displayable representation of the label, with its top left
     * corner at the anchor.
     */
    private void renderAtCorner(){
        Point modified_anchor = anchor.plus(new Point(0, 10));
        GlyphVector glyph_vector = FONT.createGlyphVector(FONT_RENDER_CONTEXT,
                string_representation);
        Shape glyph_shape = glyph_vector.getOutline(modified_anchor.getX(), modified_anchor.getY());
        glyph_bounds = glyph_vector.getPixelBounds(glyph_vector.getFontRenderContext(),
                modified_anchor.getX(), modified_anchor.getY());
        glyph_bounds.grow(BOUNDING_BOX_FACTOR, BOUNDING_BOX_FACTOR);
        glyph_shape_vector = new Vector<Shape>();
        glyph_shape_vector.addElement(glyph_shape);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // drawing
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw the displayable representation of this Label.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void drawLabel(Drawer drawer){
        if(glyph_shape_vector == null) return;
        for(int i = 0; i < glyph_shape_vector.size(); i++){
            drawer.drawShape(glyph_shape_vector.elementAt(i));
        }
    }

    /**
     * Simply recalls drawLabel()
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     * @param data
     *            Some data.
     */
    public void drawData(Drawer drawer, Vector data){
        drawLabel(drawer);
    }

    /**
     * Draw a bounding box around this Label.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void drawBox(Drawer drawer){
        drawer.drawBoxWH(glyph_bounds.x, glyph_bounds.y, glyph_bounds.width, glyph_bounds.height,
                Drawer.SMALL_DASHED);
    }

    /**
     * Draw a line from the top left corner of the bounding box of this Label to
     * a specified location.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     * @param anchor
     *            The destination point for the line from the bounding box of
     *            this Label.
     */
    public void drawTether(Drawer drawer, Point anchor){
        drawer.drawLine(anchor.getX(), anchor.getY(), glyph_bounds.x, glyph_bounds.y,
                Drawer.SMALL_DASHED);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // data
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Specify a new anchor for the displayable representation of this Label.
     * 
     * @param anchor
     *            Positioning information for the rendered Label.
     * @param anchor_type
     *            Determines how the anchor should be interpreted.
     */
    public void setAnchor(Point anchor, int anchor_type){
        if(this.anchor != null && anchor.equals(this.anchor) && anchor_type == this.anchor_type) return;

        if(this.anchor == null) this.anchor = anchor.getCopy();
        else this.anchor.copy(anchor);

        this.anchor_type = anchor_type;

        if(glyph_bounds != null && glyph_vector_vector != null && anchor_type == CENTER){
            glyph_bounds.x = anchor.getX() - glyph_bounds.width / 2;
            glyph_bounds.y = anchor.getY() - glyph_bounds.height / 2;

            // build the glyph shapes in the proper positions
            glyph_shape_vector = new Vector<Shape>();
            Rectangle these_bounds = null;
            Shape glyph_shape = null;
            for(int i = 0; i < glyph_vector_vector.size(); i++){
                these_bounds = ((GlyphVector) glyph_vector_vector.elementAt(i)).getPixelBounds(
                        FONT_RENDER_CONTEXT, 0, 0);
                glyph_shape = ((GlyphVector) glyph_vector_vector.elementAt(i)).getOutline(anchor
                        .getX()
                        - these_bounds.x - these_bounds.width / 2, anchor.getY() - these_bounds.y
                        - unpadded_box_height / 2 + i * (GLYPH_HEIGHT + LINE_SPACE));
                glyph_shape_vector.addElement(glyph_shape);
            }
        }
        else if(anchor_type == CORNER && string_representation.length() > 0){
            renderAtCorner();
        }
    }

    /**
     * Test if a mouse-click should select this Label
     * 
     * @param mouse
     *            The Point to be tested.
     * @return true if the mouse-click should select this label.
     */
    public boolean isLocated(Point mouse){
        return mouse.getX() > glyph_bounds.x + BOUNDING_BOX_FACTOR / 2
                && mouse.getY() > glyph_bounds.y + BOUNDING_BOX_FACTOR / 2
                && (mouse.getX() - glyph_bounds.x) < (glyph_bounds.width - BOUNDING_BOX_FACTOR)
                && (mouse.getY() - glyph_bounds.y) < (glyph_bounds.height - BOUNDING_BOX_FACTOR);
    }
}