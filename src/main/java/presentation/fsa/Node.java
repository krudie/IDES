/**
 * 
 */
package presentation.fsa;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import ides.api.model.fsa.FSAState;
import io.fsa.ver2_1.GraphExporter;

/**
 * The graphical representation of a state in a finite state automaton.
 * 
 * @author helen bretzke
 */
public abstract class Node extends GraphElement {
    // the state to be represented
    FSAState state;

    // label for text to be displayed on node
    GraphLabel label;

    // arrow to indicate that this represents an initial state
    // null or invisible if state is not initial
    InitialArrow initialArrow;

    /**
     * Returns the shape that visually represents this Node in a graphical display.
     * 
     * @return the shape that visually represents this Node in a graphical display
     */
    public abstract Shape getShape();

    /**
     * This method is responsible for creating a string that contains an appropriate
     * (depending on the type) representation of this node.
     * <p>
     * author Sarah-Jane Whittaker
     * 
     * @param selectionBox The area being selected or considered
     * @param exportType   The export format
     * @see GraphExporter#INT_EXPORT_TYPE_EPS
     * @see GraphExporter#INT_EXPORT_TYPE_PSTRICKS
     * @return String The string representation
     */
    public abstract String createExportString(Rectangle selectionBox, int exportType);

    /**
     * Returns an iterator of all edges adjacent to this node, i.e. have this node
     * as either source or target.
     * 
     * @return an iterator of all adjacent edges
     */
    public abstract Iterator<Edge> adjacentEdges();

    /**
     * Return a bounding rectangle for the union of this Node with all of its child
     * elements (e.g. edges and label).
     * 
     * @return bounding rectangle for union of this Node with all of its children.
     */
    public abstract Rectangle2D adjacentBounds();

    @Override
    public Long getId() {
        return state.getId();
    }

    public FSAState getState() {
        return state;
    }

    /**
     * Gets the label of the node.
     * 
     * @return the label of the node
     */
    public GraphLabel getLabel() {
        return label;
    }

    /**
     * Sets the location of this Node to p and adjusts the location of all adjacent
     * edges.
     * 
     * @param p the new location
     */
    @Override
    public void setLocation(Point2D.Float p) {
        super.setLocation(p);
        recomputeEdges();
    }

    /**
     * Compute edge layout for each edge adjacent to this Node.
     */
    protected void recomputeEdges() {
        Iterator<Edge> adjacent = adjacentEdges();
        while (adjacent.hasNext()) {
            Edge e = adjacent.next();
            e.computeEdge();
        }
    }

    /**
     * Sets my state's initial property to <code>b</code> and activates my initial
     * arrow.
     * 
     * @param b
     */
    public void setInitial(boolean b) {
        if (b && initialArrow == null) {
            setInitialArrow(new InitialArrow(this));
        }
        if (b) {
            insert(initialArrow);
        } else {
            remove(initialArrow);
        }
        getState().setInitial(b);
    }

    /**
     * @return the initial arrow for this node
     */
    protected InitialArrow getInitialArrow() {
        return initialArrow;
    }

    /**
     * Sets my intial arrow to the given instance and adds it as one of my child
     * elements. If already have an initial arrow in my set of children, removes it.
     * 
     * @param initialArrow
     */
    protected void setInitialArrow(InitialArrow initialArrow) {
        if (this.initialArrow != null) {
            remove(this.initialArrow);
        }
        this.insert(initialArrow);
        this.initialArrow = initialArrow;
    }

    /**
     * Finds the best position for the initial arrow and recompute it at the best
     * position.
     * <p>
     * author christian
     */
    public void relocateInitialArrow() {
        this.remove(initialArrow);
        this.initialArrow.resetPosition();
        this.insert(initialArrow);
    }

    public void relocateReflexiveEdges() {
        Iterator<Edge> adjEdges = adjacentEdges();
        while (adjEdges.hasNext()) {
            Edge edge = adjEdges.next();
            if (edge.getTargetNode().equals(edge.getSourceNode())) {
                this.remove(edge);
                ((ReflexiveEdge) edge).resetPosition();
                this.insert(edge);
            }

        }
    }
}
