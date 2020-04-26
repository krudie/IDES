package services.ccp;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import ides.api.model.supeventset.SupervisoryEventSet;

/**
 * Represents a selection of SupervisoryEvents on the clipboard.
 * 
 * @author Valerie Sugarman
 */
public class EventSetWrapper implements Transferable {

    public static DataFlavor eventSelectionFlavor = new DataFlavor(EventSetWrapper.class,
            "Supervisory Event Selection");

    private DataFlavor[] supportedFlavors = { eventSelectionFlavor };

    private SupervisoryEventSet events;

    public EventSetWrapper(SupervisoryEventSet set) {
        events = set;
    }

    public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
        if (arg0.equals(eventSelectionFlavor)) {
            return events;
        } else {
            throw new UnsupportedFlavorException(eventSelectionFlavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor arg0) {
        return (arg0.equals(eventSelectionFlavor));
    }

}
