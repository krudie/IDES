package presentation.fsa;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Iterator;

public class SelectionGroup extends GraphElement implements Transferable {

    public final static DataFlavor selectionGroupFlavor = new DataFlavor(SelectionGroup.class, "Graph Selection Group");

    private DataFlavor[] supportedFlavors = { selectionGroupFlavor };

    public SelectionGroup() {
        super();
    }

    public SelectionGroup(GraphElement element) {
        this();
        insert(element);
    }

    /**
     * @return a shallow copy of references to all children in this group.
     */
    public SelectionGroup copy() {
        SelectionGroup copy = new SelectionGroup();
        Iterator<GraphElement> children = children();
        while (children.hasNext()) {
            copy.insert((GraphElement) children.next());
        }
        return copy;
    }

    /**
     * Test and flag edges before moving the group. For all e in the selection group
     * If both s and t are in this group, flag e as a simple translation otherwise e
     * will need to be recomputed to preserve its shape when only one of its edges
     * is moved. Then translates all nodes (nodes will see to moving their edges)
     */
    // public void translate(float x, float y){
    // Iterator children = children();
    // while(children.hasNext()){
    // Object o = children.next();
    // // ??? If nodes are responsible for moving their edges, then why not have
    // them check
    // // the edges for ...
    // // A: because nodes don't know about the group to which they belong!
    //
    // if(o instanceof Edge){ // YUCK evil kluge indicative of poor design and
    // looming deadline.
    // Edge e = (Edge)o;
    // // BUG: rigid translation not recomputing scalars in edgelayout?
    // // Why would you need to?
    // // if( contains(e.getSource()) && contains(e.getTarget()) ){
    // // ((EdgeLayout)e.getLayout()).setRigidTranslation(true);
    // // }else{
    // e.getLayout().setRigidTranslation(false);
    // // }
    // // ??? What about Edges adjacent to translated Nodes that are
    // // NOT in the selection group? Default to FALSE (assume recomputation
    // required).
    // }
    // }
    // super.translate(x,y);
    // }
    /**
     * Override so don't break link to e's parent in graph structure.
     */
    @Override
    public void insert(GraphElement e) {
        GraphElement p = e.getParent();
        super.insert(e);
        e.setParent(p);

    }

    public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
        if (arg0.equals(selectionGroupFlavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(selectionGroupFlavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor arg0) {
        return (arg0.equals(selectionGroupFlavor));
    }

}
