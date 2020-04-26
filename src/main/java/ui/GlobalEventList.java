package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ides.api.core.Hub;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.DESModel;

public class GlobalEventList extends JList {
    private static final long serialVersionUID = 4044946736984911640L;

    public static class EventRecord {
        public DESModel model;

        public DESEvent event;

        /**
         * used to alternate the bkgrnd color of events from different models
         */
        public boolean isOdd = false;

        public EventRecord(DESModel model, DESEvent event) {
            this.model = model;
            this.event = event;
        }
    }

    DefaultListModel listModel = new DefaultListModel();

    public GlobalEventList() {
        this(new HashSet<DESModel>());
    }

    public GlobalEventList(Collection<DESModel> modelsToExclude) {
        super();
        super.setModel(listModel);
        super.setCellRenderer(new EventRecordRenderer());
        List<EventRecord> records = new LinkedList<EventRecord>();
        for (Iterator<DESModel> i = Hub.getWorkspace().getModels(); i.hasNext();) {
            DESModel model = i.next();
            if (modelsToExclude.contains(model)) {
                continue;
            }
            DESEventSet events = model.getEventSet();
            for (DESEvent event : events) {
                records.add(new EventRecord(model, event));
            }
        }
        Collections.sort(records, eventRecordComparator);
        if (!records.isEmpty()) {
            boolean oddCounter = false;
            DESModel prevModel = records.get(0).model;
            for (EventRecord er : records) {
                if (er.model != prevModel) {
                    oddCounter = !oddCounter;
                    prevModel = er.model;
                }
                er.isOdd = oddCounter;
            }
        }
        for (EventRecord record : records) {
            listModel.addElement(record);
        }
    }

    protected static Comparator<EventRecord> eventRecordComparator = new Comparator<EventRecord>() {
        public int compare(EventRecord o1, EventRecord o2) {
            int i = o1.model.getName().compareTo(o2.model.getName());
            if (i == 0) {
                return o1.event.getSymbol().compareTo(o2.event.getSymbol());
            }
            return i;
        }
    };

    private static class EventRecordRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = -2322129824895809607L;

        public EventRecordRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            boolean darker = false;
            if (value == null) {
                setText("");
            } else if (value instanceof EventRecord) {
                setText(((EventRecord) value).event.getSymbol() + " [" + ((EventRecord) value).model.getName() + "]");
                darker = ((EventRecord) value).isOdd;
            } else {
                setText(value.toString());
            }
            if (isSelected) {
                setBackground(darker ? SystemColor.textHighlight.darker() : SystemColor.textHighlight);
                setForeground(SystemColor.textHighlightText);
            } else {
                setBackground(darker ? new Color(232, 232, 232) : SystemColor.text);
                setForeground(SystemColor.textText);
            }
            return this;
        }
    }
}
