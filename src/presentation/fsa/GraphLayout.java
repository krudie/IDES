package presentation.fsa;

import java.io.Serializable;

import util.BooleanUIBinder;

/**
 * This class maintains layout properties global to an FSA graph.
 * 
 * @author Lenko Grigorov
 */
public class GraphLayout implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8588798416683307111L;

    /**
     * The global font size of the model.
     */
    protected float fontSize = GraphLabel.DEFAULT_FONT_SIZE;

    /**
     * Stores the information if the FSA graph should use a uniform radius for all
     * nodes in the graph.
     */
    protected boolean useUniformRadius = false;

    /**
     * Retrieves the {@link BooleanUIBinder} which stores information if the FSA
     * graph should use a uniform radius for all nodes in the graph.
     * 
     * @return the {@link BooleanUIBinder} which stores information if the FSA graph
     *         should use a uniform radius for all nodes in the graph
     */
    public boolean getUseUniformRadius() {
        return useUniformRadius;
    }

    /**
     * Sets if the FSA graph should use a uniform radius for all nodes in the graph.
     * 
     * @param boo <code>true</code> if the FSA graph should use a uniform radius for
     *            all nodes in the graph, <code>false</code> otherwise
     */
    public void setUseUniformRadius(boolean boo) {
        useUniformRadius = boo;
    }

    /**
     * Gets the font size associated with the model.
     * 
     * @return the font size associated with the model
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Sets the font size associated with the model.
     * 
     * @param fs the new font size
     */
    public void setFontSize(float fs) {
        fontSize = fs;
    }
}
