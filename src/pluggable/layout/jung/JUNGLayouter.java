/**
 * 
 */
package pluggable.layout.jung;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import pluggable.layout.FSALayouter;
import presentation.fsa.Edge;
import presentation.fsa.FSAGraph;
import presentation.fsa.InitialArrow;
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
			if(e instanceof InitialArrow)
			{
				continue;
			}
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
		}
		//Christian - commitMovement removed!
//		graph.commitMovement(graph);
		formatGraph(graph);
	}
	protected void formatGraph(FSAGraph graph)
	{
		for(Edge edge:graph.getEdges())
		{
			//For each edge, get the target and source nodes
			Node targetNode = edge.getTargetNode();
			Node sourceNode = edge.getSourceNode();
			//For each edge beginning on the target node, check if its target
			//is the same as the sourceNode
			Iterator<Edge> adjEdges = targetNode.adjacentEdges();
			while(adjEdges.hasNext())
			{
				Edge secondEdge = adjEdges.next();
				Node destination = secondEdge.getTargetNode();
				//If the target node has an edge pointing to the sourceNode
				//then arcMore the edge.
				if(destination.equals(sourceNode) & !(targetNode.equals(sourceNode)))
				{
					graph.arcMore(edge);
				}
			}
			
		}
		//Iterate all the nodes to recompute the positions for its children:
		//initial arrows and self-loops
		for(Node node:graph.getNodes())
		{
			//change this methods to a "auto-format" method on the node??
			
			//Resetting the position for the self-arrows
			node.relocateReflexiveEdges();
			//Resetting the position for the initial arrows
			if(node.getState().isInitial())
			{
				node.relocateInitialArrow();
			}
		}

	}
}
