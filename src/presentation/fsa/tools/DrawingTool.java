package presentation.fsa.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.ToolPopup;

/**
 * All tools used by the drawing board to handle requests forwarded from
 * keyboard and mouse xevents extend this class. These tools may also update the
 * command history and shared data model.
 * 
 * @author helen bretzke
 */
public abstract class DrawingTool {
    // protected GraphDrawingView context;
    protected Cursor cursor;

    // Dragging flag -- set to true when user presses mouse button
    // and cleared to false when user releases mouse button.
    protected boolean dragging = false;

    /**
     * It is important to remember when the mouse button went down so that the
     * "move" tool can respond correctly when the graph is too large and the mouse
     * is moved too fast to detect where exactly the dragging has started.
     */
    protected static Point lastMousePressedLocation = new Point();

    public Cursor getCursor() {
        return cursor;
    }

    public void handleRightClick(MouseEvent m) {
        if (ContextAdaptorHack.context != null) {
            ContextAdaptorHack.context.requestFocus();
        }

        // get intersected element and display appropriate popup menu
        ContextAdaptorHack.context.clearCurrentSelection();
        if (ContextAdaptorHack.context.updateCurrentSelection(m.getPoint())) {
            ContextAdaptorHack.context.getSelectedElement().showPopup(ContextAdaptorHack.context);
        } else {
            ToolPopup.showPopup(ContextAdaptorHack.context, m);
        }
    }

    public void handleMouseClicked(MouseEvent m) {
        if (ContextAdaptorHack.context != null) {
            ContextAdaptorHack.context.requestFocus();
        }
    }

    public void handleMouseDragged(MouseEvent m) {
        if (ContextAdaptorHack.context != null) {
            ContextAdaptorHack.context.requestFocus();
        }
    }

    public void handleMouseMoved(MouseEvent m) {
    }

    public void handleMousePressed(MouseEvent m) {
        if (ContextAdaptorHack.context != null) {
            ContextAdaptorHack.context.requestFocus();
        }
        lastMousePressedLocation = m.getPoint();
    }

    public void handleMouseReleased(MouseEvent m) {
        if (ContextAdaptorHack.context != null) {
            ContextAdaptorHack.context.requestFocus();
        }
    }

    public void handleKeyTyped(KeyEvent ke) {
    }

    public void handleKeyPressed(KeyEvent ke) {
    }

    public void handleKeyReleased(KeyEvent ke) {
    }

}
