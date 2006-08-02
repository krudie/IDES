package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import observer.FSMGraphMessage;
import observer.FSMGraphSubscriber;
import observer.FSMMessage;
import observer.FSMSubscriber;
import observer.Publisher;
import observer.Subscriber;

import main.Hub;
import main.IDESWorkspace;
import model.fsa.FSAEvent;
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
public class FSMGraph extends Publisher implements Subscriber, FSMSubscriber {

	
	protected class UniformRadius extends HashMap<NodeLayout,Float>
	{
		protected float r = NodeLayout.DEFAULT_RADIUS;
		protected void updateUniformRadius(NodeLayout n, float radius)
		{
			put(n,new Float(radius));
			updateUniformRadius();
		}
		protected void updateUniformRadius()
		{
			if(size()>0)
				r=Float.MIN_VALUE;
			else
				r=NodeLayout.DEFAULT_RADIUS;
			for(Float ff:values())
			{
				float f=ff.floatValue();
				if(f>r)
					r=f;
			}
		}
		public float getRadius()
		{
			return r;
		}
	}
	protected UniformRadius uniformR=new UniformRadius();
		
	/**
	 * Maps used in intersection searches
	 * TODO replace with Quadtree data structure
	 */
	private HashMap<Long, Node> nodes;
	private HashMap<Long, Edge> edges;
	private HashMap<Point2D.Float, GraphLabel> freeLabels; // use location as key
	private HashMap<Long, GraphLabel> edgeLabels; // use parent edge's id as key
	
	/**
	 * The recursive structure used to draw the graph.
	 * TODO remove after this class extends GraphElement
	 */
	private GraphElement graph;
	
	protected boolean dirty=false;
	
	/**
	 * The data models to keep synchronized.
	 */	
	private Automaton fsa;	   // system model
	
	// TODO remove after testing graph extraction by LayoutDataParser
	private MetaData metaData; // presentation data for the system model	
	
	/**
	 * TODO replace MetaData with either a reference to the graph
	 * built by LayoutDataParser or a SubElementContainer of the parsed XML
	 * and build the graph here.
	 * 
	 * NOTE: Need max element ids and a
	 * better data structure for rapid intersection search...
	 *  
	 * @param fsa
	 * @param data
	 */
	public FSMGraph(Automaton fsa, MetaData data){
		
		this.fsa = fsa;
		fsa.addSubscriber(this);
		
		this.metaData = data;	
			
		initializeGraph();
		
		setDirty(false);
	}
	
	/**
	 * 
	 * @param fsa the mathematical model
	 * @param graph the graphical layout structure
	 */
	public FSMGraph(Automaton fsa, GraphElement graph)
	{
//		 TODO set this.children = graph.children
		this.graph = graph; 
		this.fsa = fsa;
		fsa.addSubscriber(this);		
		buildIntersectionDS();
	}
	
	/**
	 * TODO Implement this as a quad tree.
	 * KLUGE just uses a bunch of maps to store each element type.
	 *
	 */
	private void buildIntersectionDS()
	{
		nodes = new HashMap<Long, Node>();
		edges = new HashMap<Long, Edge>();
		edgeLabels = new HashMap<Long, GraphLabel>();
		freeLabels = new HashMap<Point2D.Float, GraphLabel>();
	
		// TODO change this to this.children()
		Iterator children = graph.children();
		
		// children can be Nodes or FreeLabels
		// edges are children of Nodes (as are labels but we don't compute explicit intersection with them)
		// edge labels are children of edges
		GraphElement el;
		while(children.hasNext())
		{
			el = (GraphElement)children.next();
			Node n;
			
			if(el instanceof Node)
			{	
				n = (Node)el;
				nodes.put(new Long(n.getId()), n);
						
				Iterator nodeChildren = n.children();
				while(nodeChildren.hasNext())
				{
					el = (GraphElement)nodeChildren.next();
					if(el instanceof Edge)
					{
						Edge edge = (Edge)el;
						edges.put(new Long(edge.getId()), edge);
						edgeLabels.put(edge.getId(), edge.getLabel());
					}					
				}
				
			}else if(el instanceof GraphLabel)
			{
				GraphLabel label = (GraphLabel)el;
				freeLabels.put(label.getLocation(), label);
			}
		}
	}
	
