package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import ides.api.model.fsa.FSAState;
import io.fsa.ver2_1.GraphExporter;
import presentation.GraphicalLayout;
import util.BentoBox;

/**
 * The circular representation of a state in a finite state automaton. Maintains
 * a label and set of adjacent edges. Provides popup menu for operations
 * 
 * @see NodePopup#showPopup
 * @author Helen Bretzke
 */
public class CircleNode extends Node {

    /** the circle that represents the state */
    private Ellipse2D circle = null;

    /** inner circle; only drawn for final (marked) states */
    private Ellipse2D innerCircle = null;

    /**
     * Creates a circular node representing the given state with the given graphical
     * layout.
     * 
     * @param s      the state to be represented
     * @param layout the graphical layout data for the state
     */
    public CircleNode(FSAState s, CircleNodeLayout layout) {
        this(s, layout, true);
    }

    /**
     * Creates a circular node representing the given state with the given graphical
     * layout.
     * 
     * @param s      the state to be represented
     * @param layout the graphical layout data for the state
     */
    public CircleNode(FSAState s, CircleNodeLayout layout, boolean redoLayout) {
        this.state = s;
        setLayout(layout);
        label = new GraphLabel("", GraphLabel.DEFAULT_FONT_SIZE);
        this.insert(label);
        circle = computeCircle(CircleNodeLayout.DEFAULT_RADIUS + 2 * CircleNodeLayout.RADIUS_MARGIN);
        if (s.isInitial()) {
            setInitialArrow(new InitialArrow(this));
        }
        if (redoLayout) {
            refresh();
        }
    }

    /**
     * Returns a circle of the given radius.
     * 
     * @return a circle of given radius
     */
    private Ellipse2D computeCircle(float radius) {
        Point2D.Float centre = getLayout().getLocation();
        float d = 2 * radius;
        return new Ellipse2D.Double(centre.x - radius, centre.y - radius, d, d);
    }

    /**
     * Refreshes the visual representation of the node from the underlying state and
     * layout data. Also recomputes the layout for adjacent edges should they be
     * affected by changes to the node's size or position.
     */
    // TODO change to iterate over collection of labels on a state
    // Note: To accommodate composite states requires change to file reading and
    // writing.
    @Override
    public void refresh() {
        super.refresh();

        Point2D.Float centre = getLayout().getLocation();
        label.updateLayout(getLayout().getText(), centre);

        // compute new radius and visible circle
        Rectangle2D labelBounds = label.bounds();
        float radius = (float) Math.max(labelBounds.getWidth() / 2 + 2 * CircleNodeLayout.RADIUS_MARGIN,
                CircleNodeLayout.DEFAULT_RADIUS + 2 * CircleNodeLayout.RADIUS_MARGIN);
        ((CircleNodeLayout) getLayout()).setRadius(radius);
        radius = ((CircleNodeLayout) getLayout()).getRadius();
        circle = computeCircle(radius);

        // adjust adjacent edges
        recomputeEdges();

        if (state.isMarked()) {
            innerCircle = computeCircle(radius - CircleNodeLayout.RADIUS_MARGIN); // new
            // Ellipse2D.Double(centre.x
            // - r,
            // centre.y
            // - r,
            // d,
            // d);
        }

        if (initialArrow != null) {
            initialArrow.setVisible(state.isInitial());
        }
        setNeedsRefresh(false);
    }

    /**
     * Draws this node and all of its out edges in the given graphics context.
     * 
     * @param g the graphics context
     */
    @Override
    public void draw(Graphics g) {
        if (needsRefresh()) {
            refresh();
            getLayout().setDirty(false);
        }

        // only calls draw on all of the outgoing edges
        Iterator<GraphElement> c = children();
        while (c.hasNext()) {
            try {
                BezierEdge child = (BezierEdge) c.next();
                if (child.getSourceNode().equals(this)) {
                    child.draw(g);
                }
            } catch (ClassCastException cce) {
                // skip the label and keep going
                // HB says to self: Why am I skipping the label?
                // Why did I decide to do it at the end?
            }
        }

        Graphics2D g2d = (Graphics2D) g;

        if (isSelected()) {
            g.setColor(getLayout().getSelectionColor());
        } else if (isHighlighted()) {
            g.setColor(getLayout().getHighlightColor());
        } else {
            g.setColor(getLayout().getColor());
        }

        g2d.setStroke(GraphicalLayout.WIDE_STROKE);
        g2d.draw(circle);

        if (state.isMarked()) {
            g2d.draw(innerCircle);
        }

        if (state.isInitial()) {
            initialArrow.draw(g);
        }

        label.draw(g);
    }

    @Override
    public Rectangle bounds() {
        if (getState().isInitial()) {
            return circle.getBounds().union(initialArrow.bounds());
        }
        return circle.getBounds();
    }

    /**
     * @return bounding rectangle for union of this node with all of its children.
     */
    @Override
    public Rectangle adjacentBounds() {
        Rectangle bounds = bounds();
        return (Rectangle) bounds.createUnion(super.bounds());
    }

    /**
     * @return true iff p intersects the circle representing this node
     */
    @Override
    public boolean intersects(Point2D p) {

        if (state.isInitial()) {
            return circle.intersects(p.getX() - 5, p.getY() - 5, 10, 10) || initialArrow.intersects(p); // (p.getX() -
                                                                                                        // 4, p.getY() -
            // 4, 8, 8);
        }
        return circle.intersects(p.getX() - 5, p.getY() - 5, 10, 10);
    }

