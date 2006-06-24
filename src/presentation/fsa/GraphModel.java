package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import main.Hub;
import main.IDESWorkspace;
import model.Publisher;
import model.Subscriber;
import model.fsa.FSATransition;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;
import model.fsa.ver1.MetaData;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;
import presentation.PresentationElement;
import services.latex.LatexManager;
import services.latex.LatexPrerenderer;

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
	private MetaData metaData; // presentation data for the system model
	
	private long maxStateId, maxEventId, maxTransitionId;
	
	public GraphModel(Automaton fsa, MetaData data){
		
		this.fsa = fsa;
		fsa.attach(this);
		
		this.metaData = data;
		
		nodes = new HashMap<Long, Node>();
		edges = new HashMap<Long, Edge>();
		labels = new HashMap<Long, GraphLabel>();
	
		maxStateId = fsa.getStateCount();
		maxTransitionId = fsa.getTransitionCount();
		maxEventId = fsa.getEventCount();
		
		update();
	}
	
	public String getName(){
		return fsa.getName();
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
			n1 = new Node(s, metaData.getLayoutData(s));
			n1.update();  // TODO: CHANGE THIS SO JUST CALL graph.update() at end of this method.
			graph.insert(n1);
			nodes.put(new Long(s.getId()), n1);
		}

		// for all transitions in fsa
		// create all edges and connect to nodes
		// create a single edge for aggregate of all transitions from same start and end state
		// add events to collection for that edge.
		iter = fsa.getTransitionIterator();
		Transition t;
		Node n2;
		Edge e;
		while(iter.hasNext()){						
			t = (Transition)iter.next();
		
			// get the source and target nodes
			n1 = nodes.get(new Long(t.getSource().getId()));
			n2 = nodes.get(new Long(t.getTarget().getId()));
			
			// if the edge corresponding to t already exists,
			// add t to the edge's set of transitions
			e = edgeBetween(n1, n2); 
			if(e != null){
				metaData.augmentLayoutData(t, (EdgeLayout)e.getLayout());
				e.addTransition(t);
				e.update(); // TODO: CHANGE THIS SO JUST CALL graph.update() at end of this method.
			}else{
				// get the graphic data for the transition and all associated events
				// construct the edge			
				e = new Edge(t, n1, n2, metaData.getLayoutData(t));
				e.update(); // TODO: CHANGE THIS SO JUST CALL graph.update() at end of this method.
				
				// add this edge to source node's out edges
				n1.insert(e);
				n1.update(); // TODO: CHANGE THIS SO JUST CALL graph.update() at end of this method.
				
				// DON'T add this edge to target node's in edges, since it doesn't store them :)
				n2.insert(e);
				n2.update();
				
				// add to set of edges
				// id may be misleading since it is the id of only the first transition on this edge
				edges.put(new Long(t.getId()), e);
			}
		}
	
		// TODO for all free labels in metadata
		
		// tell observers that the model has been updated
		// graph.update();
		notifyAllSubscribers();
	}
	
	/**
	 * Returns the edge from <code>source</code> to <code>target</code> if exists.
	 * Otherwise returns null.
	 */
	private Edge edgeBetween(Node source, Node target){
		Edge e;
		Iterator i = edges.entrySet().iterator();
		while(i.hasNext()){
			e = (Edge)((Entry)i.next()).getValue();
			if(e.getSource().equals(source) && e.getTarget().equals(target)){
				return e;
			}
		}		
		return null;
	}
	
	/**
	 * Creates a new node with centre at the given point
	 * and a adds a new state to the automaton.
	 * 
	 * @param p the centre point for the new node
	 * @return the node added
	 */
	public Node addNode(Point2D.Float p){
		State s = new State(maxStateId++);
		s.setInitial(false);
		s.setMarked(false);
		NodeLayout layout = new NodeLayout(p);			
		metaData.setLayoutData(s, layout);
		fsa.add(s);
		fsa.notifyAllBut(this);
		Node n = new Node(s, layout);	
		nodes.put(new Long(s.getId()), n);
		graph.insert(n);
		this.notifyAllSubscribers();
		return n;
	}	
	
	/**
	 * Creates and returns an Edge with source node <code>n1</code>, 
	 * undefined target node, and terminating at the centre of node <code>n1</code>.
	 * 
	 * FIXME should the target point be something more sensible?
	 * 
	 * @param n1
	 * @return a new Edge with source node n1
	 */
	public Edge beginEdge(Node n1){
		EdgeLayout layout = new EdgeLayout();
		layout.computeCurve(n1.getLayout(), n1.getLayout().getLocation());
		Edge e = new Edge(layout, n1);
		n1.insert(e);
		return e;
	}
	
	/**
	 * Updates the layout for the given edge so it extends to the given target point.
	 * 
	 * @param e the Edge to be updated
	 * @param p the target point
	 */
	public void updateEdge(Edge e, Point2D.Float p){
		EdgeLayout layout = (EdgeLayout)e.getLayout();
		NodeLayout s = e.getSource().getLayout();
		layout.computeCurve(s, p);		
	}
	
	/**
	 * Adds a new node at point <code>p</code> and completes the edge from 
	 * <code>e</code>'s source node to the new node.
	 *
	 * @param e the edge to be finished
	 * @param p the location of the new node
	 */
	public void finishEdgeAndAddNode(Edge e, Point2D.Float p){		
		finishEdge(e, addNode(p));
	}
	
	 /**
	  * Updates the given edge from node <code>n1</code> to node <code>n2</code>.
	  * and a adds a new transition to the automaton.
	  * 
	  * @param n1 
	  * @param n2	
	  */
	public void finishEdge(Edge e, Node n2){
		e.setTarget(n2);
		n2.insert(e);		
		((EdgeLayout)e.getLayout()).computeCurve(e.getSource().getLayout(), e.getTarget().getLayout());		
		Transition t = new Transition(maxTransitionId++, fsa.getState(e.getSource().getId()), fsa.getState(n2.getId()));
		metaData.setLayoutData(t, (EdgeLayout)e.getLayout());
		fsa.add(t);
		fsa.notifyAllBut(this);
		edges.put(new Long(t.getId()), e);
		notifyAllSubscribers();
	}	
	
	/** 
	 * Creates a new edge from node <code>n1</code> to node <code>n2</code>.
	 * and a adds a new transition to the automaton.
	 * 
	 * @param n1 source node 
	 * @param n2 target node
	 */
	public void addEdge(Node n1, Node n2){
		Transition t = new Transition(maxTransitionId++, fsa.getState(n1.getId()), fsa.getState(n2.getId()));
		// computes layout of new edges (default to straight edge between pair of nodes)
		EdgeLayout layout = new EdgeLayout(n1.getLayout(), n2.getLayout());				
		metaData.setLayoutData(t, layout);
		fsa.add(t);
		fsa.notifyAllBut(this);
		Edge e = new Edge(t, n1, n2, layout);		
		n1.insert(e);
		n2.insert(e);
		edges.put(new Long(t.getId()), e);		
		notifyAllSubscribers();
	}

	public void saveMovement(SelectionGroup selection){
		Iterator children = selection.children();
		while(children.hasNext()){
			try{
				saveMovement((Node)children.next());			
			}catch(ClassCastException cce){
				// Not a node; keep going
				// TODO what if it is a label??
			}
		}
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
		
	protected void saveMovement(Node node){
		// save location of node to metadata
		State s = (State)fsa.getState(node.getId());
		metaData.setLayoutData(s, node.getLayout());
		// for all edges adjacent to node, save layout
		Iterator children = node.children();
		while(children.hasNext()){
			Edge e = (Edge)children.next();
			saveMovement(e);
		}
	}
	
	protected void saveMovement(Edge e){
		// for all transitions in e
		EdgeLayout layout = (EdgeLayout)e.getLayout();
		layout.update();
		Iterator<FSATransition> t = e.getTransitions();
		while(t.hasNext()){
			metaData.setLayoutData(t.next(), layout);
		}		
	}
	
	/**
	 * Precondition: <code>n</code> and <code>text</code> are not null
	 * 
	 * @param n the node to be labelled
	 * @param text the name for the node
	 */
	public void labelNode(Node n, String text){		
		State s = (State)fsa.getState(n.getId());
		
		// TODO set a dirty bit in layout object and only call update before drawing
		// if bit is set
		n.getLayout().setText(text);
		n.update();
		metaData.setLayoutData(s, n.getLayout());
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
		
	public void setInitial(Node n, boolean b){
		// update the state
		((State)n.getState()).setInitial(b);
		// add an arrow to the node layout
		NodeLayout layout = n.getLayout();
		if(b){
			// TODO compute best position for arrow
			layout.setArrow(new Point2D.Float(1,0));
		}else{			
			layout.setArrow(null);
		}
		// notify subscribers
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
	
	public void setMarked(Node n, boolean b){
		// update the state
		((State)n.getState()).setMarked(b);		
		// update the node
		n.update();
		// notify subscribers
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
	
	/**
	 * @param node
	 * @param arg0
	 */
	public void setSelfLoop(Node node, boolean b) {
		Edge selfLoop = edgeBetween(node, node);
		if(!b && selfLoop != null){			
			delete(selfLoop);		
		}
		// if b and node doesn't have a self loop
		if(b && selfLoop == null){
			// add the edge
			addEdge(node, node);
		}		
	}

	/**
	 * The following steps should be done by the text tool in the context 
	 * of labelling an edge.
	 * 
	 * If <code>text</code> corresponds to an event in the local alphabet find the event.
	 * If <code>text</code> corresponds to an event in the global alphabet find the event, 
	 * add it to the local alphabet.
	 * Otherwise, create a new event and add it to both alphabets. 
	 * 
	 * For now: 
	 * Open the EdgeLabellingDialog and then
	 * Creates a new transition, assigns the event with symbol corresponding to 
	 * <code>text</code> to the transition and adds the transition to the given edge.
	 * 
	 * @param text an event symbol
	 */
	public void assignEvent(Event event, Edge edge){
		
	}
	
	/**
	 * Assigns the set of events to <code>edge</code> and commits any
	 * changes to the FSAModel.
	 * 
	 * @param events
	 * @param edge
	 */
	public void assignEvents(Event[] events, Edge edge){
		for(Event e : events){
			assignEvent(e, edge);
		}
	}
	
	/**
	 * Stores the layout for the given edge for every transition represented
	 * by this edge.
	 * 
	 * FIXME changes undone when adjacent node is moved.
	 * 
	 * @param edge
	 */
	public void commitEdgeLayout(Edge edge){
		saveMovement(edge);
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
	
	public void delete(GraphElement el){
		// KLUGE This is worse (less efficient) than using instance of ...
		if(nodes.containsValue(el)){
			delete((Node)el);			
		}else if(edges.containsValue(el)){
			delete((Edge)el);
		}else{
			labels.remove(el);
		}
		notifyAllSubscribers();
	}
	
	private void delete(Node n1){
		// delete all adjacent edges
		// remove n
		// DEBUG
		Hub.displayAlert("GraphModel: Implement delete(Node) ASAP");
	}
	
	private void delete(Edge e){
		Iterator<FSATransition> transitions = e.getTransitions();
		while(transitions.hasNext()){
			fsa.remove(transitions.next());
		}
		e.getSource().remove(e);
		e.getTarget().remove(e);
		edges.remove(e);
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}

	public GraphElement getGraph() {
		return graph;
	}

	public void setGraph(GraphElement graph) {
		this.graph = graph;
	}

	/**
	 * Computes and returns the set of graph elements contained by the given rectangle.
	 * 
	 * @param rectangle
	 * @return the set of graph elements contained by the given rectangle
	 */
	protected SelectionGroup getElementsContainedBy(Rectangle rectangle) {
		SelectionGroup g = new SelectionGroup();
		
		// check intersection with all nodes		
		Iterator iter = nodes.entrySet().iterator();
		Entry entry;
		Node n;
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			n = (Node)entry.getValue();
			if(rectangle.contains(n.bounds()) ){ // TODO && do a more thorough intersection test
				g.insert(n);
				//n.setSelected(true);
			}
		}
		
		// check for intersection with edges
		iter = edges.entrySet().iterator();
		Edge e;
		while(iter.hasNext()){
			entry = (Entry)iter.next();
			e = (Edge)entry.getValue();
			if(rectangle.contains(e.bounds())){ // TODO && do a more thorough intersection test
				g.insert(e);
				//e.setSelected(true);
			}
		}
		
		// check for intersection with free labels 
		iter = labels.entrySet().iterator();
		GraphLabel l;
		while(iter.hasNext()){
			entry = (Entry)iter.next();
			l = (GraphLabel)entry.getValue();
			if(rectangle.contains(l.bounds())){
				g.insert(l);
				//l.setSelected(true);
			}
		}
		
		return g;
	}
	
	/**
	 * Computes and returns the graph element intersected by the given point.
	 * If nothing hit, returns null. 
	 * 
	 * @param p the point of intersection
	 * @return the graph element intersected by the given point or null if nothing hit.
	 */
	protected GraphElement getElementIntersectedBy(Point2D p){
		// check intersection with all nodes		
		Iterator iter = nodes.entrySet().iterator();
		Entry entry;
		Node n;
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			n = (Node)entry.getValue();
			if(n.intersects(p)){				
				return n;				
			}
		}
		
		Edge e;
		// check for intersection with edges
		iter = edges.entrySet().iterator();
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			e = (Edge)entry.getValue();
			if(e.intersects(p)){		
				return e;				
			}
		}
		
		GraphLabel l;
		// check for intersection with free labels
		iter = labels.entrySet().iterator();
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			l = (GraphLabel)entry.getValue();
			if(l.intersects(p)){				
				return l;				
			}
		}		
		// no intersection
		return null;
	}
	
	/**
	 * Returns the set of all nodes in the graph.
	 * @return the set of all nodes in the graph
	 */
	public Collection<Node> getNodes()
	{
		return nodes.values();
	}
	
	/**
	 * Returns the set of all edges in the graph.
	 * @return the set of all edges in the graph
	 */
	public Collection<Edge> getEdges()
	{
		return edges.values();
	}
	
	/**
	 * Returns the set of all free labels in the graph.
	 * @return the set of all free labels in the graph
	 */
	public Collection<GraphLabel> getLabels()
	{
		return labels.values();
	}
	
	/**
	 * @deprecated
	 * Creates a node at point <code>p</code> and an edge from the given source node
	 * to the new node.
	 * 
	 * @param source
	 * @param p
	 */
	public void addEdgeAndNode(Node source, Point2D.Float p){		
		addEdge(source, addNode(p));
	}

	public void addGraphLabel(GraphLabel label, String text) {
		// TODO Auto-generated method stub
		
	}
}
