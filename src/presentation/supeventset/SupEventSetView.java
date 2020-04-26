package presentation.supeventset;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.undo.CompoundEdit;

import ides.api.core.Hub;
import ides.api.model.supeventset.SupEventSetMessage;
import ides.api.model.supeventset.SupEventSetSubscriber;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.presentation.CopyPastePresentation;
import ides.api.plugin.presentation.Presentation;
import ides.api.utilities.GeneralUtils;
import presentation.fsa.actions.GraphUndoableEdits;
import presentation.supeventset.actions.AbstractSupEventSetAction;
import presentation.supeventset.actions.SupEventSetActions;
import services.ccp.EventSetWrapper;

/**
 * @author Valerie Sugarman but adapted from presentation.fsa.EventView so
 *         really also @author Lenko Grigorov
 */
public class SupEventSetView extends JPanel
        implements Presentation, SupEventSetSubscriber, CopyPastePresentation, ClipboardOwner {

    /**
     * 
     */
    private static final long serialVersionUID = -2741307650481618505L;

    protected class EventTableModel extends AbstractTableModel {

        /**
         * 
         */
        private static final long serialVersionUID = -8287481414784054256L;

        private SupervisoryEventSet a = null;

        private Vector<SupervisoryEvent> events = null;

        private Vector<Boolean> controllable = null;

        private Vector<Boolean> observable = null;

        private final String[] columnNames = { Hub.string("eventNameHeading"), Hub.string("controllableHeading"),
                Hub.string("observableHeading") };

        public EventTableModel() {
            events = new Vector<SupervisoryEvent>();
            controllable = new Vector<Boolean>();
            observable = new Vector<Boolean>();

        }

        public EventTableModel(SupervisoryEventSet a) {
            events = new Vector<SupervisoryEvent>();
            controllable = new Vector<Boolean>();
            observable = new Vector<Boolean>();
            this.a = a;
            for (Iterator<SupervisoryEvent> i = a.iteratorSupervisory(); i.hasNext();) {
                events.add(i.next());
            }
            Collections.sort(events, new Comparator<SupervisoryEvent>() {
                public int compare(SupervisoryEvent event1, SupervisoryEvent event2) {
                    return event1.getSymbol().compareTo(event2.getSymbol());
                }
            });
            for (int i = 0; i < events.size(); ++i) {
                if (events.elementAt(i).isControllable()) {
                    controllable.add(new Boolean(true));
                } else {
                    controllable.add(new Boolean(false));
                }
                if (events.elementAt(i).isObservable()) {
                    observable.add(new Boolean(true));
                } else {
                    observable.add(new Boolean(false));
                }
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col].toString();
        }

        public int getRowCount() {
            return events.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return events.elementAt(row).getSymbol();
            } else if (col == 1) {
                return controllable.elementAt(row);
            } else {
                return observable.elementAt(row);
            }
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }

        /**
         * Sets symbol, observable or controllable properties of the event at the given
         * row to the given value.
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            long eventId = 0;
            if (col == 0) {
                if ("".equals(value)) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                eventId = events.elementAt(row).getId();
                SupervisoryEvent existingEvent = closestEvent((String) value);
                if (((String) value).equals(existingEvent.getSymbol())) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                new SupEventSetActions.ModifyEventAction(a, events.elementAt(row), (String) value,
                        events.elementAt(row).isControllable(), events.elementAt(row).isObservable()).execute();

            } else if (col == 1) {
                new SupEventSetActions.ModifyEventAction(a, events.elementAt(row), events.elementAt(row).getSymbol(),
                        (Boolean) value, events.elementAt(row).isObservable()).execute();

                controllable.removeElementAt(row);
                controllable.insertElementAt((Boolean) value, row);
            } else {

                new SupEventSetActions.ModifyEventAction(a, events.elementAt(row), events.elementAt(row).getSymbol(),
                        events.elementAt(row).isControllable(), (Boolean) value).execute();

                observable.removeElementAt(row);
                observable.insertElementAt((Boolean) value, row);
            }

            if (col == 0) {
                Collections.sort(events, new Comparator<SupervisoryEvent>() {
                    public int compare(SupervisoryEvent event1, SupervisoryEvent event2) {
                        return event1.getSymbol().compareTo(event2.getSymbol());
                    }
                });

                fireTableDataChanged();
                int newIdx = events.indexOf(a.getEvent(eventId));
                table.setRowSelectionInterval(newIdx, newIdx);
                table.scrollRectToVisible(table.getCellRect(newIdx, 0, false));
            } else {
                fireTableCellUpdated(row, col);
            }
        }

        public Class<?> getColumnClass(int c) {
            if (c == 0) {
                return String.class;
            } else {
                return Boolean.class;
            }
        }

        public SupervisoryEvent closestEvent(String symbol) {
            SupervisoryEvent retVal = null;
            if (!events.isEmpty()) {
                retVal = events.firstElement();
            }
            for (SupervisoryEvent event : events) {
                if (event.getSymbol().compareTo(symbol) > 0) {
                    break;
                }
                retVal = event;
            }
            return retVal;
        }

        public SupervisoryEvent getEventAt(int idx) {
            return events.elementAt(idx);
        }
    }

    /**
     * The listener for the user pressing the <code>Delete</code> key.
     */
    protected Action deleteListener = new AbstractAction() {
        /**
         * 
         */
        private static final long serialVersionUID = 4695140088632822674L;

        public void actionPerformed(ActionEvent actionEvent) {
            int[] rows = table.getSelectedRows();
            if (rows.length > 0) {
                int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                        GeneralUtils.JOptionPaneKeyBinder.messageLabel(Hub.string("confirmDeleteEvents")),
                        Hub.string("deleteEventsTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            SupervisoryEvent[] delEvents = new SupervisoryEvent[rows.length];
            for (int i = 0; i < rows.length; ++i) {
                delEvents[i] = ((EventTableModel) table.getModel()).getEventAt(rows[i]);
            }
            CompoundEdit allEdits = new CompoundEdit();
            for (int i = 0; i < delEvents.length; ++i) {

                AbstractSupEventSetAction deleteEvent = new SupEventSetActions.RemoveEventAction(allEdits, model,
                        delEvents[i]);
                if (i != 0 && i == delEvents.length - 1) {
                    deleteEvent.setLastOfMultiple(true);
                }
                deleteEvent.execute();
            }
            allEdits.end();
            Hub.getUndoManager().addEdit(allEdits);
            eventNameField.requestFocus();
        }
    };

    /**
     * The listener for the user decides to add a new event.
     */
    protected Action createListener = new AbstractAction() {
        /**
         * 
         */
        private static final long serialVersionUID = 6505713526778596223L;

        public void actionPerformed(ActionEvent actionEvent) {
            if (!(actionEvent.getSource() instanceof JButton)) {
                createButton.doClick();
                return;
            }
            SupervisoryEventSet a = (SupervisoryEventSet) Hub.getWorkspace().getActiveModel();
            if (a == null || "".equals(eventNameField.getText())) {
                return;
            }
            String eventName = eventNameField.getText();

            new SupEventSetActions.CreateEventAction(model, eventName, controllableCBox.isSelected(),
                    observableCBox.isSelected()).execute();

            int rows = table.getModel().getRowCount();
            for (int i = 0; i < rows; ++i) {
                if (((String) table.getModel().getValueAt(i, 0)).equals(eventName)) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, false));
                    break;
                }
            }
            eventNameField.requestFocus();
        }
    };

    protected JTable table;

    private int col1Width;

    private int col2Width;

    protected JTextField eventNameField;

    protected JCheckBox controllableCBox;

    protected JCheckBox observableCBox;

    protected JButton createButton;

    protected JButton deleteButton;

    protected SupervisoryEventSet model;

    private boolean trackModel;

    protected String name = "Events";

    public SupEventSetView(SupervisoryEventSet model) {
        super();
        this.model = model;
        setTrackModel(true);

        Box mainBox = Box.createVerticalBox();
        Box createBox = Box.createVerticalBox();
        Box createTopBox = Box.createHorizontalBox();
        Box createBottomBox = Box.createHorizontalBox();

        eventNameField = new JTextField(40);
        eventNameField.setMaximumSize(
                new Dimension(eventNameField.getPreferredSize().width, eventNameField.getPreferredSize().height));

        DocumentListener al = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                configStuff(eventNameField.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                configStuff(eventNameField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                configStuff(eventNameField.getText());
            }

            private void configStuff(String s) {
                SupervisoryEvent event = ((EventTableModel) table.getModel()).closestEvent(s);
                if (event != null && event.getSymbol().equals(s)) {
                    createButton.setEnabled(false);
                    controllableCBox.setEnabled(false);
                    // controllableCBox.setSelected(event.isControllable());
                    observableCBox.setEnabled(false);
                    // observableCBox.setSelected(event.isObservable());
                } else if ("".equals(s)) {
                    createButton.setEnabled(false);
                    controllableCBox.setEnabled(true);
                    observableCBox.setEnabled(true);
                } else {
                    createButton.setEnabled(true);
                    controllableCBox.setEnabled(true);
                    observableCBox.setEnabled(true);
                }
                int rows = table.getModel().getRowCount();
                for (int i = 0; i < rows; ++i) {
                    if (((String) table.getModel().getValueAt(i, 0)).equals(event.getSymbol())) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, false));
                        break;
                    }
                }
            }
        };
        eventNameField.getDocument().addDocumentListener(al);
        eventNameField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), this);
        eventNameField.getActionMap().put(this, createListener);
        createTopBox.add(eventNameField);

        controllableCBox = new JCheckBox(Hub.string("controllable"));
        controllableCBox.setSelected(true);
        createBottomBox.add(controllableCBox);
        observableCBox = new JCheckBox(Hub.string("observable"));
        observableCBox.setSelected(true);
        createBottomBox.add(observableCBox);
        createBottomBox.add(Box.createHorizontalGlue());

        createButton = new JButton(Hub.string("add"));
        createButton.setPreferredSize(
                new Dimension(createButton.getPreferredSize().width, eventNameField.getPreferredSize().height));
        createButton.addActionListener(createListener);
        createTopBox.add(createButton);
        createTopBox.add(Box.createHorizontalGlue());

        createBox.add(createTopBox);
        createBox.add(createBottomBox);
        createBox.setBorder(BorderFactory.createTitledBorder(Hub.string("addNewEvent")));// .createEmptyBorder(5,5,5,5));
        // Box borderPane=Box.createHorizontalBox();
        // borderPane.setBorder(BorderFactory.createLineBorder(this.getForeground
        // ()));
        // borderPane.add(createBox);
        mainBox.add(createBox);

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        table = new JTable(new EventTableModel());
        // table.setPreferredScrollableViewportSize(new Dimension(table
        // .getPreferredScrollableViewportSize().width, 200));
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), this);
        table.getActionMap().put(this, deleteListener);

        Action cutAction = Hub.getCopyPasteManager().getCutOverwriteAction();
        Action copyAction = Hub.getCopyPasteManager().getCopyOverwriteAction();
        Action pasteAction = Hub.getCopyPasteManager().getPasteOverwriteAction();
        table.getActionMap().put("cut", cutAction);
        table.getActionMap().put("copy", copyAction);
        table.getActionMap().put("paste", pasteAction);

        table.addFocusListener(new FocusAdapter() {
            private Component editor;

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (table.isEditing()) {
                    editor = table.getEditorComponent();
                    editor.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            super.focusLost(e);
                            editor.removeFocusListener(this);
                            if (table.getCellEditor() != null) {
                                table.getCellEditor().stopCellEditing();
                            }
                        }
                    });
                }
            }
        });
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        col1Width = headerRenderer.getTableCellRendererComponent(null,
                table.getColumnModel().getColumn(1).getHeaderValue(), false, false, 0, 0).getPreferredSize().width;
        col2Width = headerRenderer.getTableCellRendererComponent(null,
                table.getColumnModel().getColumn(2).getHeaderValue(), false, false, 0, 0).getPreferredSize().width;
        mainBox.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        Box deleteBox = Box.createHorizontalBox();
        deleteBox.add(Box.createHorizontalGlue());
        // deleteBox.add(new JLabel(Hub.string("deleteSelectedEvents")));
        deleteButton = new JButton(Hub.string("delete"));
        deleteButton.addActionListener(deleteListener);
        deleteBox.add(deleteButton);
        deleteBox.setBorder(BorderFactory.createTitledBorder(Hub.string("deleteSelectedEvents")));// .createEmptyBorder(5,5,5,5))
        // ;
        mainBox.add(deleteBox);

        setLayout(new BorderLayout());
        add(mainBox);
        refreshEventTable();

    }

    public void forceRepaint() {
        refreshEventTable();
        repaint();
    }

    public JComponent getGUI() {
        return this;
    }

    public DESModel getModel() {
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
                model.addSubscriber((SupEventSetSubscriber) this);
            } else {
                model.removeSubscriber((SupEventSetSubscriber) this);
            }
        }
    }

    public void supEventSetChanged(SupEventSetMessage message) {
        refreshEventTable();
    }

    /**
     * Makes the Event tab visible or invisible
     * 
     * @param b flag indicating visibility status
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        eventNameField.requestFocus();
    }

    private void refreshEventTable() {

        eventNameField.setText("");
        createButton.setEnabled(false);
        table.setModel(new EventTableModel(model));
        table.getColumnModel().getColumn(1).setMaxWidth(col1Width);
        table.getColumnModel().getColumn(2).setMaxWidth(col2Width);

        if (table.getRowCount() == 0) {
            deleteButton.setEnabled(false);
        } else {
            deleteButton.setEnabled(true);
        }
        eventNameField.setEnabled(true);
        table.setEnabled(true);

    }

    public Action getCopyAction() {
        return new EventCopyAction();
    }

    public Action getCutAction() {
        return new EventCutAction();
    }

    public Action getPasteAction() {
        return new EventPasteAction();
    }

    public boolean isCutCopyEnabled() {
        return !table.getSelectionModel().isSelectionEmpty();
    }

    public boolean isPasteEnabled() {
        if (Hub.getCopyPasteManager().getClipboard().isDataFlavorAvailable(EventSetWrapper.eventSelectionFlavor)) {
            return true;
        }

        return false;
    }

    public void newItemOnClipboard() {

    }

    public void lostOwnership(Clipboard arg0, Transferable arg1) {

    }

    private class EventCopyAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 5587004651412055515L;

        public void actionPerformed(ActionEvent arg0) {
            SupervisoryEventSet eventSet = ModelManager.instance().createModel(SupervisoryEventSet.class);
            int[] rows = table.getSelectedRows();
            for (int i = 0; i < rows.length; i++) {
                eventSet.add(((EventTableModel) table.getModel()).getEventAt(rows[i]));
            }
            EventSetWrapper selection = new EventSetWrapper(eventSet);
            Clipboard clip = Hub.getCopyPasteManager().getClipboard();
            clip.setContents(selection, SupEventSetView.this);
        }

    }

    private class EventCutAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 4876758349363695584L;

        public void actionPerformed(ActionEvent arg0) {
            SupervisoryEventSet eventSet = ModelManager.instance().createModel(SupervisoryEventSet.class);
            int[] rows = table.getSelectedRows();
            CompoundEdit allEdits = new CompoundEdit();
            for (int i = 0; i < rows.length; i++) {
                SupervisoryEvent e = ((EventTableModel) table.getModel()).getEventAt(rows[i]);
                eventSet.add(e);
            }
            for (Iterator<SupervisoryEvent> i = eventSet.iteratorSupervisory(); i.hasNext();) {
                SupervisoryEvent e = i.next();
                new SupEventSetActions.RemoveEventAction(allEdits, model, e).execute();
                // new GraphActions.RemoveEventAction(allEdits, graph, e)
                // .execute();
            }
            allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub.string("cut")));// TODO take me out?
            allEdits.end();
            Hub.getUndoManager().addEdit(allEdits);
            EventSetWrapper selection = new EventSetWrapper(eventSet);
            Clipboard clip = Hub.getCopyPasteManager().getClipboard();
            clip.setContents(selection, SupEventSetView.this);

        }

    }

    protected class EventPasteAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -3518141890000056919L;

        public void actionPerformed(ActionEvent arg0) {
            Transferable clipboardContent = Hub.getCopyPasteManager().getClipboard().getContents(SupEventSetView.this);
            if (clipboardContent != null
                    && clipboardContent.isDataFlavorSupported(EventSetWrapper.eventSelectionFlavor)) {
                SupervisoryEventSet clipboardSelection = null;
                try {
                    clipboardSelection = (SupervisoryEventSet) clipboardContent
                            .getTransferData(EventSetWrapper.eventSelectionFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (clipboardSelection.size() > 0) {
                    CompoundEdit allEdits = new CompoundEdit();
                    boolean contained = false;
                    for (Iterator<SupervisoryEvent> i = clipboardSelection.iteratorSupervisory(); i.hasNext();) {
                        SupervisoryEvent e = i.next();
                        if (!((EventTableModel) table.getModel()).events.contains(e)) {
                            new SupEventSetActions.CreateEventAction(allEdits, model, e.getSymbol(), e.isControllable(),
                                    e.isObservable()).execute();
                            // new GraphActions.CreateEventAction(
                            // allEdits,
                            // graph,
                            // e.getSymbol(),
                            // e.isControllable(),
                            // e.isObservable()).execute();
                        } else {
                            contained = true;
                        }
                    }
                    if (contained) {
                        Hub.getNoticeManager().postWarningTemporary(Hub.string("errorModelContainsPastedEventsDigest"),
                                Hub.string("errorModelContainsPastedEventsFull"));
                    }
                    allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub.string("paste")));
                    allEdits.end();
                    Hub.getUndoManager().addEdit(allEdits);

                }
            }

        }

    }

}
