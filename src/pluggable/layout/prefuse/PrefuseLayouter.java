/**
 * 
 */
package pluggable.layout.prefuse;

// import prefuse.Visualization;
// import prefuse.action.Action;
// import prefuse.action.layout.CircleLayout;
// import prefuse.action.layout.GridLayout;
// import prefuse.action.layout.graph.ForceDirectedLayout;
// import prefuse.action.layout.graph.FruchtermanReingoldLayout;
// import prefuse.activity.Activity;
// import prefuse.data.Graph;
// import prefuse.util.collections.IntIterator;
// import prefuse.visual.NodeItem;
// import prefuse.visual.VisualGraph;
// import prefuse.visual.VisualItem;

/**
 * @author Lenko Grigorov
 */
public class PrefuseLayouter {// implements FSMLayouter {

    /*
     * (non-Javadoc)
     * 
     * @see pluggable.layout.FSMLayouter#layout(presentation.fsa.FSMGraph)
     */
    // public void layout(FSMGraph graph) {
    // BridgeMapper.edgeMap.clear();
    // BridgeMapper.nodeMap.clear();
    // Graph g=new Graph(true);
    // for(Node n:graph.getNodes())
    // {
    // prefuse.data.Node pn=g.addNode();
    // //NodeBridge nb=new NodeBridge(n,g);
    // BridgeMapper.nodeMap.put(n,pn);
    // BridgeMapper.nodeMapInverse.put(pn,n);
    // }
    // for(Edge e:graph.getEdges())
    // {
    // //prefuse.data.Edge pe=
    // g.addEdge(BridgeMapper.nodeMap.get(e.getSource()),
    // BridgeMapper.nodeMap.get(e.getTarget()));
    // //BridgeMapper.edgeMap.put(e,pe);
    // }
    // Visualization v=new Visualization();
    // VisualGraph vg=v.addGraph("graph",g);
    // for(Iterator<prefuse.data.Node> i=g.nodes();i.hasNext();)
    // {
    // prefuse.data.Node n=i.next();
    // VisualItem vn=v.getVisualItem("graph",n);
    // CircleNode gn=(CircleNode)BridgeMapper.nodeMapInverse.get(n);
    //// vn.setBounds(vn.getX()-gn.getRadius(),vn.getY()-gn.getRadius(),200,200);
    // // vn.setX(0);
    // // vn.setStartX(-200);
    // // vn.setEndX(200);
    // // vn.setY(0);
    // // vn.setStartY(-200);
    // // vn.setEndY(200);
    // System.out.println(""+vn.getBounds());
    // }
    // GridLayout cl=new GridLayout("graph",10,10);
    // //cl.setIterations(10);
    // cl.setLayoutBounds(new Rectangle2D.Float(0,0,100,100));
    // //int[] dim=gl.analyzeGraphGrid(g.getNodes());
    // //gl.setNumCols(dim[1]);
    // //gl.setNumRows(dim[0]);
    // Action a=v.putAction("layout",cl);
    // a.run(0);
    // System.out.println(v.getBounds("graph"));
    // for(Iterator<prefuse.data.Node> i=g.nodes();i.hasNext();)
    // {
    // prefuse.data.Node n=i.next();
    // VisualItem vn=v.getVisualItem("graph",n);
    // Node gn=BridgeMapper.nodeMapInverse.get(n);
    // gn.setLocation(new
    // Point2D.Float((float)vn.getX()*14,(float)vn.getY()*14));
    // System.out.println(""+vn.getBounds()+","+vn.getX()+","+vn.getY()+",
    // "+gn.bounds()+","+gn.getLocation());
    // }
    //// graph.translate((float)-v.getBounds("graph").getX(),(float)-v.getBounds(
    // "graph").getY());
    // System.out.println("foo");
    // }
}
