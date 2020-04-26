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
public class EdgeBridge {// implements Edge {

    // protected presentation.fsa.Edge e;
    // protected Graph g;
    //
    // public EdgeBridge(presentation.fsa.Edge e, Graph g)
    // {
    // this.e=e;
    // this.g=g;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Edge#getGraph()
    // */
    // public Graph getGraph() {
    // return g;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Edge#isDirected()
    // */
    // public boolean isDirected() {
    // return true;
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Edge#getSourceNode()
    // */
    // public Node getSourceNode() {
    // return BridgeMapper.nodeMap.get(e.getSource());
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Edge#getTargetNode()
    // */
    // public Node getTargetNode() {
    // return BridgeMapper.nodeMap.get(e.getTarget());
    // }
    //
    // /* (non-Javadoc)
    // * @see prefuse.data.Edge#getAdjacentNode(prefuse.data.Node)
    // */
    // public Node getAdjacentNode(Node arg0) {
    // if(arg0==BridgeMapper.nodeMap.get(e.getSource()))
    // return BridgeMapper.nodeMap.get(e.getTarget());
    // else if(arg0==BridgeMapper.nodeMap.get(e.getTarget()))
    // return BridgeMapper.nodeMap.get(e.getSource());
    // else
    // throw new RuntimeException("Input node is not adjacent.");
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
    //
}
