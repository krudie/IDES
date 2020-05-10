package pluggable.layout.jgraph;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.layout.FSALayouter;

/**
 * Lays out an FSA via the fast organic layout algorithm from JGraph.
 * 
 * @author Lenko Grigorov
 */
public class JGraphLayouter implements FSALayouter {
    private static final int STATE_SIZE = 75;
    private static final int SIZE_FACTOR = 175;

    @Override
    public String getName() {
        return "JGraph organic layout";
    }

    @Override
    public Map<Long, Point2D.Float> layout(FSAModel model) {
        Random rand = new Random();
        int dim = (int) Math.ceil(Math.sqrt(model.getStateCount())) * SIZE_FACTOR;
        Hashtable<FSAState, mxCell> stateMap = new Hashtable<>();

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            for (Iterator<FSAState> si = model.getStateIterator(); si.hasNext();) {
                FSAState s = si.next();
                // initial positions need to be randomized
                // because disconnected vertices will be ignored by the algorithm
                mxCell v = (mxCell) graph.insertVertex(parent, null, null, rand.nextInt(dim), rand.nextInt(dim),
                        STATE_SIZE, STATE_SIZE);
                stateMap.put(s, v);
            }
            for (Iterator<FSATransition> ti = model.getTransitionIterator(); ti.hasNext();) {
                FSATransition t = ti.next();
                if (t.getSource() != t.getTarget()) {
                    graph.insertEdge(parent, null, null, stateMap.get(t.getSource()), stateMap.get(t.getTarget()));
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
        mxGraphLayout layout = new mxFastOrganicLayout(graph);
        layout.execute(graph.getDefaultParent());
        Map<Long, Point2D.Float> ret = new HashMap<>();
        for (Iterator<FSAState> si = model.getStateIterator(); si.hasNext();) {
            FSAState s = si.next();
            mxGeometry geom = stateMap.get(s).getGeometry();
            ret.put(s.getId(), new Point2D.Float((float) geom.getX(), (float) geom.getY()));
        }
        return ret;
    }
}