    /**
     * Sets the selected property to <code>selected</code>.
     */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * NOTE since we are sharing references to unique node objects, this shouldn't
     * be necessary. But just in case...
     * 
     * @param n an instance of a CircleNode
     * @return true iff this node has the same ID as the given node
     */
    @Override
    public boolean equals(Object n) {
        try {
            return this.getId().equals(((CircleNode) n).getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Translates the location of this node by the given horizontal and vertical
     * displacements.
     * 
     * @param dx the displacement in the horizontal dimension
     * @param dy the displacement in the vertical dimension
     */
    @Override
    public void translate(float dx, float dy) {
        super.translate(dx, dy);
        refresh();
    }

    /**
     * Displays a popup menu providing operations on this node.
     */
    @Override
    public void showPopup(Component context) {
        NodePopup.showPopup((GraphDrawingView) context, this); // cast is a
        // KLUGE
    }

    /**
     * This method is needed by the GraphExporter to draw the initial arrow.
     * <p>
     * author Sarah-Jane Whittaker
     * 
     * @return Rectangle The bounding box for the initial arrow
     */
    protected Rectangle getInitialArrowBounds() {
        return (state.isInitial() ? initialArrow.bounds()
                /*
                 * new Rectangle( BentoBox.convertFloatToInt(arrow1.x +
                 * ArrowHead.SHORT_HEAD_LENGTH), BentoBox.convertFloatToInt(arrow1.y),
                 * BentoBox.convertFloatToInt(arrow2.x - arrow1.x),
                 * BentoBox.convertFloatToInt(arrow2.y - arrow1.y))
                 */
                : new Rectangle(0, 0, 0, 0));
    }

    /**
     * Creates and returns a string that contains an appropriate (depending on the
     * type) encoding of this node. NOTE: Initial arrows aren't handles, as those
     * are now independent edges
     * <p>
     * author Sarah-Jane Whittaker
     * 
     * @param selectionBox The area being selected or considered
     * @param exportType   The export format
     * @see GraphExporter#INT_EXPORT_TYPE_EPS
     * @see GraphExporter#INT_EXPORT_TYPE_PSTRICKS
     * @return the string representation
     */
    @Override
    public String createExportString(Rectangle selectionBox, int exportType) {
        String exportString = "";

        CircleNodeLayout nodeLayout = ((CircleNodeLayout) getLayout());
        Rectangle squareBounds = getSquareBounds();
        Point2D.Float nodeLocation = nodeLayout.getLocation();

        // Make sure this node is contained within the selection box
        if (!selectionBox.contains(squareBounds)) {
            // System.out.println("Node " + squareBounds
            // + " (Radius " + radius * 2
            // + ") outside bounds " + selectionBox);
            return exportString;
        }

        if (exportType == GraphExporter.INT_EXPORT_TYPE_PSTRICKS) {
            // A QUOTE FROM MIKE WOOD - thanks, Mike!
            // "java coords are origin @ top left, x increasing right,
            // y increasing down
            // latex coords are origin @ bottom left, x increasing right,
            // y increasing up"
            exportString += "  \\pscircle(" + (nodeLocation.x - selectionBox.x) + ","
                    + (selectionBox.height + selectionBox.y - nodeLocation.y) + "){" + nodeLayout.getRadius() + "}\n";

            // If this is a marked state, make a smaller circle within
            // this one to simulate double lines
            if (state.isMarked()) {
                exportString += "    \\pscircle(" + +(nodeLocation.x - selectionBox.x) + ","
                        + (selectionBox.height + selectionBox.y - nodeLocation.y) + "){"
                        + (nodeLayout.getRadius() - GraphExporter.INT_PSTRICKS_MARKED_STATE_RADIUS_DIFF) + "}\n";
            }

            // Now for the label
            if (getLayout().getText() != null) {
                exportString += "  " + label.createExportString(selectionBox, exportType);
            }
        } else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS) {
            // LENKO ?
        }

        return exportString;
    }

    /**
     * @return an iterator of all adjacent edges
     */
    @Override
    public Iterator<Edge> adjacentEdges() {
        Iterator<GraphElement> children = children();
        ArrayList<Edge> edges = new ArrayList<Edge>();
        while (children.hasNext()) {
            try {
                Edge e = (Edge) children.next();
                edges.add(e);
            } catch (ClassCastException cce) {
                // Child is not an edge
            }
        }
        return edges.iterator();
    }

    /**
     * @return the radius of the circle representing this node
     */
    public float getRadius() {
        return ((CircleNodeLayout) getLayout()).getRadius();
    }

    /**
     * Used to compute bounds for LaTeX export.
     * <p>
     * author Sarah-Jane Whittaker
     */
    protected Rectangle getSquareBounds() {
        Point2D.Float nodeLocation = getLayout().getLocation();
        float radius = getRadius();

        // Node location is at the centre of the circle
        return new Rectangle(BentoBox.convertFloatToInt(nodeLocation.x - radius),
                BentoBox.convertFloatToInt(nodeLocation.y - radius), BentoBox.convertFloatToInt(radius * 2),
                BentoBox.convertFloatToInt(radius * 2));
    }

    /**
     * Sets the graphical layout to <code>layout</code>.
     * 
     * @param layout the graphical layout data to be set
     */
    public void setLayout(CircleNodeLayout layout) {
        // if(getLayout()!=null)
        // getLayout().dispose();
        //
        super.setLayout(layout);
        ((CircleNodeLayout) getLayout()).setNode(this);
        setNeedsRefresh(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.fsa.Node#getShape()
     */
    @Override
    public Shape getShape() {
        return circle;
    }

}
