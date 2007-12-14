package presentation.fsa;

import util.BooleanUIBinder;

/**
 * This class maintains layout properties global to an FSA graph.
 * 
 * @author Lenko Grigorov
 */
public class GraphLayout
{

	/**
	 * Stores the information if the FSA graph should use a uniform radius for
	 * all nodes in the graph.
	 */
	protected BooleanUIBinder useUniformRadius = new BooleanUIBinder();

	/**
	 * Retrieves the {@link BooleanUIBinder} which stores information if the FSA
	 * graph should use a uniform radius for all nodes in the graph.
	 * 
	 * @return the {@link BooleanUIBinder} which stores information if the FSA
	 *         graph should use a uniform radius for all nodes in the graph
	 */
	public BooleanUIBinder getUseUniformRadius()
	{
		return useUniformRadius;
	}

	/**
	 * Sets a new {@link BooleanUIBinder} to store information if the FSA graph
	 * should use a uniform radius for all nodes in the graph.
	 * 
	 * @param binder
	 *            the new {@link BooleanUIBinder} to store information if the
	 *            FSA graph should use a uniform radius for all nodes in the
	 *            graph
	 */
	public void setUseUniformRadius(BooleanUIBinder binder)
	{
		useUniformRadius = binder;
	}
}
