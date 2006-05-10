package ui;

import java.awt.Event;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;

import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;
import presentation.Glyph;
import presentation.GraphLabel;
import presentation.fsa.Edge;
import presentation.fsa.GraphElement;
import presentation.fsa.Node;

/**
 * Mediates between the Automaton model and the visual representation.
 * Observes and updates the Automaton.
 * Updates the graphical visualization metadata and synchronizes it with the Automaton model.
 * Is observed and updated by the GraphDrawingView.  
 * 
 * @author helen bretzke
 *
 */
public class GraphModel extends Publisher implements Subscriber {

	/**
	 * TODO implement as a more usable form of list or map.
	 */
	private HashMap<Long, Node> nodes;
	private HashMap<Long, Edge> edges;
	private HashMap<Long, GraphLabel> labels;
	
	/**
	 * The recursive structure used to draw the graph.
	 */
	private GraphElement graph;
	
	/**
	 * The data models to keep synchronized.
	 */	
	private Automaton fsa;		 // system model
	private MetaData layoutData; // presentation data for the system model
	
	public GraphModel(Automaton fsa, MetaData data){
		
		this.fsa = fsa;
		this.layoutData = data;
		
		nodes = new HashMap<Long, Node>();
		edges = new HashMap<Long, Edge>();
		labels = new HashMap<Long, GraphLabel>();
		
		update();
	}
	
	/**
	 * TODO Keep a set of dirty bits on the the Automaton
	 * so that the whole model needn't be rebuilt every time there is a change.
	 * 
	 * Although modifying the recursive graph structure will be trickier than simply rebuilding... 
	 */
	public void update(){
		
				
		// For now, just create everthing new.		
		// TODO OPTIMIZE How expensive is this?
		nodes.clear();
		edges.clear();
		labels.clear();
				
		// for all states in fsa, 
		// get the graphic data, 
		// construct a node and 
		// add to set of nodes		
		Iterator iter = fsa.getStateIterator();
		State s;
		Node n1;
		
		graph = new GraphElement();
		while(iter.hasNext()){
			s = (State)iter.next();
			n1 = new Node(s, layoutData.getLayoutData(s));			
			graph.insert(n1);
			nodes.put(new Long(s.getId()), n1);
		}

		// for all transitions in fsa
		// create all edges and connect to nodes
		iter = fsa.getTransitionIterator();
		Transition t;
		Node n2;
		Edge e;
		while(iter.hasNext()){						
			t = (Transition)iter.next();
		
			// get the source and target nodes
			n1 = nodes.get(new Long(t.getSource().getId()));
			n2 = nodes.get(new Long(t.getTarget().getId()));
			
			// get the graphic data for the transition and all associated events
			// construct the edge			
			e = new Edge(n1, n2, layoutData.getLayoutData(t));
					
			// add this edge to source node's out edges
			n1.insert(e);
			
			// DON'T add this edge to target node's in edges, since it doesn't store them :)
						
			// add to set of edges
			edges.put(new Long(t.getId()), e);
		}
	
		// TODO for all free labels in metadata				
		
		notifyAllSubscribers();
	}
	
	public void addNode(Point p){
		State state = null;
		// create a State corresponding to the point p
		fsa.add(state);
	}
	
	public void addEdge(Node n1, Node n2){
		Transition t = null;
		Event e = null;
		
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

	public GraphElement getGraph() {
		return graph;
	}

	public void setGraph(GraphElement graph) {
		this.graph = graph;
	}
}
