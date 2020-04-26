package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Encapsulates the basic position and appearance characteristics of graphical
 * elements displayed in the graph views. NOTE This class was originally
 * intended as a way to pack and pass all graphical layout information between
 * the Automaton and elements of the FSAGraph via the MetaData class. This will
 * no longer be necessary once graphcial layout metadata is read and stored
 * directly in the FSAGraph (on its initialization) and written directly from
 * the FSAGraph on save.
 * 
 * @author Helen Bretzke
 */
public class GraphicalLayout implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7662307895177593570L;

    /* whether the element needs to be repainted due to changes in the layout */
    private boolean dirty = false;

    public static final int GRID_SIZE = 20;

    public static final Color DEFAULT_COLOR = Color.BLACK;

    public static final Color DEFAULT_HIGHLIGHT_COLOR = Color.RED;

    public static final Color DEFAULT_SELECTION_COLOR = Color.BLUE;

    public static final Color DEFAULT_BG_COLOR = Color.WHITE;

    public static final Stroke FINE_STROKE = new BasicStroke(1);

    public static final Stroke WIDE_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

    public static final Stroke DASHED_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 50,
            new float[] { 5, 2 }, 0);

    public static final Stroke WIDE_DASHED_STROKE = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 50,
            new float[] { 5, 2 }, 0);

    /** x and y coordinates of the associated element on the graph canvas */
    private Point2D.Float location;

    /** x and y displacement of the label from the location of the element */
    private Point2D.Float labelOffset;

    /**
     * the text to be displayed on the associated graph element Note: this is
     * redundant, since a label is a graph element and will have it's own text
     * variable
     */
    private String text;

    /** colour of the element */
    private Color color = DEFAULT_COLOR;

    /** colour of the element when it is highlighted */
    private Color highlightColor = DEFAULT_HIGHLIGHT_COLOR;

    /** colour of the element when it is selected */
    private Color selectionColor = DEFAULT_SELECTION_COLOR;

    /**
     * background colour (??? is this the solid fill colour of the element i.e.
     * behind the text)
     */
    private Color backgroundColor = DEFAULT_BG_COLOR;

    /**
     * Creates a graphical layout instance with empty string as text.
     */
    public GraphicalLayout() {
        this("");
    }

    /**
     * Creates a graphical layout instance with the given string as label text.
     * 
     * @param text the text to display on the label
     */
    public GraphicalLayout(String text) {
        this.text = text;
        location = new Point2D.Float(0, 0);
        labelOffset = new Point2D.Float(0, 0);
    }

    /**
     * Creates a graphical layout instance at the given location with empty string
     * as text.
     * 
     * @param location
     */
    public GraphicalLayout(Point2D.Float location) {
        this(location, "");
    }

    /**
     * Creates a graphical layout instance at the given location and the given
     * string as label text.
     * 
     * @param location
     * @param text
     */
    public GraphicalLayout(Point2D.Float location, String text) {
        this.location = location;
        this.text = text;
        labelOffset = new Point2D.Float(0, 0);
    }

    /**
     * Creates a graphical layout instance at the given location, with the given
     * label text, colour and highlight colour.
     * 
     * @param location
     * @param text
     * @param color
     * @param highlightColor
     */
    public GraphicalLayout(Point2D.Float location, String text, Color color, Color highlightColor) {
        this.location = location;
        this.text = text;
        this.color = color;
        this.highlightColor = highlightColor;
        labelOffset = new Point2D.Float(0, 0);
    }

    /**
     * Returns the x and y coordinates of the associated element on the graph
     * canvas.
     * 
     * @return x and y coordinates of the associated element on the graph canvas
     */
    public Point2D.Float getLocation() {
        return location;
    }

    /**
     * Sets the location to the given x and y coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setLocation(float x, float y) {
        location.setLocation(x, y);
        dirty = true;
    }

    /**
     * Translates the location by the given x and y offsets.
     * 
     * @param x the x offset
     * @param y the y offset
     */
    public void translate(float x, float y) {
        location.setLocation(location.x + x, location.y + y);
        dirty = true;
    }

    /**
     * Returns the colour.
     * 
     * @return the colour.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the colour to <code>color</code>.
     * 
     * @param color the colour to be set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the highlight colour.
     * 
     * @return the highlight colour
     */
    public Color getHighlightColor() {
        return highlightColor;
    }

    /**
     * Sets the highlight colour.
     * 
     * @param highlightColor the colour to be set
     */
    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    /**
     * Returns the background colour.
     * 
     * @return the background colour
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the label text.
     * 
     * @return the text
     */
    public String getText() {
        return (text != null ? text : "");
    }

    /**
     * Sets the label text to <code>text</code>.
     * 
     * @param text the label text to be set
     */
    public void setText(String text) {
        this.text = text;
        dirty = true;
    }

    /**
     * Returns the selection colour.
     * 
     * @return the selection colour
     */
    public Color getSelectionColor() {
        return selectionColor;
    }

    /**
     * Sets the selection colour to <code>selectionColor</code>.
     * 
     * @param selectionColor the selection colour to be set
     */
    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
    }

    /**
     * Returns true iff this layout needs to be repainted.
     * 
     * @return true iff this layout needs to be repainted
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Sets whether this layout needs to be repainted.
     * 
     * @param dirty the flag to be set
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Returns the vector representing the displacement of the label from the
     * location of graphical element.
     * 
     * @return offset of the label from its associated graphical element
     */
    public Point2D.Float getLabelOffset() {
        return labelOffset;
    }

    /**
     * Sets the vector representing the displacement of the label from the location
     * of the graphical element.
     * 
     * @param labelOffset offset of the label from its associated graphical element
     */
    public void setLabelOffset(Point2D.Float labelOffset) {
        this.labelOffset = labelOffset;
        setDirty(true);
    }

    /**
     * Lenko
     */
    public Point2D.Float snapToGrid() {
        int x = (int) Math.floor(location.x);
        int lGrid = (x / GRID_SIZE) * GRID_SIZE;
        int rGrid = (x / GRID_SIZE + 1) * GRID_SIZE;
        if (x - lGrid < rGrid - x) {
            x = lGrid;
        } else {
            x = rGrid;
        }
        int y = (int) Math.floor(location.y);
        int bGrid = (y / GRID_SIZE) * GRID_SIZE;
        int tGrid = (y / GRID_SIZE + 1) * GRID_SIZE;
        if (y - bGrid < tGrid - y) {
            y = bGrid;
        } else {
            y = tGrid;
        }
        Point2D.Float delta = new Point2D.Float(x - location.x, y - location.y);
        location.x = x;
        location.y = y;
        setDirty(true);
        return delta;
    }

    /**
     * Explicitly saves its own fields
     * 
     * @serialData Store own serializable fields
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeBoolean(this.dirty);
        out.writeFloat(location.x);
        out.writeFloat(location.y);
        out.writeFloat(this.labelOffset.x);
        out.writeFloat(this.labelOffset.y);
        out.writeObject(this.backgroundColor);
        out.writeObject(this.color);
        out.writeObject(this.highlightColor);
        out.writeObject(this.selectionColor);
        out.writeObject(this.text);
    }

    /**
     * Restores its own fields by calling defaultReadObject and then explicitly
     * restores the fields of its supertype.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        dirty = in.readBoolean();
        location = new Point2D.Float(in.readFloat(), in.readFloat());
        labelOffset = new Point2D.Float(in.readFloat(), in.readFloat());
        backgroundColor = (Color) in.readObject();
        color = (Color) in.readObject();
        highlightColor = (Color) in.readObject();
        selectionColor = (Color) in.readObject();
        text = (String) in.readObject();
    }
}
