/**
 * 
 */
package pluggable.layout.jung;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.DAGLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.uci.ics.jung.visualization.contrib.TreeLayout;
import pluggable.layout.FSMLayouter;
import presentation.fsa.Edge;
import presentation.fsa.FSMGraph;
import presentation.fsa.Node;
import samples.graph.BasicRenderer;

/**
 *
 * @author Lenko Grigorov
 */
public class JUNGLayouter implements FSMLayouter {

	/* (non-Javadoc)
	 * @see pluggable.layout.FSMLayouter#layout(presentation.fsa.FSMGraph)
	 */
	public void layout(FSMGraph graph) {
		DirectedSparseGraph g=new DirectedSparseGraph();
		BridgeMapper.nodeMap.clear();
		BridgeMapper.nodeMapInverse.clear();
		for(Node n:graph.getNodes())
		{
			SimpleDirectedSparseVertex v=new SimpleDirectedSparseVertex();
			g.addVertex(v);
			BridgeMapper.nodeMap.put(n,v);
			BridgeMapper.nodeMapInverse.put(v,n);
		}
		for(Edge e:graph.getEdges())
		{
			DirectedSparseEdge edge=new DirectedSparseEdge(
					BridgeMapper.nodeMap.get(e.getSource()),
					BridgeMapper.nodeMap.get(e.getTarget()));
			g.addEdge(edge);
		}
		KKLayout l=new KKLayout(g);
		int dim=(int)Math.ceil(Math.sqrt(BridgeMapper.nodeMap.size()))*100;
		l.initialize(new Dimension(dim,dim));
//		l.setRepulsionMultiplier(.99);
//		l.update();
		while(!l.incrementsAreDone())
			l.advancePositions();
		for(Vertex v:BridgeMapper.nodeMapInverse.keySet())
		{
			Node n=BridgeMapper.nodeMapInverse.get(v);
			n.setLocation(l.getLocation(v));
			graph.saveMovement(graph);
		}
	}

}
