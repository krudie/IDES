/**
 * 
 */
package ides.api.plugin.layout;

import java.awt.geom.Point2D;
import java.util.Map;

import ides.api.model.fsa.FSAModel;

/**
 * Interface for classes which layout {@link FSAModel}s.
 * 
 * @author Lenko Grigorov
 */
public interface FSALayouter {
    /**
     * Retrieve the name of the layout algorithm.
     * 
     * @return the name of the layout algorithm
     */
    public String getName();

    /**
     * Compute the locations of the states of an {@link FSAModel}.
     * 
     * @param model the {@link FSAModel} whose states should be laid out.
     * @return a map where the key is the id of a state of the model and the value
     *         is the location of the state; the map must contain locations for all
     *         the states in the model; if there are no states in the model, return
     *         an empty map
     */
    public abstract Map<Long, Point2D.Float> layout(FSAModel model);
}
