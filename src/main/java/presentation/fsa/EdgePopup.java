/**
 * 
 */
package presentation.fsa;

import java.awt.geom.Point2D.Float;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import presentation.fsa.actions.UIActions;

/**
 * A popup menu providing operations to modify or delete an edge. TODO enable
 * Symmetrize command after the command has been debugged and tested.
 * 
 * @author Helen Bretzke
 */
public class EdgePopup extends JPopupMenu {

    /**
     * 
     */
    private static final long serialVersionUID = -8195738501550858344L;

    private static GraphDrawingView view;

    private Action deleteCmd;

    private JMenuItem miEditEvents, miStraighten, miSymmetrize, miArcMore, miArcLess;

    // Using a singleton pattern (delayed instantiation)
    // rather than initializing here since otherwise get
    // java.lang.NoClassDefFoundError error
    private static EdgePopup popup;

    /**
     * Creates a popup menu to for displaying when user right-clicks on an edge.
     * 
     * @param edge the edge to associate with this menu instance
     */
    protected EdgePopup(GraphDrawingView gdv, Edge edge) {

        miEditEvents = new JMenuItem(new UIActions.TextAction(edge));
        add(miEditEvents);

        // if the edge can't be straightened, then we assume we cannot
        // otherwise tamper with its shape
        if (edge.canBeStraightened()) {
            miStraighten = new JMenuItem(new UIActions.StraightenEdgeAction(gdv.graphModel, edge));
            // miStraighten.addActionListener(listener);
            add(miStraighten);

            miSymmetrize = new JMenuItem(new UIActions.SymmetrizeEdgeAction(gdv.graphModel, edge));
            add(miSymmetrize);

            miArcMore = new JMenuItem(new UIActions.ArcMoreEdgeAction(gdv.graphModel, edge));
            // miArcMore.addActionListener(listener);
            add(miArcMore);

            miArcLess = new JMenuItem(new UIActions.ArcLessEdgeAction(gdv.graphModel, edge));
            // miArcLess.addActionListener(listener);
            add(miArcLess);
            // miStraighten.setVisible(edge.canBeStraightened());
            // miArcLess.setVisible(edge.canBeStraightened());
            // miArcMore.setVisible(edge.canBeStraightened());
            // miSymmetrize.setVisible(edge.canBeStraightened());

            // Don't enable straightening, flattening or symmetrizing if edge is
            // already straight
            // since there is nothing to to.
            miArcLess.setEnabled(!edge.isStraight());
            miStraighten.setEnabled(!edge.isStraight());
            miSymmetrize.setEnabled(!edge.isStraight());
        }

        add(new JPopupMenu.Separator());

        deleteCmd = gdv.getDeleteAction();
        add(deleteCmd);

        addPopupMenuListener(new PopupListener());
    }

    protected static void showPopup(GraphDrawingView context, Edge e) {
        view = context;
        popup = new EdgePopup(context, e);
        Float p = e.getLayout().getLocation();
        p = context.localToScreen(p);
        popup.show(context, (int) p.x, (int) p.y);
    }

    // /**
    // * Associates the given edge with this menu instance.
    // *
    // * @param edge the edge to associate with this menu instance
    // */
    // public void setEdge(Edge edge){
    // this.edge = edge;
    // // deleteCmd.setElement(edge);
    // // deleteCmd.setContext(view);
    // if(edge != null){
    // miStraighten.setVisible(edge.canBeStraightened());
    // // if the edge can't be straightened, then we assume we cannot
    // // otherwise tamper with its shape
    // miArcLess.setVisible(edge.canBeStraightened());
    // miArcMore.setVisible(edge.canBeStraightened());
    // miSymmetrize.setVisible(edge.canBeStraightened());
    //
    // // Don't enable straightening, flattening or symmetrizing if edge is
    // already straight
    // // since there is nothing to to.
    // miArcLess.setEnabled(!edge.isStraight());
    // miStraighten.setEnabled(!edge.isStraight());
    // miSymmetrize.setEnabled(!edge.isStraight());
    // }
    // }

    // /**
    // * Listens to events on the EdgePopup menu.
    // *
    // * @author helen bretzke
    // */
    // class MenuListener implements ActionListener {
    //
    // /* (non-Javadoc)
    // * @see
    // java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    // */
    // public void actionPerformed(ActionEvent arg0) {
    // Object source = arg0.getSource();
    // if(source.equals(miModify)){
    // edge.getHandler().setVisible(true);
    // view.setTool(GraphDrawingView.MODIFY);
    // }else if(source.equals(miEditEvents)){
    // EdgeLabellingDialog.showDialog(view, edge);
    //
    // // TODO should be UNDOABLE graph commands
    //
    // }else if(source.equals(miSymmetrize)){
    // edge.getGraph().symmetrize(edge);
    // }else if(source.equals(miArcMore)){
    // edge.getGraph().arcMore(edge);
    // }else if(source.equals(miArcLess)){
    // edge.getGraph().arcLess(edge);
    // }else if(source.equals(miStraighten)){
    // edge.getGraph().straighten(edge);
    // }else{
    // Hub.displayAlert("Edge popup: " + source.toString());
    // }
    // }
    // }

    class PopupListener implements PopupMenuListener {

        boolean wasCanceled = false;

        boolean becomeInvisible = false;

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(
         * javax.swing.event.PopupMenuEvent)
         */
        public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
            view.repaint();
            if (wasCanceled == true) {
                wasCanceled = false;
            } else {
                view.setAvoidNextDraw(false);
            }

        }

        public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
            view.setAvoidNextDraw(true);
        }

        public void popupMenuCanceled(PopupMenuEvent arg0) {
            wasCanceled = true;
        }
    }
}
