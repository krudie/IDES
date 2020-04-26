/**
 * 
 */
package pluggable.layout.jung;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.layout.FSALayouter;

/**
 * @author Lenko Grigorov
 */
public class JUNGLayouter implements FSALayouter {

    /**
     * A fudge factor for laying out the graph. Used in determining the dimensions
     * of the final layout, and directly affects node spacing.
     */
    private static final int SIZE_FACTOR = 175;

    public String getName() {
        return "Default layout";
    }

    public Map<Long, Point2D.Float> layout(FSAModel model) {
        DirectedSparseGraph g = new DirectedSparseGraph();
        BridgeMapper.stateMap.clear();
        BridgeMapper.stateMapInverse.clear();
        for (Iterator<FSAState> si = model.getStateIterator(); si.hasNext();) {
            FSAState s = si.next();
            SimpleDirectedSparseVertex v = new SimpleDirectedSparseVertex();
            g.addVertex(v);
            BridgeMapper.stateMap.put(s, v);
            BridgeMapper.stateMapInverse.put(v, s);
        }
        for (Iterator<FSATransition> ti = model.getTransitionIterator(); ti.hasNext();) {
            FSATransition t = ti.next();
            if (!BridgeMapper.stateMap.get(t.getSource()).isPredecessorOf(BridgeMapper.stateMap.get(t.getTarget()))) {
                DirectedSparseEdge edge = new DirectedSparseEdge(BridgeMapper.stateMap.get(t.getSource()),
                        BridgeMapper.stateMap.get(t.getTarget()));
                g.addEdge(edge);
            }
        }
        KKLayout l = new KKLayout(g);
        int dim = (int) Math.ceil(Math.sqrt(BridgeMapper.stateMap.size())) * SIZE_FACTOR;
        l.initialize(new Dimension(dim, dim));
        // l.setRepulsionMultiplier(.99);
        // l.update();
        while (!l.incrementsAreDone()) {
            l.advancePositions();
        }
        Map<Long, Point2D.Float> ret = new HashMap<Long, Point2D.Float>();
        for (Vertex v : BridgeMapper.stateMapInverse.keySet()) {
            ret.put(BridgeMapper.stateMapInverse.get(v).getId(),
                    new Point2D.Float((float) l.getLocation(v).getX(), (float) l.getLocation(v).getY()));
        }
        return ret;
    }
}