	public String getName()
	{
		return fsa.getName();
	}
	
	public String getDecoratedName()
	{
		return (isDirty()?"* ":"")+getName();
	}
	
	public Automaton getAutomaton()
	{
		return fsa;
	}
	
	public GraphElement getGraph() {
		return graph;
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
	
	public void update(){			
		// notifyAllSubscribers();
	}
	
//	public void setGraph(GraphElement graph) {
//		this.graph = graph;
//		setDirty(true);
//		notifyAllSubscribers();
//	}

	
	/**
	 * TODO 
	 * Graph to be built in LayoutDataParser.
	 * Replace the intersection lists with a quadtree.
	 */
	private void initializeGraph(){
				
		nodes = new HashMap<Long, Node>();
		edges = new HashMap<Long, Edge>();
		edgeLabels = new HashMap<Long, GraphLabel>();
		freeLabels = new HashMap<Point2D.Float, GraphLabel>();
		
//		for(Node n:nodes.values())
//			((NodeLayout)n.getLayout()).dispose();
//		
//		nodes.clear();
//		edges.clear();
//		edgeLabels.clear();
//		freeLabels.clear();		
		
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
			NodeLayout nL=metaData.getLayoutData(s);
			nL.setUniformRadius(uniformR);
			n1 = new Node(s, nL);			
			graph.insert(n1);
			nodes.put(new Long(s.getId()), n1);
//			maxStateId = maxStateId < s.getId() ? s.getId() : maxStateId;
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
			e = directEdgeBetween(n1, n2); 
			if(e != null){								
//				Event event = (Event) t.getEvent();
//				if(event != null){			
//					e.addEventName(event.getSymbol());
//				}				
				e.addTransition(t);			
			}else{
				// get the graphic data for the transition and all associated events
				// construct the edge				
				e = new BezierEdge(metaData.getLayoutData(t), n1, n2, t);			
				
				// add this edge to source and target nodes' children
				n1.insert(e);				
				n2.insert(e);
				
				// add to set of edges
				// id may be misleading since it is the id of only the first transition on this edge
				edges.put(new Long(t.getId()), e);
			}
//			maxTransitionId = maxTransitionId < t.getId() ? t.getId() : maxTransitionId;
//			FSAEvent event = t.getEvent();
//			if(event != null){
//				maxEventId = maxEventId < event.getId() ? event.getId() : maxEventId;
//			}
		}
	
		// collect all labels on edges				
		for(Edge edge : edges.values())
		{
			edgeLabels.put(edge.getId(), edge.getLabel());
		}
		
		// TODO for all free labels in layout data structure
		
		// clear all dirty bits in the graph structure		
		graph.refresh();
	}

	/**
	 * @deprecated
	 * Graph is now built in LayoutDataParser
	 * 
	 * Returns the directed edge from <code>source</code> to <code>target</code> if exists.
	 * Otherwise returns null.
	 */
	private Edge directEdgeBetween(Node source, Node target){		
		for(Edge e : edges.values())
		{			
			if(e.getSource().equals(source) && e.getTarget().equals(target)){
				return e;
			}
		}		
		return null;
	}
	
	
	/**
	 * @deprecated 
	 * TODO wait until automaton is saved before committing
	 * layout changes. 
	 * 
	 * @param t
	 * @param layout
	 */
	private void addToLayout(FSATransition t, Edge edge) {
		Event event = (Event) t.getEvent();
		if(event != null){			
			edge.addEventName(event.getSymbol());
		}						
	}

