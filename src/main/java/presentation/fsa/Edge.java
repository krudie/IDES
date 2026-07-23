package presentation.fsa;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import ides.api.model.fsa.FSATransition;
import io.fsa.ver2_1.GraphExporter;
import presentation.GraphicalLayout;
import util.AnnotationKeys;

/**
 * The abstract description of data and behaviours common to all edges that
 * represent transitions and initial arrows in the graphical display.
 * 
 * @author Helen Bretzke
 */
public abstract class Edge extends GraphElement {

    private ArrayList<FSATransition> transitions; // the transitions that this

    // edge represents

    private Node source; // the source node at the edge's head

    private Node target; // the target node adjacent on the edge's tail

    private EdgeHandler handler; // Anchors for modifying the shape and

    // position of the curve.

    private GraphLabel label; // extra pointer for O(1) access to avoid

    // iterating over children and using instanceof

    /**
     * Creates an edge emanating from the given source node.
     * 
     * @param source the source node
     */
    public Edge(Node source) {
        this(source, null);
    }

    /**
     * Creates an edge emanating from the given source node, terminating at the
     * given target node and representing the transition <code>t</code>.
     * 
     * @param source the source node
     * @param target the target node
     * @param t      the transition to be represented by this edge
     */
    public Edge(Node source, Node target, FSATransition t) {
        this(source, target);
        transitions.add(t);
    }

    /**
     * Creates an edge adjacent on the source and target nodes with no abstract
     * transition. NOTE No default transition is created since this edge gets its Id
     * from its first transition.
     * 
     * @param source the source node
     * @param target the target node
     */
    public Edge(Node source, Node target) {
        transitions = new ArrayList<FSATransition>();
        this.source = source;
        this.target = target;
        this.label = new GraphLabel("", GraphLabel.DEFAULT_FONT_SIZE);
        insert(label);
    }

    /**
     * Creates a string that contains an appropriate (depending on the type)
     * encoding of this edge.
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

    protected static int SOURCE_NODE = 0;

    protected static int TARGET_NODE = 1;

    /**
     * Computes an approximation to the point where this edge intersects the
     * boundary of <code>node</code>. Returns the first point between midpoint of
     * the edge and centre of Node with given type where this edge intersects the
     * boundary of <code>node</code>, null if no intersection exists. NOTE more than
     * one intersection is possible (e.g. reflexive edges and curved edges with
     * multiple crossings).
     * 
     * @param node
     * @param type SOURCE or TARGET
     * @return the first point between middle of edge and centre of Node with given
     *         type where this edge intersects the boundary of <code>node</code>,
     *         null if no intersection exists.
     */
    public abstract Point2D intersectionWithBoundary(Node node, int type);

    /**
     * Sets the handler for this edge with <code>handler</code>. Replace the current
     * edge handler (if exist) with the <code>handler</code>.
     * 
     * @param handler
     */
    public void setHandler(EdgeHandler handler) {
        if (this.handler != null) {
            remove(this.handler);
        }
        this.handler = handler;
        this.insert(handler);
    }

    public EdgeHandler getHandler() {
        return handler;
    }

    public Node getSourceNode() {
        return source;
    }

    public void setSourceNode(Node source) {
        this.source = source;
    }

    public Node getTargetNode() {
        return target;
    }

    public void setTargetNode(Node target) {
        this.target = target;
    }

    /**
     * Adds the given transition to the set of transitions that this edge
     * represents.
     * 
     * @param t
     */
    public void addTransition(FSATransition t) {
        transitions.add(t);
    }

    public void removeTransition(FSATransition t) {
        transitions.remove(t);
    }

    public Iterator<FSATransition> getTransitions() {
        return transitions.iterator();
    }

    /**
     * Returns true iff this edge has at least one transition fired by an
     * uncontrollable event or if all transitions have <code>null</code> events.
     * 
     * @return true iff this edge has at least one transition fired by an
     *         uncontrollable event or if all transitions have <code>null</code>
     *         events.
     */
    public boolean hasUncontrollableEvent() {
        boolean uncontrol = false;
        boolean control = false;
        Iterator<FSATransition> i = getTransitions();
        while (i.hasNext()) {
            FSATransition t = i.next();
            if (t.getEvent() != null) {
                if (t.getEvent().isControllable()) {
                    control = true;
                } else {
                    uncontrol = true;
                }
            }
        }
        return uncontrol || !control;
    }

    /**
     * Returns true iff this edge has at least one transition fired by an
     * unobservable event.
     * 
     * @return true iff this edge has at least one transition fired by an
     *         unobservable event.
     */
    public boolean hasUnobservableEvent() {
        Iterator<FSATransition> i = getTransitions();
        while (i.hasNext()) {
            FSATransition t = i.next();
            if (t.getEvent() != null && !t.getEvent().isObservable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the number of transitions that this edge represents.
     * 
     * @return the number of transitions that this edge represents
     */
    public int transitionCount() {
        return transitions.size();
    }

    // /**
    // * @return the Id of the first transition in the list of
    // * transitions. If the list is empty, returns the GraphElement getId()
    // */
    // public Long getId(){
    // if(!transitions.isEmpty()){
    // return transitions.get(0).getId();
    // }
    // return super.getId();
    // }

    /**
     * Displays a popup menu providing operations on this edge.
     */
    @Override
    public void showPopup(Component c) {
        EdgePopup.showPopup((GraphDrawingView) c, this);
    }

    public GraphLabel getLabel() {
        return label;
    }

    public void setLabel(GraphLabel label) {
        this.remove(this.label);
        this.insert(label);
        this.label = label;
    }

    @Override
    public void setLayout(GraphicalLayout layout) {
        super.setLayout(layout);
        for (FSATransition t : transitions) {
            t.setAnnotation(AnnotationKeys.LAYOUT, layout);
        }
    }

    /**
     * Adds the symbol to the list of event symbols to be displayed on this edge.
     * TODO event symbols should be passed directly to the edge label
     * 
     * @param symbol
     */
    public abstract void addEventName(String symbol);

    /**
     * Returns the point where this edge intersects its source node.
     * 
     * @return the point where this edge intersects its source node.
     */
    public abstract Point2D.Float getSourceEndPoint();

    /**
     * Returns the point where this edge intersects its target node.
     * 
     * @return the point where this edge intersects its target node.
     */
    public abstract Point2D.Float getTargetEndPoint();

    /**
     * Computes the shape (curve, line etc.) that visually represents this edge.
     */
    public abstract void computeEdge();

    /**
     * Returns true iff this is a straight edge.
     * 
     * @return true iff this is a straight edge
     */
    public abstract boolean isStraight();

    /**
     * Sets the point of type <code>pointType</code> to <code>point</code>.
     * 
     * @param point     the value to set the point
     * @param pointType a constant indicating which point to set
     */
    public abstract void setPoint(Point2D point, int pointType);

    /**
     * @param pointType
     * @return true iff the given point type is movable for this edge
     */
    public abstract boolean isMovable(int pointType);

    /**
     * If this edge is not already straight and can be straightened straightens it.
     */
    public abstract void straighten();

    // /**
    // * If this edge is not straight, make it have a symmetrical appearance.
    // *
    // */
    // public abstract void symmetrize();

    /**
     * Returns whether this edge can be straightened.
     * 
     * @return false by default
     */
    public boolean canBeStraightened() {
        return false;
    }
}
