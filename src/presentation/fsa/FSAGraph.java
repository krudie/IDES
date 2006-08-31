package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;
import model.fsa.ver1.MetaData;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;
import observer.FSAGraphMessage;
import observer.FSAGraphSubscriber;
import observer.FSAMessage;
import observer.FSASubscriber;
import pluggable.layout.LayoutManager;
import presentation.GraphicalLayout;
import presentation.PresentationElement;

/**
 * A recursive structure used to draw and edit the graph representation of an Automaton.
 * Computes intersections with graph elements given a point or rectangular area.
 * 
 * Observes and updates the Automaton.
 * Updates the graphical visualization metadata and synchronizes it with the Automaton model.
 * Is observed and updated by the GraphView and GraphDrawingView.  
 * 
 * @author helen bretzke
 *
 */
public class FSAGraph extends GraphElement implements FSASubscriber {

	
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
	private HashMap<Long, CircleNode> nodes;
	private HashMap<Long, Edge> edges;
	private HashMap<Long, GraphLabel> freeLabels;
	private HashMap<Long, GraphLabel> edgeLabels; // use parent edge's id as key
	
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
	public FSAGraph(Automaton fsa, MetaData data){
		
		this.fsa = fsa;
		fsa.addSubscriber(this);
		
		this.metaData = data;	
			
		nodes = new HashMap<Long, CircleNode>();
		edges = new HashMap<Long, Edge>();
		edgeLabels = new HashMap<Long, GraphLabel>();
		freeLabels = new HashMap<Long, GraphLabel>();
		
		initializeGraph();	
	}
	
	/**
	 * @param fsa the mathematical model	
	 */
	public FSAGraph(Automaton fsa)
	{
		this.fsa = fsa;
		metaData=new MetaData(fsa);
		fsa.addSubscriber(this);
		nodes = new HashMap<Long, CircleNode>();
		edges = new HashMap<Long, Edge>();
		edgeLabels = new HashMap<Long, GraphLabel>();
		freeLabels = new HashMap<Long, GraphLabel>();
		
		Set<Set<FSATransition>> groups=new HashSet<Set<FSATransition>>();
		HashMap<FSAState,Set<FSATransition>> stateGroups=new HashMap<FSAState,Set<FSATransition>>();
		for(Iterator<FSAState> i=fsa.getStateIterator();i.hasNext();)
		{
			FSAState s=i.next();
			wrapState(s,new Point2D.Float(0,0));//(float)Math.random()*200,(float)Math.random()*200));
			stateGroups.clear();
			for(Iterator<FSATransition> j=s.getSourceTransitionsListIterator();j.hasNext();)
			{
				FSATransition t=j.next();
				Set<FSATransition> ts;
				if(stateGroups.containsKey(t.getTarget()))
					ts=stateGroups.get(t.getTarget());
				else
					ts=new HashSet<FSATransition>();
				ts.add(t);
				stateGroups.put(t.getTarget(),ts);
			}
			groups.addAll(stateGroups.values());
		}
		for(Iterator<Set<FSATransition>> i=groups.iterator();i.hasNext();)
			wrapTransitions(i.next());
		
		LayoutManager.getDefaultFSMLayouter().layout(this);
		buildIntersectionDS();
	}
	
	/**
	 * TODO Implement this as a quad tree.
	 * KLUGE just uses a bunch of maps to store each element type.
	 *
	 */
	private void buildIntersectionDS()
	{
		nodes = new HashMap<Long, CircleNode>();
		edges = new HashMap<Long, Edge>();
		edgeLabels = new HashMap<Long, GraphLabel>();
		freeLabels = new HashMap<Long, GraphLabel>();
			
		Iterator children = children();
		
		// children can be Nodes or FreeLabels
		// edges are children of Nodes (as are labels but we don't compute explicit intersection with them)
		// edge labels are children of edges
		GraphElement el;
		while(children.hasNext())
		{
			el = (GraphElement)children.next();
			CircleNode n;
			
			if(el instanceof CircleNode)
			{	
				n = (CircleNode)el;
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
				freeLabels.put(label.getId(), label);
			}
		}
	}
	
	public String getName()
	{
		return fsa.getName();
	}
	
	public void setName(String name)
	{
		fsa.setName(name);
	}
	
	public String getDecoratedName()
	{
		return (isDirty()?"* ":"")+getName();
	}
	
