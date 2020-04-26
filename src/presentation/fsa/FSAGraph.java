package presentation.fsa;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ides.api.core.Annotable;
import ides.api.core.Hub;
import ides.api.latex.LatexPresentation;
import ides.api.model.fsa.FSAMessage;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSASubscriber;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.presentation.Presentation;
import util.AnnotationKeys;
import util.BooleanUIBinder;

/**
 * A recursive structure used to view, draw and modify the graph representation
 * of an Automaton. Given a point or rectangular area, computes intersections
 * with graph elements. Observes and updates the Automaton. Updates the
 * graphical visualization metadata and synchronizes it with the Automaton
 * model. Is observed and updated by the GraphView and GraphDrawingView.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class FSAGraph extends GraphElement implements FSASubscriber, Annotable {

    private static final String BINDER = "binder";

    private long bezierLayoutFreeGroup = 0;

    // This flag is set to true when the FSAGraph is a result of an automatic
    // DES operation and this result has more than 100 states
    private boolean avoidLayoutDrawing;

    public boolean isAvoidLayoutDrawing() {
        return avoidLayoutDrawing;
    }

    protected float fontSize;

    protected UniformRadius uniformR = new UniformRadius();

    public boolean isUseUniformRadius() {
        return ((GraphLayout) fsa.getAnnotation(AnnotationKeys.LAYOUT)).getUseUniformRadius();
    }

    public void setUseUniformRadius(boolean b) {
        // BooleanUIBinder binder = ((GraphLayout)fsa
        // .getAnnotation(Annotable.LAYOUT)).getUseUniformRadius();
        BooleanUIBinder binder = (BooleanUIBinder) fsa.getAnnotation(BINDER);
        if (b != binder.get()) {
            binder.set(b);
            ((GraphLayout) fsa.getAnnotation(AnnotationKeys.LAYOUT)).setUseUniformRadius(b);
            setNeedsRefresh(true);
            fireFSAGraphChanged(
                    new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.GRAPH, -1, getBounds(false), this));
        }
    }

    public BooleanUIBinder getUseUniformRadiusBinder() {
        return (BooleanUIBinder) fsa.getAnnotation(BINDER);
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fs) {
        fontSize = fs;
        GraphLayout lay = (GraphLayout) fsa.getAnnotation(AnnotationKeys.LAYOUT);
        lay.setFontSize(fs);
        fsa.metadataChanged();
        setNeedsRefresh(true);
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.GRAPH,
                FSAGraphMessage.UNKNOWN_ID, getBounds(false), this));
    }

    protected Set<Presentation> hooks = new HashSet<Presentation>();

    public int hookCount() {
        return hooks.size();
    }

    public void addHook(Presentation p) {
        hooks.add(p);
    }

    public void removeHook(Presentation p) {
        hooks.remove(p);
    }

    /**
     * Maps used in searches of intersection TODO replace with Quadtree data
     * structure
     */
    private HashMap<Long, Node> nodes;

    private HashMap<Long, Edge> edges;

    private HashMap<Long, GraphLabel> freeLabels;

    private HashMap<Long, GraphLabel> edgeLabels; // use parent edge's id as

    // key

    /**
     * The system data model
     */
    private FSAModel fsa; // system model

    /**
     * Creates a graph model from the given system data model using an automatic
     * layout tool.
     * 
     * @param fsa the mathematical model
     */
    public FSAGraph(FSAModel fsa) {
        // if (!(fsa instanceof Automaton))
        // {
        // throw new RuntimeException("FSAGraph can only layout Automatons");
        // }
        this.fsa = /* (Automaton) */fsa;
        fsa.addSubscriber(this);

        // test for global layout
        if (!fsa.hasAnnotation(AnnotationKeys.LAYOUT)) {
            fsa.setAnnotation(AnnotationKeys.LAYOUT, new GraphLayout());
        }

        if (!fsa.hasAnnotation(BINDER)) {
            BooleanUIBinder b = new BooleanUIBinder();
            b.set(((GraphLayout) fsa.getAnnotation(AnnotationKeys.LAYOUT)).getUseUniformRadius());
            fsa.setAnnotation(BINDER, b);
        }

        nodes = new HashMap<Long, Node>();
        edges = new HashMap<Long, Edge>();
        edgeLabels = new HashMap<Long, GraphLabel>();
        freeLabels = new HashMap<Long, GraphLabel>();

        long statesCounter = fsa.getStateCount();

        avoidLayoutDrawing = (statesCounter > 100 ? true : false);

        if (avoidLayoutDrawing) {
            return;
        }

        initializeGraph();
    }

    protected void wrapAutomaton() {
        for (Node n : nodes.values()) {
            if (n instanceof CircleNode) {
                ((CircleNodeLayout) n.getLayout()).dispose();
            }
        }

        this.clear();

        nodes.clear();
        edges.clear();
        edgeLabels.clear();
        freeLabels.clear();

        bezierLayoutFreeGroup = 0;

        // Prepare elements for automatic layout
        Set<Set<FSATransition>> groups = new HashSet<Set<FSATransition>>();
        HashMap<FSAState, Set<FSATransition>> stateGroups = new HashMap<FSAState, Set<FSATransition>>();
        Iterator<FSAState> i = fsa.getStateIterator();

        while (i.hasNext()) {
            FSAState s = i.next();
            // Labeling the states according to the id:
            if (s.getName() == null) {
                s.setName(String.valueOf(s.getId()));
            }
            wrapState(s, new Point2D.Float(0, 0));// (float)Math.random()*200,(
            // float
            // )Math.random()*200));
            stateGroups.clear();
            Iterator<FSATransition> j = s.getOutgoingTransitionsListIterator();

            while (j.hasNext()) {
                FSATransition t = j.next();
                Set<FSATransition> ts;
                if (stateGroups.containsKey(t.getTarget())) {
                    ts = stateGroups.get(t.getTarget());
                } else {
                    ts = new HashSet<FSATransition>();
                }
                ts.add(t);
                stateGroups.put(t.getTarget(), ts);
            }
            groups.addAll(stateGroups.values());
        }

        Iterator<Set<FSATransition>> groupsIter = groups.iterator();
        while (groupsIter.hasNext()) {
            wrapTransition(groupsIter.next());
        }

        FSAGraphLayouter.layout(this);

        // collect all labels on edges
        for (Edge edge : edges.values()) {
            edgeLabels.put(edge.getId(), edge.getLabel());
        }

    }

    /**
     * Returns a pointer to itself.
     * 
     * @return a pointer to itself
     */
    @Override
    public FSAGraph getGraph() {
        return this;
    }

    public void release() {
        // TODO add more things necessary to release memory
        fsa.removeSubscriber(this);
    }

    public String getName() {
        return fsa.getName();
    }

    public void setName(String name) {
        fsa.setName(name);
    }

    public FSAModel getModel() {
        return fsa;
    }

    /**
     * Returns the set of all nodes in the graph.
     * 
     * @return the set of all nodes in the graph
     */
    public Collection<Node> getNodes() {
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
     * Builds this graph from the elements in <code>fsa</code>. TODO Build this
     * graph in LayoutDataParser Build free labels (those not associated with
     * elements of the automaton). Replace the intersection lists with a quadtree.
     */
    private void initializeGraph() {

        for (Node n : nodes.values()) {
            if (n instanceof CircleNode) {
                ((CircleNodeLayout) n.getLayout()).dispose();
            }
        }

        this.clear();

        nodes.clear();
        edges.clear();
        edgeLabels.clear();
        freeLabels.clear();

        fontSize = ((GraphLayout) fsa.getAnnotation(AnnotationKeys.LAYOUT)).getFontSize();

        // Testing annotations:
        boolean hasLayout = true;
        Iterator<FSAState> sIt = fsa.getStateIterator();
        sIt = fsa.getStateIterator();
        while (sIt.hasNext()) {
            FSAState s = sIt.next();
            CircleNodeLayout l = (CircleNodeLayout) s.getAnnotation(AnnotationKeys.LAYOUT);
            if (l == null) {
                hasLayout = false;
                break;
            }
            // set this FSAGraph's uniform radius management
            l.setUniformRadius(uniformR);
            // Avoid give the name "" to the state!
            if (l.getText() != "") {
                s.setName(l.getText());
            }
            // Create a new node
            CircleNode node = new CircleNode(s, l, false);
            long id = s.getId();
            nodes.put(id, node);
            if (s.isInitial()) {
                // Insert the initial arrow among the egdes
                edges.put(node.getInitialArrow().getId(), node.getInitialArrow());
            }
            l.setText(s.getName());
            // Insert the node in the graph
            insert(node);
        }

        // ///////////////////
        if (!hasLayout)// Generate automatic layout:
        {
            wrapAutomaton();
            return;
        }

        // Add the edges to the FSAGraph

        // start by computing greatest edge group id
        long maxGroup = 0;
        for (Iterator<FSATransition> i = fsa.getTransitionIterator(); i.hasNext();) {
            FSATransition t = i.next();
            if (t.getAnnotation(AnnotationKeys.LAYOUT) != null) {
                BezierLayout l = (BezierLayout) t.getAnnotation(AnnotationKeys.LAYOUT);
                if (l.getGroup() != BezierLayout.UNGROUPPED && l.getGroup() > maxGroup) {
                    maxGroup = l.getGroup();
                }
            }
        }

        HashMap<Long, BezierEdge> groupsMap = new HashMap<Long, BezierEdge>();
        Iterator<FSATransition> tIt = fsa.getTransitionIterator();
        while (tIt.hasNext()) {
            FSATransition t = tIt.next();
            BezierLayout l = (BezierLayout) t.getAnnotation(AnnotationKeys.LAYOUT);
            if (l == null) {
                // check if it's a simple self-loop
                if (t.getSource() == t.getTarget()) {
                    // attach to existing self-loop if possible
                    boolean attached = false;
                    for (Iterator<FSATransition> i = t.getSource().getOutgoingTransitionsListIterator(); i.hasNext();) {
                        FSATransition et = i.next();
                        if (et == t || et.getAnnotation(AnnotationKeys.LAYOUT) == null) {
                            continue;
                        }
                        if (et.getSource() == et.getTarget()) {
                            BezierLayout el = (BezierLayout) et.getAnnotation(AnnotationKeys.LAYOUT);
                            t.setAnnotation(AnnotationKeys.LAYOUT, el);
                            if (el.getGroup() == BezierLayout.UNGROUPPED) {
                                maxGroup++;
                                el.setGroup(maxGroup);
                                if (el.getEdge() != null) {
                                    // add to group map otherwise a second edge
                                    // will be created later
                                    groupsMap.put(maxGroup, el.getEdge());
                                }
                            }
                            attached = true;
                            break;
                        }
                    }
                    if (!attached) {
                        Set<FSATransition> set = new HashSet<FSATransition>();
                        set.add(t);
                        wrapTransition(set);
                        continue;
                    }
                    l = (BezierLayout) t.getAnnotation(AnnotationKeys.LAYOUT);
                }
                // not a self-loop
                else {
                    hasLayout = false;
                    break;
                }
            }
            // find source and target nodes
            Node src = nodes.get(t.getSource().getId());
            Node dst = nodes.get(t.getTarget().getId());
            if (src == null || dst == null) {
                hasLayout = false;
                break;
            }
            if (l.getGroup() != BezierLayout.UNGROUPPED) {
                // find edge group
                BezierEdge existingEdge = groupsMap.get(l.getGroup());
                if (existingEdge == null) {
                    if (src != dst) {
                        existingEdge = new BezierEdge(l, src, dst, t);
                        // add this edge among the childs of its source and
                        // target
                        src.insert(existingEdge);
                        dst.insert(existingEdge);
                    } else {
                        // Create a reflexive edge
                        existingEdge = new ReflexiveEdge(l, src, t);
                        // add this edge among the childs of its source and
                        // target
                        src.insert(existingEdge);
                    }
                    // Put the edge in the set of edges
                    edges.put(existingEdge.getId(), existingEdge);
                    groupsMap.put(l.getGroup(), existingEdge);
                } else {
                    existingEdge.addTransition(t);
                }
                t.setAnnotation(AnnotationKeys.LAYOUT, existingEdge.getLayout());
            } else {
                BezierEdge edge;
                if (src != dst) {
                    edge = new BezierEdge(l, src, dst, t);
                    // add this edge among the childs of its source and target
                    src.insert(edge);
                    dst.insert(edge);
                } else {
                    // Create a reflexive edge
                    edge = new ReflexiveEdge(l, src, t);
                    // add this edge among the childs of its source and target
                    src.insert(edge);
                }
                // Put the edge in the set of edges
                edges.put(edge.getId(), edge);
            }
        }

        // ///////////////////
        if (!hasLayout)// Generate automatic layout:
        {
            wrapAutomaton();
            return;
        }

        // collect all labels on edges
        for (Edge edge : edges.values()) {
            edgeLabels.put(edge.getId(), edge.getLabel());
        }

        // TODO for all free labels in layout data structure

        // updating free edge group
        Set<Long> setEdges = edges.keySet();
        Iterator<Long> it = setEdges.iterator();
        while (it.hasNext()) {
            try {
                Long groupid = ((BezierLayout) edges.get(it.next()).getLayout()).getGroup();
                bezierLayoutFreeGroup = (groupid > bezierLayoutFreeGroup ? groupid : bezierLayoutFreeGroup);
            } catch (ClassCastException e) {
                // No problem... that happened because we can have initial edges
                // amongst the edges
            }
        }

        // clear all dirty bits in the graph structure
        // also sets the font size in GraphLabels
        refresh();
    }

    /**
     * @param n1 the source or target node
     * @param n2 the target or source node
     * @return the set of all edges connecting n1 and n2
     */
    public Set<Edge> getEdgesBetween(Node n1, Node n2) {
        Set<Edge> set = new HashSet<Edge>();
        for (Edge e : edges.values()) {
            if ((e.getSourceNode() != null && e.getTargetNode() != null)
                    && (e.getSourceNode().equals(n1) && e.getTargetNode().equals(n2)
                            || e.getSourceNode().equals(n2) && e.getTargetNode().equals(n1))) {
                set.add(e);
            }
        }
        return set;
    }

    ////////////////////////////////////////////////////////////////////////////
    // /

    /**
     * Creates a new node with centre at the given point and a adds a new state to
     * the automaton.
     * 
     * @param p the centre point for the new node
     * @return the node added
     */
    public CircleNode createNode(Point2D.Float p) {
        FSAState s = fsa.assembleState();// new State(fsa.getFreeStateId());
        s.setInitial(false);
        s.setMarked(false);
        CircleNodeLayout layout = new CircleNodeLayout(uniformR, p);
        s.setAnnotation(AnnotationKeys.LAYOUT, layout);
        fsa.removeSubscriber(this);
        fsa.add(s);
        fsa.addSubscriber(this);

        CircleNode n = new CircleNode(s, layout);
        nodes.put(new Long(s.getId()), n);
        insert(n);
        setNeedsRefresh(true);

        Rectangle2D dirtySpot = n.adjacentBounds();

        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.ADD, FSAGraphMessage.NODE, n.getId(), dirtySpot, this, ""));

        labelNode(n, String.valueOf(s.getId()));
        return n;
    }

    /**
     * Reinstates a node. If the node is already in the graph, the method will have
     * no effect.
     * 
     * @param node the node to be reinstated
     */
    public void reviveNode(Node node) {
        // Return if the graph already countains the node
        if (nodes.containsValue(node)) {
            return;
        }

        FSAState s = node.getState();
        fsa.removeSubscriber(this);
        fsa.add(s);
        fsa.addSubscriber(this);

        // Insert the new node to the graph
        nodes.put(new Long(node.getId()), node);
        insert(node);

        if (node.getState().isInitial()) {
            node.insert(node.getInitialArrow());
        }
        Rectangle2D dirtySpot = node.adjacentBounds();

        Iterator<GraphElement> it = node.children();
        while (it.hasNext()) {
            GraphElement ge = it.next();
            if (ge instanceof Edge && !(ge instanceof InitialArrow)) {
                reviveEdge((Edge) ge);
            }
        }

        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.ADD, FSAGraphMessage.NODE, node.getId(), dirtySpot, this, ""));
    }

    public void reviveEdge(Edge edge) {
        if (edges.containsValue(edge)) {
            return;
        }
        long id_dest = edge.getTargetNode().getState().getId();
        FSAState s = edge.getSourceNode().getState();
        long id_source = s.getId();

        Node n1 = nodes.get(id_source);
        Node n2 = nodes.get(id_dest);

        fsa.removeSubscriber(this);
        for (Iterator<FSATransition> i = edge.getTransitions(); i.hasNext();) {
            fsa.add(i.next());
        }
        fsa.addSubscriber(this);
        n1.insert(edge);
        n2.insert(edge);
        edges.put(edge.getId(), edge);
        edgeLabels.put(edge.getId(), edge.getLabel());
        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.ADD, FSAGraphMessage.EDGE, edge.getId(), edge.bounds(), this, ""));
    }

    /**
     * Creates a new node which wraps the provided automaton state.
     * 
     * @param s automaton state to be wrapped
     * @param p the centre point for the new node
     * @return the node added
     */
    public CircleNode wrapState(FSAState s, Point2D.Float p) {
        CircleNodeLayout layout = new CircleNodeLayout(uniformR, p);
        if (s.isInitial()) {
            layout.setArrow(new Point2D.Float(1, 0));
        }
        layout.setText(s.getName());

        // metaData.setLayoutData(s, layout);
        // Christian - The following line is to supress the use of metadata, the
        // above line should be erased as soon as possible.
        s.setAnnotation(AnnotationKeys.LAYOUT, layout);

        CircleNode n = new CircleNode(s, layout);
        nodes.put(new Long(s.getId()), n);
        insert(n);

        if (n.getState().isInitial()) {
            // Insert the initial arrow among the egdes
            edges.put(n.getInitialArrow().getId(), n.getInitialArrow());
        }

        setNeedsRefresh(true);

        Rectangle2D dirtySpot = n.adjacentBounds();
        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.ADD, FSAGraphMessage.NODE, n.getId(), dirtySpot, this, ""));
        return n;
    }

    /**
     * Creates a new edge from node <code>n1</code> to node <code>n2</code>. and a
     * adds a new transition to the automaton.
     * 
     * @param n1 source node
     * @param n2 target node
     */
    public BezierEdge createEdge(Node n1, Node n2) {
        FSATransition t = fsa.assembleEpsilonTransition(fsa.getState(n1.getId()).getId(),
                fsa.getState(n2.getId()).getId());// new
        // Transition(fsa.getFreeTransitionId(),
        // fsa
        // .getState(n1.getId()), fsa.getState(n2.getId()));
        Edge e;
        if (n1.equals(n2)) {
            // let e figure out how to place itself among its neighbours
            e = new ReflexiveEdge(n1, t);
        } else {
            BezierLayout layout = new BezierLayout((CircleNodeLayout) n1.getLayout(),
                    (CircleNodeLayout) n2.getLayout());
            // computes layout of new edges (default to straight edge between
            // pair of nodes)
            e = new BezierEdge(layout, n1, n2, t);
            ((BezierEdge) e).insertAmong(getEdgesBetween(e.getSourceNode(), e.getTargetNode()));
        }
        // Set the BezierLayout as an annotation for the edge
        t.setAnnotation(AnnotationKeys.LAYOUT, e.getLayout());
        fsa.removeSubscriber(this);
        fsa.add(t);
        fsa.addSubscriber(this);
        n1.insert(e);
        n2.insert(e);
        edges.put(e.getId(), e);
        edgeLabels.put(e.getId(), e.getLabel());
        setNeedsRefresh(true);
        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.ADD, FSAGraphMessage.EDGE, e.getId(), e.bounds(), this, ""));
        return (BezierEdge) e;
    }

    /**
     * Creates a new edge between the nodes which correspond to the states between
     * which is the first transition in the provided set. All transitions from the
     * set are wrapped by the edge.
     * 
     * @param ts the set of transitions to be wrapped
     */
    public void wrapTransition(Set<FSATransition> ts) {
        if (ts.isEmpty()) {
            return;
        }

        Iterator<FSATransition> i = ts.iterator();
        FSATransition t = i.next();
        Node n1 = nodes.get(new Long(t.getSource().getId()));
        Node n2 = nodes.get(new Long(t.getTarget().getId()));
        BezierEdge e;
        if (n1.equals(n2)) {
            // let e figure out how to place itself among its neighbours
            e = new ReflexiveEdge(n1, t);
        } else {
            BezierLayout layout = new BezierLayout((CircleNodeLayout) n1.getLayout(),
                    (CircleNodeLayout) n2.getLayout());
            // computes layout of new edges (default to straight edge between
            // pair of nodes)
            e = new BezierEdge(layout, n1, n2, t);
        }

        if (t.getEvent() != null) {
            ((BezierLayout) e.getLayout()).addEventName(t.getEvent().getSymbol());
        }

        // Set the BezierLayout as an annotation for the edge
        t.setAnnotation(AnnotationKeys.LAYOUT, e.getLayout());
        n1.insert(e);
        n2.insert(e);
        edges.put(e.getId(), e);
        edgeLabels.put(e.getId(), e.getLabel());
        setNeedsRefresh(true);

        // if there is more than one transition on the edge
        if (ts.size() > 1) {
            e.getBezierLayout().setGroup(getFreeBezierLayoutGroup());
        }

        while (i.hasNext()) {
            t = i.next();
            e.addTransition(t);
            t.setAnnotation(AnnotationKeys.LAYOUT, e.getLayout());
            if (t.getEvent() != null) {
                ((BezierLayout) e.getLayout()).addEventName(t.getEvent().getSymbol());
            }
        }

        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.ADD, FSAGraphMessage.EDGE, e.getId(), e.bounds(), this, ""));
    }

    public void setInitial(Node node, boolean b) {
        node.setInitial(b);
        // add or remove the intial arrow from the set of edges
        InitialArrow arrow = node.getInitialArrow();
        if (arrow != null) {
            if (b) {
                edges.put(arrow.getId(), arrow);
            } else {
                edges.remove(arrow.getId());
            }
        }
        // update the state
        node.getState().setInitial(b);
        // tell the node that it must refresh its appearance
        node.setNeedsRefresh(true);

        // save the arrow to metadata
        FSAState s = fsa.getState(node.getId());
        // Set the Layout as an annotation for the model element
        s.setAnnotation(AnnotationKeys.LAYOUT, node.getLayout());

        fsa.removeSubscriber(this);
        fsa.fireFSAStructureChanged(new FSAMessage(FSAMessage.MODIFY, FSAMessage.STATE, node.getId(), fsa));
        fsa.addSubscriber(this);

        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.NODE, node.getId(),
                node.bounds(), this, "set initial property: " + node.toString()));
    }

    public void setMarked(Node node, boolean b) {
        // update the state
        node.getState().setMarked(b);
        // tell node it must refresh its appearance
        node.setNeedsRefresh(true);

        fsa.removeSubscriber(this);
        fsa.fireFSAStructureChanged(new FSAMessage(FSAMessage.MODIFY, FSAMessage.STATE, node.getId(), fsa));
        fsa.addSubscriber(this);

        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.NODE, node.getId(),
                node.bounds(), this, "set initial property: " + node.toString()));
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
     * that are not in the given list and commits any changes to the LayoutData
     * (MetaData).
     * 
     * @param events a non-null, non-empty list of FSA events
     * @param edge   the edge to which the edges will be assigned
     */
    public void replaceEventsOnEdge(SupervisoryEvent[] events, Edge edge) {

        List<FSATransition> toAdd = new ArrayList<FSATransition>();
        List<FSATransition> toRemove = new ArrayList<FSATransition>();

        Set<SupervisoryEvent> allEvents = new HashSet<SupervisoryEvent>();
        Set<SupervisoryEvent> newEvents = new HashSet<SupervisoryEvent>();
        for (SupervisoryEvent e : events) {
            allEvents.add(e);
            newEvents.add(e);
        }

        Iterator<FSATransition> trans = edge.getTransitions();
        while (trans.hasNext()) {
            FSATransition t = trans.next();

            if (!allEvents.contains(t.getEvent())) {
                toRemove.add(t);
            } else {
                newEvents.remove(t.getEvent());
            }
        }

        if (newEvents.size() > 0 || edge.transitionCount() - toRemove.size() > 0) {
            for (SupervisoryEvent e : newEvents) {
                toAdd.add(fsa.assembleTransition(edge.getSourceNode().getState().getId(),
                        edge.getTargetNode().getState().getId(), e.getId())); // new
                // Transition(fsa.getFreeTransitionId(),
                // edge
                // .getSourceNode().getState(), edge
                // .getTargetNode().getState(), e));
            }
        } else
        // we'll be removing all events, so leave only one "empty" transition
        {
            toAdd.add(fsa.assembleEpsilonTransition(edge.getSourceNode().getState().getId(),
                    edge.getTargetNode().getState().getId()));// new
            // Transition(fsa.getFreeTransitionId(),
            // edge
            // .getSourceNode().getState(), edge
            // .getTargetNode().getState(), null));
        }

        addAndRemoveTransitions(toRemove, toAdd, edge);
    }

    public void replaceTransitionsOnEdge(List<FSATransition> transitions, Edge edge) {
        List<FSATransition> oldTrans = new ArrayList<FSATransition>();
        for (Iterator<FSATransition> i = edge.getTransitions(); i.hasNext();) {
            oldTrans.add(i.next());
        }
        addAndRemoveTransitions(oldTrans, transitions, edge);
    }

    protected void addAndRemoveTransitions(List<FSATransition> toRemove, List<FSATransition> toAdd, Edge edge) {
        BezierLayout layout = (BezierLayout) edge.getLayout();
        // get the transitions for edge
        Iterator<FSATransition> trans;

        fsa.removeSubscriber(this);

        // remove extra transitions
        Iterator<FSATransition> iter = toRemove.iterator();
        while (iter.hasNext()) {
            FSATransition t = iter.next();
            edge.removeTransition(t);
            fsa.remove(t);
        }

        // add transitions to accommodate added events
        iter = toAdd.iterator();
        while (iter.hasNext()) {
            FSATransition t = iter.next();
            // add the transition to the edge
            edge.addTransition(t);
            // add the transition to the FSA
            fsa.add(t);
        }

        fsa.addSubscriber(this);

        // Update the event labels in the layout
        List<SupervisoryEvent> events = new ArrayList<SupervisoryEvent>();
        trans = edge.getTransitions();
        while (trans.hasNext()) {
            FSATransition t = trans.next();
            events.add(t.getEvent());
            // add the transition data to the layout
            // addToLayout(t, edge);
            // set the layout data for the transition in metadata model
            // metaData.setLayoutData(t, (BezierLayout)edge.getLayout());
            // Christian - The following line is to supress the use of metadata,
            // the above line should be erased as soon as possible.
            t.setAnnotation(AnnotationKeys.LAYOUT, layout);
        }
        // replace in the layout the field EventNames:
        layout.getEventNames().clear();
        for (SupervisoryEvent e : events) {
            if (e != null) {
                layout.getEventNames().add(e.getSymbol());
            }
        }

        if (layout.getEventNames().size() > 1 && layout.getGroup() == BezierLayout.UNGROUPPED) {
            layout.setGroup(this.getFreeBezierLayoutGroup());
        } else if (layout.getEventNames().size() <= 1 && layout.getGroup() != BezierLayout.UNGROUPPED) {
            layout.setGroup(BezierLayout.UNGROUPPED);
        }

        // System.out.println(layout.getEventNames() + ", GroupID: " +
        // layout.getGroup());
        setNeedsRefresh(true);
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE, edge.getId(),
                edge.bounds(), this, "replaced events on edge label"));
    }

    public void delete(GraphElement el) {
        if (el instanceof Node) {
            delete((Node) el);
        }
        if (el instanceof Edge) {
            delete((Edge) el);
        }
    }

    /**
     * Deletes the given node and all of its adjacent edges from the graph and the
     * state from my underlying FSA.
     * 
     * @param n the node to be deleted
     */
    private void delete(Node n) {
        // delete all adjacent edges
        Iterator<Edge> edges = n.adjacentEdges();
        while (edges.hasNext()) {
            delete(edges.next());
        }

        // remove n but don't listen to update notifications since
        // we already know about the change
        fsa.removeSubscriber(this);
        fsa.remove(n.getState());
        fsa.addSubscriber(this);

        remove(n);
        if (n instanceof CircleNode) {
            ((CircleNodeLayout) n.getLayout()).dispose();
        }
        nodes.remove(new Long(n.getId()));
        setNeedsRefresh(true);
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.REMOVE, FSAGraphMessage.NODE, n.getId(),
                n.adjacentBounds(), this, ""));
    }

    /**
     * Deletes the given edge from the graph and all of its transitions from my
     * underlying FSA.
     * 
     * @param e
     */
    private void delete(Edge e) {
        fsa.removeSubscriber(this);
        Iterator<FSATransition> transitions = e.getTransitions();
        while (transitions.hasNext()) {

            fsa.remove(transitions.next());
        }
        fsa.addSubscriber(this);
        Node source = e.getSourceNode();
        if (source != null) {
            source.remove(e);
        }
        Node target = e.getTargetNode();
        if (target != null) {
            target.remove(e);
        }
        edgeLabels.remove(e.getId());
        edges.remove(e.getId());
        setNeedsRefresh(true);
        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.REMOVE, FSAGraphMessage.EDGE, e.getId(), e.bounds(), this, ""));
    }

    /**
     * Assigns the given text the be displayed on the label of the given node.
     * Precondition: <code>n</code> and <code>text</code> are not null
     * 
     * @param n    the node to be labelled
     * @param text the name for the node
     */
    public void labelNode(Node n, String text) {
        // get uniform radius to compare if there was change
        float originalRadius = uniformR.getRadius();
        FSAState s = fsa.getState(n.getId());
        n.getLayout().setText(text);
        n.getLabel().setText(text);
        s.setAnnotation(AnnotationKeys.LAYOUT, n.getLayout());
        s.setName(text);
        // this is needed to update the uniform radius database
        n.refresh();
        if (isUseUniformRadius() && originalRadius != uniformR.getRadius()) {
            setNeedsRefresh(true);
            fireFSAGraphChanged(
                    new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.GRAPH, -1, getBounds(false), this));
        } else {
            fireFSAGraphChanged(
                    new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.NODE, n.getId(), n.bounds(), this));
        }
    }

    /**
     * Adds a free label, i.e. one that is not attached to any other graph element,
     * displaying the given text at point <code>p</code>.
     * 
     * @param text the text to be displayed
     * @param p    the location to place the label
     */
    public void addFreeLabel(String text, int fontSize, Point2D.Float p) {
        GraphLabel label = new GraphLabel(text, fontSize, p);
        freeLabels.put(label.getId(), label);
        insert(label);
        setNeedsRefresh(true);
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.ADD, FSAGraphMessage.LABEL, FSAGraphMessage.UNKNOWN_ID,
                label.bounds(), this, ""));
    }

    /**
     * @param freeLabel
     * @param text
     */
    public void setLabelText(GraphLabel freeLabel, String text) {
        freeLabel.setText(text);
        setNeedsRefresh(true);
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.LABEL,
                FSAGraphMessage.UNKNOWN_ID, freeLabel.bounds(), this, ""));
    }

    /**
     * @param symbol
     * @param controllable
     * @param observable
     * @return the new Event
     */
    public SupervisoryEvent createAndAddEvent(String symbol, boolean controllable, boolean observable) {
        SupervisoryEvent event = fsa.assembleEvent(symbol);// new
        // Event(fsa.getFreeEventId());
        // event.setSymbol(symbol);
        event.setControllable(controllable);
        event.setObservable(observable);
        fsa.add(event);
        return event;
    }

    public void setControllable(SupervisoryEvent event, boolean b) {
        // update the event
        event.setControllable(b);
        fsa.fireFSAEventSetChanged(new FSAMessage(FSAMessage.MODIFY, FSAMessage.EVENT, event.getId(), fsa));
    }

    public void setObservable(SupervisoryEvent event, boolean b) {
        // update the event
        event.setObservable(b);
        fsa.fireFSAEventSetChanged(new FSAMessage(FSAMessage.MODIFY, FSAMessage.EVENT, event.getId(), fsa));
    }

    // /////////////////////////////////////////////////////////////////////

    /**
     * Increases the amplitude of the arc on this edge.
     * 
     * @param edge
     */
    public void arcMore(Edge edge) {
        if (!(edge instanceof BezierEdge)) {
            return;
        }
        ((BezierEdge) edge).arcMore();
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE, edge.getId(),
                edge.bounds(), this, "increased arc of edge"));
    }

    /**
     * Flattens the given edge by a fixed amount.
     * 
     * @param edge
     */
    public void arcLess(Edge edge) {
        if (!(edge instanceof BezierEdge)) {
            return;
        }
        ((BezierEdge) edge).arcLess();
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE, edge.getId(),
                edge.bounds(), this, "reduced arc of edge"));
    }

    public void symmetrize(Edge edge) {
        if (!(edge instanceof BezierEdge)) {
            return;
        }
        ((BezierEdge) edge).symmetrize();
        // TODO include edge label in bounds (dirty spot)
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE, edge.getId(),
                edge.bounds(), this, "symmetrized edge"));
    }

    /**
     * If this edge is not straight and can be straightened (e.g. is not reflexive)
     * straighten it.
     * 
     * @param edge
     */
    public void straighten(Edge edge) {
        if (!edge.isStraight() && edge.canBeStraightened()) {
            edge.straighten();
            fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE, edge.getId(),
                    edge.bounds(), this, "straightened edge"));
        }
    }

    /**
     * Calculates the size of the bounding box necessary for the entire graph.
     * Visits every node, edge and label and uses the union of their bounds to
     * create the box.
     * <p>
     * author Sarah-Jane Whittaker
     * <p>
     * author Lenko Grigorov
     * 
     * @param initAtZeroZero Whether you want the box to begin at (0, 0) (true) or
     *                       tightly bound around the graph (false)
     * @return Rectangle The bounding box for the graph
     */
    public Rectangle getBounds(boolean initAtZeroZero) {
        if (needsRefresh()) {
            refresh();
        }

        Rectangle graphBounds = initAtZeroZero ? new Rectangle() : getElementBounds();

        // Start with the nodes
        for (Node graphNode : nodes.values()) {
            graphBounds = graphBounds.union(graphNode.bounds());
        }

        for (Edge graphEdge : edges.values()) {
            graphBounds = graphBounds.union(graphEdge.bounds());
        }

        for (GraphLabel edgeLabel : edgeLabels.values()) {
            if (edgeLabel != null) {
                graphBounds = graphBounds.union(edgeLabel.bounds());
            }
        }

        for (GraphLabel freeLabel : freeLabels.values()) {
            graphBounds = graphBounds.union(freeLabel.bounds());
        }

        return graphBounds;
    }

    /**
     * TODO: Comment!
     * 
     * @author Sarah-Jane Whittaker
     */
    private Rectangle getElementBounds() {
        for (Node graphNode : nodes.values()) {
            return graphNode.bounds();
        }

        for (Edge graphEdge : edges.values()) {
            return ((BezierEdge) graphEdge).bounds();
        }

        for (GraphLabel freeLabel : freeLabels.values()) {
            return freeLabel.bounds();
        }

        return new Rectangle();
    }

    @Override
    public void translate(float x, float y) {
        super.translate(x, y);
        // FIXME refreshBounds
        // commitMovement(this); // fires graph changed, so don't need to do it
        // here
    }

    /**
     * Computes and returns the set of graph elements contained by the given
     * rectangle.
     * 
     * @param rectangle the rectangular area
     * @return the set of graph elements contained by the given rectangle
     */
    protected SelectionGroup getElementsContainedBy(Rectangle rectangle) {
        if (needsRefresh()) {
            refresh();
        }

        // NOTE that the order in which the element maps are checked is
        // important.

        // the group of elements selected
        SelectionGroup g = new SelectionGroup();

        for (Node n : nodes.values()) {
            if (rectangle.intersects(n.bounds())) { // TODO && do a more thorough intersection test
                g.insert(n);
            }
        }

        for (Edge e : edges.values()) {
            if (rectangle.contains(e.bounds())) {
                g.insert(e);
            }
        }

        for (GraphLabel l : freeLabels.values()) {
            if (rectangle.intersects(l.bounds())) {
                g.insert(l);
            }
        }

        fireFSAGraphSelectionChanged(
                new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.SELECTION, g.getId(), g.bounds(), this));

        return g;
    }

    /**
     * Computes and returns the graph element intersected by the given point. If
     * nothing hit, returns null.
     * 
     * @param p the point of intersection
     * @return the graph element intersected by the given point or null if nothing
     *         hit.
     */
    protected GraphElement getElementIntersectedBy(Point2D p) {
        if (needsRefresh()) {
            refresh();
        }

        // NOTE the order in which the element maps are checked is important.

        // The element selected
        GraphElement el = null;
        int type = FSAGraphMessage.UNKNOWN_TYPE;

        for (GraphLabel gLabel : edgeLabels.values()) {
            // Initial arrows can have null label
            if (gLabel != null) {
                if (gLabel.intersects(p)) {
                    type = FSAGraphMessage.LABEL;
                    el = gLabel;
                }
            }
        }

        // Need to check for null so that overlapping elements don't conflict
        // for intersection.

        if (el == null) {
            for (Edge e : edges.values()) {
                if (e.intersects(p)) {
                    type = FSAGraphMessage.EDGE;
                    el = e;
                }
            }
        }

        if (el == null) {
            for (Node n : nodes.values()) {
                if (n.intersects(p)) {
                    type = FSAGraphMessage.NODE;
                    el = n;
                }
            }
        }

        if (el == null) {
            for (GraphLabel l : freeLabels.values()) {
                if (l.intersects(p)) {
                    type = FSAGraphMessage.LABEL;
                    el = l;
                }
            }
        }

        if (el != null) {
            fireFSAGraphSelectionChanged(
                    new FSAGraphMessage(FSAGraphMessage.MODIFY, type, el.getId(), el.bounds(), this));
        }

        return el;
    }

    /**
     * Computes and returns the node intersected by the given point. If nothing hit,
     * returns null.
     * 
     * @param p the point of intersection
     * @return the node intersected by the given point or null if nothing hit.
     */
    protected Node getNodeIntersectedBy(Point2D p) {
        if (needsRefresh()) {
            refresh();
        }

        for (Node n : nodes.values()) {
            if (n.intersects(p)) {
                return n;
            }
        }

        return null;
    }

    public boolean isRenderingOn() {
        if (!Hub.getLatexManager().isLatexEnabled()) {
            return false;
        }
        for (Presentation p : hooks) {
            if (p instanceof LatexPresentation) {
                if (((LatexPresentation) p).isAllowedRendering()) {
                    return true;
                }
            }
        }
        return false;
    }

    // //////////////////////////////////////////////////////////////////////
    /**
     * FSAGraphPublisher part which maintains a collection of, and sends change
     * notifications to, all interested observers (subscribers).
     */
    private ArrayList<FSAGraphSubscriber> subscribers = new ArrayList<FSAGraphSubscriber>();

    // //////////////////////////////////////////////////////////////////////

    /**
     * Attaches the given subscriber to this publisher. The given subscriber will
     * receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void addSubscriber(FSAGraphSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * Removes the given subscriber to this publisher. The given subscriber will no
     * longer receive notifications of changes from this publisher.
     * 
     * @param subscriber
     */
    public void removeSubscriber(FSAGraphSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * Returns all current subscribers to this publisher.
     * 
     * @return all current subscribers to this publisher
     */
    public FSAGraphSubscriber[] getFSAGraphSubscribers() {
        return subscribers.toArray(new FSAGraphSubscriber[] {});
    }

    /**
     * Notifies all subscribers that there has been a change to an element of this
     * graph publisher.
     * 
     * @param message
     */
    public void fireFSAGraphChanged(FSAGraphMessage message) {
        fsa.metadataChanged();
        for (FSAGraphSubscriber s : subscribers) {
            s.fsaGraphChanged(message);
        }
    }

    /**
     * Notifies all subscribers that there has been a change to the elements
     * currently selected in this graph publisher.
     * 
     * @param message
     */
    public void fireFSAGraphSelectionChanged(FSAGraphMessage message) {
        for (FSAGraphSubscriber s : subscribers) {
            s.fsaGraphSelectionChanged(message);
        }
    }

    /***************************************************************************
     * The following methods implement the FSASubscriber features that respond to
     * change notifications from the FSAmodel this graph represents.
     **************************************************************************/

    /*
     * (non-Javadoc)
     * 
     * @see observer.FSMSubscriber#fsmStructureChanged(observer.FSMMessage)
     */
    public void fsaStructureChanged(FSAMessage message) {
        // TODO if one can isolate the change just modify the structure as
        // required
        // e.g. properties set on states or events.
        // and only refresh the affected part of the graph
        // Currently just rebuilds the whole graph, which is clearly
        // inefficient.

        int elementType = message.getElementType();

        switch (elementType) {
        case FSAMessage.STATE:

            break;

        case FSAMessage.TRANSITION:

            if (message.getEventType() == FSAMessage.REMOVE) {
                Long tId = message.getElementId();
                removeTransition(tId);
            }
            break;

        case FSAMessage.EVENT:

            break;

        }
        initializeGraph();

    }

    /**
     * Removes the transition with the given id from its edge and if it is the only
     * transition on that edge, deletes the edge.
     * 
     * @param id identity of the the transition to remove
     */
    private void removeTransition(Long id) {

        Edge edge = null;

        for (Edge e : new HashSet<Edge>(edges.values())) {

            Iterator<FSATransition> trans = e.getTransitions();
            FSATransition t = null;

            while (trans.hasNext() && edge == null) {
                t = trans.next();
                if (id.equals(t.getId())) {
                    edge = e;
                }
            }

            if (edge != null) {
                edge.removeTransition(t);
                edge.setNeedsRefresh(true);

                FSAGraphMessage message;
                if (edge.transitionCount() == 0) {
                    delete(edge);
                    message = new FSAGraphMessage(FSAGraphMessage.REMOVE, FSAGraphMessage.EDGE, edge.getId(),
                            edge.bounds(), this);
                } else {
                    message = new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE, edge.getId(),
                            edge.bounds(), this);
                }
                fireFSAGraphChanged(message);
            }
        }
    }

    /**
     * @see FSASubscriber#fsaEventSetChanged(FSAMessage)
     */
    public void fsaEventSetChanged(FSAMessage message) {
        for (Edge edge : edges.values()) {
            edge.setNeedsRefresh(true);
        }
        fireFSAGraphChanged(
                new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.GRAPH, -1, getBounds(false), this));
        // Remove any edges that have only one transition and
        // that transition is fired by the removed event

        // NOTE See removeTransition and fsaStructureChanged.
    }

    /**
     * TODO comment and format
     * 
     * @author Lenko Grigorov
     */
    protected class UniformRadius extends HashMap<CircleNodeLayout, Float> {
        /**
         * 
         */
        private static final long serialVersionUID = 4557208341793142849L;

        protected float r = CircleNodeLayout.DEFAULT_RADIUS;

        protected void updateUniformRadius(CircleNodeLayout n, float radius) {
            put(n, new Float(radius));
            updateUniformRadius();
        }

        protected void updateUniformRadius() {
            if (size() > 0) {
                r = Float.MIN_VALUE;
            } else {
                r = CircleNodeLayout.DEFAULT_RADIUS;
            }
            for (Float ff : values()) {
                float f = ff.floatValue();
                if (f > r) {
                    r = f;
                }
            }
        }

        public float getRadius() {
            return r;
        }

    }

    private long getFreeBezierLayoutGroup() {
        return ++bezierLayoutFreeGroup;
    }

    // This is to notify the subscribers that the layout was changed, so they
    // can repaint.
    public void commitLayoutModified(GraphElement selection) {
        if (selection == null) {
            fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.SELECTION, this.getId(),
                    this.bounds(), this, "all graph"));
            return;
        }
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.SELECTION, 0,
                selection.bounds(), this, "updating selection"));

    }

    // This is to notify the subscribers that the layout was changed, so they
    // can repaint.
    public void commitLayoutModified() {
        fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.SELECTION, this.getId(),
                this.bounds(), this, "all graph"));
    }

    /**
     * If avoidLayoutDrawing is true, this function will draw a button, so the user
     * can choose whether or not to show the layout, setting the avoidLayoutDrawing
     * to false, if desired.
     * 
     * @param g the graphics context
     */
    @Override
    public void draw(Graphics g) {
        if (!this.avoidLayoutDrawing) {
            if (needsRefresh()) {
                refresh();
            }
            super.draw(g);
        }
    }

    public void forceLayoutDisplay() {
        Cursor backupCursor = Hub.getMainWindow().getCursor();
        Hub.getMainWindow().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            for (Presentation p : hooks) {
                if (p instanceof LatexPresentation) {
                    ((LatexPresentation) p).setAllowedRendering(false);
                }
            }
            initializeGraph();
            avoidLayoutDrawing = false;
            if (Hub.getLatexManager().isLatexEnabled()) {
                for (Presentation p : hooks) {
                    if (p instanceof LatexPresentation) {
                        Hub.getLatexManager().prerenderAndRepaint((LatexPresentation) p);
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } finally {
            Hub.getMainWindow().setCursor(backupCursor);
        }
        this.getBounds(true);
    }

    protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

    public Object getAnnotation(String key) {
        return annotations.get(key);
    }

    public void setAnnotation(String key, Object annotation) {
        if (annotation != null) {
            annotations.put(key, annotation);
        }
    }

    public void removeAnnotation(String key) {
        annotations.remove(key);
    }

    public boolean hasAnnotation(String key) {
        return annotations.containsKey(key);
    }

    /**
     * Setting needed to circumvent a bug in AWT. Set to false before drawing on a
     * scaled down canvas (e.g. thumbnail).
     */
    protected boolean drawRenderedLabels = true;

    /**
     * Set if rendered labels have to be drawn.
     * 
     * @param b <code>true</code> to draw rendered labels; <code>false</code> not to
     */
    public void setDrawRenderedLabels(boolean b) {
        drawRenderedLabels = b;
    }

    /**
     * Returns if rendered labels have to be drawn. To be used by graph labels to
     * determine whether to draw or not.
     * 
     * @return <code>true</code> to draw rendered labels; <code>false</code> not to
     */
    public boolean isDrawRenderedLabels() {
        return drawRenderedLabels;
    }
}
