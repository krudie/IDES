package presentation.fsa.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import javax.swing.undo.CompoundEdit;

import ides.api.core.Hub;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.CircleNode;
import presentation.fsa.CircleNodeLayout;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.Node;
import presentation.fsa.ReflexiveEdge;
import presentation.fsa.actions.GraphActions;
import presentation.fsa.actions.GraphUndoableEdits;

/**
 * Creates nodes and edges by drawing with mouse in a GraphDrawingView context.
 * NOTE: Can NOT just make changes to the graph model from here because must
 * save each command in the command history.
 * 
 * @author helen bretzke
 */
public class CreationTool extends DrawingTool {

    private boolean drawingEdge = false;

    private CircleNode sourceNode, targetNode; // nodes to be source and target

    // of created edge

    private CircleNode startNode, endNode; // nodes intersected on mouse

    // pressed and released respectively

    private BezierEdge edge;

    private boolean aborted;

    private boolean firstClick;

    private boolean edgeLeftLayout = false;

    public CreationTool() {
        // context = board;
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // JAVA BUG: for any preferred dimension, always 32 X 32 on Windows
        // (works on MAC, what about Linux?)!!
        // System.out.println(toolkit.getBestCursorSize(10, 10));

        // FIXME dynamic cursor names in UISettings class
        cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getIDESResource("images/cursors/create.gif")),
                new Point(0, 0), "CREATE_NODES_OR_EDGES");
    }

    public void init() {
        startNode = null;
        endNode = null;
        sourceNode = null;
        endNode = null;
        drawingEdge = false;
        if (edge != null) {
            aborted = true;
        }
    }

    @Override
    public void handleMouseClicked(MouseEvent me) {
        super.handleMouseClicked(me);

        if (aborted) {
            aborted = false;
            return;
        }

        if (drawingSelfLoop()) {
            if (!firstClick && me.getClickCount() != 2) {
                // second click on same node so make a self-loop
                finishSelfLoop();
            } else { // double click should activate text (labelling) tool
                abortEdge();
                ContextAdaptorHack.context.setTool(GraphDrawingView.TEXT);
                ContextAdaptorHack.context.getCurrentTool().handleMouseClicked(me);
            }
        } else {
            if (me.getClickCount() == 2) {
                ContextAdaptorHack.context.setTool(GraphDrawingView.TEXT);
                ContextAdaptorHack.context.getCurrentTool().handleMouseClicked(me);
            }
        }
    }

    @Override
    public void handleMousePressed(MouseEvent me) {
        super.handleMousePressed(me);

        startNode = null;

        // Do not clear the selection if the selected element is a BezierEdge
        // Reason: let the user modify the control points of the bezier curve
        // without need to change the curve.
        if (!(ContextAdaptorHack.context.getSelectedElement() instanceof BezierEdge)) {
            ContextAdaptorHack.context.clearCurrentSelection();
        }
        // Refresh the selection
        ContextAdaptorHack.context.updateCurrentSelection(me.getPoint());
        ContextAdaptorHack.context.repaint();
        GraphElement selectedElement = ContextAdaptorHack.context.getSelectedElement();
        if (selectedElement instanceof CircleNode || ContextAdaptorHack.context.getSelectedElement() == null) { // If a
                                                                                                                // node
                                                                                                                // or an
                                                                                                                // empty
                                                                                                                // space
                                                                                                                // is
                                                                                                                // clicked
            if (ContextAdaptorHack.context.getAvoidNextDraw() == true) {
                return;
            }
            startNode = (CircleNode) ContextAdaptorHack.context.getSelectedElement();
            if (!drawingEdge) {
                if (startNode != null) {
                    startEdge(); // assume we're drawing an edge until mouse
                    // released decides otherwise.
                    dragging = true; // assume we're dragging until mouse
                    // released decides otherwise.
                    return;
                }
            }
        } else if (drawingEdge) {
            targetNode = null;
        } else {// If an edge or label is selected:
            startNode = null;
            ContextAdaptorHack.context.setAvoidNextDraw(true);
            // Let the SELECTION tool work on the edge or label modification
            ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
            ContextAdaptorHack.context.getCurrentTool().handleMousePressed(me);
            return;
        }

    }

    @Override
    public void handleMouseReleased(MouseEvent me) {
        super.handleMouseReleased(me);
        if (!(me.getButton() == MouseEvent.BUTTON1)) {
            return;
        }
        // Cleaning the selection
        ContextAdaptorHack.context.clearCurrentSelection();
        ContextAdaptorHack.context.updateCurrentSelection(me.getPoint());
        ContextAdaptorHack.context.repaint();
        GraphElement selection = ContextAdaptorHack.context.getSelectedElement();
        try {
            targetNode = (CircleNode) selection;
        } catch (ClassCastException e) {
            // Point pt = me.getPoint();
            // Iterator<CircleNode> it =
            // ContextAdaptorHack.context.getGraphModel().getNodes().iterator();
            // while(it.hasNext())
            // {
            // Node tmpNode = it.next();
            // if(tmpNode.intersects(pt))
            // {
            // targetNode = (CircleNode)tmpNode;
            // break;
            // }
            // }
        }

        // Avoiding a new node to be created based in some of the context flags.
        // These flags may be initially set by interface elements which can
        // interrupt the
        // "normal" interaction with the software.
        // Example: 1 - the user open a popup.
        // 2 - the user click at a point in the canvas to cancel the popup.
        // A new node should not be created in this case because the user just
        // wanted to destroy the popup.
        if (ContextAdaptorHack.context.getAvoidNextDraw() == true) {
            ContextAdaptorHack.context.setAvoidNextDraw(false);
            return;
        }

        // Creating a new node:

        endNode = null;
        try {
            endNode = (CircleNode) selection;
        } catch (ClassCastException e) {
        }

        if (targetNode == sourceNode && sourceNode != null) {
            finishEdge();
        }

        if (startNode == endNode && endNode == sourceNode && drawingEdge && !dragging && firstClick) { // drawing edge
                                                                                                       // by not
                                                                                                       // dragging
                                                                                                       // IDEA To fix
                                                                                                       // conflict with
                                                                                                       // TextTool,
                                                                                                       // delay
                                                                                                       // creation of
                                                                                                       // self loops
                                                                                                       // until we know
                                                                                                       // if user has
                                                                                                       // double
                                                                                                       // clicked.
                                                                                                       // Don't finish
                                                                                                       // edge on mouse
                                                                                                       // released if
                                                                                                       // target ==
                                                                                                       // source.
                                                                                                       // // second
                                                                                                       // click on same
                                                                                                       // node so make
                                                                                                       // a self-loop
                                                                                                       // finishSelfLoop();
            firstClick = false;
            return;

        } else if (startNode != null && startNode == endNode && startNode == sourceNode && dragging) { // select source
                                                                                                       // node, keep
                                                                                                       // drawing edge
                                                                                                       // by mouse move
                                                                                                       // (not
                                                                                                       // dragging)
            dragging = false;
            firstClick = true;
            finishEdge();
        } else if (startNode == null && endNode == null && !drawingEdge) {
            // create a new node at current location
            createNode(me.getPoint());
            firstClick = false;
        } else if (startNode == endNode && startNode != sourceNode && endNode != null) { // select target node, finish
                                                                                         // drawing edge by mouse
                                                                                         // move
            finishEdge();
            firstClick = false;
        } else if (drawingEdge && endNode == null) { //
                                                     // Assumption: startNode and sourceNode are non-null
            finishEdgeAndCreateTarget(me.getPoint());
            firstClick = false;
        } else if (drawingEdge && dragging && endNode != null) { // Assumption: sourceNode != null
            finishEdge();
        } else {
            try {
                finishEdge();
            } catch (Exception e) {
            }
            ;
        }

        endNode = null;
        ContextAdaptorHack.context.repaint();
    }

    private boolean drawingSelfLoop() {
        return startNode == endNode && endNode == sourceNode && drawingEdge && !dragging;
    }

    /**
     * 
     */
    private void finishSelfLoop() {
        targetNode = endNode;
        abortEdge();
        // cmd = new CreateAction(CreateAction.SELF_LOOP, targetNode);
        // cmd.execute();
        new GraphActions.CreateEdgeAction(ContextAdaptorHack.context.getGraphModel(), targetNode, targetNode).execute();
        sourceNode = null;
        targetNode = null;
        ContextAdaptorHack.context.clearCurrentSelection();
    }

    /**
     * @param point
     */
    private void createNode(Point point) {
        // cmd = new CreateAction(CreateAction.NODE, point);
        // cmd.execute();
        new GraphActions.CreateNodeAction(ContextAdaptorHack.context.getGraphModel(),
                new Point2D.Float(point.x, point.y)).execute();
        abortEdge();
        // dragging = false;
        sourceNode = null;
        targetNode = null;
        // context.clearCurrentSelection();
    }

    /**
     * @param point
     */
    private void finishEdgeAndCreateTarget(Point point) {
        // cmd = new CreateAction(
        // CreateAction.NODE_AND_EDGE, edge, point);
        // cmd.execute();
        CompoundEdit allEdits = new CompoundEdit();
        Node[] nodeBuffer = new Node[1];
        new GraphActions.CreateNodeAction(allEdits, ContextAdaptorHack.context.getGraphModel(),
                new Point2D.Float(point.x, point.y), nodeBuffer).execute();
        new GraphActions.CreateEdgeAction(allEdits, ContextAdaptorHack.context.getGraphModel(), edge.getSourceNode(),
                nodeBuffer[0]).execute();
        allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub.string("undoCreateElements")));
        allEdits.end();
        Hub.getUndoManager().addEdit(allEdits);
        // IDEA Don't keep a copy of the temp edge in this class, just use the
        // get and set in context.
        // TODO call abortEdge here and have it do all the work (duplicate code
        // here and in finishEdge).
        edge = null;
        ContextAdaptorHack.context.setTempEdge(null);
        drawingEdge = false;
        dragging = false;
        sourceNode = null;
        targetNode = null;
        ContextAdaptorHack.context.clearCurrentSelection();
    }

    /**
     * 
     */
    private void startEdge() {
        sourceNode = startNode;
        targetNode = null;
        edge = beginEdge(sourceNode);
        drawingEdge = true;
    }

    /**
     * Creates and returns an Edge with source node <code>n1</code>, undefined
     * target node, and terminating at the centre of node <code>n1</code>.
     * 
     * @param n1 source node
     * @return a new Edge with source node n1
     */
    private BezierEdge beginEdge(CircleNode n1) {
        BezierLayout layout = new BezierLayout();
        BezierEdge e = new BezierEdge(layout, n1);
        layout.computeCurve((CircleNodeLayout) n1.getLayout(), n1.getLayout().getLocation());
        ContextAdaptorHack.context.setTempEdge(e);
        edgeLeftLayout = false;
        return e;
    }

    /**
     * Updates the layout for the given edge so it extends to the given target
     * point. If the focused point is a point of the source node, the layout will
     * capture the possible intencion of a reflexive edge and IDES will draw a
     * reflexive layout.
     * 
     * @param e the Edge to be updated
     * @param p the target point
     */
    private void updateEdge(BezierEdge e, Point2D.Float p) {
        CircleNodeLayout s = (CircleNodeLayout) e.getSourceNode().getLayout();
        // only draw the edge if the point is outside the bounds of the source
        // node
        if (!e.getSourceNode().intersects(p)) {
            e = beginEdge((CircleNode) e.getSourceNode());
            e.computeCurve(s, p);
            edge = e;
            ContextAdaptorHack.context.setTempEdge(edge);
            this.edgeLeftLayout = true;
        } else if (this.edgeLeftLayout) {
            edge = new ReflexiveEdge(e.getSourceNode(), null);
            targetNode = (CircleNode) edge.getTargetNode();
            sourceNode = (CircleNode) edge.getSourceNode();
            ((ReflexiveEdge) edge).computeEdge();
            ContextAdaptorHack.context.setTempEdge(edge);
        }
    }

    private void finishEdge() {
        // DEBUG there are some circumstances where we make it to this method
        // with edge==null
        // ... don't know what they are yet ...

        // Cancel the execution flow if the user has clicked once and never left
        // the node
        // with the mouse. The user could be either dragging an edge or has just
        // clicked once to
        // create a new edge.
        // If this variable is false, it is still not time to finish the edge.
        if (!this.edgeLeftLayout) {
            return;
        }

        if (edge != null) {
            // cmd = new CreateAction(CreateAction.EDGE, edge, targetNode);
            // cmd.execute();
            new GraphActions.CreateEdgeAction(ContextAdaptorHack.context.getGraphModel(), edge.getSourceNode(),
                    targetNode).execute();
            // System.out.println(edge.getSourceNode().contains(edge));
        }
        edge = null;
        ContextAdaptorHack.context.setTempEdge(null);
        edgeLeftLayout = false;
        drawingEdge = false;
        dragging = false;
        sourceNode = null;
        targetNode = null;
        edge = null;
        ContextAdaptorHack.context.clearCurrentSelection();
    }

    @Override
    public void handleRightClick(MouseEvent me) {
        super.handleRightClick(me);
        abortEdge();
        ContextAdaptorHack.context.repaint();
        // super.handleRightClick(me);
    }

    public boolean isDrawingEdge() {
        return drawingEdge;
    }

    public void abortEdge() {
        if (drawingEdge) {
            // context.getGraphModel().abortEdge(edge);
            // TODO garbage collect edge
            ContextAdaptorHack.context.setTempEdge(null);
            drawingEdge = false;
        }
        aborted = true;
        ContextAdaptorHack.context.repaint();
    }

    @Override
    public void handleMouseDragged(MouseEvent me) {
        super.handleMouseDragged(me);
        // if drawing an edge, recompute the curve
        if (dragging && drawingEdge || (sourceNode == targetNode && targetNode != null)) {
            updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));
            // context.getGraphModel().updateEdge(edge, new
            // Float(me.getPoint().x, me.getPoint().y));
            ContextAdaptorHack.context.repaint();
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent me) {
        // if drawing an edge, recompute the curve
        if (!dragging && drawingEdge) {
            updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));
            // context.getGraphModel().updateEdge(edge, new
            // Float(me.getPoint().x, me.getPoint().y));
            ContextAdaptorHack.context.repaint();
        }
    }

    @Override
    public void handleKeyTyped(KeyEvent ke) {
    }

    @Override
    public void handleKeyPressed(KeyEvent ke) {
    }

    @Override
    public void handleKeyReleased(KeyEvent ke) {
    }
}
