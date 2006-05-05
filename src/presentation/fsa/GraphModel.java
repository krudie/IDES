package presentation.fsa;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;

import model.DESMetaData;
import model.fsa.FSAMetaData;
import model.fsa.FSAModel;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;
import presentation.Glyph;
import presentation.GraphLabel;
import ui.UIStateModel;

public class GraphModel {

	/**
	 * TODO implement as a more usable form of list or map.
	 */
	private HashMap<Long, Node> nodes;
	private HashMap<Long, Edge> edges;
	private HashMap<Long, GraphLabel> labels;
	
	/**
	 * The recursive structure used to draw the graph.
	 */
	private Glyph graph;
	
	/**
	 * The data models to keep synchronized.
	 */	
	private FSAModel fsa;			// abstract system model
	private FSAMetaData layoutData; // presentation data for the system model
	
	public GraphModel(FSAModel fsa, FSAMetaData data){
		
		this.fsa = fsa;
		this.layoutData = data;
		
		nodes = new HashMap<Long, Node>();
		edges = new HashMap<Long, Edge>();
		labels = new HashMap<Long, GraphLabel>();
		
		// create all nodes
		// for all states in fsa, 
		// get the graphic data, 
		// construct a node and 
		// add to set of nodes		
		Iterator iter = fsa.getStateIterator();
		State s;
		Node n;
		// TODO for all states in the model, refresh all of my nodes		
		// For now, just create everthing new.		
		graph = new GraphElement();
		while(iter.hasNext()){
			s = (State)iter.next();
			n = new Node(s);
			n.setLayout(layoutData.getLayoutData(s));
			graph.insert(n, s.getId());
			nodes.put(new Long(s.getId()), n);
		}
		
		// create all edges and connect to nodes
		// for all transitions in fsa
		iter = fsa.getTransitionIterator();
		Transition t;
		Edge e;
		while(iter.hasNext()){						
			t = (Transition)iter.next();
		
			// TODO get the source and target nodes
		
			// get the graphic data for the transition and all associated events
			// construct the edge			
			e = new Edge(t, layoutData.getLayoutData(t));
			
			// TODO
			// add this edge to source node's out edges
			// add this edge to target node's in edges
			// add to set of edges

		}
	
		// TODO for all free labels in metadata
		
	}
	
	public void addNode(Point p){
		// create a State corresponding to the point p
	}
	
	public void addEdge(Node n1, Node n2){
		
	}
	
	public void addEdgeAndNode(Node n1, Point p){
		
	}
	
	public void addLabelToNode(String text){
		// TODO Think about rendering the text as LaTeX.		
	}
		
	public void delete(Node n1){
		// delete all adjacent edges
		// remove n
	}
	
	public void delete(Edge e){
		// remove the edge from the list
		// don't remove the event
		// get n1 and n2, the source and target nodes for this edge
		// if e is the only edge adjacent to n1, delete n1
		// if e is the only edge adjacent to n2, delete n2
	}
}
