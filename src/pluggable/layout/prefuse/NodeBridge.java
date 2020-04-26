/**
 * 
 */
package pluggable.layout.prefuse;

// import prefuse.data.Edge;
// import prefuse.data.Graph;
// import prefuse.data.Node;
// import prefuse.data.Schema;
// import prefuse.data.Table;

/**
 * @author Lenko Grigorov
 */
public class NodeBridge {// implements Node {

    // protected presentation.fsa.Node n;
    // protected Graph g;
    //
    // public NodeBridge(presentation.fsa.Node n, Graph g)
    // {
    // this.n=n;
    // this.g=g;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getGraph()
    // */
    // public Graph getGraph() {
    // return g;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getInDegree()
    // */
    // public int getInDegree() {
    // int count=0;
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // if(i.next().getTarget()==n)
    // ++count;
    // return count;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getOutDegree()
    // */
    // public int getOutDegree() {
    // int count=0;
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // if(i.next().getSource()==n)
    // ++count;
    // return count;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getDegree()
    // */
    // public int getDegree() {
    // int count=0;
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // ++count;
    // return count;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#inEdges()
    // */
    // public Iterator inEdges() {
    // Set<Edge> edges=new HashSet<Edge>();
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // {
    // presentation.fsa.Edge edge=i.next();
    // if(edge.getTarget()==n)
    // edges.add(BridgeMapper.edgeMap.get(edge));
    // }
    // return edges.iterator();
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#outEdges()
    // */
    // public Iterator outEdges() {
    // Set<Edge> edges=new HashSet<Edge>();
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // {
    // presentation.fsa.Edge edge=i.next();
    // if(edge.getSource()==n)
    // edges.add(BridgeMapper.edgeMap.get(edge));
    // }
    // return edges.iterator();
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#edges()
    // */
    // public Iterator edges() {
    // Set<Edge> edges=new HashSet<Edge>();
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // edges.add(BridgeMapper.edgeMap.get(i.next()));
    // return edges.iterator();
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#inNeighbors()
    // */
    // public Iterator inNeighbors() {
    // Set<Node> nodes=new HashSet<Node>();
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // {
    // presentation.fsa.Edge edge=i.next();
    // if(edge.getTarget()==n)
    // nodes.add(BridgeMapper.nodeMap.get(edge.getSource()));
    // }
    // return nodes.iterator();
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#outNeighbors()
    // */
    // public Iterator outNeighbors() {
    // Set<Node> nodes=new HashSet<Node>();
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // {
    // presentation.fsa.Edge edge=i.next();
    // if(edge.getSource()==n)
    // nodes.add(BridgeMapper.nodeMap.get(edge.getTarget()));
    // }
    // return nodes.iterator();
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#neighbors()
    // */
    // public Iterator neighbors() {
    // Set<Node> nodes=new HashSet<Node>();
    // for(Iterator<presentation.fsa.Edge> i=n.adjacentEdges();i.hasNext();)
    // {
    // presentation.fsa.Edge edge=i.next();
    // if(edge.getTarget()==n)
    // nodes.add(BridgeMapper.nodeMap.get(edge.getSource()));
    // else if(edge.getSource()==n)
    // nodes.add(BridgeMapper.nodeMap.get(edge.getTarget()));
    // }
    // return nodes.iterator();
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getParent()
    // */
    // public Node getParent() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getParentEdge()
    // */
    // public Edge getParentEdge() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getDepth()
    // */
    // public int getDepth() {
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getChildCount()
    // */
    // public int getChildCount() {
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getChildIndex(prefuse.data.Node)
    // */
    // public int getChildIndex(Node arg0) {
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getChild(int)
    // */
    // public Node getChild(int arg0) {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getFirstChild()
    // */
    // public Node getFirstChild() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getLastChild()
    // */
    // public Node getLastChild() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getPreviousSibling()
    // */
    // public Node getPreviousSibling() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#getNextSibling()
    // */
    // public Node getNextSibling() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#children()
    // */
    // public Iterator children() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Node#childEdges()
    // */
    // public Iterator childEdges() {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getSchema()
    // */
    // public Schema getSchema() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getTable()
    // */
    // public Table getTable() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getRow()
    // */
    // public int getRow() {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#isValid()
    // */
    // public boolean isValid() {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getColumnType(java.lang.String)
    // */
    // public Class getColumnType(String arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getColumnType(int)
    // */
    // public Class getColumnType(int arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getColumnIndex(java.lang.String)
    // */
    // public int getColumnIndex(String arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getColumnCount()
    // */
    // public int getColumnCount() {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getColumnName(int)
    // */
    // public String getColumnName(int arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGet(java.lang.String, java.lang.Class)
    // */
    // public boolean canGet(String arg0, Class arg1) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSet(java.lang.String, java.lang.Class)
    // */
    // public boolean canSet(String arg0, Class arg1) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#get(java.lang.String)
    // */
    // public Object get(String arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#set(java.lang.String, java.lang.Object)
    // */
    // public void set(String arg0, Object arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#get(int)
    // */
    // public Object get(int arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#set(int, java.lang.Object)
    // */
    // public void set(int arg0, Object arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getDefault(java.lang.String)
    // */
    // public Object getDefault(String arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#revertToDefault(java.lang.String)
    // */
    // public void revertToDefault(String arg0) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGetInt(java.lang.String)
    // */
    // public boolean canGetInt(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSetInt(java.lang.String)
    // */
    // public boolean canSetInt(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getInt(java.lang.String)
    // */
    // public int getInt(String arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setInt(java.lang.String, int)
    // */
    // public void setInt(String arg0, int arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getInt(int)
    // */
    // public int getInt(int arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setInt(int, int)
    // */
    // public void setInt(int arg0, int arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGetLong(java.lang.String)
    // */
    // public boolean canGetLong(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSetLong(java.lang.String)
    // */
    // public boolean canSetLong(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getLong(java.lang.String)
    // */
    // public long getLong(String arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setLong(java.lang.String, long)
    // */
    // public void setLong(String arg0, long arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getLong(int)
    // */
    // public long getLong(int arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setLong(int, long)
    // */
    // public void setLong(int arg0, long arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGetFloat(java.lang.String)
    // */
    // public boolean canGetFloat(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSetFloat(java.lang.String)
    // */
    // public boolean canSetFloat(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getFloat(java.lang.String)
    // */
    // public float getFloat(String arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setFloat(java.lang.String, float)
    // */
    // public void setFloat(String arg0, float arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getFloat(int)
    // */
    // public float getFloat(int arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setFloat(int, float)
    // */
    // public void setFloat(int arg0, float arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGetDouble(java.lang.String)
    // */
    // public boolean canGetDouble(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSetDouble(java.lang.String)
    // */
    // public boolean canSetDouble(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getDouble(java.lang.String)
    // */
    // public double getDouble(String arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setDouble(java.lang.String, double)
    // */
    // public void setDouble(String arg0, double arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getDouble(int)
    // */
    // public double getDouble(int arg0) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setDouble(int, double)
    // */
    // public void setDouble(int arg0, double arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGetBoolean(java.lang.String)
    // */
    // public boolean canGetBoolean(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSetBoolean(java.lang.String)
    // */
    // public boolean canSetBoolean(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getBoolean(java.lang.String)
    // */
    // public boolean getBoolean(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setBoolean(java.lang.String, boolean)
    // */
    // public void setBoolean(String arg0, boolean arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getBoolean(int)
    // */
    // public boolean getBoolean(int arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setBoolean(int, boolean)
    // */
    // public void setBoolean(int arg0, boolean arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGetString(java.lang.String)
    // */
    // public boolean canGetString(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSetString(java.lang.String)
    // */
    // public boolean canSetString(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getString(java.lang.String)
    // */
    // public String getString(String arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setString(java.lang.String, java.lang.String)
    // */
    // public void setString(String arg0, String arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getString(int)
    // */
    // public String getString(int arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setString(int, java.lang.String)
    // */
    // public void setString(int arg0, String arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canGetDate(java.lang.String)
    // */
    // public boolean canGetDate(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#canSetDate(java.lang.String)
    // */
    // public boolean canSetDate(String arg0) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getDate(java.lang.String)
    // */
    // public Date getDate(String arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setDate(java.lang.String, java.util.Date)
    // */
    // public void setDate(String arg0, Date arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#getDate(int)
    // */
    // public Date getDate(int arg0) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Tuple#setDate(int, java.util.Date)
    // */
    // public void setDate(int arg0, Date arg1) {
    // // TODO Auto-generated method stub
    //
    // }

}
