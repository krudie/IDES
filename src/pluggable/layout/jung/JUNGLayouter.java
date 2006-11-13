/**
 * 
 */
package pluggable.layout.jung;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import pluggable.layout.FSALayouter;
import presentation.fsa.Edge;
import presentation.fsa.FSAGraph;
import presentation.fsa.Node;

/**
 *
 * @author Lenko Grigorov
 */
public class JUNGLayouter implements FSALayouter {
	
	/**
	 * A fudge factor for laying out the graph.  Used in determining the dimensions
	 * of the final layout, and directly affects node spacing.
	 */
	private static final int SIZE_FACTOR = 175;

	/* (non-Javadoc)
	 * @see pluggable.layout.FSMLayouter#layout(presentation.fsa.FSMGraph)
	 */
	public void layout(FSAGraph graph) {
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
					BridgeMapper.nodeMap.get(e.getSourceNode()),
					BridgeMapper.nodeMap.get(e.getTargetNode()));
			g.addEdge(edge);
		}
		KKLayout l=new KKLayout(g);
		int dim=(int)Math.ceil(Math.sqrt(BridgeMapper.nodeMap.size()))*SIZE_FACTOR;
		l.initialize(new Dimension(dim,dim));
//		l.setRepulsionMultiplier(.99);
//		l.update();
		while(!l.incrementsAreDone())
			l.advancePositions();
		for(Vertex v:BridgeMapper.nodeMapInverse.keySet())
		{
			Node n=BridgeMapper.nodeMapInverse.get(v);
			n.setLocation(new Point2D.Float((float)l.getLocation(v).getX(),(float)l.getLocation(v).getY()));
			graph.commitMovement(graph);
		}
	}

}
