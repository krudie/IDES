/**
 * 
 */
package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import presentation.GraphicalLayout;

/**
 * A draggable representation of the control points and tangent lines for the
 * bezier curve of an edge. Used to modify the shape of a bezier edge.
 * 
 * @author Helen Bretzke
 */

public class BezierHandler extends EdgeHandler {

    /* Circles indicating movable (draggable) control points */
    private Ellipse2D.Double[] anchors;

    /* Radius of anchors */
    protected static final int RADIUS = 5;

    /**
     * Creates a handler for the given edge.
     * 
     * @param edge the edge to be handled
     */
    public BezierHandler(BezierEdge edge) {
        super(edge);
        anchors = new Ellipse2D.Double[4];
        refresh();
    }

    /**
     * Returns the edge.
     * 
     * @return the edge
     */
    @Override
    public BezierEdge getEdge() {
        return (BezierEdge) super.getEdge();
    }

    /**
     * Update my layout information from the edge.
     */
    @Override
    public void refresh() {
        int d = 2 * RADIUS;
        // upper left corner, width and height of circle's bounding box
        anchors[BezierLayout.P1] = new Ellipse2D.Double(getEdge().getP1().x - RADIUS / 2,
                getEdge().getP1().y - RADIUS / 2, RADIUS, RADIUS);
        anchors[BezierLayout.CTRL1] = new Ellipse2D.Double(getEdge().getCTRL1().x - RADIUS,
                getEdge().getCTRL1().y - RADIUS, d, d);
        anchors[BezierLayout.CTRL2] = new Ellipse2D.Double(getEdge().getCTRL2().x - RADIUS,
                getEdge().getCTRL2().y - RADIUS, d, d);
        anchors[BezierLayout.P2] = new Ellipse2D.Double(getEdge().getP2().x - RADIUS / 2,
                getEdge().getP2().y - RADIUS / 2, RADIUS, RADIUS);
        setNeedsRefresh(false);
    }

    /**
     * Draws this handler in the given graphics context.
     * 
     * @param g the graphics context
     */
    @Override
    public void draw(Graphics g) {

        if (needsRefresh()) {
            refresh();
        }

        if (visible) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.BLUE);
            g2d.setStroke(GraphicalLayout.FINE_STROKE);

            // don't display end point circles since they are not moveable.
            for (int i = 1; i < 3; i++) {
                g2d.setColor(Color.WHITE);
                g2d.fill(anchors[i]);
                g2d.setColor(Color.BLUE);
            }

            g2d.drawLine((int) (getEdge().getP1().x), (int) (getEdge().getP1().y), (int) (getEdge().getCTRL1().x),
                    (int) (getEdge().getCTRL1().y));

            g2d.drawLine((int) (getEdge().getP2().x), (int) (getEdge().getP2().y), (int) (getEdge().getCTRL2().x),
                    (int) (getEdge().getCTRL2().y));

            for (int i = 1; i < 3; i++) { // don't display end point circles since not moveable.
                g2d.draw(anchors[i]);
            }

        }
    }

    /**
     * Returns true if p intersects one of the control point circles and stores the
     * index of the last intersected point; if false the next call to
     * getLastIntersected will return NO_INTERSECTION.
     * 
     * @return true iff p intersects one of the control point circles.
     */
    @Override
    public boolean intersects(Point2D p) {
        for (int i = 0; i < 4; i++) {
            if (anchors[i] != null && anchors[i].contains(p)) {
                lastIntersected = i;
                return true;
            }
        }
        lastIntersected = NO_INTERSECTION;
        return false;
    }
}
