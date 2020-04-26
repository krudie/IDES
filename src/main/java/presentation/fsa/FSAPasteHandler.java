package presentation.fsa;

import java.awt.datatransfer.DataFlavor;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;

import ides.api.core.Hub;
import ides.api.plugin.presentation.Presentation;
import services.ccp.EventSetWrapper;

/**
 * Handles the pasting for FSAModels. GraphDrawingView and EventView pass the
 * queries for isPasteEnabled and getPasteAction to this class, so that
 * depending on the flavor on the clipboard, events or graph selections will be
 * pasted in the right presentation even if the other is the active
 * CopyPastePresentation.
 * 
 * @author Valerie Sugarman
 */
public class FSAPasteHandler {

    protected static final DataFlavor graphFlavor = SelectionGroup.selectionGroupFlavor;

    protected static final DataFlavor eventFlavor = EventSetWrapper.eventSelectionFlavor;

    public static boolean isPasteEnabled() {
        // assume active model is an FSAModel since otherwise wouldn't get here
        // (via GraphDrawingView or EventView)
        if (Hub.getCopyPasteManager().getClipboard().isDataFlavorAvailable(graphFlavor)) {
            return true;
        } else if (Hub.getCopyPasteManager().getClipboard().isDataFlavorAvailable(eventFlavor)) {
            return true;
        }

        return false;
    }

    public static Action getPasteAction() {
        Collection<Presentation> presentations = Hub.getWorkspace().getPresentations();
        GraphDrawingView gdv = null;
        EventView ev = null;
        for (Iterator<Presentation> i = presentations.iterator(); i.hasNext();) {
            Presentation pres = i.next();
            if (pres instanceof GraphDrawingView) {
                gdv = (GraphDrawingView) pres;
            } else if (pres instanceof EventView) {
                ev = (EventView) pres;
            }
        }
        if (Hub.getCopyPasteManager().getClipboard().isDataFlavorAvailable(graphFlavor)) {
            return gdv.new GraphSelectionPasteAction();
        } else if (Hub.getCopyPasteManager().getClipboard().isDataFlavorAvailable(eventFlavor)) {
            return ev.new EventPasteAction();
        }

        return null;

    }

}
