package presentation.fsa;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.CompoundEdit;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.utilities.EscapeDialog;
import presentation.fsa.actions.EdgeActions;
import presentation.fsa.actions.GraphActions;

/**
 * Dialog window for assigning multiple events from the global events model to
 * transitions represented by an edge in the graph model.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class EdgeLabellingDialog extends EscapeDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -7957612019296034490L;

    private static EdgeLabellingDialog dialog;

    private static GraphView presentation;

    protected CompoundEditEx allEdits;

    public static void initialize(JComponent parent) {
        Frame f = JOptionPane.getFrameForComponent(parent);
        dialog = new EdgeLabellingDialog(f);
    }

    /**
     * @param view parent component
     * @param e    the edge to be labelled
     */
    public static void showDialog(GraphView view, Edge e) {
        if (dialog == null) {
            initialize(view);
        }
        presentation = view;
        dialog.allEdits = new CompoundEditEx();
        dialog.checkControllable.setSelected(dialog.cbCState);
        dialog.checkObservable.setSelected(dialog.cbOState);
        dialog.setEdge(e);
        dialog.setLocationRelativeTo(view);
        dialog.textField.requestFocus();
        dialog.setVisible(true);
    }

    private EdgeLabellingDialog() {
        this(null);
    }

    /**
     * The listener for the user decides to add a new event.
     */
    protected Action createListener = new AbstractAction() {
        /**
         * 
         */
        private static final long serialVersionUID = 6032935908775212274L;

        public void actionPerformed(ActionEvent actionEvent) {
            if (!(actionEvent.getSource() instanceof JButton)) {
                if ("".equals(textField.getText())) {
                    buttonOK.doClick();
                } else {
                    buttonCreate.doClick();
                }
                return;
            }
            if ("".equals(textField.getText())) {
                textField.requestFocus();
                return;
            }
            if (((JButton) actionEvent.getSource()).getText().equals(Hub.string("assignNew"))) {
                SupervisoryEvent[] eventBuffer = new SupervisoryEvent[1];
                // FIXME use passed FSAGraph rather than take current from
                // Workspace
                new GraphActions.CreateEventAction(allEdits, presentation.getGraphModel(), textField.getText(),
                        checkControllable.isSelected(), checkObservable.isSelected(), eventBuffer).execute();
                updateOnlyAvailable();
                listAvailableEvents.setSelectedValue(eventBuffer[0], true);
            } else {
                if (listAssignedEvents.existsElement(textField.getText())) {
                    return;
                }
                listAvailableEvents
                        .setSelectedIndex(listAvailableEvents.indexOfFirstElementWithPrefix(textField.getText()));
            }
            new AddButtonListener().actionPerformed(new ActionEvent(this, 0, ""));
            textField.setText("");
            textField.requestFocus();
        }
    };

    private EdgeLabellingDialog(Frame owner) {
        super(owner, "Assign events to edge", true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                buttonOK.doClick();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Box mainBox = Box.createVerticalBox();
        createBox = Box.createHorizontalBox();

        textField = new JTextField();
        textField.setMaximumSize(new Dimension(textField.getMaximumSize().width, textField.getPreferredSize().height));
        DocumentListener al = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                inserted = false;
                configStuff(textField.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                inserted = true;
                configStuff(textField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                inserted = false;
                configStuff(textField.getText());
            }

            private void configStuff(String symbol) {
                if (!listAvailableEvents.existsElement(symbol) && !listAssignedEvents.existsElement(symbol)) {
                    createBox.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(
                                    (textField.getText().equals("") ? UIManager.getColor("TextField.shadow")
                                            : UIManager.getColor("TextField.darkShadow")),
                                    1),
                            Hub.string("enterAssignEvent")));
                    buttonCreate.setText(Hub.string("assignNew"));
                    checkControllable.setEnabled(true);
                    checkControllable.setSelected(cbCState);
                    checkObservable.setEnabled(true);
                    checkObservable.setSelected(cbOState);
                } else {
                    createBox.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(UIManager.getColor("TextField.shadow"), 1),
                            Hub.string("enterAssignEvent")));
                    buttonCreate.setText(Hub.string("assign"));
                    checkControllable.setEnabled(false);
                    checkControllable.setSelected(cbCState);
                    for (Iterator<SupervisoryEvent> i = ((FSAModel) Hub.getWorkspace().getActiveModel())
                            .getEventIterator(); i.hasNext();) {
                        SupervisoryEvent event = i.next();
                        if (event.getSymbol().equals(symbol)) {
                            checkControllable.setSelected(event.isControllable());
                            break;
                        }
                    }
                    checkObservable.setEnabled(false);
                    checkObservable.setSelected(cbOState);
                    for (Iterator<SupervisoryEvent> i = ((FSAModel) Hub.getWorkspace().getActiveModel())
                            .getEventIterator(); i.hasNext();) {
                        SupervisoryEvent event = i.next();
                        if (event.getSymbol().equals(symbol)) {
                            checkObservable.setSelected(event.isObservable());
                            break;
                        }
                    }
                }
            }
        };
        textField.getDocument().addDocumentListener(al);
        textField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), this);
        textField.getActionMap().put(this, createListener);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.isActionKey()) {
                    return;
                }
                listAvailableEvents.clearSelection();
                listAssignedEvents.clearSelection();
                String symbol = textField.getText();
                if ("".equals(symbol)) {
                    return;
                }

                // Select the first event in the lists for which symbol
                // is a prefix

                int i = listAssignedEvents.indexOfFirstElementWithPrefix(symbol);
                if (i > -1) {
                    listAssignedEvents.setSelectedIndex(i);
                }

                i = listAvailableEvents.indexOfFirstElementWithPrefix(symbol);
                if (i > -1) {
                    listAvailableEvents.setSelectedIndex(i);
                }
            }
        });
        createBox.add(textField);

        checkControllable = new JCheckBox(Hub.string("controllable"));
        checkControllable.setSelected(true);
        checkControllable.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                cbCState = ((JCheckBox) arg0.getSource()).isSelected();
                textField.requestFocus();
            }

        });
        createBox.add(checkControllable);
        checkObservable = new JCheckBox(Hub.string("observable"));
        checkObservable.setSelected(true);
        checkObservable.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                cbOState = ((JCheckBox) arg0.getSource()).isSelected();
                textField.requestFocus();
            }

        });
        createBox.add(checkObservable);

        buttonCreate = new JButton(Hub.string("assignNew"));
        buttonCreate.setToolTipText(Hub.string("createEventTooltip"));
        buttonCreate.setPreferredSize(
                new Dimension(buttonCreate.getPreferredSize().width, textField.getPreferredSize().height));
        buttonCreate.addActionListener(createListener);
        createBox.add(buttonCreate);

        createBox.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("TextField.shadow"), 1),
                Hub.string("enterAssignEvent")));
        mainBox.add(createBox);
        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        Box listBox = Box.createHorizontalBox();

        listAvailableEvents = new MutableList();
        listAvailableEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listAvailableEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    listAssignedEvents.clearSelection();
                    Object o = listAvailableEvents.getSelectedValue();
                    if (o != null) {
                        selectedEvent = (SupervisoryEvent) o;
                        listAvailableEvents.setSelectedValue(o, true);
                        if (listAvailableEvents.hasFocus()) {
                            textField.setText(selectedEvent.getSymbol());
                        }
                        if (inserted && textField.getSelectedText() == null
                                && textField.getCaretPosition() == textField.getText().length()) {
                            String originalText = textField.getText();
                            textField.setText(selectedEvent.getSymbol());
                            textField.setSelectionEnd(textField.getText().length());
                            textField.setSelectionStart(originalText.length());
                        }

                    }
                }
            }
        });
        JScrollPane pane = new JScrollPane(listAvailableEvents);
        pane.setPreferredSize(new Dimension(200, 300));
        pane.setBorder(BorderFactory.createTitledBorder("Available"));

        listBox.add(pane);
        listBox.add(Box.createHorizontalGlue());

        buttonAdd = new JButton(">>");
        buttonAdd.setToolTipText("Assign events to edge");
        AddButtonListener abl = new AddButtonListener();
        buttonAdd.addActionListener(abl);
        listAvailableEvents.addMouseListener(abl);
        buttonRemove = new JButton("<<");
        buttonRemove.setToolTipText("Remove events from edge");
        RemoveButtonListener rbl = new RemoveButtonListener();
        buttonRemove.addActionListener(rbl);
        JPanel pCentre = new JPanel();
        BoxLayout boxLayout = new BoxLayout(pCentre, BoxLayout.Y_AXIS);
        pCentre.setLayout(boxLayout);
        pCentre.add(buttonAdd);
        pCentre.add(Box.createRigidArea(new Dimension(0, 5)));
        pCentre.add(buttonRemove);

        listBox.add(pCentre);
        listBox.add(Box.createHorizontalGlue());

        listAssignedEvents = new MutableList();
        listAssignedEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAssignedEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    listAvailableEvents.clearSelection();
                    Object o = listAssignedEvents.getSelectedValue();
                    if (o != null) {
                        selectedEvent = (SupervisoryEvent) o;
                        listAssignedEvents.setSelectedValue(o, true);
                        if (listAssignedEvents.hasFocus()) {
                            textField.setText(selectedEvent.getSymbol());
                        }

                    }
                }
            }

        });
        listAssignedEvents.addMouseListener(rbl);
        pane = new JScrollPane(listAssignedEvents);
        pane.setPreferredSize(new Dimension(200, 300));
        pane.setBorder(BorderFactory.createTitledBorder("Assigned to Edge"));

        listBox.add(pane);

        mainBox.add(listBox);
        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        ActionListener commitListener = new CommitListener();
        buttonOK = new JButton("OK");
        buttonOK.addActionListener(commitListener);
        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onEscapeEvent();
            }
        });

        JPanel p = new JPanel(new FlowLayout());
        p.add(buttonOK);
        p.add(buttonCancel);
        rootPane.setDefaultButton(buttonOK);

        mainBox.add(p);
        mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(mainBox);
        pack();

        buttonOK.setPreferredSize(
                new Dimension(Math.max(buttonOK.getWidth(), buttonCancel.getWidth()), buttonOK.getHeight()));
        buttonOK.invalidate();
        buttonCancel.setPreferredSize(
                new Dimension(Math.max(buttonOK.getWidth(), buttonCancel.getWidth()), buttonCancel.getHeight()));
        buttonCancel.invalidate();
    }

    // TODO refresh available events list with data from global eventsModel
    // For now just refresh list model with local event set from the active FSA
    public void updateOnlyAvailable() {
        listAvailableEvents.removeAll();
        Iterator<SupervisoryEvent> events = ((FSAModel) Hub.getWorkspace().getActiveModel()).getEventIterator();

        while (events.hasNext()) {
            SupervisoryEvent e = events.next();
            if (!listAssignedEvents.getContents().contains(e)) {
                listAvailableEvents.insertElement(e);
            }
        }
        textField.setText("");
    }

    public void update() {
        // Selected events are those assigned to transitions on the edge
        listAssignedEvents.removeAll();
        Iterator<FSATransition> trans = edge.getTransitions();
        while (trans.hasNext()) {
            FSATransition t = trans.next();
            if (t.getEvent() != null) {
                listAssignedEvents.addElement(t.getEvent());
            }
        }

        // Available events are those in the active FSA minus those already
        // selected
        updateOnlyAvailable();
    }

    public void setEdge(Edge e) {
        this.edge = e;
        originalEvents.clear();
        if (edge != null) {
            update();
            Object[] contents = listAssignedEvents.getContents().toArray();
            for (int i = 0; i < contents.length; i++) {
                originalEvents.add((SupervisoryEvent) contents[i]);
            }
        } else {
            textField.setText("");
            // TODO clear out lists
        }
    }

    @Override
    public void onEscapeEvent() {
        textField.requestFocus();
        dialog.setVisible(false);
        allEdits.end();
        allEdits.undo();
    }

    private Edge edge;

    private SupervisoryEvent selectedEvent;

    private boolean inserted = false;

    /**
     * state of the controllable checkbox when creating new event
     */
    private boolean cbCState = true;

    /**
     * state of the observable checkbox when creating new event
     */
    private boolean cbOState = true;

    // for bordercolor change
    private Box createBox;

    // TODO Subscribe this dialog to automaton to receive messages about the
    // updates of the event set

    // GUI controls
    private JTextField textField;

    private JCheckBox checkObservable, checkControllable;

    private MutableList listAssignedEvents;

    private MutableList listAvailableEvents;

    private Vector<SupervisoryEvent> originalEvents = new Vector<SupervisoryEvent>();

    private JButton buttonCreate, buttonAdd, buttonRemove, buttonOK, buttonCancel;

    /*
     * Picking up the slack for Swing.
     */
    @SuppressWarnings("serial")
    private class MutableList extends JList {

        MutableList() {
            super(new DefaultListModel());
        }

        /**
         * @param symbol
         * @return index of the first element found with prefix of string representation
         *         matching symbol, -1 if no such element
         */
        public int indexOfFirstElementWithPrefix(String prefix) {
            DefaultListModel model = getContents();
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).toString().startsWith(prefix)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean existsElement(String symbol) {
            DefaultListModel model = getContents();
            for (int i = 0; i < model.getSize(); i++) {
                if (symbol.equals(model.getElementAt(i).toString())) {
                    return true;
                }
            }
            return false;
        }

        DefaultListModel getContents() {
            return (DefaultListModel) getModel();
        }

        void insertElement(Comparable<?> o) {
            int i = findInsertionPoint(o);
            getContents().insertElementAt(o, i);
        }

        void addElement(Object o) {
            getContents().addElement(o);
        }

        void removeElement(Object o) {
            getContents().removeElement(o);
        }

        @Override
        public void removeAll() {
            getContents().removeAllElements();
        }

        /**
         * Internal helper method to find the insertion point for a new entry in a
         * sorted (ascending) model.
         */
        @SuppressWarnings("unchecked")
        private int findInsertionPoint(Comparable entry) {
            int insertionPoint = getContents().getSize();
            for (int i = 0; i < insertionPoint; i++) {
                int c = ((Comparable) getContents().elementAt(i)).compareTo(entry);
                if (c >= 0) {
                    return i;
                }
            }
            return insertionPoint;
        }

    }

    /**
     * Responds to clicks on add button.
     * 
     * @author Squirrel
     */
    private class AddButtonListener extends MouseAdapter implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        public void actionPerformed(ActionEvent arg0) {
            // get elements selected from available events list
            Object selected = listAvailableEvents.getSelectedValue();
            if (selected != null) {
                listAssignedEvents.addElement(selected);
                listAvailableEvents.removeElement(selected);
                textField.setText("");
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && !listAvailableEvents.isSelectionEmpty()) {
                actionPerformed(new ActionEvent(listAvailableEvents, 0, ""));
            }
        }
    }

    /**
     * Responds to clicks on remove button.
     * 
     * @author Squirrel
     */
    private class RemoveButtonListener extends MouseAdapter implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        public void actionPerformed(ActionEvent arg0) {
            SupervisoryEvent selected = (SupervisoryEvent) listAssignedEvents.getSelectedValue();
            if (selected != null) {
                listAssignedEvents.removeElement(selected);
                listAvailableEvents.insertElement(selected);
                textField.setText("");
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && !listAssignedEvents.isSelectionEmpty()) {
                actionPerformed(new ActionEvent(listAssignedEvents, 0, ""));
            }
        }
    }

    private class CommitListener implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        public void actionPerformed(ActionEvent arg0) {

            if (!"".equals(textField.getText())) {
                buttonCreate.doClick();
            }
            // Apply any changes to edge's events
            Vector<SupervisoryEvent> events = new Vector<SupervisoryEvent>();
            Object[] contents = listAssignedEvents.getContents().toArray();
            for (int i = 0; i < contents.length; i++) {
                events.add((SupervisoryEvent) contents[i]);
            }
            if (allEdits.size() > 0 || !originalEvents.equals(events))
            // if new events have been created, or the set of assigned events
            // has been changed
            {
                // FIXME use passed FSAGraph rather than take current from
                // Workspace
                new EdgeActions.LabelAction(allEdits, presentation.getGraphModel(), edge, events).execute();
                allEdits.end();
                Hub.getUndoManager().addEdit(allEdits);
            } else {
                allEdits.end();
                allEdits.undo();
            }

            // ((FSAGraph)Hub.getWorkspace().getActiveLayoutShell()).
            // replaceEventsOnEdge(events,
            // edge);

            if (arg0.getSource().equals(buttonOK)) {
                textField.requestFocus();
                dialog.setVisible(false);
                if (dialog.getParent() != null) {
                    dialog.getParent().repaint();
                }
            }
        }

    }

    private static class CompoundEditEx extends CompoundEdit {
        private static final long serialVersionUID = -9190099966292314915L;

        public int size() {
            return edits.size();
        }
    }

}
