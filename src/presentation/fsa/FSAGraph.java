package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import main.Annotable;
import main.Hub;
import model.fsa.FSAMessage;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSAEvent;
import model.fsa.FSASubscriber;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.Event;
import model.fsa.ver2_1.State;
import model.fsa.ver2_1.Transition;
import pluggable.layout.LayoutManager;
import presentation.GraphicalLayout;
import presentation.LayoutShell;
import presentation.LayoutShellMessage;
import presentation.LayoutShellPublisher;
import presentation.LayoutShellSubscriber;
import presentation.PresentationElement;
import presentation.Geometry;
import presentation.fsa.ReflexiveEdge.ReflexiveLayout;

/**
 * A recursive structure used to view, draw and modify the graph representation of an Automaton.
 * Given a point or rectangular area, computes intersections with graph elements.
 * 
 * Observes and updates the Automaton.
 * Updates the graphical visualization metadata and synchronizes it with the Automaton model.
 * Is observed and updated by the GraphView and GraphDrawingView.  
 *
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class FSAGraph extends GraphElement implements FSASubscriber, LayoutShell, LayoutShellPublisher {
	private long bezierLayoutFreeGroup = 0;
	//This flag is set to true when the FSAGraph is a result of an automatic
	//DES operation and this result has more than 100 states
	private boolean avoidLayoutDrawing;
	public boolean isAvoidLayoutDrawing()
	{
		return avoidLayoutDrawing;
	}

	protected UniformRadius uniformR=new UniformRadius();

	/** Flag indicating whether this graph needs to be saved to file */
	private boolean needsSave = false;

	/**
	 * Maps used in searches of intersection
	 * TODO replace with Quadtree data structure
	 */
	private HashMap<Long, CircleNode> nodes;
	private HashMap<Long, Edge> edges;
	private HashMap<Long, GraphLabel> freeLabels;
	private HashMap<Long, GraphLabel> edgeLabels; // use parent edge's id as key

	/**
	 * The system data model
	 */	
	private Automaton fsa;	   // system model

