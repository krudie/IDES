package pluggable.layout.tree;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.layout.FSALayouter;

/**
 * Simple deterministic tree layout for FSA graphs. Can be used to layout any
 * FSA graph, however, the layout of non-tree graphs is usually poor.
 * 
 * @author Lenko Grigorov
 */

public class TreeLayouter implements FSALayouter {
    protected final static float DELTA_X = 150;

    protected final static float DELTA_Y = 100;

    protected final static float DELTA_SUBTREE = 100;

    public String getName() {
        return "Simple tree layout";
    }

    public Map<Long, Point2D.Float> layout(FSAModel model) {
        Map<Long, Point2D.Float> ret = new HashMap<Long, Point2D.Float>();

        TreeSet<Long> statesToProcess = new TreeSet<Long>();
        TreeSet<Long> initialStates = new TreeSet<Long>();
        for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();) {
            FSAState state = i.next();
            statesToProcess.add(state.getId());
            if (state.isInitial()) {
                initialStates.add(state.getId());
            }
        }

        float currentHighest = 0;

        while (!initialStates.isEmpty()) {
            long init = initialStates.iterator().next();
            initialStates.remove(init);
            if (!statesToProcess.contains(init)) {
                continue;
            }
            Map<Long, Point2D.Float> subtree = layoutSubtree(model, statesToProcess, init);
            float max = highest(subtree);
            float deltaSubtree = ret.isEmpty() ? 0 : DELTA_SUBTREE;
            shiftVertical(subtree, currentHighest + deltaSubtree + max);
            currentHighest = currentHighest + deltaSubtree + 2 * max;
            ret.putAll(subtree);
        }

        while (!statesToProcess.isEmpty()) {
            long init = statesToProcess.iterator().next();
            Map<Long, Point2D.Float> subtree = layoutSubtree(model, statesToProcess, init);
            float max = highest(subtree);
            float deltaSubtree = ret.isEmpty() ? 0 : DELTA_SUBTREE;
            shiftVertical(subtree, currentHighest + deltaSubtree + max);
            currentHighest = currentHighest + deltaSubtree + 2 * max;
            ret.putAll(subtree);
        }

        return ret;
    }

    protected Map<Long, Point2D.Float> layoutSubtree(FSAModel model, TreeSet<Long> statesToProcess, long root) {
        Map<Long, Point2D.Float> ret = new HashMap<Long, Point2D.Float>();

        float baseline = 0;

        TreeSet<Long> wave = new TreeSet<Long>();
        wave.add(root);
        statesToProcess.remove(root);

        float nextX = 0;

        while (!wave.isEmpty()) {
            TreeSet<Long> nextWave = new TreeSet<Long>();
            float nextY = baseline - (DELTA_Y * (wave.size() - 1) / 2);
            for (long id : wave) {
                ret.put(id, new Point2D.Float(nextX, nextY));
                nextY += DELTA_Y;
                for (Iterator<FSATransition> i = model.getState(id).getOutgoingTransitionsListIterator(); i
                        .hasNext();) {
                    FSAState child = i.next().getTarget();
                    if (statesToProcess.contains(child.getId())) {
                        nextWave.add(child.getId());
                        statesToProcess.remove(child.getId());
                    }
                }
            }
            wave = nextWave;
            nextX += DELTA_X;
        }

        return ret;
    }

    protected float highest(Map<Long, Point2D.Float> map) {
        float max = Float.MIN_VALUE;
        for (Point2D.Float p : map.values()) {
            if (max < p.y) {
                max = p.y;
            }
        }
        return max;
    }

    protected void shiftVertical(Map<Long, Point2D.Float> map, float deltaY) {
        for (Point2D.Float p : map.values()) {
            p.setLocation(p.x, p.y + deltaY);
        }
    }
}
