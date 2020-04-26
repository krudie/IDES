package presentation.fsa.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import ides.api.core.Hub;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.actions.GraphActions.MoveAction;

public class MovementTool extends DrawingTool {

    private Point start, end, prev, next;

    public MovementTool() {
        // this.context = context;
        // this.cursor = new Cursor(Cursor.MOVE_CURSOR);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getIDESResource("images/cursors/move.gif")),
                new Point(12, 12), "MOVE_NODES_OR_LABELS");
    }

    @Override
    public void handleMousePressed(MouseEvent me) {
        super.handleMousePressed(me);

        // get the object to be moved
        start = me.getPoint();
        prev = start;

        // a group has been selected, move the whole thing
        if (ContextAdaptorHack.context.hasCurrentSelection()
                && ContextAdaptorHack.context.getSelectedGroup().size() > 1) {
            // FIXME What if user clicks outside the selection group?
            dragging = true;
        } else { // otherwise update the currently selected element
            ContextAdaptorHack.context.clearCurrentSelection();
            ContextAdaptorHack.context.updateCurrentSelection(start);

            if (ContextAdaptorHack.context.hasCurrentSelection()) {
                dragging = true;
            }

        }
        ContextAdaptorHack.context.repaint();
    }

    @Override
    public void handleMouseDragged(MouseEvent me) {
        super.handleMouseDragged(me);

        // update the location of the selected objects
        if (start == null) {
            start = (Point) lastMousePressedLocation.clone();
            prev = start;
        }
        next = me.getPoint();
        ContextAdaptorHack.context.getSelectedGroup().translate(next.x - prev.x, next.y - prev.y);
        prev = next;
        ContextAdaptorHack.context.repaint();
    }

    @Override
    public void handleMouseReleased(MouseEvent me) {
        super.handleMouseReleased(me);

        end = me.getPoint();

        // Null pointer exception, hard to replicate
        if ((end != null) && (start != null)) {
            Point2D.Float displacement = new Point2D.Float(end.x - start.x, end.y - start.y);
            if (displacement.x != 0 || displacement.y != 0) {
                // undo needs to know the selection of moved objects
                // and the total translation
                // save the set of selected objects for undo purposes
                new MoveAction(ContextAdaptorHack.context.getGraphModel(),
                        ContextAdaptorHack.context.getSelectedGroup(), displacement).execute();
            }
        }

        dragging = false;
        start = null;
        prev = null;
        next = null;
        end = null;

        // don't deselect groups of multiple elements since user may wish to
        // revise movement
        if (!(ContextAdaptorHack.context.getSelectedGroup().size() > 1)) {
            ContextAdaptorHack.context.clearCurrentSelection();
            ContextAdaptorHack.context.updateCurrentSelection(me.getPoint());
        }

        ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
        ContextAdaptorHack.context.repaint();
    }

    @Override
    public void handleKeyTyped(KeyEvent ke) {
        // if user types escape, switch to selection tool
        int code = ke.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE) {
            ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
        }
    }

    // @Override
    // public void handleMouseMoved(MouseEvent me) {}
    //
    // @Override
    // public void handleMouseClicked(MouseEvent me) {}
    //
    // @Override
    // public void handleKeyPressed(KeyEvent ke) {}
    //
    // @Override
    // public void handleKeyReleased(KeyEvent ke) {}

}