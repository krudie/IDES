package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import main.Hub;
import main.IDESWorkspace;
import model.Publisher;
import model.Subscriber;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;
import model.fsa.ver1.MetaData;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;
import presentation.Geometry;
import presentation.PresentationElement;
import services.latex.LatexManager;
import services.latex.LatexPrerenderer;
import util.BentoBox;

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
	private HashMap<Long, GraphLabel> freeLabels;
	private HashMap<Long, GraphLabel> edgeLabels;
	
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
		edgeLabels = new HashMap<Long, GraphLabel>();
		freeLabels = new HashMap<Long, GraphLabel>();
			
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
		edgeLabels.clear();
		freeLabels.clear();		
				
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
				metaData.addToLayout(t, (EdgeLayout)e.getLayout());
				e.addTransition(t);
				//e.update(); // TODO: CHANGE THIS SO JUST CALL graph.update() at end of this method.
			}else{
				// get the graphic data for the transition and all associated events
				// construct the edge			
				e = new Edge(t, n1, n2, metaData.getLayoutData(t));
				//e.update(); // TODO: CHANGE THIS SO JUST CALL graph.update() at end of this method.
				
				// add this edge to source node's out edges
				n1.insert(e);
				//n1.update(); // TODO: CHANGE THIS SO JUST CALL graph.update() at end of this method.
				
				// DON'T add this edge to target node's in edges, since it doesn't store them :)
				n2.insert(e);
				//n2.update();
				
				// add to set of edges
				// id may be misleading since it is the id of only the first transition on this edge
				edges.put(new Long(t.getId()), e);
			}
		}
	
		// collect all labels on edges				
		Entry entry;
		iter = edges.entrySet().iterator();
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			e = (Edge)entry.getValue();
			edgeLabels.put(e.getId(), e.getLabel());
		}
		
		// TODO for all free labels in metadata
		
		// tell observers that the model has been updated
		graph.update();
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
		Edge e = new Edge(layout, n1);
		layout.computeCurve(n1.getLayout(), n1.getLayout().getLocation());		
		n1.insert(e);
		return e;
	}
	
	public void abortEdge(Edge e){
		e.getSource().remove(e);		
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
		// FIXME only draw the edge if the point is outside the bounds of the source node
		if( ! e.getSource().intersects(p) ){
			layout.computeCurve(s, p);
			e.setVisible(true);
		}else{
			e.setVisible(false);
		}
	}
	
	/**
	 * Adds a new node at point <code>p</code> and completes the edge from 
	 * <code>e</code>'s source node to the new node.
	 *
	 * @param e the edge to be finished
	 * @param p the location of the new node
	 */
	public void finishEdgeAndAddNode(Edge e, Point2D.Float p){	
		if( ! e.getSource().intersects(p) ){
			finishEdge(e, addNode(p));
		}else{
			abortEdge(e);			
		}		
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
			PresentationElement el = (PresentationElement)children.next();
			try{
				saveMovement((Node)el);			
			}catch(ClassCastException cce){
				// Not a node; keep going
				// TODO what if it is a label??
				//saveMovement((GraphLabel)el);
			}
		}
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
		
	/**
	 * @param label
	 */
	private void saveMovement(GraphLabel label) {
		// update offset vector in EdgeLayout		
		if(label.getParent() != null){
			try{
				Edge edge = (Edge)label.getParent();
				EdgeLayout layout = (EdgeLayout)edge.getLayout();
				layout.setLabelOffset(Geometry.subtract(label.getLayout().getLocation(), layout.getLocation()));
				Iterator<FSATransition> t = edge.getTransitions();
				while(t.hasNext()){
					metaData.setLayoutData(t.next(), layout);
				}		
			}catch(ClassCastException cce){}			
		}else{ // TODO Move free label, tell MetaData
			
		}
	}

	protected void saveMovement(Node node){
		// save location of node to metadata
		State s = (State)fsa.getState(node.getId());
		metaData.setLayoutData(s, node.getLayout());
		// for all edges adjacent to node, save layout
		Iterator children = node.children();
		while(children.hasNext()){
			// FIXME not all children are Edges (GraphLabels)
			Edge e = (Edge)children.next();
			saveMovement(e);
		}
	}
	
	protected void saveMovement(Edge e){
		// for all transitions in e
		EdgeLayout layout = (EdgeLayout)e.getLayout();		
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

	public void setControllable(Event event, boolean b){
		// update the event
		event.setControllable(b);
		// notify subscribers
		fsa.notifyAllBut(this);
	}
	
	public void setObservable(Event event, boolean b){
		// update the event
		event.setObservable(b);
		// notify subscribers
		fsa.notifyAllBut(this);
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
	 * @param text an event symbol
	 */
	public void assignEvent(Event event, Edge edge){	
		
	}
	
	/**
	 * Assigns the set of events to <code>edge</code>, removes any events from edge
	 * that are not in the given list and commits any changes to the FSAModel.
	 * 
	 * @param events a non-null, non-empty list of FSA events
	 * @param edge the edge to which the edges will be assigned
	 */
	protected void replaceEventsOnEdge(Event[] events, Edge edge){

		// get the transitions for edge
		Iterator<FSATransition> trans = edge.getTransitions();
		FSATransition t;			
		// temp lists for adding or removing transitions since can't change
		// collection while iterating over it
		ArrayList<Transition> toAdd = new ArrayList<Transition>();
		ArrayList<Transition> toRemove = new ArrayList<Transition>();

		// reset the EdgeLayout's event labels
		while(trans.hasNext()){
			metaData.removeFromLayout(trans.next(), (EdgeLayout)edge.getLayout());
		}
		
		trans = edge.getTransitions();
		// Boundary case:  if there are no events on the edge
		// there has to be exactly one transition.
		if(events.length == 0){
			if(trans.hasNext()){
				t = trans.next();
				t.setEvent(null);
			}
			while(trans.hasNext()){
				toRemove.add((Transition)trans.next());
			}
		}				
		
		for(Event e : events){
			if(trans.hasNext()){
				 t = trans.next();			
				 t.setEvent(e);
			}else{ // more events than transitions
				// create a new transition
				toAdd.add(new Transition(maxTransitionId++, edge.getSource().getState(), edge.getTarget().getState(), e));
			}
		}
		
		// more transitions than events
		while(trans.hasNext()){			
			toRemove.add((Transition)trans.next());			
		}
		 
		// remove extra transitions	
		Iterator iter = toRemove.iterator();
		while(iter.hasNext()){
			t = (Transition)iter.next();		
			edge.removeTransition((Transition)t);						
			fsa.remove(t);
		}	
		
		// add transitions to accommodate added events
		iter = toAdd.iterator();
		while(iter.hasNext()){
			t = (Transition)iter.next();
			// add the transition to the edge
			edge.addTransition((Transition)t);		
			// add the transition to the FSA					
			fsa.add(t);
		}		
		
		// Update the event labels in the layout
		trans = edge.getTransitions();
		while(trans.hasNext()){
			t = trans.next();
			// add the transition data to the layout
			metaData.addToLayout(t, (EdgeLayout)edge.getLayout());	
			// set the layout data for the transition in metadata model
			metaData.setLayoutData(t, (EdgeLayout)edge.getLayout());
		}		
		
		// notify observers
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
	
	/**
	 * Stores the layout for the given edge for every transition represented
	 * by this edge.
	 * 
	 * @parasm edge
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
			freeLabels.remove(el);
		}
		notifyAllSubscribers();
	}
	
	private void delete(Node n){
		// delete all adjacent edges
		Iterator edges = n.adjacentEdges();
		while(edges.hasNext()){
			delete((Edge)edges.next());
		}
		// remove n		
		fsa.remove(n.getState());
		fsa.notifyAllBut(this);
		graph.remove(n);		
		nodes.remove(new Long(n.getId()));
		notifyAllSubscribers();		
	}
	
	private void delete(Edge e){
		Iterator<FSATransition> transitions = e.getTransitions();
		while(transitions.hasNext()){
			fsa.remove(transitions.next());
		}
		e.getSource().remove(e);
		e.getTarget().remove(e);
		edges.remove(e.getId());
		fsa.notifyAllBut(this);
		notifyAllSubscribers();
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
		iter = freeLabels.entrySet().iterator();
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
		
		////////////////////////////////////////////
		
		// Should this only be done when in ShowAllEdgeLabels mode or something?
		// It is handy to be able to select an edge by its label... hmmmm.
		GraphLabel gLabel;
		iter = edgeLabels.entrySet().iterator();
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			gLabel = (GraphLabel)entry.getValue();
			if(gLabel.intersects(p)){		
				return gLabel;				
			}
		}
		
		////////////////////////////////////////////
		
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
		iter = freeLabels.entrySet().iterator();
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
	public Collection<GraphLabel> getFreeLabels()
	{
		return freeLabels.values();
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
	
	/**
	 * @return event id which is not in use
	 */
	public long getFreeEventId()
	{
		return maxEventId++;
	}

	/**
	 * @param symbol
	 * @param b
	 * @param c
	 * @return
	 */
	public Event createEvent(String symbol, boolean controllable, boolean observable) {
		Event event=new Event(getFreeEventId());
		event.setSymbol(symbol);
		event.setControllable(controllable);
		event.setObservable(observable);
		fsa.add(event);
		fsa.notifyAllBut(this);		
		return event;
	}
	
	/**
	 * This method is reponsible for calculcating the size of the 
	 * bounding box necessary for the entire graph.  It goes
	 * through every node, edge and label and uses the union of
	 * their bounds to create the box.
	 * 
	 * @param boolean Whether you want the box to begin at (0, 0) 
	 *                (true) or tightly bound around the graph (false) 
	 * @return Rectangle The bounding box for the graph
	 * 
	 * @author Sarah-Jane Whittaker
	 * @author Lenko Grigorov
	 */
	public Rectangle getBounds(boolean initAtZeroZero)
	{
		Rectangle graphBounds = initAtZeroZero ? 
			new Rectangle() : getElementBounds();

		FSAState nodeState = null;
		
		// Start with the nodes
		for (Node graphNode : nodes.values())
		{
			// If the node is initial, take into account the initial
			// arrow
			nodeState = graphNode.getState();
			if (nodeState.isInitial())
			{		
				graphBounds = graphBounds.union(
					graphNode.getInitialArrowBounds());
			}

			graphBounds = graphBounds.union(graphNode.getSquareBounds());
		}

		for (Edge graphEdge : edges.values())
		{
			graphBounds = graphBounds.union(graphEdge.getCurveBounds());
		}
		
		for (GraphLabel edgeLabel : edgeLabels.values())
		{
			graphBounds = graphBounds.union(edgeLabel.bounds());
		}

		for (GraphLabel freeLabel : freeLabels.values())
		{
			graphBounds = graphBounds.union(freeLabel.bounds());
		}
		
		return graphBounds;
	}
	
	/**
	 * TODO: Comment!
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	private Rectangle getElementBounds()
	{
		for (Node graphNode : nodes.values())
		{
			return graphNode.getSquareBounds();
		}

		for (Edge graphEdge : edges.values())
		{
			return graphEdge.getCurveBounds();
		}

		for (GraphLabel freeLabel : freeLabels.values())
		{
			return freeLabel.bounds();
		}
		
		return new Rectangle();
	}
	
	/**
	 * A mid-version of getBounds()
	 * 
	 * @return the smallest rectangle that containing all elements of the graph
	 * 
	 * @author Lenko Grigorov
	 */
	/*
	public Rectangle getBounds()
	{
		Rectangle r=new Rectangle();

		for (Node n: nodes.values())
		{
			if (n.getState().isInitial())
				r=r.union(n.getInitialArrowBounds());
			int radius = (int)n.getLayout().getRadius();
			r=r.union(new Rectangle((int)n.getLayout().getLocation().x - radius,
					(int)n.getLayout().getLocation().y - radius,
					2*radius,
					2*radius));
		}

		for (Edge e:edges.values())
		{
			r=r.union(e.getCurveBounds());
			r=r.union(e.getLabel().bounds());
		}

		for (GraphLabel l:freeLabels.values())
			r=r.union(l.bounds());
		
		return r;
	}
	*/
}


