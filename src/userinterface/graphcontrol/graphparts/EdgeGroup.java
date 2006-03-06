/*
 * Created on Jun 21, 2004
 */
package userinterface.graphcontrol.graphparts;

import java.util.Vector;

import userinterface.geometric.Point;
import userinterface.geometric.UnitVector;

/**
 * An EdgeGroup represents a connection between two Nodes. There may be an
 * unlimited number of Edges in an EdgeGroup, and the Nodes are not necessarily
 * distinct (as in the case of a self loop). This grouping allows localized
 * auto-configuration of edges between two nodes. Note that since both the
 * individual Edges and the EdgeGroup maintian pointers to the start and end
 * nodes, deletion of edges is somwhat complicated.
 * 
 * @author Michael Wood
 */
public class EdgeGroup{
    /**
     * List of the Edges between two Nodes. Usually there is just one edge per
     * EdgeGroup, but there can be many. Each Edge knows in which Node it
     * originates and in which Node it terminates.
     */
    private Vector<Edge> edge_list = null;

    /**
     * The nodes. These may be equal for the purpose of a self loop. node1 is
     * the n1 of the origional edge, and serves as an absolute reference for
     * direction.
     */
    private Node node1 = null, node2 = null;

    /**
     * Some actions may modify the exclusion flag. Node actions can choose to
     * include an edgegroup based on the exclusion flag. This is used in group
     * movement.
     */
    public boolean exclusive = false;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EdgeGroup construction
    // /////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the EdgeGroup
     * 
     * @param n1
     *            One of the two Nodes. These may be equal for the purpose of a
     *            self loop.
     * @param n2
     *            One of the two Nodes. These may be equal for the purpose of a
     *            self loop.
     * @param e
     *            The Edge between n1 and n2. The Edge knows in which Node it
     *            originates and in which Node it terminates.
     */
    public EdgeGroup(Node n1, Node n2, Edge e){
        edge_list = new Vector<Edge>();
        edge_list.addElement(e);
        node1 = n1;
        node2 = n2;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EdgeGroup modification
    // /////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add another Edge to this group. The Edge knows in which Node it
     * originates and in which Node it terminates.
     * 
     * @param e
     *            The Edge to be added.
     */
    public void addEdge(Edge e){
        edge_list.addElement(e);
    }

    /**
     * Recalculate all Edges in this EdgeGroup. This is used, for example, when
     * a Node is moved.
     */
    public void recalculate(){
        for(int i = 0; i < edge_list.size(); i++){
            ((Edge) edge_list.elementAt(i)).addAttribute(GraphObject.SIMPLE);
            ((Edge) edge_list.elementAt(i)).autoConfigureCurve();
        }
    }

    /**
     * Tell all edges to accomodiate their labels.
     */
    public void accomodateLabels(){
        for(int i = 0; i < edge_list.size(); i++){
            ((Edge) edge_list.elementAt(i)).accomodateLabel();
        }
    }

    /**
     * Initiate all edges for movement by node position.
     */
    public void initiateNodeMovement(int attribute){
        for(int i = 0; i < edge_list.size(); i++){
            ((Edge) edge_list.elementAt(i)).initiateNodeMovement(attribute);
        }
    }

    /**
     * Recalculate all Edges in this EdgeGroup, based on node movement.
     * 
     * @param n
     *            The initiating Node.
     * @param origional_configuration
     *            The configuration of the Node at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     */
    public void updateNodeMovement(Node n, Configuration origional_configuration, Point mouse){
        for(int i = 0; i < edge_list.size(); i++){
            ((Edge) edge_list.elementAt(i)).updateNodeMovement(n, origional_configuration, mouse);
        }
    }

    /**
     * Terminate movement by node position for all edges.
     */
    public void terminateNodeMovement(int attribute){
        for(int i = 0; i < edge_list.size(); i++){
            ((Edge) edge_list.elementAt(i)).terminateNodeMovement(attribute);
        }
    }

    /**
     * Delete the specified Edge from this EdgeGroup. If it is the only Edge,
     * delete the entire EdgeGroup.
     * 
     * @param edge
     *            The Edge to be deleted.
     */
    public void removeEdge(Edge edge){
        edge_list.remove(edge);
        if(edge_list.size() < 1){
            edge_list = null;
            node1.removeEdgeGroup(this);
            node2.removeEdgeGroup(this);
            node1 = null;
            node2 = null;
        }
    }

    /**
     * Delete this EdgeGroup.
     */
    public void delete(){
        if(edge_list != null){
            ((Edge) edge_list.elementAt(0)).delete();
            delete();
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miscelaneous ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public UnitVector newUnitVector(Edge this_edge){
        UnitVector d = new UnitVector(0, -1);
        Edge e = null;
        for(int i = 0; i < edge_list.size(); i++){
            e = (Edge) edge_list.elementAt(i);
            if(e.isSelfLoop() && e != this_edge){
                d = new UnitVector();
                break;
            }
        }
        return d;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EdgeGroup information //////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Test to see if a given Node is node1. This provides an arbitrary and
     * absolute direction, used for edge synchronization.
     * 
     * @param n
     *            The Node being tested.
     * @return true if node1 is n.
     */
    public boolean isStartNode(Node n){
        return node1 == n;
    }

    /**
     * Test to see if the given Node is involved in this EdgeGroup.
     * 
     * @param n
     *            The Node being tested.
     * @return true if n is involved in this EdgeGroup.
     */
    public boolean hasNode(Node n){
        return node1 == n || node2 == n;
    }

    /**
     * Test to see if the given Nodes are involved in this EdgeGroup.
     * 
     * @param n1
     *            A Node being tested.
     * @param n2
     *            A Node being tested.
     * @return true if n1 and n2 are involved in this EdgeGroup.
     */
    public boolean hasNodes(Node n1, Node n2){
        return (node1 == n1 && node2 == n2) || (node1 == n2 && node2 == n1);
    }

    /**
     * Test to see if there are an odd number of edges in this edge group.
     * 
     * @return true if there are an odd number of edges in this edge group.
     */
    public boolean hasOddEdges(){
        return edge_list.size() % 2 == 1;
    }

    /**
     * Find an Edges order within this EdgeGroup.
     * 
     * @param e
     *            The Edge being tested.
     * @return The order of e in this EdgeGroup.
     */
    public int indexOf(Edge e){
        return edge_list.indexOf(e);
    }

    /**
     * Check out how many levels there are in this edge group. edges = 1 ->
     * populated levels: 0. edges = 2 -> populated levels: 1. edges = 3 ->
     * populated levels: 0,1. edges = 4 -> populated levels: 1,2.
     * 
     * @return The largest level number.
     */
    public int levels(){
        return (int) Math.ceil(edge_list.size() - 1 / 2.0);
    }

    /**
     * Add all edges in this group to the given list.
     * 
     * @param valid_edges
     *            A list of edges.
     */
    public void addToList(Vector<Edge> valid_edges){
        for(int i = 0; i < edge_list.size(); i++){
            valid_edges.add(edge_list.elementAt(i));
        }
    }
}