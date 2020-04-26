package presentation.fsa;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Map;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import ides.api.core.Hub;
import ides.api.plugin.layout.FSALayoutManager;
import ides.api.plugin.layout.FSALayouter;
import presentation.fsa.actions.GraphUndoableEdits;

public class FSAGraphLayouter {
    public static void layout(FSAGraph graph) {
        layout(graph, FSALayoutManager.instance().getDefaultFSALayouter());
    }

    public static void layout(FSAGraph graph, FSALayouter layouter) {
        Map<Long, Point2D.Float> positions = layouter.layout(graph.getModel());
        for (Node n : graph.getNodes()) {
            n.setLocation(positions.get(n.getState().getId()));
        }
        formatGraph(graph);
    }

    protected static void formatGraph(FSAGraph graph) {
        for (Edge edge : graph.getEdges()) {
            // For each edge, get the target and source nodes
            Node targetNode = edge.getTargetNode();
            Node sourceNode = edge.getSourceNode();
            // For each edge beginning on the target node, check if its target
            // is the same as the sourceNode
            Iterator<Edge> adjEdges = targetNode.adjacentEdges();
            while (adjEdges.hasNext()) {
                Edge secondEdge = adjEdges.next();
                Node destination = secondEdge.getTargetNode();
                // If the target node has an edge pointing to the sourceNode
                // then arcMore the edge.
                if (destination.equals(sourceNode) & !(targetNode.equals(sourceNode))) {
                    graph.arcMore(edge);
                }
            }

        }
        // Iterate all the nodes to recompute the positions for its children:
        // initial arrows and self-loops
        for (Node node : graph.getNodes()) {
            // change this methods to a "auto-format" method on the node??

            // Resetting the position for the self-arrows
            node.relocateReflexiveEdges();
            // Resetting the position for the initial arrows
            if (node.getState().isInitial()) {
                node.relocateInitialArrow();
            }
        }
    }

    public static UndoableEdit layoutUndoable(FSAGraph graph) {
        return layoutUndoable(graph, FSALayoutManager.instance().getDefaultFSALayouter());
    }

    public static UndoableEdit layoutUndoable(FSAGraph graph, FSALayouter layouter) {
        Map<Long, Point2D.Float> positions = layouter.layout(graph.getModel());
        CompoundEdit allEdits = new CompoundEdit();
        for (Node n : graph.getNodes()) {
            Point2D.Float p1 = n.getLocation();
            Point2D.Float p2 = positions.get(n.getState().getId());
            UndoableEdit edit = new GraphUndoableEdits.UndoableMove(graph, n,
                    new Point2D.Float(p2.x - p1.x, p2.y - p1.y));
            edit.redo();
            allEdits.addEdit(edit);
        }
        allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub.string("undoLayoutGraph")));
        allEdits.end();
        return allEdits;
    }
}
