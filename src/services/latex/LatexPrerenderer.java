package services.latex;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import presentation.fsa.Edge;
import presentation.fsa.GraphLabel;
import presentation.fsa.GraphModel;
import presentation.fsa.Node;

import main.Hub;
import util.InterruptableProgressDialog;

public class LatexPrerenderer extends InterruptableProgressDialog {

	protected GraphModel model;
	private boolean cancel=false;
	protected enum Elements{NODES,EDGES,LABELS};
	
	public LatexPrerenderer(GraphModel model)
	{
		super(Hub.getMainWindow(),Hub.string("renderPrerenderTitle"),Hub.string("renderPrerender"));
		this.model=model;
	}
	
	public void interrupt()
	{
		cancel=true;
	}
	
	public void run()
	{
		Collection<Node> nodes=model.getNodes();
		Collection<Edge> edges=model.getEdges();
		Collection<GraphLabel> labels=model.getLabels();
		int total=nodes.size()+edges.size()+labels.size();
		progressBar.setMinimum(0);
		progressBar.setMaximum((int)total);
		int current=0;
		
		Elements idx=Elements.NODES;
		Iterator i=nodes.iterator();
		while(!cancel)
		{
			if(idx==Elements.NODES)
			{
				if(i.hasNext())
				{
					Node n=(Node)i.next();
					try
					{
						n.getLabel().render();
					}catch(LatexRenderException e)
					{
						dispose();
						LatexManager.handleRenderingProblem();
						return;
					}
					current++;
				}
				else
				{
					idx=Elements.EDGES;
					i=edges.iterator();
				}
			}
			else if(idx==Elements.EDGES)
			{
				if(i.hasNext())
				{
					Edge d=(Edge)i.next();
					try
					{
						d.getLabel().render();
					}catch(LatexRenderException e)
					{
						dispose();
						LatexManager.handleRenderingProblem();
						return;
					}
					current++;
				}
				else
				{
					idx=Elements.LABELS;
					i=labels.iterator();
				}
			}
			else if(idx==Elements.LABELS)
			{
				if(i.hasNext())
				{
					GraphLabel l=(GraphLabel)i.next();
					try
					{
						l.render();
					}catch(LatexRenderException e)
					{
						dispose();
						LatexManager.handleRenderingProblem();
						return;
					}
					current++;
				}
				else
					cancel=true;
			}
			progressBar.setValue(current);			
		}
		dispose();
		return;
	}
}