	public Automaton getAutomaton()
	{
		return fsa;
	}
	
	public MetaData getMeta()
	{
		return metaData;
	}

	/**
	 * Returns the set of all nodes in the graph.
	 * @return the set of all nodes in the graph
	 */
	public Collection<CircleNode> getNodes()
	{
		return nodes.values();
	}

	public Node getNode(long id)
	{
		return nodes.get(new Long(id));
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
	 * Builds this graph from the elements in <code>fsa</code>. 
	 * 
	 * TODO 
	 * Build this graph in LayoutDataParser
	 * Build free labels (those not associated with elements of the automaton).
	 * Replace the intersection lists with a quadtree
	 */
	private void initializeGraph(){		
		
		for(CircleNode n:nodes.values())
			((NodeLayout)n.getLayout()).dispose();
		
		this.clear();
		
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
		CircleNode n1;
				
		while(iter.hasNext()){
			s = (State)iter.next();
			NodeLayout nL=metaData.getLayoutData(s);
			nL.setUniformRadius(uniformR);
			n1 = new CircleNode(s, nL);			
			insert(n1);
			nodes.put(new Long(s.getId()), n1);
		}

		// for all transitions in fsa
		// create all edges and connect to nodes
		// create a single edge for aggregate of all transitions from same start and end state
		// add events to collection for that edge.
		iter = fsa.getTransitionIterator();
		Transition t;
		CircleNode n2;
		Edge e;
		while(iter.hasNext()){						
			t = (Transition)iter.next();
		
			// get the source and target nodes
			n1 = nodes.get(new Long(t.getSource().getId()));
			n2 = nodes.get(new Long(t.getTarget().getId()));
			
			// if the edge corresponding to t already exists,
			// and its layout is the same
			// add t to the edge's set of transitions			
			e = directedEdgeBetween(n1, n2); 
			BezierLayout layout = metaData.getLayoutData(t);
			if(e != null && e.getLayout().equals(layout)){			
				e.addTransition(t);
			}else{
				// get the graphic data for the transition and all associated events
				// construct the edge
				if(n1.equals(n2)){
					e = new ReflexiveEdge(layout, n1, t);
				}else{
					e = new BezierEdge(layout, n1, n2, t);
				}
				
				// add this edge to source and target nodes' children
				n1.insert(e);				
				n2.insert(e);
				
				// add to set of edges
				// id may be misleading since it is the id of only the first transition on this edge
				edges.put(new Long(e.getId()), e);
			}
		}
	
		// collect all labels on edges				
		for(Edge edge : edges.values())
		{
			edgeLabels.put(edge.getId(), edge.getLabel());
		}
		
		// add all intialArrows to the set of edges
		for(Node node : nodes.values())
		{
			if(node.getState().isInitial()){
				edges.put(node.getInitialArrow().getId(), node.getInitialArrow());
			}
		}
		
		// TODO for all free labels in layout data structure
		
		// clear all dirty bits in the graph structure		
		refresh();
	}

	/**
	 * @deprecated
	 * Graph is now built in LayoutDataParser
	 * 
	 * Returns the directed edge from <code>source</code> to <code>target</code> if exists.
	 * Otherwise returns null.
	 */
	private Edge directedEdgeBetween(Node source, Node target){		
		for(Edge e : edges.values())
		{			
			if(e.getSourceNode() != null && e.getSourceNode().equals(source) 
					&& e.getTargetNode() != null && e.getTargetNode().equals(target)){
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
		
	/**
	 * Adds a new node at point <code>p</code> and completes the edge from 
	 * <code>e</code>'s source node to the new node.
	 *
	 * @param e the edge to be finished
	 * @param p the location of the new node
	 */
	public void finishEdgeAndCreateTargetNode(BezierEdge e, Point2D.Float p){	
		if( ! e.getSourceNode().intersects(p) ){
			finishEdge(e, createNode(p));
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
	  * @param e 
	  * @param target	
	  */
	public void finishEdge(BezierEdge e, CircleNode target){
			
		e.setTargetNode(target);			
		e.computeEdge();	
		
		// Distribute multiple directed edges between same node pair.
		Set<Edge> neighbours = getEdgesBetween(target, e.getSourceNode());
		if(neighbours.size() > 0)
		{
			e.insertAmong(neighbours);
		}

		Transition t = new Transition(fsa.getFreeTransitionId(), fsa.getState(e.getSourceNode().getId()), fsa.getState(target.getId()));
		
		// TO BE REMOVED /////////////////////////////
		metaData.setLayoutData(t, e.getBezierLayout());
		//////////////////////////////////////////////
		
		e.addTransition(t);
		
		// NOTE must assign transition to edge before inserting edge as children of end nodes.
		e.getSourceNode().insert(e);	
		target.insert(e);		

		//fsa.notifyAllBut(this);
		// avoid spurious update
		fsa.removeSubscriber(this);
		fsa.add(t);
		fsa.addSubscriber(this);
		
		edges.put(e.getId(), e);
		edgeLabels.put(e.getId(), e.getLabel());
		setDirty(true);
	
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.EDGE,
				e.getId(), 
				e.bounds(),
				this, ""));
	}	
	
	/** 
	 * @param n1
	 * @param n2
	 * @return the set of all edges connecting n1 and n2
	 */
	private Set<Edge> getEdgesBetween(Node n1, Node n2){
		Set<Edge> set = new HashSet<Edge>();
		for(Edge e : edges.values())
		{			
			if((e.getSourceNode() != null && e.getTargetNode() != null)
				&& (e.getSourceNode().equals(n1) && e.getTargetNode().equals(n2) 
						|| e.getSourceNode().equals(n2) && e.getTargetNode().equals(n1)) 
				)
			{
				set.add(e);
			}
		}		
		return set;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a new node with centre at the given point
	 * and a adds a new state to the automaton.
	 * 
	 * @param p the centre point for the new node
	 * @return the node added
	 */
	public CircleNode createNode(Point2D.Float p){
		State s = new State(fsa.getFreeStateId());
		s.setInitial(false);
		s.setMarked(false);
		NodeLayout layout = new NodeLayout(uniformR,p);			
		metaData.setLayoutData(s, layout);
		fsa.removeSubscriber(this);
		fsa.add(s);
		fsa.addSubscriber(this);
		
		CircleNode n = new CircleNode(s, layout);	
		nodes.put(new Long(s.getId()), n);
		insert(n);
		//setDirty(true);		
		
		Rectangle2D dirtySpot = n.adjacentBounds(); 
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.NODE,
				n.getId(), 
				dirtySpot,
				this, ""));
		return n;
	}
	
	/**
	 * Creates a new node which wraps the provided automaton state.
	 * 
	 * @param s automaton state to be wrapped
	 * @param p the centre point for the new node
	 * @return the node added
	 */
	public CircleNode wrapState(FSAState s, Point2D.Float p){
		NodeLayout layout = new NodeLayout(uniformR,p);
		if(s.isInitial())
			layout.setArrow(new Point2D.Float(1,0));
		
		metaData.setLayoutData(s, layout);
		
		CircleNode n = new CircleNode(s, layout);
		nodes.put(new Long(s.getId()), n);
		insert(n);
		setDirty(true);		
		
		Rectangle2D dirtySpot = n.adjacentBounds(); 
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.NODE,
				n.getId(), 
				dirtySpot,
				this, ""));
		//labelNode(n,)
		return n;
	}	
	
	/** 
	 * Creates a new edge from node <code>n1</code> to node <code>n2</code>.
	 * and a adds a new transition to the automaton.
	 * 
	 * @param n1 source node 
	 * @param n2 target node
	 */
	public void createEdge(Node n1, Node n2){
		Transition t = new Transition(fsa.getFreeTransitionId(), fsa.getState(n1.getId()), fsa.getState(n2.getId()));				
		Edge e;
		if(n1.equals(n2)){
			// let e figure out how to place itself among its neighbours			
			e = new ReflexiveEdge(n1, t);			
		}else{			
			BezierLayout layout = new BezierLayout((NodeLayout)n1.getLayout(), (NodeLayout)n2.getLayout());
//			 computes layout of new edges (default to straight edge between pair of nodes)			
			e = new BezierEdge(layout, n1, n2, t);			
		}
		metaData.setLayoutData(t, (BezierLayout)e.getLayout());
		fsa.removeSubscriber(this);
		fsa.add(t);
		fsa.addSubscriber(this);		
		n1.insert(e);
		n2.insert(e);
		edges.put(e.getId(), e);		
		edgeLabels.put(e.getId(), e.getLabel());
		setDirty(true);
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.EDGE,
				e.getId(), 
				e.bounds(),
				this, ""));
	}
	
	/** 
	 * Creates a new edge between the nodes which correspond to the states
	 * between which is the first transition in the provided set. All transitions
	 * from the set are wrapped by the edge.
	 * 
	 * @param ts the set of transitions to be wrapped
	 */
	public void wrapTransitions(Set<FSATransition> ts){
		if(ts.isEmpty())
			return;
		Iterator<FSATransition> i=ts.iterator();
		FSATransition t=i.next();
		Node n1=nodes.get(new Long(t.getSource().getId()));
		Node n2=nodes.get(new Long(t.getTarget().getId()));
		Edge e;
		if(n1.equals(n2)){
			// let e figure out how to place itself among its neighbours			
			e = new ReflexiveEdge(n1, t);
		}else{			
			BezierLayout layout = new BezierLayout((NodeLayout)n1.getLayout(), (NodeLayout)n2.getLayout());
//			 computes layout of new edges (default to straight edge between pair of nodes)			
			e = new BezierEdge(layout, n1, n2, t);			
		}
		if(t.getEvent()!=null)
			((BezierLayout)e.getLayout()).addEventName(t.getEvent().getSymbol());
		metaData.setLayoutData(t, (BezierLayout)e.getLayout());
		n1.insert(e);
		n2.insert(e);
		edges.put(e.getId(), e);		
		edgeLabels.put(e.getId(), e.getLabel());
		setDirty(true);

		while(i.hasNext())
		{
			t=i.next();
			e.addTransition(t);
			if(t.getEvent()!=null)
				((BezierLayout)e.getLayout()).addEventName(t.getEvent().getSymbol());
		}

		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.EDGE,
				e.getId(), 
				e.bounds(),
				this, ""));
	}

	/**
	 * @deprecated
	 * Creates a node at point <code>p</code> and an edge from the given source node
	 * to the new node.
	 * 
	 * @param source
	 * @param p
	 */
	public void createEdgeAndNode(CircleNode source, Point2D.Float p){		
		createEdge(source, createNode(p));
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * TODO remove this and delay committing layout changes until save. 
	 * @param selection
	 */
	public void saveMovement(PresentationElement selection){
		Iterator children = selection.children();
		while(children.hasNext()){
			PresentationElement el = (PresentationElement)children.next();
			if(edgeLabels.containsValue(el)){
				saveMovement((GraphLabel)el);
			}else if(nodes.containsValue(el)){
				saveMovement((CircleNode)el);
			}else if(edges.containsValue(el)){
				saveMovement((Edge)el);				
			}else if(freeLabels.containsValue(el)){
				// FIXME move free labels
				saveMovement((GraphLabel)el);
			}
		}

		// ??? fire one nodification for the whole selection 
		// or a message for each element in the group?
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.SELECTION,
				FSAGraphMessage.UNKNOWN_ID, 
				selection.bounds(),
				this, ""));
	}
		
	/**
	 * @param label
	 */
	private void saveMovement(GraphLabel label) {
		// update offset vector in EdgeLayout		
		if(label.getParent() != null){
			try{
				BezierEdge edge = (BezierEdge)label.getParent();
				BezierLayout layout = edge.getBezierLayout();				
				Iterator<FSATransition> t = edge.getTransitions();
				while(t.hasNext()){
					metaData.setLayoutData(t.next(), layout);
				}
			}catch(ClassCastException cce){}			
		}else{ // TODO Move free label, tell MetaData
			
		}
//		fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.MODIFY, 
//				FSMGraphMessage.LABEL,
//				label.getId(), 
//				label.bounds(),
//				this, ""));
	}

	private void saveMovement(Node node){
		// save location of node to metadata
		State s = (State)fsa.getState(node.getId());
		metaData.setLayoutData(s, (NodeLayout)node.getLayout());

		// for all edges adjacent to node, save layout
		Iterator<Edge> adjEdges = node.adjacentEdges();
		while(adjEdges.hasNext()){			
			saveMovement(adjEdges.next());
		}	
	}
	
	private void saveMovement(Edge e){		
		GraphicalLayout layout = e.getLayout();		
		Iterator<FSATransition> t = e.getTransitions();
		// for all transitions in e, save the edge layout		
		while(t.hasNext()){
			try{  // FIXME need to handle other types of layout (e.g. Quad, Linear) 
				metaData.setLayoutData(t.next(), (BezierLayout)layout);
			}catch(ClassCastException cce){
				// FIXME InitialArrows are a special case 
				// and are saved as properties of their parent nodes.
				// DEBUG
				System.err.println(cce.getMessage());
			}
		}		
	}
	
	///////////////////////////////////////////////////////////////////
	
	
	public void setInitial(CircleNode n, boolean b){
		
		n.setInitial(b);
		
		// add or remove the intial arrow from the set of edges
		InitialArrow arrow = n.getInitialArrow();
		if(arrow != null){
			if(b){
				edges.put(arrow.getId(), arrow);					
			}else{
				edges.remove(arrow.getId());			
			}
		}				
		
		fsa.removeSubscriber(this);
		fsa.fireFSMStructureChanged(new FSAMessage(FSAMessage.MODIFY,
    			FSAMessage.STATE, n.getId(), fsa));
		fsa.addSubscriber(this);
		
	}
	
	public void setMarked(CircleNode n, boolean b){
		// update the state
		((State)n.getState()).setMarked(b);		
		
		n.setDirty(true);		
		
		fsa.removeSubscriber(this);
		fsa.fireFSMStructureChanged(new FSAMessage(FSAMessage.MODIFY,
    			FSAMessage.STATE, n.getId(), fsa));
		fsa.addSubscriber(this);
	}
	
	/**
	 * Adds a self-loop adjacent on the given node. 
	 *  
	 * @param node
	 */
	public void addSelfLoop(CircleNode node) {	
		createEdge(node, node);			
	}
	
	/**
	 * Assigns the set of events to <code>edge</code>, removes any events from edge
	 * that are not in the given list and commits any changes to the LayoutData (MetaData).
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
				toAdd.add(new Transition(fsa.getFreeTransitionId(), edge.getSourceNode().getState(), edge.getTargetNode().getState(), e));
			}
		}
		
		// more transitions than events
		while(trans.hasNext()){			
			toRemove.add((Transition)trans.next());			
		}
		 
		fsa.removeSubscriber(this);
		
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
		
		fsa.addSubscriber(this);
		
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
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.EDGE,
				edge.getId(), 
				edge.bounds(),
				this, "replaced events on edge label"));
	}
	
	/**
	 * Stores the layout for the given edge for every transition represented
	 * by this edge.
	 * 
	 * @param edge
	 */
	public void commitEdgeLayout(Edge edge){
		saveMovement(edge);	
		//setDirty(true);
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.EDGE,
				edge.getId(), 
				edge.bounds(),
				this, "commit edge layout"));		
	}
		
	public void delete(GraphElement el){
		// KLUGE This is worse (less efficient) than using instance of ...
		if(nodes.containsValue(el)){			
			delete((Node)el);			
		}else if(edges.containsValue(el)){
			delete((Edge)el);
		}else{
			freeLabels.remove(el.getId());
			this.remove(el);
			fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.REMOVE, 
					FSAGraphMessage.LABEL,
					FSAGraphMessage.UNKNOWN_ID, 
					el.bounds(),
					this, ""));
		}
	}
	
	private void delete(Node n){
		// delete all adjacent edges
		// TODO does this include the initial arrow ?
		Iterator<Edge> edges = n.adjacentEdges();
		while(edges.hasNext()){
			delete(edges.next());
		}
		// remove n
		fsa.removeSubscriber(this);
		fsa.remove(n.getState());
		fsa.addSubscriber(this);
				
		super.remove(n);
		((NodeLayout)n.getLayout()).dispose();
		nodes.remove(new Long(n.getId()));
		setDirty(true);
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.REMOVE, 
				FSAGraphMessage.NODE,
				n.getId(), 
				n.adjacentBounds(),
				this, ""));
	}
	
	private void delete(Edge e){
		Iterator<FSATransition> transitions = e.getTransitions();
		while(transitions.hasNext()){
			fsa.removeSubscriber(this);
			fsa.remove(transitions.next());
			fsa.addSubscriber(this);
		}
		Node source = e.getSourceNode();
		if(source != null){
			source.remove(e);
		}
		Node target = e.getTargetNode();
		if(target != null){
			target.remove(e);
		}
		edgeLabels.remove(e.getId());
		edges.remove(e.getId());
		setDirty(true);		
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.REMOVE, 
				FSAGraphMessage.NODE,
				e.getId(), 
				e.bounds(),
				this, ""));
	}

	/**
	 * Precondition: <code>n</code> and <code>text</code> are not null
	 * 
	 * @param n the node to be labelled
	 * @param text the name for the node
	 */
	public void labelNode(Node n, String text){		
		State s = (State)fsa.getState(n.getId());
		n.getLayout().setText(text);
		n.getLabel().setText(text);
		// KLUGE ///////////////////////////////////////////
		metaData.setLayoutData(s, (NodeLayout)n.getLayout());
		/////////////////////////////////////////////////////
		setDirty(true);		
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.NODE,
				n.getId(), 
				n.bounds(),
				this, ""));
	}

	public void addFreeLabel(String text, Point2D.Float p) {		
		GraphLabel label = new GraphLabel(text, p);
		freeLabels.put(label.getId(), label);
		insert(label);
		setDirty(true);		
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.LABEL,
				FSAGraphMessage.UNKNOWN_ID, 
				label.bounds(),
				this, ""));
	}
	
	
	/**
	 * @param freeLabel
	 * @param text
	 */
	public void setLabelText(GraphLabel freeLabel, String text) {
		freeLabel.setText(text);
		//setDirty(true);		
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.LABEL,
				FSAGraphMessage.UNKNOWN_ID, 
				freeLabel.bounds(),
				this, ""));		
	}

	/**
	 * @param symbol
	 * @param controllable
	 * @param observable
	 * @return the new Event
	 */
	public Event createAndAddEvent(String symbol, boolean controllable, boolean observable) {
		Event event=new Event(fsa.getFreeEventId());
		event.setSymbol(symbol);
		event.setControllable(controllable);
		event.setObservable(observable);
		fsa.add(event);
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
		Set<BezierEdge> edgesToRemove=new HashSet<BezierEdge>();
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
			if(toRemove != null){
				edge.removeTransition((Transition)toRemove);
			}
			if(edge.transitionCount()==0)
			{
				edgesToRemove.add(edge);
			}
		}
		for(BezierEdge e:edgesToRemove)
			delete(e);
		
		fsa.removeSubscriber(this);
		fsa.remove(event);
		fsa.addSubscriber(this);
	}
	
	public void setControllable(Event event, boolean b){
		// update the event
		event.setControllable(b);
		fsa.fireFSMEventSetChanged(new FSAMessage(FSAMessage.MODIFY,
    			FSAMessage.EVENT, event.getId(), fsa));
	}

	public void setObservable(Event event, boolean b){
		// update the event
		event.setObservable(b);
		fsa.fireFSMEventSetChanged(new FSAMessage(FSAMessage.MODIFY,
    			FSAMessage.EVENT, event.getId(), fsa));
	}
	
	public void symmetrize(Edge edge){
		BezierLayout el=(BezierLayout)edge.getLayout();
		el.symmetrize();	
		
		// TODO include edge label in bounds (dirty spot)
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.EDGE,
				edge.getId(), 
				edge.bounds(),
				this, "symmetrized edge"));
	}	
	
	/**
	 * Calculates the size of the bounding box necessary for the entire graph.  
	 * Visits every node, edge and label and uses the union of
	 * their bounds to create the box.
	 * 
	 * @param initAtZeroZero Whether you want the box to begin at (0, 0) 
	 *                (true) or tightly bound around the graph (false) 
	 * @return Rectangle The bounding box for the graph
	 * 
	 * @author Sarah-Jane Whittaker
	 * @author Lenko Grigorov Grigorov
	 */
	public Rectangle getBounds(boolean initAtZeroZero)
	{
		Rectangle graphBounds = initAtZeroZero ? 
			new Rectangle() : getElementBounds();
	
		FSAState nodeState = null;
		
		// Start with the nodes
		for (CircleNode graphNode : nodes.values())
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
			graphBounds = graphBounds.union(graphEdge.bounds());
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
		for (CircleNode graphNode : nodes.values())
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
		super.translate(x,y);		
		// FIXME refreshBounds
		saveMovement(this);  // fires graph changed			
	}

	/**
	 * Computes and returns the set of graph elements contained by the given rectangle.
	 * 
	 * @param rectangle
	 * @return the set of graph elements contained by the given rectangle
	 */
	protected SelectionGroup getElementsContainedBy(Rectangle rectangle) {
		SelectionGroup g = new SelectionGroup();
		
		
		for(CircleNode n : nodes.values()) {
			if(rectangle.contains(n.bounds()) ){ // TODO && do a more thorough intersection test
				g.insert(n);				
			}
		}
				
		for(Edge e : edges.values()) {
			if(rectangle.contains(e.bounds())){
				g.insert(e);
			}
		}
		
		// check for intersection with free labels 
		for(GraphLabel l : freeLabels.values()) {
			if(rectangle.contains(l.bounds())){
				g.insert(l);				
			}
		}
		
		fireFSAGraphSelectionChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
										FSAGraphMessage.SELECTION, g.getId(), g.bounds(), this));
		
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
						
		GraphElement el = null;
		int type = FSAGraphMessage.UNKNOWN_TYPE;
		
		for(GraphLabel gLabel : edgeLabels.values()){
			if(gLabel.intersects(p)){
				type = FSAGraphMessage.LABEL;
				el = gLabel;				
			}
		}
		
		if(el == null){
			for(Edge e : edges.values()){			
				if(e.intersects(p)){		
					type  = FSAGraphMessage.EDGE;
					el = e;				
				}
			}
		}
		
		if(el == null){
			for(Node n : nodes.values()){			
				if(n.intersects(p)){
					type = FSAGraphMessage.NODE;
					el = n;				
				}
			}	
		}
			
		if(el == null){
			for(GraphLabel l : freeLabels.values()){			
				if(l.intersects(p)){
					type = FSAGraphMessage.LABEL;
					el = l;				
				}
			}		
		}
		
		if(el != null){
			fireFSAGraphSelectionChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
											type, el.getId(), el.bounds(), this));
		}
		return el;
	}

	
	//////////////////////////////////////////////////////////////////////// 
	/* FSMGraphPublisher part which maintains a collection of, and 
	 * sends change notifications to, all interested observers (subscribers). 
	 **/
	private ArrayList<FSAGraphSubscriber> subscribers = new ArrayList<FSAGraphSubscriber>();
		
	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(FSAGraphSubscriber subscriber) {
		subscribers.add(subscriber);		
	}
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(FSAGraphSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	
	/**
	 * Notifies all subscribers that there has been a change to an element of 
	 * the graph publisher.
	 * 
	 * @param message
	 */
	private void fireFSAGraphChanged(FSAGraphMessage message)
	{
		for(FSAGraphSubscriber s : subscribers)
		{
			s.fsmGraphChanged(message);
		}		
	}

	/**
	 * Notifies all subscribers that there has been a change to the elements  
	 * currently selected in the graph publisher.
	 * 
	 * TODO call this from GraphDrawingView (or SelectionTool?)
	 * 
	 * @param message
	 */
	protected void fireFSAGraphSelectionChanged(FSAGraphMessage message)
	{
		for(FSAGraphSubscriber s : subscribers)
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
	public void fsmStructureChanged(FSAMessage message) {
		// TODO if can isolate the change just modify the structure as required
		// e.g. properties set on states or events.
		// and only refresh the affected part of the graph
		int elementType = message.getElementType();
		// otherwise rebuild the graph structure 
		switch(elementType){
		case FSAMessage.STATE:
			
			
			/*fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.???, 
					FSMGraphMessage.???,
					?.getId(), 
					?.bounds(),
					this, ""));
					*/	
			break;
		case FSAMessage.TRANSITION:

			/*fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.???, 
			FSMGraphMessage.???,
			?.getId(), 
			?.bounds(),
			this, ""));
			*/	

			break;
		case FSAMessage.EVENT:

			/*fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.???, 
			FSMGraphMessage.???,
			?.getId(), 
			?.bounds(),
			this, ""));
			*/	

			break;
		default:
			initializeGraph();
		
		/*fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.???, 
		FSMGraphMessage.???,
		?.getId(), 
		?.bounds(),
		this, ""));
		*/	

		}		
		
	}

	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmEventSetChanged(observer.FSMMessage)
	 */
	public void fsmEventSetChanged(FSAMessage message) {
		// Remove transitions fired by affected events
		// TODO construct bounds of area affected
		
		if(message.getEventType() == FSAMessage.REMOVE){
			Set<Edge> edgesToRemove=new HashSet<Edge>();
			for(Edge e : edges.values()){
				Iterator<FSATransition> trans = e.getTransitions();
				while(trans.hasNext()){
					FSATransition t = trans.next();  // FIXME ConcurrentModificationException
					FSAEvent event = t.getEvent();
					if(event != null && event.getId() == message.getElementId()){
						if(e.transitionCount()>0){
							fsa.removeSubscriber(this);
							fsa.remove(t);
							fsa.addSubscriber(this);
							trans.remove();		
						}else{
							t.setEvent(null);
						}
						// TODO handle different edge types
						if(e.transitionCount()>0)
						{
							((BezierLayout)e.getLayout()).removeEventName(event.getSymbol());
							e.setDirty(true);
						}
						else
							edgesToRemove.add(e);
					}
				}
			}
			for(Edge e:edgesToRemove)
				delete(e);
			// FIXME this does not update the labels on the edges
			fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY,
													FSAGraphMessage.EDGE,
													message.getElementId(),
													this.bounds(),
													this));	
		}
		
	}
	///////////////////////////////////////////////////////////////////////

	/**
	 * @param edge
	 */
	public void arcMore(Edge edge) {
		((BezierEdge)edge).arcMore();
		// TODO fire graph changed event
	}

	/**
	 * @param edge
	 */
	public void arcLess(Edge edge) {
		((BezierEdge)edge).arcLess();	
		// TODO fire graph changed event
	}

	//FIXME: move this to a plugin that reads/writes composition data for states
	public void labelCompositeNodes()
	{
		if(fsa.getAutomataCompositionList().length>1)
		{
			FSAGraph[] gs=new FSAGraph[fsa.getAutomataCompositionList().length];
			for(int i=0;i<gs.length;++i)
			{
				FSAGraph g=Hub.getWorkspace().getGraphById(fsa.getAutomataCompositionList()[i]);
				if(g==null)
					return;
				gs[i]=g;
			}
			for(Node n:nodes.values())
			{
				State s=(State)n.getState();
				boolean emptyLabel=true;
				String label="(";
				for(int i=0;i<gs.length-1;++i)
				{
					if(!"".equals(gs[i].getNode(s.getStateCompositionList()[i]).getLabel().getText()))
						emptyLabel=false;
					label+=gs[i].getNode(s.getStateCompositionList()[i]).getLabel().getText()+",";
				}
				if(!"".equals(gs[gs.length-1].getNode(s.getStateCompositionList()[gs.length-1]).getLabel().getText()))
					emptyLabel=false;
				label+=gs[gs.length-1].getNode(s.getStateCompositionList()[gs.length-1]).getLabel().getText()+")";
				if(!emptyLabel)
				{
					n.getLayout().setText(label);
					n.getLabel().softSetText(label);
					// KLUGE ///////////////////////////////////////////
					metaData.setLayoutData(s, (NodeLayout)n.getLayout());
					/////////////////////////////////////////////////////
					setDirty(true);		
					fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
							FSAGraphMessage.NODE,
							n.getId(), 
							n.bounds(),
							this, ""));
				}
			}
		}
		else if(fsa.getAutomataCompositionList().length==1)
		{
			FSAGraph g=Hub.getWorkspace().getGraphById(fsa.getAutomataCompositionList()[0]);
			if(g==null)
				return;
			for(Node n:nodes.values())
			{
				State s=(State)n.getState();
				String label="";
				if(s.getStateCompositionList().length>1)
				{
					label="(";
					for(int i=0;i<s.getStateCompositionList().length-1;++i)
						label+=g.getNode(s.getStateCompositionList()[i]).getLabel().getText()+",";
					label+=g.getNode(s.getStateCompositionList()[s.getStateCompositionList().length-1]).getLabel().getText()+")";
				}
				else if(s.getStateCompositionList().length>0)
					label=g.getNode(s.getStateCompositionList()[0]).getLabel().getText();
				{
					n.getLayout().setText(label);
					n.getLabel().softSetText(label);
					// KLUGE ///////////////////////////////////////////
					metaData.setLayoutData(s, (NodeLayout)n.getLayout());
					/////////////////////////////////////////////////////
					setDirty(true);		
					fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
							FSAGraphMessage.NODE,
							n.getId(), 
							n.bounds(),
							this, ""));
				}
			}
		}
	}
}


