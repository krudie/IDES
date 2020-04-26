package presentation.fsa.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import ides.api.core.Hub;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.actions.UIActions;

public class TextTool extends DrawingTool {

    // private GraphDrawingView context;

    public TextTool() {
        // this.context = context;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getIDESResource("images/cursors/text.gif")),
                new Point(0, 0), "MAKE_LABELS");
    }

    @Override
    public void handleMouseClicked(MouseEvent me) {
        super.handleMouseClicked(me);
        if (me.getClickCount() != 2) {
            ContextAdaptorHack.context.setTool(ContextAdaptorHack.context.getPreferredTool());
            ContextAdaptorHack.context.getCurrentTool().handleMouseClicked(me);
            return;
        }
        // get current selection
        if (ContextAdaptorHack.context.updateCurrentSelection(me.getPoint())) {
            new UIActions.TextAction(ContextAdaptorHack.context.getSelectedElement()).execute();
        } else {
            // if nothing selected
            // create a free label
            new UIActions.TextAction(me.getPoint()).execute();
        }
        ContextAdaptorHack.context.clearCurrentSelection();
        ContextAdaptorHack.context.setAvoidNextDraw(false);
        ContextAdaptorHack.context.setTool(ContextAdaptorHack.context.getPreferredTool());

    }

    // @Override
    // public void handleKeyTyped(KeyEvent ke) {}
    //
    // @Override
    // public void handleMouseDragged(MouseEvent me) {}
    //
    // @Override
    // public void handleMouseMoved(MouseEvent me) {}
    //
    // @Override
    // public void handleMousePressed(MouseEvent me) {}
    //
    // @Override
    // public void handleMouseReleased(MouseEvent me) {}
    //
    // @Override
    // public void handleKeyPressed(KeyEvent ke) {}
    //
    // @Override
    // public void handleKeyReleased(KeyEvent ke) {}
}