	////////////////////////////////////////////////////////////////////////////
	// TODO Move this block of code into GraphDrawingView
	//
	/**
	 * Creates and returns an Edge with source node <code>n1</code>, 
	 * undefined target node, and terminating at the centre of node <code>n1</code>.
	 * 
	 * FIXME should the target point be something more sensible?
	 * 
	 * @param n1
	 * @return a new Edge with source node n1
	 */
//	public BezierEdge beginEdge(Node n1){
//		BezierLayout layout = new BezierLayout();
//		BezierEdge e = new BezierEdge(layout, n1);
//		layout.computeCurve(n1.getLayout(), n1.getLayout().getLocation());		
//		n1.insert(e);
//		return e;
//	}
//	
//	public void abortEdge(BezierEdge e){
//		e.getSource().remove(e);		
//	}
//	
//	/**
//	 * Updates the layout for the given edge so it extends to the given target point.
//	 * 
//	 * @param e the Edge to be updated
//	 * @param p the target point
//	 */
//	public void updateEdge(BezierEdge e, Point2D.Float p){		
//		NodeLayout s = e.getSource().getLayout();
//		// only draw the edge if the point is outside the bounds of the source node
//		if( ! e.getSource().intersects(p) ){
//			e.computeCurve(s, p);
//			e.setVisible(true);
//		}else{
//			e.setVisible(false);
//		}
//	}
	
	/**
	 * Adds a new node at point <code>p</code> and completes the edge from 
	 * <code>e</code>'s source node to the new node.
	 *
	 * @param e the edge to be finished
	 * @param p the location of the new node
	 */
	public void finishEdgeAndCreateTargetNode(BezierEdge e, Point2D.Float p){	
		if( ! e.getSource().intersects(p) ){
			finishEdge(e, addNode(p));
			//return true;
		}else{
			// FIXME dispose of temp edge and return a boolean success indicator
			// return false;
			// abortEdge(e);			
		}		
	}
	
	 /**
	  * Updates the given edge from node <code>n1</code> to node <code>n2</code>.
	  * and a adds a new transition to the automaton.
	  * 
	  * @param n1 
	  * @param n2	
	  */
	public void finishEdge(BezierEdge e, Node n2){
		
		// FIXME Distribute multiple directed edges between same node pair. 
		
		//if( directEdgeBetween(e.getSource(), n2) == null){
		
		e.setTarget(n2);			
			
			BezierEdge opposite = (BezierEdge)directEdgeBetween(n2, e.getSource()); 
			if(opposite != null && opposite.isStraight()){
				e.arcAway(opposite);
				opposite.getLayout().computeCurve();
				saveMovement(opposite);		
			}
			
			e.computeCurve(e.getSource().getLayout(), e.getTarget().getLayout());		
			
			Transition t = new Transition(fsa.getFreeTransitionId(), fsa.getState(e.getSource().getId()), fsa.getState(n2.getId()));
			metaData.setLayoutData(t, e.getLayout());
			e.addTransition(t);
			
			// Note must assign transition to edge before inserting as children to end nodes.
			e.getSource().insert(e);	
			n2.insert(e);		
			
			fsa.add(t);
			fsa.notifyAllBut(this);
			edges.put(e.getId(), e);
			edgeLabels.put(e.getId(), e.getLabel());
			setDirty(true);
			notifyAllSubscribers();
//		}else{ // duplicate edge
//			abortEdge(e);
//		}
	}	
	
	/////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a new node with centre at the given point
	 * and a adds a new state to the automaton.
	 * 
	 * @param p the centre point for the new node
	 * @return the node added
	 */
	public Node addNode(Point2D.Float p){
		State s = new State(fsa.getFreeStateId());
		s.setInitial(false);
		s.setMarked(false);
		NodeLayout layout = new NodeLayout(uniformR,p);			
		metaData.setLayoutData(s, layout);
		fsa.add(s);
		fsa.notifyAllBut(this);
		Node n = new Node(s, layout);	
		nodes.put(new Long(s.getId()), n);
		graph.insert(n);
		setDirty(true);
		this.notifyAllSubscribers();
		return n;
	}

