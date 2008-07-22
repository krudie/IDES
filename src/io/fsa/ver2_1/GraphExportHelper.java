package io.fsa.ver2_1;

import javax.swing.undo.CompoundEdit;

import model.fsa.FSAModel;
import pluggable.io.FormatTranslationException;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphView;
import presentation.fsa.actions.GraphActions;
import services.latex.LatexElement;
import services.latex.LatexManager;
import services.latex.LatexRenderException;

/**
 * Contains methods useful for the export of graphs of FSAs.
 * 
 * @author Lenko Grigorov
 */
public class GraphExportHelper
{
	/**
	 * Wraps an {@link FSAModel} into an {@link FSAGraph}, recomputes the
	 * locations and positions of graph elements so that the graph matches what
	 * you get on the screen when modelling, and shifts the graph so it doesn't
	 * have negative bounds.
	 * 
	 * @param a
	 *            the {@link FSAModel} to be wrapped
	 * @return the {@link FSAGraph} for the model
	 * @throws FormatTranslationException
	 *             in case LaTeX rendering is on and rendering the labels fails
	 */
	public static FSAGraph wrapRecomputeShift(FSAModel a)
			throws FormatTranslationException
	{
		GraphView g = new GraphView(a);
		if (LatexManager.isLatexEnabled())
		{
			try
			{
				g.setAllowedRendering(true);
				for (LatexElement e : g.getUnrenderedLatexElements())
				{
					e.renderIfNeeded();
				}
			}
			catch (LatexRenderException e)
			{
				throw new FormatTranslationException(e);
			}
		}
		FSAGraph graph = g.getGraphModel();
		// apparently 2 refreshes are necessary to fit transitions around states
		graph.refresh();
		graph.refresh();
		new GraphActions.ShiftGraphInViewAction(new CompoundEdit(), graph)
				.execute();
		return graph;
	}
}
