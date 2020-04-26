package presentation.fsa;

import java.util.Set;

/**
 * Algorithms for laying out a Bezier edge among a set of existing edges
 * connecting the same pair of nodes. FIXME In some cases, the placed edge
 * increases its arc one more time than required.
 * 
 * @author Helen Bretzke for DES Lab, Dept of ECE, Queen's University, Kingston
 *         13 August 2006
 */
public class BezierEdgePlacer {

    /**
     * Inserts <code>edge</code> among the given set of other edges, where all edges
     * connect the same pair of nodes. Computes the layouts for each edge such that
     * they are distributed comfortably along the circumferences of each end node.
     * TODO change this algorithm so that it accommodates reflexive edges and
     * initial arrows (adjacent on only one of <code>edge</code>'s end nodes).
     * 
     * @param edge       the edge to be inserted
     * @param otherEdges set of all other edges connecting the same pair of nodes
     */
    public static void insertEdgeAmong(BezierEdge edge, Set<Edge> otherEdges) {
        // compute default straight edge
        edge.computeEdge();
        // int n = edges.size();

        Edge straightEdge = containsStraightEdge(otherEdges);
        if (straightEdge != null) {
            // find outermost free position
            BezierLayout outPos = setToOutermostFreeLayout(edge, otherEdges);
            // move straight edge to reflection of newly placed edge
            ((BezierLayout) straightEdge.getLayout()).setToReflectionOf(outPos);

            // TODO if we only call this to update the endpts, then should be
            // calling
            // a method that is named appropriately e.g. updateEndpoints()
            straightEdge.computeEdge();

            // unless that position is taken
            if (tooClose(straightEdge, otherEdges)) {
                ((BezierLayout) straightEdge.getLayout()).arcMore(false);
            }

            straightEdge.computeEdge();
            // Christian - commitMovement removed!
            // straightEdge.getGraph().commitMovement(straightEdge);

        } /*
           * else{ // No straight edge if(n % 2 != 0) // Odd # of neighbours { // LENKO:
           * Why not use straight position? // For now... //edge.computeEdge(); // LATER
           * // find edge at outermost position // place new edge symmetric to found edge
           * } // otherwise do nothing since edge is already straight by default }
           */
    }

    /**
     * Returns the layout for, and sets <code>edge</code>'s layout to, the outermost
     * free position such that the arcs of all edges in <code>otherEdges</code> are
     * flatter. NOTE Outermost is defined as having the greatest arc in the curve.
     * 
     * @see CubicCurve2D.flatness() ??? Precondition: there is already a straight
     *      edge in <code>edges</code>. else why wouldn't we just make a straight
     *      edge...
     * @param edge       the edge to be laid out
     * @param otherEdges set of all other edges connecting the same pair of nodes
     * @return the layout for the first free outermost position
     */
    private static BezierLayout setToOutermostFreeLayout(Edge edge, Set<Edge> otherEdges) {
        BezierLayout layout = findOutermostTakenPosition(otherEdges);
        if (layout != null) {
            BezierLayout layout1 = (BezierLayout) edge.getLayout();
            // if curve is 'S'-shaped, return a new layout that arcs beyond it
            if (layout.angle1 * layout.angle2 > 0) {
                double maxAngle = (Math.abs(layout.angle1) > Math.abs(layout.angle2) ? layout.angle1 : layout.angle2);
                layout1.angle1 = maxAngle;
                layout1.angle2 = maxAngle * -1;
                layout1.arcMore();
            } else {
                // otherwise, try reflected position
                layout1.setToReflectionOf(layout);

                // CLM: in accordance with the rules for automatic edge
                // placement, we don't
                // actually want the "outermost free layout"; but rather the
                // outermost free
                // PAIR of layouts, to maintain the reflective symmetry of the
                // outermost edges.
                layout1.arcMore();
            }

            if (tooClose(edge, otherEdges)) {
                layout1.arcMore();
            }

            edge.computeEdge();
            return layout1;
        }
        // let edge do what it wants to by default
        return (BezierLayout) edge.getLayout();
    }

    /**
     * Returns the layout for the outermost non-flat, occupied edge layout among
     * <code>edges</code> if all edges are flat, returns a flat layout. NOTE
     * Outermost is defined as having the greatest arc in the curve.
     * 
     * @see CubicCurve2D.flatness() Precondition: <code>edges</code> is not empty
     * @param edges the set of edges
     * @return the layout for the outermost non-flat, occupied edge layout among
     *         <code>edges</code>. If all edges are flat, returns a flat layout.
     */
    private static BezierLayout findOutermostTakenPosition(Set<Edge> edges) {
        BezierLayout layout = null;
        double max = 0;
        for (Edge edge : edges) {
            double flatness = ((BezierLayout) edge.getLayout()).getCurve().getFlatness();
            if (flatness >= max) {
                max = flatness;
                layout = (BezierLayout) edge.getLayout();
            }
        }
        return layout;
    }

    /**
     * Returns true if the one or more endpoints of <code>edge1</code> is within a
     * distance threshold of any of the endpoints of an edge in <code>edges</code>
     * TODO find a nice-looking minimum comfortable distance between endpoints to
     * allow margins for arrow head along node boundary or add a parameter to this
     * method. Precondition: edge and edges are non-null
     * 
     * @param edge1 the edge to be placed
     * @param edges the set of edges
     * @return true iff the one or more endpoints of <code>edge1</code> is within a
     *         distance threshold of any of the endpoints of an edge in
     *         <code>edges</code>
     */
    public static boolean tooClose(Edge edge1, Set<Edge> edges) {
        /*
         * TODO find a nice-looking minimum comfortable distance between endpoints to
         * allow margins for arrow head along node boundary. For now just use:
         */

        // CLM: I changed this back to SHORT_HEAD_LENGTH/2 because the higher
        // minimum was
        // causing some edges to incorrectly be classified as "too close"
        double min = ArrowHead.SHORT_HEAD_LENGTH / 2;

        for (Edge edge : edges) {
            assert (edge.getSourceEndPoint() != null);
            assert (edge.getTargetEndPoint() != null);

            // check if any pair of visible endpoints (intersections with node
            if (!edge.equals(edge1) && ((edge.getSourceEndPoint().distance(edge1.getSourceEndPoint()) < min)
                    || (edge.getSourceEndPoint().distance(edge1.getTargetEndPoint()) < min)
                    || (edge.getTargetEndPoint().distance(edge1.getTargetEndPoint()) < min)
                    || (edge.getTargetEndPoint().distance(edge1.getSourceEndPoint()) < min))) {
                return true;
            }

        }
        return false;
    }

    /**
     * Returns the first straight edge found in <code>edges</code>, null if no such
     * edge
     * 
     * @param edges the set of edges to be searched
     * @return the first straight edge found in <code>edges</code>, null if no such
     *         edge
     */
    private static Edge containsStraightEdge(Set<Edge> edges) {
        for (Edge edge : edges) {
            if (edge.isStraight()) {
                return edge;
            }
        }
        return null;
    }
}
