package services.latex;

import java.awt.Cursor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import presentation.fsa.Edge;
import presentation.fsa.GraphLabel;
import presentation.fsa.GraphModel;
import presentation.fsa.Node;

import main.Hub;
import util.InterruptableProgressDialog;

/**
 * This object renders all labels in a DES model and displays the
 * progress in a dialog box with a progress bar. The rendering can be
 * interrupted by the user.
 * <p>The intended use is when loading a file or when turning on LaTeX rendering
 * for the labels.
 *
 * @author Lenko Grigorov
 */
public class LatexPrerenderer extends InterruptableProgressDialog {

	/**
	 * The DES model whose labels will be rendered.
	 */
	protected GraphModel model;

	/**
	 * Set to <code>true</code> if the rendering has to be interrupted.
	 */
	private boolean cancel=false;
	
	/**
	 * Displays a dialog box with a progress bar and starts rendering the labels of a
	 * {@link GraphModel}. The user may cancel the process
	 * using the controls in the dialog box. 
	 * @param model the DES model whose labels have to be rendered 
	 */
	public LatexPrerenderer(GraphModel model)
	{
		super(Hub.getMainWindow(),Hub.string("renderPrerenderTitle"),Hub.string("renderPrerender"));
		this.model=model;
		new Thread(this).start();
		setVisible(true);
	}
	
	/**
	 * Interrupts the process of rendering. 
	 */
	public void interrupt()
	{
		Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		cancel=true;
	}
	
	/**
	 * The main loop where the labels are rendered. Call this method to start
	 * rendering.
	 */
	public void run()
	{
		HashSet<GraphLabel> labels=new HashSet<GraphLabel>();
		Collection<Node> nodes=model.getNodes();
		for(Node n: nodes)
		{
			labels.add(n.getLabel());
		}
		Collection<Edge> edges=model.getEdges();
		for(Edge e: edges)
		{
			labels.add(e.getLabel());
		}
		labels.addAll(model.getLabels());
		progressBar.setMinimum(0);
		progressBar.setMaximum(labels.size());
		int current=0;
		
		Iterator<GraphLabel> i=labels.iterator();
		while(!cancel)
		{
			if(i.hasNext())
			{
				GraphLabel l=i.next();
				try
				{
					l.renderIfNeeded();
				}catch(LatexRenderException e)
				{
					close();
					LatexManager.handleRenderingProblem();
					return;
				}
				current++;
			}
			else
				cancel=true;
			progressBar.setValue(current);
		}
		close();
		return;
	}
	
	/**
	 * Performs the operations needed to be done when the rendering ends
	 * or gets interrupted (such as closing the dialog box).
	 */
	protected void close()
	{
		Hub.getMainWindow().setCursor(Cursor.getDefaultCursor());
		dispose();
	}
}
