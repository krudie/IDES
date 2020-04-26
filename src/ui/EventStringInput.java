package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ides.api.core.Hub;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESModel;
import ides.api.utilities.EscapeDialog;

/**
 * The input for an Event String to be used in OperationDialog. Displays the
 * current string and allows modification.
 * 
 * @author Valerie Sugarman
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class EventStringInput extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 4343540606884598362L;

    protected JLabel stringLabel = new JLabel("[epsilon]");

    protected JButton modifyStringButton = new JButton("Modify String");

    protected LinkedList<DESEvent> list;

    public EventStringInput() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        list = new LinkedList<DESEvent>();
        refresh();
        modifyStringButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new EventStringChooser(EventStringInput.this);
            }

        });
        this.add(stringLabel);
        this.add(modifyStringButton);

    }

    public void refresh() {
        if (list.size() == 0) {
            stringLabel.setText("[epsilon]");
            return;
        }

        String text = "";
        for (Iterator<DESEvent> i = list.iterator(); i.hasNext();) {
            text += i.next().getSymbol() + " ";
        }
        text = text.substring(0, text.length() - 1);
        stringLabel.setText(text);
        repaint();
    }

    public LinkedList<DESEvent> getList() {
        return list;
    }

    /**
     * Dialog for the actual selection of events to compose an event string.
     */
    protected static class EventStringChooser extends EscapeDialog {

        /**
         * 
         */
        private static final long serialVersionUID = 5326525912994996203L;

        protected EventStringInput parent;

        public EventStringChooser(EventStringInput input) {
            super(JOptionPane.getFrameForComponent(input), "Select events to form string", true);
            parent = input;

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    onEscapeEvent();
                }
            });
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

            Box mainBox = Box.createVerticalBox();
            createBox = Box.createHorizontalBox();

            textField = new JTextField();
            textField.setMaximumSize(
                    new Dimension(textField.getMaximumSize().width, textField.getPreferredSize().height));
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
                    buttonCreate.setText(Hub.string("concat"));

                    if (!listAvailableEvents.existsElement(symbol)) {
                        buttonCreate.setEnabled(false);
                        createBox.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(UIManager.getColor("TextField.shadow"), 1),
                                Hub.string("concatToString")));
                    } else {
                        buttonCreate.setEnabled(true);
                        createBox.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                        (textField.getText().equals("") ? UIManager.getColor("TextField.shadow")
                                                : UIManager.getColor("TextField.darkShadow")),
                                        1),

                                Hub.string("concatToString")));
                    }
                }
            };
            textField.getDocument().addDocumentListener(al);
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

                    int i = listAvailableEvents.indexOfFirstElementWithPrefix(symbol);
                    if (i > -1) {
                        listAvailableEvents.setSelectedIndex(i);
                    }
                }
            });
            createBox.add(textField);

            buttonCreate = new JButton(Hub.string("concat"));
            buttonCreate.setPreferredSize(
                    new Dimension(buttonCreate.getPreferredSize().width, textField.getPreferredSize().height));
            buttonCreate.addActionListener(createListener);
            createBox.add(buttonCreate);

            createBox.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(UIManager.getColor("TextField.shadow"), 1),
                    Hub.string("concatToString")));
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
                            selectedEvent = (DESEvent) o;
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
            pane.setBorder(BorderFactory.createTitledBorder("Events in workspace"));

            listBox.add(pane);
            listBox.add(Box.createHorizontalGlue());

            buttonAdd = new JButton(">>");
            buttonAdd.setToolTipText("Add event to string");
            AddButtonListener abl = new AddButtonListener();
            buttonAdd.addActionListener(abl);
            listAvailableEvents.addMouseListener(abl);
            buttonRemove = new JButton("<<");
            buttonRemove.setToolTipText("Remove event from string");
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
                            selectedEvent = (DESEvent) o;
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
            pane.setBorder(BorderFactory.createTitledBorder("String"));

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

            update();
            setLocationRelativeTo(input);
            textField.requestFocus();
            setVisible(true);
        }

        public void update() {
            listAssignedEvents.removeAll();

            for (Iterator<DESEvent> i = parent.list.iterator(); i.hasNext();) {
                listAssignedEvents.addElement(i.next());
            }

            listAvailableEvents.removeAll();
            // DESEvent is not comparable so sort them ahead of time
            HashSet<DESEvent> tempSet = new HashSet<DESEvent>();
            for (Iterator<DESModel> models = Hub.getWorkspace().getModels(); models.hasNext();) {
                DESModel model = models.next();
                for (DESEvent event : model.getEventSet()) {
                    tempSet.add(event);
                }
            }

            LinkedList<DESEvent> tempList = new LinkedList<DESEvent>(tempSet);
            Collections.sort(tempList, new Comparator<DESEvent>() {
                public int compare(DESEvent event1, DESEvent event2) {
                    return event1.getSymbol().compareTo(event2.getSymbol());
                }
            });

            for (Iterator<DESEvent> i = tempList.iterator(); i.hasNext();) {
                listAvailableEvents.addElement(i.next());
            }

            textField.setText("");
        }

        @Override
        public void onEscapeEvent() {
            dispose();
        }

        private DESEvent selectedEvent;

        private boolean inserted = false;

        // for bordercolor change
        private Box createBox;

        // GUI controls
        private JTextField textField;

        private MutableList listAssignedEvents;

        private MutableList listAvailableEvents;

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
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
             * ActionEvent )
             */
            public void actionPerformed(ActionEvent arg0) {
                // get elements selected from available events list
                Object selected = listAvailableEvents.getSelectedValue();
                if (selected != null) {
                    listAssignedEvents.addElement(selected);
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
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
             * ActionEvent )
             */
            public void actionPerformed(ActionEvent arg0) {
                DESEvent selected = (DESEvent) listAssignedEvents.getSelectedValue();
                if (selected != null) {
                    listAssignedEvents.removeElement(selected);
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

        protected Action createListener = new AbstractAction() {

            /**
             * 
             */
            private static final long serialVersionUID = -7770482834580610017L;

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

                listAvailableEvents
                        .setSelectedIndex(listAvailableEvents.indexOfFirstElementWithPrefix(textField.getText()));

                new AddButtonListener().actionPerformed(new ActionEvent(this, 0, ""));
                textField.setText("");
                textField.requestFocus();
            }
        };

        private class CommitListener implements ActionListener {

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
             * ActionEvent )
             */
            public void actionPerformed(ActionEvent arg0) {

                if (!listAvailableEvents.existsElement(textField.getText()) && !"".equals(textField.getText())) {

                    Toolkit.getDefaultToolkit().beep();
                    textField.setText("");
                    return;
                } else if (!"".equals(textField.getText())) {
                    buttonCreate.doClick();
                    return;
                }

                Object[] contents = listAssignedEvents.getContents().toArray();
                parent.list.clear();
                for (int i = 0; i < contents.length; i++) {
                    parent.list.add((DESEvent) contents[i]);
                }

                parent.refresh();
                onEscapeEvent();
            }

        }

    }

}