//	/**
//	* Intermediary used to extract and update presentation data between the system model
//	* and the graphical display. 
//	* TODO remove after removing all layout data from Automaton,
//	* 		testing graph extraction by LayoutDataParser, and 
//	*		delaying committing all changes until time of save.	
//	*/	
//	//TODO eliminate the need for this attribute and erase it
//	private MetaData metaData;


	/**
	 * Creates a graph model from the given system data model using
	 * an automatic layout tool.
	 * 
	 * @param fsa the mathematical model	
	 */
	public FSAGraph(FSAModel fsa) {
		if(!(fsa instanceof Automaton))
		{
			throw new RuntimeException("FSAGraph can only layout Automatons");
		}
		this.fsa = (Automaton)fsa;
//		metaData = new MetaData(this.fsa);
		fsa.addSubscriber(this);
		nodes = new HashMap<Long, CircleNode>();
		edges = new HashMap<Long, Edge>();
		edgeLabels = new HashMap<Long, GraphLabel>();
		freeLabels = new HashMap<Long, GraphLabel>();

		int statesCounter = fsa.getStateCount();

		avoidLayoutDrawing = (statesCounter > 900 ? true: false);
		if(avoidLayoutDrawing)
		{
			return;
		}

		/////////////////
		//////////
		//////
		//Testing annotations:
		boolean hasLayout = true;

		Iterator <FSAState> sIt = fsa.getStateIterator();
		while(sIt.hasNext())
		{
			try{
				FSAState s = sIt.next();
				CircleNodeLayout l = (CircleNodeLayout)s.getAnnotation(Annotable.LAYOUT);
				
				//Avoid give the name "" to the state!
				if(l.getText() != "")
				{
					s.setName(l.getText());
				}
				//Create a new node
				CircleNode node = new CircleNode(s, l);
				long id = s.getId();
				nodes.put(id, node);
				if(s.isInitial())
				{
					//Insert the initial arrow among the egdes
					edges.put( node.getInitialArrow().getId(), node.getInitialArrow() );
				}
				l.setText(s.getName());
				//Insert the node in the graph
				insert(node);
			}catch(Exception e)
			{
				hasLayout = false;
			}
		}


		//Add the edges to the FSAGraph
		Iterator <FSATransition> tIt = fsa.getTransitionIterator();
		while(tIt.hasNext())
		{			
			FSATransition t = tIt.next();
			BezierLayout l = (BezierLayout)t.getAnnotation(Annotable.LAYOUT);
			if(l == null)
			{
				hasLayout = false;
				break;
			}
			Iterator<GraphElement> nIt =  children();
			CircleNode src = null;
			CircleNode dst = null;
			boolean hasSrc = false, hasDst = false;

			//Find the source and target nodes for this edge:
			while(nIt.hasNext() || !(hasSrc & hasDst))
			{
				CircleNode n = null;
				try{
					n =(CircleNode)nIt.next();
				}catch(Exception e)
				{
					break;
				}
				//Find source
				if(n.getId() == t.getSource().getId())
				{
					src = n;
					hasSrc = true;
				}
				//Find target
				if(n.getId()  == t.getTarget().getId())
				{
					dst = n;
					hasDst = true;
				}
			}
			Edge edge = null;	
			boolean groupExists = false;
			if(l.getGroup() != BezierLayout.UNGROUPPED)
			{
				//The edge must have a group of transitions.
				//Check if there is already an edge with the same layout addressed by: "l".
				Set<Long> keys = edges.keySet();

				Iterator<Long> kIt = keys.iterator();
				while(kIt.hasNext())
				{
					boolean alreadyInserted = false;
					boolean skipIteration = false;
					Edge tmpEdge = edges.get(kIt.next());
					BezierLayout tmpLayout = null;
					try{
						tmpLayout = (BezierLayout)tmpEdge.getLayout();
					}catch(ClassCastException e)
					{
						//The edge is an initial edge, there is no meaning in processing it.
						//"lets go to the next edge"
						skipIteration = true;
					}
					if(!skipIteration)
					{
						//If there is already an Edge with BezierEdges from the same group, then there 
						//is no need to create a new Edge, just add the transition to the existent one.
						if((tmpLayout.getGroup() == l.getGroup()) && tmpLayout.getGroup() != BezierLayout.UNGROUPPED)
						{
//							System.out.println("Should group transition");
							//Add the transition to the edge in case it is not there yet.
							edge =  (Edge)tmpEdge;
							Iterator<FSATransition> it = edge.getTransitions();
							while(it.hasNext())
							{
								if(t == it.next())
								{
									//The transition is already in the edge.
									alreadyInserted = true;
								}
							}
							if(!alreadyInserted)
							{
								//Add the transition to the edge
								edge.addTransition(t);
							}
							groupExists = true;
						}
					}		
				}
			
				if(!groupExists)//If an Edge still does noe exist for this group, create one.
				{
					if(src != dst){
						edge = new BezierEdge(l, src,dst, t);
					}else{
						//Create a reflexive edge
						edge = new ReflexiveEdge(l, src, t);
//						System.out.println("A first reflexive edge was created for a group. It is " + edge + ", first event " + t.getEvent().getSymbol());
						//The first reflexiveEdge is being created, but not the others.
					}
					
				}
				//If the edge already exists, assign the transition t to this edge.
				//Otherwise, create a new edge!
			}else{
				if(src != dst){
					edge = new BezierEdge(l, src,dst, t);
				}else{
					//Create a reflexive edge
					edge = new ReflexiveEdge(l, src, t);
				}
			}			
			//add this edge among the childs of its source and target
			src.insert(edge);
			dst.insert(edge);

			if(edge.getId() != null & edge.getLabel() != null)
			{
				//Add the edge label to the set of edge labels in the graph
				edgeLabels.put(edge.getId(), edge.getLabel());
			}
			//Put the edge in the set of edges
			edges.put(t.getId(), edge);
		}

		/////////////////////
		if(!hasLayout)//Generate automatic layout:
		{

			// Prepare elements for automatic layout
			Set<Set<FSATransition>> groups = new HashSet<Set<FSATransition>>();
			HashMap<FSAState,Set<FSATransition>> stateGroups = new HashMap<FSAState,Set<FSATransition>>();
			Iterator<FSAState> i = fsa.getStateIterator();

			while( i.hasNext() ) {
				FSAState s=i.next();
				wrapState(s,new Point2D.Float(0,0));//(float)Math.random()*200,(float)Math.random()*200));
				stateGroups.clear();
				Iterator<FSATransition> j = s.getSourceTransitionsListIterator();

				while( j.hasNext() ) {
					FSATransition t = j.next();
					Set<FSATransition> ts;
					if(stateGroups.containsKey(t.getTarget())) {
						ts = stateGroups.get(t.getTarget());
					} else {
						ts = new HashSet<FSATransition>();
					}
					ts.add(t);
					stateGroups.put(t.getTarget(),ts);
				}
				groups.addAll(stateGroups.values());
			}

			Iterator<Set<FSATransition>> groupsIter = groups.iterator();
			while( groupsIter.hasNext() ) {
				wrapTransitions( groupsIter.next() );
			}

			LayoutManager.getDefaultFSMLayouter().layout(this);
			buildIntersectionDS();
		}
//		System.out.println("Graph created!");
		//Update the bezierLayoutFreeGroup
		Set<Long> setEdges = edges.keySet();
		Iterator<Long> it = setEdges.iterator();
		while(it.hasNext())
		{
			try{
			Long groupid = ((BezierLayout)edges.get(it.next()).getLayout()).getGroup();
			bezierLayoutFreeGroup = (groupid > bezierLayoutFreeGroup?groupid:bezierLayoutFreeGroup);
			}catch(ClassCastException e){
				//No problem... that happened because we can have initial edges amongst the edges
			}
		}
		this.refresh();
	}

	/**
	 * Returns a pointer to itself.
	 * @return a pointer to itself 
	 */
	public FSAGraph getGraph()
	{
		return this;
	}

	public void release()
	{
		//TODO add more things necessary to release memory
		fsa.removeSubscriber(this);
	}

	/**
	 * Builds the data structure used to compute the intersection of a point 
	 * or rectangle with elements of the graph. 
	 * 
	 * TODO Reimplement this as a quad tree.
	 * Currently uses a bunch of maps to store each element type.
	 * Note that the order in which these maps are checked within the intesection methods is important.
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
		while(children.hasNext()) {
			el = (GraphElement)children.next();
			CircleNode n;

			if(el instanceof CircleNode) {	
				n = (CircleNode)el;
				nodes.put(new Long(n.getId()), n);

				Iterator nodeChildren = n.children();
				while(nodeChildren.hasNext()) {
					el = (GraphElement)nodeChildren.next();
					if(el instanceof Edge) {
						Edge edge = (Edge)el;
						edges.put(new Long(edge.getId()), edge);
						edgeLabels.put(edge.getId(), edge.getLabel());
					}					
				}

			} else if ( el instanceof GraphLabel ) {
				GraphLabel label = (GraphLabel)el;
				freeLabels.put(label.getId(), label);
			}
		}
	}

	public String getName()	{
		return fsa.getName();
	}

	public void setName(String name) {
		fsa.setName(name);
	}

//	public String getDecoratedName() {
//	return ( needsSave ? "* " : "" ) + getName();
//	}


	/**
	 * Sets a flag to indicate that this graph needs to refresh itself
	 * because it is out of sync with its underlying model or some of its elements
	 * are out of sync.
	 */
	public void setNeedsRefresh( boolean b ) {
		super.setNeedsRefresh(b);
	}

