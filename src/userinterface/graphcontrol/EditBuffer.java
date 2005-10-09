/*
 * Created on Dec 7, 2004
 */
package userinterface.graphcontrol;

import java.util.Vector;

import org.eclipse.swt.graphics.Rectangle;

import userinterface.GraphingPlatform;
import userinterface.geometric.Box;
import userinterface.geometric.Point;
import userinterface.graphcontrol.graphparts.Edge;
import userinterface.graphcontrol.graphparts.Node;

/**
 * The EditBuffer keeps cloned copies of subsets of the GraphModel for use in
 * copy/paste.
 * 
 * @author MichaelWood
 */
public class EditBuffer{
    /**
     * The platform in which this EditBuffer will exist.
     */
    private GraphingPlatform gp = null;

    /**
     * A list of cloned nodes currently held by this buffer.
     */
    private Vector<Node> node_list = null;

    /**
     * A list of cloned edges currently held by this buffer.
     */
    private Vector<Edge> edge_list = null;

    /**
     * Records if the buffer is holding anything to paste.
     */
    public boolean has_contents = false;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EditBuffer construction
    // ////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the EditBuffer.
     * 
     * @param gp
     *            The platform in which this EditBuffer will exist.
     */
    public EditBuffer(GraphingPlatform gp){
        this.gp = gp;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miscelaneous
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void copyCollection(){
        node_list = new Vector<Node>();
        edge_list = new Vector<Edge>();
        gp.gc.gpc.cloneCollection(node_list, edge_list);
        has_contents = true;
    }

    /**
     * Paste the parts collection into the graph. The pasted collection will be
     * centered in the screen, unless the origin values are non zero, in which
     * case it will be centered at the given origin values.
     * 
     * @param origin_x
     *            An optional origin for the paste location instead of the
     *            centre-screen position.
     * @param origin_y
     *            An optional origin for the paste location instead of the
     *            centre-screen position.
     */
    public void pasteCollection(int origin_x, int origin_y){
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        Node n = null;
        Edge e = null;

        gp.gc.gpc.abandonGroupHistory();

        // paste the nodes
        for(int i = 0; i < node_list.size(); i++){
            n = (Node) node_list.elementAt(i);
            gp.gc.gm.addNode(n);
            gp.gc.gpc.addToGrouping(n);
            if(x1 == x2){
                // first node
                x1 = n.getX() - n.getR();
                y1 = n.getY() - n.getR();
                x2 = n.getX() + n.getR();
                y2 = n.getY() + n.getR();
            }
            else{
                // grow
                if(n.getX() - n.getR() < x1) x1 = n.getX() - n.getR();
                if(n.getY() - n.getR() < y1) y1 = n.getY() - n.getR();
                if(n.getX() + n.getR() > x2) x2 = n.getX() + n.getR();
                if(n.getY() + n.getR() > y2) y2 = n.getY() + n.getR();
            }
        }

        // paste the edges
        for(int i = 0; i < edge_list.size(); i++){
            e = (Edge) edge_list.elementAt(i);
            gp.gc.gm.addEdge(e);
            gp.gc.gpc.addToGrouping(e);
        }

        // bounding boxes

        Box area = new Box(x1 - SelectionArea.PADDING, y1 - SelectionArea.PADDING, x2
                + SelectionArea.PADDING, y2 + SelectionArea.PADDING);
        gp.gc.group_area.highlite(area);

        Rectangle canvas = gp.gc.j2dcanvas.getBounds();
        Point displacement = null;
        if(origin_x != 0 || origin_y != 0){
            displacement = new Point(origin_x - (area.cx()), origin_y - (area.cy()));
        }
        else{
            displacement = new Point(canvas.width / 2 - (area.cx()), canvas.height / 2
                    - (area.cy()));
        }

        gp.gc.gpc.translateAll(displacement.getX(), displacement.getY(), true);
        gp.gc.group_area.translateAll(displacement);
        gp.gc.repaint();

        node_list = new Vector<Node>();
        edge_list = new Vector<Edge>();
        gp.gc.gpc.cloneCollection(node_list, edge_list);
        gp.gc.io.markUnsavedChanges();
        gp.gc.refreshScrollbars();
    }
}
