package presentation.supeventset;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;

import ides.api.model.supeventset.SupEventSetMessage;
import ides.api.model.supeventset.SupEventSetSubscriber;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.presentation.Presentation;

/**
 * @author Valerie Sugarman
 */
public class SupEventSetThumbnail extends Box implements Presentation, SupEventSetSubscriber {

    /**
     * 
     */
    private static final long serialVersionUID = -7963989822990876272L;

    protected SupervisoryEventSet model;

    protected String name = "thumbnail";

    protected boolean trackModel;

    protected JLabel numEvents;

    protected JLabel eventNamesLine1;

    protected JLabel eventNamesLine2;

    protected JLabel etc;

    public SupEventSetThumbnail(SupervisoryEventSet model, int width, int height) {
        super(BoxLayout.Y_AXIS);
        this.model = model;
        setTrackModel(true);
        this.setSize(width, height);
        numEvents = new JLabel();
        numEvents.setAlignmentX(CENTER_ALIGNMENT);
        JLabel space = new JLabel(" ");
        space.setAlignmentX(CENTER_ALIGNMENT);
        eventNamesLine1 = new JLabel();
        eventNamesLine1.setAlignmentX(CENTER_ALIGNMENT);
        eventNamesLine2 = new JLabel();
        eventNamesLine2.setAlignmentX(CENTER_ALIGNMENT);
        etc = new JLabel("...");
        etc.setAlignmentX(CENTER_ALIGNMENT);
        refresh();
        this.add(numEvents);
        this.add(space);
        this.add(eventNamesLine1);
        this.add(eventNamesLine2);
        this.add(etc);

    }

    /**
     * Generates the text to display in the thumbnail. The first line (numEvents)
     * shows the number of events. The second line (space) is blank for readability.
     * The third line (eventNamesLine1) is the first few events (in alphabetical
     * order), if they exist (up to 15 characters). The fourth line
     * (eventNamesLine2) is the next few events (in alphabetical order), if they
     * exist (up to 15 characters). The fifth line (etc) is simply "..." which is
     * displayed if there are more events that are not displayed on EventNamesLine1
     * or EventNameLine2.
     */
    public void refresh() {
        if (model.size() == 1) {
            numEvents.setText(model.size() + " event");
        } else {
            numEvents.setText(model.size() + " events");
        }

        String someNames = "";
        LinkedList<SupervisoryEvent> events = new LinkedList<SupervisoryEvent>();
        for (Iterator<SupervisoryEvent> i = model.iteratorSupervisory(); i.hasNext();) {
            events.add(i.next());
        }

        Collections.sort(events, new Comparator<DESEvent>() {
            public int compare(DESEvent event1, DESEvent event2) {
                return event1.getSymbol().compareTo(event2.getSymbol());
            }
        });

        Iterator<SupervisoryEvent> i = events.iterator();
        while (i.hasNext() && someNames.length() <= 15) {
            someNames += i.next().getSymbol() + ", ";
        }

        if (!i.hasNext() && someNames.endsWith(", ")) {
            someNames = someNames.substring(0, someNames.length() - 2);
        }

        eventNamesLine1.setText(someNames);
        someNames = "";
        while (i.hasNext() && someNames.length() <= 15) {
            someNames += i.next().getSymbol() + ", ";
        }
        if ((!i.hasNext()) && someNames.endsWith(", ")) {
            someNames = someNames.substring(0, someNames.length() - 2);
        }

        eventNamesLine2.setText(someNames);

        if (i.hasNext()) {
            etc.setVisible(true);
        } else {
            etc.setVisible(false);
        }
    }

    public void forceRepaint() {
        refresh();
        repaint();
    }

    public JComponent getGUI() {
        return this;
    }

    public SupervisoryEventSet getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public void release() {
        setTrackModel(false);
    }

    public void setTrackModel(boolean b) {
        if (trackModel != b) {
            trackModel = b;
            if (trackModel) {
                model.addSubscriber(this);
            } else {
                model.removeSubscriber(this);
            }
        }
    }

    public void supEventSetChanged(SupEventSetMessage message) {
        refresh();
    }

}
