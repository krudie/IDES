package presentation.fsa;

/**
 * Visual representation of the handles used to modify an edge.
 * 
 * @author Helen Bretzke
 */
public class EdgeHandler extends GraphElement {

    /** indicates that there has been no intersection with the handler */
    public static final int NO_INTERSECTION = -1;

    /**
     * Index of last intersected control point anchor, <code>NO_INTERSECTION</code>
     * if there was no intersection.
     * 
     * @see BezierLayout#P1
     * @see BezierLayout#CTRL1 etc.
     */
    protected int lastIntersected = NO_INTERSECTION;

    public EdgeHandler(Edge edge) {
        setParent(edge);
        // setDirty(true);
    }

    public Edge getEdge() {
        return (Edge) getParent();
    }

    /**
     * @return index of the last intersected control point anchor, if no
     *         intersection returns <code>NO_INTERSECTION</code>.
     */
    public int getLastIntersected() {
        return lastIntersected;
    }
}