	/** 
	 * Creates a new edge from node <code>n1</code> to node <code>n2</code>.
	 * and a adds a new transition to the automaton.
	 * 
	 * @param n1 source node 
	 * @param n2 target node
	 */
	public void addEdge(Node n1, Node n2){
		Transition t = new Transition(fsa.getFreeTransitionId(), fsa.getState(n1.getId()), fsa.getState(n2.getId()));
		// computes layout of new edges (default to straight edge between pair of nodes)
		BezierLayout layout = new BezierLayout(n1.getLayout(), n2.getLayout());				
		metaData.setLayoutData(t, layout);
		fsa.add(t);
		fsa.notifyAllBut(this);
		BezierEdge e = new BezierEdge(layout, n1, n2, t);		
		n1.insert(e);
		n2.insert(e);
		edges.put(e.getId(), e);		
		edgeLabels.put(e.getId(), e.getLabel());
		setDirty(true);
		notifyAllSubscribers();
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

	public void saveMovement(PresentationElement selection){
		Iterator children = selection.children();
		while(children.hasNext()){
			PresentationElement el = (PresentationElement)children.next();
			if(edgeLabels.containsValue(el)){
				saveMovement((GraphLabel)el);
			}else if(nodes.containsValue(el)){
				saveMovement((Node)el);
			}else if(edges.containsValue(el)){
				if( ((BezierEdge)el).isSelfLoop() ){					
					saveMovement((BezierEdge)el);
				}
			}else if(freeLabels.containsValue(el)){
				// TODO move free labels
			}
		}

		setDirty(true);

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
				BezierEdge edge = (BezierEdge)label.getParent();
				BezierLayout layout = edge.getLayout();
				//layout.setLabelOffset(Geometry.subtract(label.getLayout().getLocation(), layout.getLocation()));
				Iterator<FSATransition> t = edge.getTransitions();
				while(t.hasNext()){
					metaData.setLayoutData(t.next(), layout);
				}
			}catch(ClassCastException cce){}			
		}else{ // TODO Move free label, tell MetaData
			
		}
	}

	private void saveMovement(Node node){
		// save location of node to metadata
		State s = (State)fsa.getState(node.getId());
		metaData.setLayoutData(s, node.getLayout());
		// for all edges adjacent to node, save layout
		Iterator<BezierEdge> adjEdges = node.adjacentEdges();
		while(adjEdges.hasNext()){			
			saveMovement((BezierEdge)adjEdges.next());
		}
	}
	
	private void saveMovement(BezierEdge e){
		// for all transitions in e		
		BezierLayout layout = e.getLayout();		
		Iterator<FSATransition> t = e.getTransitions();
		while(t.hasNext()){
			metaData.setLayoutData(t.next(), layout);
		}		
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
		metaData.setLayoutData((State)n.getState(), layout);
		setDirty(true);
		// notify subscribers
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
	
	public void setMarked(Node n, boolean b){
		// update the state
		((State)n.getState()).setMarked(b);		
		// update the node
		n.refresh();
		setDirty(true);
		// notify subscribers
		fsa.notifyAllBut(this);
		this.notifyAllSubscribers();
	}
	
	/**
	 * FIXME Change to add self-loop, so can have multiples. 
	 * 
	 * @param node
	 * @param arg0
	 */
	public void setSelfLoop(Node node, boolean b) {
		Edge selfLoop = directEdgeBetween(node, node);
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
			metaData.removeFromLayout(trans.next(), (BezierLayout)edge.getLayout());
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
				toAdd.add(new Transition(fsa.getFreeTransitionId(), edge.getSource().getState(), edge.getTarget().getState(), e));
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
			addToLayout(t, edge);	
			// set the layout data for the transition in metadata model
			metaData.setLayoutData(t, (BezierLayout)edge.getLayout());
		}		
		
		setDirty(true);

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
		//fsa.notifyAllSubscribers();
		this.notifyAllSubscribers();
		fsa.notifyAllBut(this);		
		setDirty(true);
	}
	
	/**
	 * TODO After extend GraphElement, go through this interface to build
	 * graph in LayoutDataParser.
	 * 
	 * ??? What about adding edges to nodes ???
	 * 
	 * @param n
	 */