//	/**
//	* Tells the graph that it needs to be saved to file. 
//	*  
//	* @param b 
//	*/
//	public void setNeedsSave( boolean b ) {			
//	needsSave = b;
//	}

	/**
	 * Returns true iff this graph needs to be saved to file. 
	 * 
	 * @return true iff this graph needs to be saved to file.
	 */
	public boolean needsSave() {
		return needsSave;
	}

	public FSAModel getModel() {
		return fsa;
	}

	public Class getModelInterface()
	{
		return FSAModel.class;
	}


	/**
	 * Returns the set of all nodes in the graph.
	 * 
	 * @return the set of all nodes in the graph
	 */
	public Collection<CircleNode> getNodes() {
		return nodes.values();
	}

	public Node getNode(long id) {
		return nodes.get(new Long(id));
	}

	/**
	 * Returns the set of all edges in the graph.
	 * 
	 * @return the set of all edges in the graph
	 */
	public Collection<Edge> getEdges() {
		return edges.values();
	}

	/**
	 * Returns the set of all free labels in the graph.
	 * 
	 * @return the set of all free labels in the graph
	 */
	public Collection<GraphLabel> getFreeLabels() {
		return freeLabels.values();
	}

	/**
	 * Builds this graph from the elements in <code>fsa</code>. 
	 * 
	 * TODO 
	 * Build this graph in LayoutDataParser
	 * Build free labels (those not associated with elements of the automaton).
	 * Replace the intersection lists with a quadtree. 
	 */
	private void initializeGraph() {		

		for( CircleNode n : nodes.values() ) {
			((CircleNodeLayout)n.getLayout()).dispose();
		}

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

		while( iter.hasNext() ) {
			s = (State)iter.next();
			CircleNodeLayout nL = (CircleNodeLayout)s.getAnnotation(Annotable.LAYOUT);
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
		while( iter.hasNext() ) {						
			t = (Transition)iter.next();

			// get the source and target nodes
			n1 = nodes.get(new Long(t.getSource().getId()));
			n2 = nodes.get(new Long(t.getTarget().getId()));

			// if the edge corresponding to t already exists,
			// and its layout is the same
			// add t to the edge's set of transitions	
			// FIXME Only finds the first one; need to keep searching until find one with matching layout.
			e = existingEdge(t);
			// = directedEdgeBetween(n1, n2); 
			BezierLayout layout = (BezierLayout)t.getAnnotation(Annotable.LAYOUT);
			if( e != null ) {		
				e.addTransition(t);	

				FSAEvent tEvent = t.getEvent();
				if (tEvent != null)	{
					e.addEventName(tEvent.getSymbol());
				}

			}else{

				// get the graphic data for the transition and all associated events
				// construct the edge
				if(n1 == n2) {
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
		for( Edge edge : edges.values() )	{
			edgeLabels.put(edge.getId(), edge.getLabel());
		}

		// add all intialArrows to the set of edges
		for( Node node : nodes.values() )	{
			if( node.getState().isInitial() ) {
				edges.put( node.getInitialArrow().getId(), node.getInitialArrow() );
			}
		}

		// TODO for all free labels in layout data structure

		// clear all dirty bits in the graph structure		
		refresh();
	}

	/** 
	 * Returns the edge representing the given transition if it exists,
	 * otherwise returns null.
	 *  
	 * TODO Will need a method like this in LayoutDataParser.
	 * 
	 * @param t a transition
	 * @return the edge representing the given transition, null if no such edge
	 */
	private Edge existingEdge(Transition t) {		
		BezierLayout layout = (BezierLayout)t.getAnnotation(Annotable.LAYOUT);
		for( Edge e : edges.values() ) {			
			if( e.getSourceNode() != null && e.getSourceNode().getState().equals(t.getSource()) 
					&& e.getTargetNode() != null && e.getTargetNode().getState().equals(t.getTarget())
					&& e.getLayout().equals(layout) ) {
				return e;
			}
		}		
		return null;
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
	 * If <code>p</code> is not contained within the boundary of <code>e</code>'s source node,
	 * adds a new node at point <code>p</code> and completes the edge from 
	 * <code>e</code>'s source node to the new node.
	 *
	 * @param e the edge to be finished
	 * @param p the location of the new node
	 */
	public void finishEdgeAndCreateTargetNode(BezierEdge e, Point2D.Float p) {	
		if( ! e.getSourceNode().intersects(p) ){
			finishEdge( e, createNode(p) );
		}
	}

	/**
	 * Updates the given edge from node <code>n1</code> to node <code>n2</code>.
	 * and a adds a new transition to the automaton.
	 * 
	 * @param e 
	 * @param target	
	 */
	public void finishEdge(BezierEdge e, CircleNode target) {

		e.setTargetNode(target);			
		e.computeEdge();	

		// Distribute multiple directed edges between same node pair.
//		Set<Edge> neighbours = getEdgesBetween(target, e.getSourceNode());
//		if( neighbours.size() > 0 ) {
//		e.insertAmong(neighbours);
//		// TODO commit the layout for modified straight edge (if any)
//		}


		// Preliminary code for arcing a newly created edge around any obstructing
		// nodes.
		// Commented out while I work out the kinks.  CLM
//		if (e.isStraight()) {
//		Set<Node> intersections = new HashSet<Node>();
//		for (Node n : nodes.values()) {
//		if (!n.equals(e.getSourceNode()) && !n.equals(e.getTargetNode())
//		&& e.getBezierLayout().curve.intersects(n.bounds())) {
//		intersections.add(n);
//		}
//		}
//		if (!intersections.isEmpty()) {
//		e.arcMore();
//		}
//		}

		e.insertAmong(getEdgesBetween(target, e.getSourceNode()));

		Transition t = new Transition(fsa.getFreeTransitionId(), fsa.getState(e.getSourceNode().getId()), fsa.getState(target.getId()));			
		e.addTransition(t);

		// NOTE must assign transition to edge before inserting edge as children of end nodes.
		e.getSourceNode().insert(e);	
		target.insert(e);		

		// avoid spurious update
		fsa.removeSubscriber(this);
		fsa.add(t);
		fsa.addSubscriber(this);

		// TO BE REMOVED /////////////////////////////
//		metaData.setLayoutData(t, e.getBezierLayout());
		//Christian: the following line is to set the layout without using metadata:
		t.setAnnotation(Annotable.LAYOUT, e.getBezierLayout());
		//////////////////////////////////////////////

		edges.put(e.getId(), e);
		edgeLabels.put(e.getId(), e.getLabel());
		setNeedsRefresh(true);

		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.EDGE,
				e.getId(), 
				e.bounds(),
				this, ""));
	}	

	/** 
	 * @param n1 the source or target node
	 * @param n2 the target or source node
	 * 
	 * @return the set of all edges connecting n1 and n2
	 */
	private Set<Edge> getEdgesBetween(Node n1, Node n2){
		Set<Edge> set = new HashSet<Edge>();
		for(Edge e : edges.values()) {	
			if((e.getSourceNode() != null && e.getTargetNode() != null)
					&& (e.getSourceNode().equals(n1) && e.getTargetNode().equals(n2) 
							|| e.getSourceNode().equals(n2) && e.getTargetNode().equals(n1)) ) {
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
		CircleNodeLayout layout = new CircleNodeLayout(uniformR,p);			
//		metaData.setLayoutData(s, layout);
//		Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
		s.setAnnotation(Annotable.LAYOUT, layout);
		fsa.removeSubscriber(this);
		fsa.add(s);
		fsa.addSubscriber(this);

		CircleNode n = new CircleNode(s, layout);	
		nodes.put(new Long(s.getId()), n);
		insert(n);
		setNeedsRefresh(true);

		Rectangle2D dirtySpot = n.adjacentBounds();

		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, 
				FSAGraphMessage.NODE,
				n.getId(), 
				dirtySpot,
				this, ""));

		labelNode(n, String.valueOf(s.getId()));
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
		CircleNodeLayout layout = new CircleNodeLayout(uniformR,p);
		if(s.isInitial())
			layout.setArrow(new Point2D.Float(1,0));

//		metaData.setLayoutData(s, layout);
//		Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
		s.setAnnotation(Annotable.LAYOUT, layout);

		CircleNode n = new CircleNode(s, layout);
		nodes.put(new Long(s.getId()), n);
		insert(n);
		setNeedsRefresh(true);		

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
			BezierLayout layout = new BezierLayout((CircleNodeLayout)n1.getLayout(), (CircleNodeLayout)n2.getLayout());
//			computes layout of new edges (default to straight edge between pair of nodes)			
			e = new BezierEdge(layout, n1, n2, t);			
		}
//		metaData.setLayoutData(t, (BezierLayout)e.getLayout());
//		Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
		t.setAnnotation(Annotable.LAYOUT, (BezierLayout)e.getLayout());
		fsa.removeSubscriber(this);
		fsa.add(t);
		fsa.addSubscriber(this);		
		n1.insert(e);
		n2.insert(e);
		edges.put(e.getId(), e);		
		edgeLabels.put(e.getId(), e.getLabel());
		setNeedsRefresh(true);
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
		if( ts.isEmpty() ) {
			return;
		}

		Iterator<FSATransition> i=ts.iterator();
		FSATransition t=i.next();
		Node n1=nodes.get(new Long(t.getSource().getId()));
		Node n2=nodes.get(new Long(t.getTarget().getId()));
		Edge e;
		if( n1.equals(n2) ) {
			// let e figure out how to place itself among its neighbours			
			e = new ReflexiveEdge(n1, t);
		} else {			
			BezierLayout layout = new BezierLayout((CircleNodeLayout)n1.getLayout(), (CircleNodeLayout)n2.getLayout());
			// computes layout of new edges (default to straight edge between pair of nodes)			
			e = new BezierEdge(layout, n1, n2, t);			
		}

		if( t.getEvent() != null ) {
			((BezierLayout)e.getLayout()).addEventName(t.getEvent().getSymbol());
		}

//		metaData.setLayoutData(t, (BezierLayout)e.getLayout());
//		Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
		t.setAnnotation(Annotable.LAYOUT, (BezierLayout)e.getLayout());
		n1.insert(e);
		n2.insert(e);
		edges.put(e.getId(), e);		
		edgeLabels.put(e.getId(), e.getLabel());
		setNeedsRefresh(true);

		while(i.hasNext()) {
			t = i.next();
			e.addTransition(t);
			if(t.getEvent() != null) {
				((BezierLayout)e.getLayout()).addEventName(t.getEvent().getSymbol());
			}
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

//	/////////////////////////////////////////////////////////////////
//	/**
//	* TODO remove this and delay committing layout changes until save. 
//	* @param selection
//	*/
//	public void commitMovement(GraphElement selection){
//	Iterator children = selection.children();
//	while(children.hasNext()){
//	PresentationElement el = (PresentationElement)children.next();
//	if(edgeLabels.containsValue(el)){
//	commitMovement((GraphLabel)el);
//	}else if(nodes.containsValue(el)){
//	commitMovement((CircleNode)el);
//	}else if(edges.containsValue(el)){
//	commitMovement((Edge)el);				
//	}else if(freeLabels.containsValue(el)){				
//	commitMovement((GraphLabel)el);
//	}
//	}
//	setNeedsRefresh(true);		
//	}

//	/**
//	* @param label
//	*/
//	private void commitMovement(GraphLabel label) {
//	// update offset vector in EdgeLayout		
//	if(label.getParent() != null){
//	try{
//	BezierEdge edge = (BezierEdge)label.getParent();
//	BezierLayout layout = edge.getBezierLayout();				
//	Iterator<FSATransition> t = edge.getTransitions();
//	while(t.hasNext()){
//	FSATransition tmpT = t.next();
////	metaData.setLayoutData(tmpT, layout);
////	Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
//	tmpT.setAnnotation(Annotable.LAYOUT, layout);
//	}
//	}catch(ClassCastException cce){}			
//	}else{ 
//	// TODO Move free label, tell MetaData
//	}
//	fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
//	FSAGraphMessage.LABEL,
//	label.getId(), 
//	label.bounds(),
//	this, "committed label movement: " + label.toString())
//	);
//	}

//	private void commitMovement(Node node){
//	// save location of node to metadata
//	State s = (State)fsa.getState(node.getId());
////	metaData.setLayoutData(s, (CircleNodeLayout)node.getLayout());
////	Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
//	s.setAnnotation(Annotable.LAYOUT, (CircleNodeLayout)node.getLayout());
//	// for all edges adjacent to node, save layout
//	Iterator<Edge> adjEdges = node.adjacentEdges();
//	while(adjEdges.hasNext()){			
//	commitMovement(adjEdges.next());
//	}	

//	node.setNeedsRefresh(true);

//	fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
//	FSAGraphMessage.NODE,
//	node.getId(), 
//	node.bounds(),
//	this, "committed node movement: " + node.toString()));
//	}

//	/**
//	* Stores the layout for the given edge for every transition represented
//	* by this edge.
//	* 
//	* @param edge
//	*/
//	private void commitMovement(Edge edge){
//	if(edge instanceof InitialArrow){ // arrow layout is stored as part of target Node's layout
//	Node node = ((Node)edge.getParent());
//	State s = (State)fsa.getState(node.getId());
////	metaData.setLayoutData(s, (CircleNodeLayout)node.getLayout());	
////	Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
//	s.setAnnotation(Annotable.LAYOUT, (CircleNodeLayout)node.getLayout());
//	}else{		
//	GraphicalLayout layout = edge.getLayout();		
//	Iterator<FSATransition> t = edge.getTransitions();
//	// for all transitions in e, save the edge layout		
//	while( t.hasNext() ) {
//	try {  // TODO need to handle other types of layout (e.g. Quad, Linear) 
//	FSATransition tmpT = t.next();
////	metaData.setLayoutData(tmpT, (BezierLayout)layout);	
////	Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
//	tmpT.setAnnotation(Annotable.LAYOUT, (BezierLayout)layout);
//	} catch ( ClassCastException cce ) {
//	System.err.println(cce.getMessage());
//	}
//	}
//	}
//	fireFSAGraphChanged( new FSAGraphMessage( FSAGraphMessage.MODIFY, 
//	FSAGraphMessage.EDGE,
//	edge.getId(), 
//	edge.bounds(),
//	this, "committed edge movement: " + edge.toString() ) );
//	}

	///////////////////////////////////////////////////////////////////

	public void setInitial(Node node, boolean b){

		node.setInitial(b);

		// add or remove the intial arrow from the set of edges
		InitialArrow arrow = node.getInitialArrow();
		if(arrow != null){
			if(b){
				edges.put(arrow.getId(), arrow);					
			}else{
				edges.remove(arrow.getId());				
			}
		}				

		// tell node it must refresh its appearance
		node.setNeedsRefresh(true);

		// save arrow to metadata
		State s = (State)fsa.getState(node.getId());
//		metaData.setLayoutData(s, (CircleNodeLayout)node.getLayout());
		//Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
		s.setAnnotation(Annotable.LAYOUT, (CircleNodeLayout)node.getLayout());

		fsa.removeSubscriber(this);
		fsa.fireFSAStructureChanged(new FSAMessage(FSAMessage.MODIFY,
				FSAMessage.STATE, node.getId(), fsa));
		fsa.addSubscriber(this);

		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.NODE,
				node.getId(), 
				node.bounds(),
				this, "set initial property: " + node.toString()));
	}

	public void setMarked(CircleNode node, boolean b){
		// update the state
		((State)node.getState()).setMarked(b);		

		// tell node it must refresh its appearance
		node.setNeedsRefresh(true);		

		fsa.removeSubscriber(this);
		fsa.fireFSAStructureChanged(new FSAMessage(FSAMessage.MODIFY,
				FSAMessage.STATE, node.getId(), fsa));
		fsa.addSubscriber(this);

		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.NODE,
				node.getId(), 
				node.bounds(),
				this, "set initial property: " + node.toString()));
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
		BezierLayout layout = (BezierLayout)edge.getLayout();
		// get the transitions for edge
		Iterator<FSATransition> trans = edge.getTransitions();
		FSATransition t;			
		// temp lists for adding or removing transitions since can't change
		// collection while iterating over it
		ArrayList<Transition> toAdd = new ArrayList<Transition>();
		ArrayList<Transition> toRemove = new ArrayList<Transition>();

		// reset the EdgeLayout's event labels
		while(trans.hasNext()){
			//TODO: remove annotation from the model element 
			trans.next().removeAnnotation(Annotable.LAYOUT);
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
		
		//replace in the layout the field EventNames:
		layout.getEventNames().clear();
		for(int i = 0; i < events.length; i++)
		{
			layout.getEventNames().add(events[i].getSymbol());
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
//			addToLayout(t, edge);	
			// set the layout data for the transition in metadata model
//			metaData.setLayoutData(t, (BezierLayout)edge.getLayout());
//			Christian - The following line is to supress the use of metadata, the above line should be erased as soon as possible.
			t.setAnnotation(Annotable.LAYOUT,layout);
		}		
		if(layout.getEventNames().size() > 1 && layout.getGroup() == BezierLayout.UNGROUPPED)
		{
			layout.setGroup(this.getFreeBezierLayoutGroup());
		}else if(layout.getEventNames().size() <= 1 && layout.getGroup() != BezierLayout.UNGROUPPED)
		{
			layout.setGroup(BezierLayout.UNGROUPPED);
		}

//		System.out.println(layout.getEventNames() + ", GroupID: " + layout.getGroup());
		setNeedsRefresh(true);
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.EDGE,
				edge.getId(), 
				edge.bounds(),
				this, "replaced events on edge label"));
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
			setNeedsRefresh(true);
			fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.REMOVE, 
					FSAGraphMessage.LABEL,
					FSAGraphMessage.UNKNOWN_ID, 
					el.bounds(),
					this, ""));
		}
	}

	/**
	 * Deletes the given node and all of its adjacent edges from the graph
	 * and the state from my underlying FSA.
	 * 
	 * @param n the node to be deleted
	 */
	private void delete(Node n){
		// delete all adjacent edges		
		Iterator<Edge> edges = n.adjacentEdges();
		while(edges.hasNext()){
			delete(edges.next());
		}
		// remove n but don't listen to update notifications since 
		// we already know about the change
		fsa.removeSubscriber(this);
		fsa.remove(n.getState());
		fsa.addSubscriber(this);

		super.remove(n);
		((CircleNodeLayout)n.getLayout()).dispose();
		nodes.remove(new Long(n.getId()));
		setNeedsRefresh(true);
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.REMOVE, 
				FSAGraphMessage.NODE,
				n.getId(), 
				n.adjacentBounds(),
				this, ""));
	}

	/**
	 * Deletes the given edge from the graph and all of its transitions
	 * from my underlying FSA.  
	 * 
	 * @param e
	 */
	private void delete(Edge e){
		if(e instanceof InitialArrow){
			setInitial(e.getTargetNode(), false);			
		}else{		
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
			setNeedsRefresh(true);		
			fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.REMOVE, 
					FSAGraphMessage.EDGE,
					e.getId(), 
					e.bounds(),
					this, ""));
		}
	}

	/**
	 * Assigns the given text the be displayed on the label of the given
	 * node. 
	 * 
	 * Precondition: <code>n</code> and <code>text</code> are not null
	 * 
	 * @param n the node to be labelled
	 * @param text the name for the node
	 */
	public void labelNode(Node n, String text){		
		State s = (State)fsa.getState(n.getId());
		n.getLayout().setText(text);
		n.getLabel().setText(text);
		////////////////////////////////////////////////////
		s.setAnnotation(Annotable.LAYOUT, (CircleNodeLayout)n.getLayout());
		s.setName(text);
		/////////////////////////////////////////////////////
		setNeedsRefresh(true);		
//		Iterator<Edge> adjEdges = n.adjacentEdges();


		//Christian: CODE REMOVED! IT IS NOT NECESSARY ANYMORE
		// /TODO remove this code as soon as possible, since it is metadata related
		//every time a node have its size changed.
//		//Updating the self-loops since its handler is changed when the label
//		//makes the node change its size.
//		while(adjEdges.hasNext()){
//		Edge itsEdge = adjEdges.next();
//		if(itsEdge.getTargetNode().equals(itsEdge.getSourceNode()))
//		{
//		this.commitMovement(itsEdge);
//		}
//		}
		//<<move it>>

		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.NODE,
				n.getId(), 
				n.bounds(),
				this, ""));

	}

	/**
	 * Adds a free label, i.e. one that is not attached to any other 
	 * graph element, displaying the given text at point <code>p</code>. 
	 * 
	 * @param text the text to be displayed 
	 * @param p the location to place the label
	 */
	public void addFreeLabel(String text, Point2D.Float p) {		
		GraphLabel label = new GraphLabel(text, p);
		freeLabels.put(label.getId(), label);
		insert(label);
		setNeedsRefresh(true);		
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
		setNeedsRefresh(true);		
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

	public void setControllable(Event event, boolean b){
		// update the event
		event.setControllable(b);
		fsa.fireFSAEventSetChanged(new FSAMessage(FSAMessage.MODIFY,
				FSAMessage.EVENT, event.getId(), fsa));
	}

	public void setObservable(Event event, boolean b){
		// update the event
		event.setObservable(b);
		fsa.fireFSAEventSetChanged(new FSAMessage(FSAMessage.MODIFY,
				FSAMessage.EVENT, event.getId(), fsa));
	}

	///////////////////////////////////////////////////////////////////////

	/**
	 * Increases the amplitude of the arc on this edge. 
	 * 
	 * @param edge
	 */
	public void arcMore(Edge edge) {
		((BezierEdge)edge).arcMore();	
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.EDGE,
				edge.getId(), 
				edge.bounds(),
				this, "increased arc of edge"));
	}

	/**
	 * Flattens the given edge by a fixed amount.
	 *  
	 * @param edge
	 */
	public void arcLess(Edge edge) {
		((BezierEdge)edge).arcLess();
		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
				FSAGraphMessage.EDGE,
				edge.getId(), 
				edge.bounds(),
				this, "reduced arc of edge"));
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
	 * If this edge is not straight and can be straightened (e.g. is not reflexive)
	 * straighten it.
	 * 
	 * @param edge
	 */
	public void straighten(Edge edge) {
		if(!edge.isStraight() && edge.canBeStraightened()){
			edge.straighten();
			fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
					FSAGraphMessage.EDGE,
					edge.getId(), 
					edge.bounds(),
					this, "straightened edge"));
		}
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
					if(edgeLabel != null)
					{
						graphBounds = graphBounds.union(edgeLabel.bounds());
					}
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


	public void translate(float x, float y) {
		super.translate(x,y);		
		// FIXME refreshBounds
//		commitMovement(this);  // fires graph changed, so don't need to do it here			
	}

	/**
	 * Computes and returns the set of graph elements contained by the given rectangle.
	 *  
	 * @param rectangle the rectangular area
	 * @return the set of graph elements contained by the given rectangle
	 */
	protected SelectionGroup getElementsContainedBy(Rectangle rectangle) {

		// NOTE that the order in which the element maps are checked is important.

		// the group of elements selected
		SelectionGroup g = new SelectionGroup();

		for(CircleNode n : nodes.values()) {
			if(rectangle.intersects(n.bounds()) ){ // TODO && do a more thorough intersection test
				g.insert(n);				
			}
		}

		for(Edge e : edges.values()) {
			if(rectangle.contains(e.bounds())){
				g.insert(e);
			}
		}

		for(GraphLabel l : freeLabels.values()) {
			if(rectangle.intersects(l.bounds())){
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
	protected GraphElement getElementIntersectedBy(Point2D p) {
		// NOTE the order in which the element maps are checked is important.

		// The element selected
		GraphElement el = null;
		int type = FSAGraphMessage.UNKNOWN_TYPE;

		for(GraphLabel gLabel : edgeLabels.values()){
			//Initial arrows can have null label
			if(gLabel != null)
			{
				if(gLabel.intersects(p))
				{
					type = FSAGraphMessage.LABEL;
					el = gLabel;				
				}
			}
		}

		// Need to check for null so that overlapping elements don't conflict for intersection.

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
	/** FSAGraphPublisher part which maintains a collection of, and 
	 * sends change notifications to, all interested observers (subscribers). 
	 */
	private ArrayList<FSAGraphSubscriber> subscribers = new ArrayList<FSAGraphSubscriber>();

	//////////////////////////////////////////////////////////////////////// 
	/** LayoutShellPublisher part which maintains a collection of, and 
	 * sends change notifications to, all interested observers (subscribers). 
	 */
	private ArrayList<LayoutShellSubscriber> mwsubscribers = new ArrayList<LayoutShellSubscriber>();

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
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(LayoutShellSubscriber subscriber) {
		mwsubscribers.add(subscriber);
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
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(LayoutShellSubscriber subscriber) {
		mwsubscribers.remove(subscriber);
	}

	/**
	 * Returns all current subscribers to this publisher.
	 * @return all current subscribers to this publisher
	 */
	public FSAGraphSubscriber[] getFSAGraphSubscribers()
	{
		return subscribers.toArray(new FSAGraphSubscriber[]{});
	}

	/**
	 * Returns all current subscribers to this publisher.
	 * @return all current subscribers to this publisher
	 */
	public LayoutShellSubscriber[] getLayoutShellSubscribers()
	{
		return mwsubscribers.toArray(new LayoutShellSubscriber[]{});
	}

	/**
	 * Notifies all subscribers that there has been a change to an element of 
	 * the save status of the graph.
	 */
	private void fireSaveStatusChanged() {
		LayoutShellMessage message=new LayoutShellMessage(
				needsSave?LayoutShellMessage.DIRTY:LayoutShellMessage.CLEAN,
						this);
		for(LayoutShellSubscriber s : mwsubscribers)	{
			s.saveStatusChanged(message);
		}		
	}

	/**
	 * Notifies all subscribers that there has been a change to an element of 
	 * this graph publisher.
	 * 
	 * @param message
	 */
	private void fireFSAGraphChanged(FSAGraphMessage message) {
		if( message.getEventType() != FSAGraphMessage.SAVE ) {
			if(!needsSave)
			{
				needsSave = true;
				fireSaveStatusChanged();
			}
		}

		for(FSAGraphSubscriber s : subscribers)	{
			s.fsaGraphChanged(message);
		}		
	}

	/**
	 * Notifies all subscribers that there has been a change to the elements  
	 * currently selected in this graph publisher.
	 * 
	 * @param message
	 */
	protected void fireFSAGraphSelectionChanged(FSAGraphMessage message) {
		for(FSAGraphSubscriber s : subscribers) {
			s.fsaGraphSelectionChanged(message);
		}
	}


	/* ***********************************************************************
	 * The following methods implement the FSASubscriber features that respond 
	 * to change notifications from the FSAmodel this graph represents.  
	 * ***********************************************************************/

	/* (non-Javadoc)
	 * @see observer.FSASubscriber#fsaPersistenceChanged(observer.FSAMessage)
	 */
	public void fsaSaved() {
		needsSave = false;
//		fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.SAVE, 
//		FSAGraphMessage.GRAPH,
//		this.getId(), 
//		this.bounds(),
//		this, ""));
		fireSaveStatusChanged();
	}

	/* (non-Javadoc)
	 * @see observer.FSMSubscriber#fsmStructureChanged(observer.FSMMessage)
	 */
	public void fsaStructureChanged(FSAMessage message) {
		// TODO if one can isolate the change just modify the structure as required
		// e.g. properties set on states or events.
		// and only refresh the affected part of the graph
		// Currently just rebuilds the whole graph, which is clearly inefficient.

		int elementType = message.getElementType();

		switch( elementType ) {
		case FSAMessage.STATE:


			/*fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.???, 
					FSMGraphMessage.???,
					?.getId(), 
					?.bounds(),
					this, ""));
			 */	
			break;

		case FSAMessage.TRANSITION:

			if( message.getEventType() == FSAMessage.REMOVE ) {
				Long tId = message.getElementId();
				removeTransition(tId);				
			}			
			break;

		case FSAMessage.EVENT:

			/*fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.???, 
			FSMGraphMessage.???,
			?.getId(), 
			?.bounds(),
			this, ""));
			 */
			break;

		default: // otherwise rebuild the graph structure

			initializeGraph();

		/*fireFSMGraphChanged(new FSMGraphMessage(FSMGraphMessage.???, 
			FSMGraphMessage.???,
			?.getId(), 
			?.bounds(),
			this, ""));
		 */	

		}		

	}

	/**
	 * Removes the transition with the given id from its edge
	 * and if it is the only transition on that edge, deletes the edge.
	 * 
	 * @param id  identity of the the transition to remove
	 */
	private void removeTransition( Long id ) {				

		Edge edge = null;

		for( Edge e : edges.values() ) {

			Iterator<FSATransition> trans = e.getTransitions();
			FSATransition t = null;

			while( trans.hasNext() && edge == null ) {
				t = trans.next();						
				if( id.equals( t.getId() ) ) {					
					edge = e;				
				}					
			}	

			if( edge != null ) {
				edge.removeTransition( t );				
				edge.setNeedsRefresh( true );

				FSAGraphMessage message;
				if( edge.transitionCount() == 0 ) {
					delete( edge );
					message = new FSAGraphMessage( FSAGraphMessage.REMOVE,
							FSAGraphMessage.EDGE,
							edge.getId(),
							edge.bounds(),
							this );
				} else {
					message = new FSAGraphMessage( FSAGraphMessage.MODIFY,
							FSAGraphMessage.EDGE,
							edge.getId(),
							edge.bounds(),
							this );
				}			
				fireFSAGraphChanged(message);
			}
		}		 
	}

	/**
	 * 
	 * @see FSASubscriber#fsaEventSetChanged(FSAMessage)
	 */
	public void fsaEventSetChanged(FSAMessage message) {
		// Remove any edges that have only one transition and
		// that transition is fired by the removed event

		// NOTE See removeTransition and fsaStructureChanged.		
	}
	///////////////////////////////////////////////////////////////////////


	/**
	 * TODO: comment and format
	 * FIXME: move this to a plugin that reads/writes composition data for states
	 * @author Lenko Grigorov
	 */
	public void labelCompositeNodes()
	{
		if(fsa.getAnnotation(Annotable.COMPOSED_OF)==null)
			return;
		if(((String[])fsa.getAnnotation(Annotable.COMPOSED_OF)).length>1)
		{
			FSAGraph[] gs=new FSAGraph[((String[])fsa.getAnnotation(Annotable.COMPOSED_OF)).length];
			for(int i=0;i<gs.length;++i)
			{
				FSAGraph g=(FSAGraph)Hub.getWorkspace().getLayoutShellById(((String[])fsa.getAnnotation(Annotable.COMPOSED_OF))[i]);
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
					////////////////////////////////////////////////////
					s.setAnnotation(Annotable.LAYOUT, (CircleNodeLayout)n.getLayout());
					/////////////////////////////////////////////////////
					setNeedsRefresh(true);		
					fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
							FSAGraphMessage.NODE,
							n.getId(), 
							n.bounds(),
							this, ""));
				}
			}
		}
		else if(((String[])fsa.getAnnotation(Annotable.COMPOSED_OF)).length==1)
		{
			FSAGraph g=(FSAGraph)Hub.getWorkspace().getLayoutShellById(((String[])fsa.getAnnotation(Annotable.COMPOSED_OF))[0]);
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
					////////////////////////////////////////////////////
					s.setAnnotation(Annotable.LAYOUT, (CircleNodeLayout)n.getLayout());
					/////////////////////////////////////////////////////
					setNeedsRefresh(true);		
					fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, 
							FSAGraphMessage.NODE,
							n.getId(), 
							n.bounds(),
							this, ""));
				}
			}
		}
	}
	
	/**
	 * TODO comment and format
	 * 
	 * @author Lenko Grigorov
	 */
	protected class UniformRadius extends HashMap<CircleNodeLayout,Float>
	{
		protected float r = CircleNodeLayout.DEFAULT_RADIUS;
		protected void updateUniformRadius(CircleNodeLayout n, float radius)
		{
			put(n,new Float(radius));
			updateUniformRadius();
		}
		protected void updateUniformRadius()
		{
			if(size()>0)
				r=Float.MIN_VALUE;
			else
				r=CircleNodeLayout.DEFAULT_RADIUS;
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
	private long getFreeBezierLayoutGroup()
	{
		return ++bezierLayoutFreeGroup;
	}
}