//	public void insert(Node n)
//	{
//		// TODO super.insert(n, new Long(n.getId()));
//		nodes.put(new Long(n.getId()), n);
//		fireFSMGraphChanged(new FSMGraphMessage(this, FSMGraphMessage.ADD, FSMGraphMessage.NODE, n.getId(), n.bounds()));
//	}
	
	public void remove(GraphElement el){
		// KLUGE This is worse (less efficient) than using instance of ...
		if(nodes.containsValue(el)){			
			delete((Node)el);			
		}else if(edges.containsValue(el)){
			delete((BezierEdge)el);
		}else{
			freeLabels.remove(el);
		}
		setDirty(true);
		notifyAllSubscribers();
	}
	
	private void delete(Node n){
		// delete all adjacent edges
		Iterator edges = n.adjacentEdges();
		while(edges.hasNext()){
			delete((BezierEdge)edges.next());
		}
		// remove n		
		fsa.remove(n.getState());
		fsa.notifyAllBut(this);
		graph.remove(n);	// FIXME fails
		((NodeLayout)n.getLayout()).dispose();
		nodes.remove(new Long(n.getId()));  // Succeeds
		setDirty(true);
		notifyAllSubscribers();		
	}
	
	private void delete(Edge e){
		Iterator<FSATransition> transitions = e.getTransitions();
		while(transitions.hasNext()){
			fsa.remove(transitions.next());
		}
		e.getSource().remove(e);
		e.getTarget().remove(e);
		edgeLabels.remove(e.getId());
		edges.remove(e.getId());
		setDirty(true);
		fsa.notifyAllBut(this);
		notifyAllSubscribers();
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
		metaData.setLayoutData(s, n.getLayout());
		setDirty(true);
		//update();
		//fsa.notifyAllBut(this);  // mathematical model has not changed
		notifyAllSubscribers();
	}

	public void addFreeLabel(String text, Point2D.Float p) {		
		GraphLabel label = new GraphLabel(text, p);
		freeLabels.put(p, label);
		graph.insert(label);
		this.notifyAllSubscribers();
		setDirty(true);
	}
	
	

	/**
	 * @param symbol
	 * @param b
	 * @param c
	 * @return
	 */
	public Event createEvent(String symbol, boolean controllable, boolean observable) {
		Event event=new Event(fsa.getFreeEventId());
		event.setSymbol(symbol);
		event.setControllable(controllable);
		event.setObservable(observable);
		fsa.add(event);
		fsa.notifyAllBut(this);		
		setDirty(true);
		notifyAllSubscribers();
		return event;
	}
	
	public void removeEvent(Event event)
	{
		// remove event from all edges that may have transitions holding
		// references to it.
		Entry entry;
		BezierEdge edge;
		FSATransition toRemove = null;
		Iterator iter = edges.entrySet().iterator();
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			edge = (BezierEdge)entry.getValue();
			Iterator<FSATransition> trans = edge.getTransitions();
			while(trans.hasNext()){
				FSATransition t = trans.next();
				FSAEvent e = t.getEvent();
				if(e != null && e.equals(event)){
					// remove e and possibly t from edge
					t.setEvent(null);
					toRemove = t;
				}
			}
			if(edge.transitionCount() > 1 && toRemove != null){
				edge.removeTransition((Transition)toRemove);
			}
		}
		
		fsa.remove(event);
		setDirty(true);
		fsa.notifyAllSubscribers();
		notifyAllSubscribers();
	}
	
	/**
	 * FIXME since can set even properties directly, coders are not
	 * obligated to go through this method.  Make sure changes to DES element
	 * properties trigger a notification from FSA.
	 *  
	 * IDEA go through FSA interface?
	 * 
	 * @param event
	 * @param b
	 */
	public void setControllable(Event event, boolean b){
		// update the event
		event.setControllable(b);
		// notify subscribers
		fsa.notifyAllBut(this);
		setDirty(true);
		notifyAllSubscribers();
	}

	public void setObservable(Event event, boolean b){
		// update the event
		event.setObservable(b);
		// notify subscribers
		fsa.notifyAllBut(this);
		setDirty(true);
		notifyAllSubscribers();
	}
	///////////////////////////////////////////////////////////////////////////
	// These can go once extend GraphElement
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void setDirty(boolean b)
	{
		dirty=b;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Calculcates the size of the bounding box necessary for the entire graph.  
	 * Visits every node, edge and label and uses the union of
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
			graphBounds = graphBounds.union(((BezierEdge)graphEdge).bounds());
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
			return ((BezierEdge)graphEdge).bounds();
		}

		for (GraphLabel freeLabel : freeLabels.values())
		{
			return freeLabel.bounds();
		}
		
		return new Rectangle();
	}
	
	public void translate(float x, float y)
	{
		graph.translate(x,y);
		setDirty(true);
		saveMovement(graph);  // calls notifyAllSubscribers				
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
		for(Node n : nodes.values())
		{
			if(rectangle.contains(n.bounds()) ){ // TODO && do a more thorough intersection test
				g.insert(n);				
			}
		}
				
		for(Edge e : edges.values())
		{
			if(rectangle.contains(e.bounds())){
				g.insert(e);
			}
		}
		
		// check for intersection with free labels 
		for(GraphLabel l : freeLabels.values())
		{
			if(rectangle.contains(l.bounds())){
				g.insert(l);				
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
		
		
		GraphLabel gLabel;
		iter = edgeLabels.entrySet().iterator();
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			gLabel = (GraphLabel)entry.getValue();
			if(gLabel.intersects(p)){		
				return gLabel;				
			}
		}
		
		BezierEdge e;
		// check for intersection with edges
		iter = edges.entrySet().iterator();
		while(iter.hasNext()){			
			entry = (Entry)iter.next();
			e = (BezierEdge)entry.getValue();
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

	
	//////////////////////////////////////////////////////////////////////// 
	/* FSMGraphPublisher part which maintains a collection of, and 
	 * sends change notifications to, all interested observers (subscribers). 
	 **/
	private ArrayList<FSMGraphSubscriber> subscribers = new ArrayList<FSMGraphSubscriber>();;
		
	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(FSMGraphSubscriber subscriber) {
		subscribers.add(subscriber);		
	}
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(FSMGraphSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	/**
	 * Notifies all subscribers that there has been a change to an element of 
	 * the graph publisher.
	 * 
	 * @param message
	 */
	private void fireFSMGraphChanged(FSMGraphMessage message)
	{
		for(FSMGraphSubscriber s : subscribers)
		{
			s.fsmGraphChanged(message);
		}		
	}

	/**
	 * Notifies all subscribers that there has been a change to the elements  
	 * currently selected in the graph publisher.
	 * 
	 * @param message
	 */
	private void fireFSMGraphSelectionChanged(FSMGraphMessage message)
	{
		for(FSMGraphSubscriber s : subscribers)
		{
			s.fsmGraphSelectionChanged(message);
		}		
	}
	
	////////////////////////////////////////////////////////////////////////	
	/* FSMSubscriber part which responds to change notifications from the FSM
	 * model this graph represents.  
	 */
	
	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmStructureChanged(observer.FSMMessage)
	 */
	public void fsmStructureChanged(FSMMessage message) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmEventSetChanged(observer.FSMMessage)
	 */
	public void fsmEventSetChanged(FSMMessage message) {
		// TODO Auto-generated method stub
		
	}
	///////////////////////////////////////////////////////////////////////
}


