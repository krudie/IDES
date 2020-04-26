/**
 * 
 */
package ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ItemSelectable;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;
import ides.api.presentation.fsa.FSAStateLabeller;
import ides.api.utilities.EscapeDialog;
import ides.api.utilities.GeneralUtils;
import util.AnnotationKeys;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class OperationDialog extends EscapeDialog {
    private static final long serialVersionUID = 6177704794804850005L;

    protected JList opList = new JList();

    protected Vector<JComponent> inputs = null;

    protected Vector<JTextField> outputNames = null;

    protected Vector<String> lastSetNames = null;

    private Box inputsBox;

    private Box outputsBox;

    private JTextArea description;

    private JButton okButton;

    public OperationDialog() {
        super(Hub.getMainWindow(), Hub.string("operationsDialogTitle"), true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onEscapeEvent();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        inputs = new Vector<JComponent>();
        outputNames = new Vector<JTextField>();
        lastSetNames = new Vector<String>();

        Box mainBox = Box.createVerticalBox();

        Box controlBox = Box.createHorizontalBox();

        Vector<String> ops = new Vector<String>();
        for (Iterator<String> i = OperationManager.instance().getOperationNames().iterator(); i.hasNext();) {
            ops.add(i.next());
        }
        Collections.sort(ops);
        opList.setListData(ops);
        opList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        opList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    reconfigureUI();
                }
            }
        });
        JScrollPane spo = new JScrollPane(opList);
        spo.setPreferredSize(new Dimension(150, 275));
        spo.setBorder(BorderFactory.createTitledBorder(Hub.string("operationsListTitle")));

        description = new JTextArea();
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new JLabel().getFont());
        description.setBackground(SystemColor.control);
        JScrollPane spd = new JScrollPane(description);
        spd.setPreferredSize(new Dimension(100, 100));
        spd.setBorder(BorderFactory.createTitledBorder(Hub.string("descriptionTitle")));

        Box opBox = Box.createVerticalBox();
        opBox.add(spo);
        opBox.add(Box.createRigidArea(new Dimension(0, 5)));
        opBox.add(spd);
        controlBox.add(opBox);

        controlBox.add(Box.createRigidArea(new Dimension(5, 0)));

        inputsBox = Box.createVerticalBox();
        JScrollPane sp = new JScrollPane(inputsBox);
        sp.setPreferredSize(new Dimension(225, 375));
        sp.setBorder(BorderFactory.createTitledBorder(Hub.string("modelListTitle")));
        controlBox.add(sp);

        outputsBox = Box.createVerticalBox();
        sp = new JScrollPane(outputsBox);
        sp.setPreferredSize(new Dimension(225, 375));
        sp.setBorder(BorderFactory.createTitledBorder(Hub.string("outputTitle")));
        controlBox.add(sp);

        mainBox.add(controlBox);

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        okButton = new JButton();
        JButton cancelButton = new JButton();
        okButton = new JButton(Hub.string("compute"));
        okButton.addActionListener(computeAction);
        cancelButton = new JButton(Hub.string("close"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                onEscapeEvent();
            }
        });
        JPanel p = new JPanel(new FlowLayout());
        p.add(okButton);
        p.add(cancelButton);
        mainBox.add(p);

        mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(mainBox);
        pack();

        okButton.setPreferredSize(
                new Dimension(Math.max(okButton.getWidth(), cancelButton.getWidth()), okButton.getHeight()));
        okButton.invalidate();
        cancelButton.setPreferredSize(
                new Dimension(Math.max(okButton.getWidth(), cancelButton.getWidth()), cancelButton.getHeight()));
        cancelButton.invalidate();

        setLocation(Hub.getCenteredLocationForDialog(getSize()));
        setVisible(true);
    }

    protected void reconfigureUI() {
        String opName = (String) opList.getSelectedValue();
        Operation operation = OperationManager.instance().getOperation(opName);

        description.setText(operation.getDescription());
        description.setCaretPosition(0);

        inputs.clear();
        inputsBox.removeAll();
        Class<?>[] inputTypes = operation.getTypeOfInputs();
        for (int i = 0; i < inputTypes.length; ++i) {
            JComponent selector;
            Box p = Box.createHorizontalBox();
            p.setBorder(BorderFactory.createTitledBorder(operation.getDescriptionOfInputs()[i]));
            if (i == inputTypes.length - 1 && operation.getNumberOfInputs() < 0) {
                selector = getMultipleSelector(inputTypes[i]);
            } else {
                selector = getSingleSelector(inputTypes[i]);
            }
            if (selector instanceof JList) {
                JScrollPane sp = new JScrollPane(selector);
                sp.setPreferredSize(new Dimension(200, sp.getPreferredSize().height));
                p.add(sp);
            } else {
                p.add(selector);
            }
            inputs.add(selector);
            inputsBox.add(p);
            if (i != inputTypes.length - 1) {
                inputsBox.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        inputsBox.add(Box.createVerticalGlue());

        outputNames.clear();
        lastSetNames.clear();
        outputsBox.removeAll();
        if (operation.getNumberOfOutputs() >= 0) {
            Class<?>[] outputTypes = operation.getTypeOfOutputs();
            for (int i = 0; i < outputTypes.length; ++i) {
                if (DESModel.class.isAssignableFrom(outputTypes[i])) {
                    JTextField nameField = new JTextField();
                    nameField.setMaximumSize(
                            new Dimension(nameField.getMaximumSize().width, nameField.getPreferredSize().height));
                    Box p = Box.createHorizontalBox();
                    p.setBorder(BorderFactory.createTitledBorder(operation.getDescriptionOfOutputs()[i]));
                    p.add(nameField);
                    outputNames.add(nameField);
                    lastSetNames.add("");
                    outputsBox.add(p);
                    if (i != outputTypes.length - 1) {
                        outputsBox.add(Box.createRigidArea(new Dimension(0, 5)));
                    }
                }
            }
        }
        outputsBox.add(Box.createVerticalGlue());

        fillInOutputs();

        validate();
        repaint();
        // select active model if possible
        if (Hub.getWorkspace().getActiveModel() != null) {
            for (int i = 0; i < inputs.size(); ++i) {
                if (inputs.elementAt(i) instanceof JList || inputs.elementAt(i) instanceof JComboBox) {
                    DESModel m = Hub.getWorkspace().getActiveModel();
                    boolean sameAsActiveType = false;
                    Class<?>[] ifaces = m.getModelType().getModelPerspectives();
                    for (int j = 0; j < ifaces.length; ++j) {
                        if (ifaces[j].equals(inputTypes[i])) {
                            sameAsActiveType = true;
                            break;
                        }
                    }
                    if (sameAsActiveType) {
                        if (inputs.elementAt(i) instanceof JComboBox) {
                            ((JComboBox) inputs.elementAt(i)).setSelectedItem(m);
                        } else if (inputs.elementAt(i) instanceof JList) {
                            ((JList) inputs.elementAt(i)).setSelectedValue(m, true);
                        }
                    }
                }
            }
        }
    }

    protected String suggestOutputName() {
        String suggestedName = opList.getSelectedValue().toString() + "(";
        for (int i = 0; i < inputs.size() - 1; ++i) {
            suggestedName += getSelectedNames(i) + ",";
        }
        if (!inputs.isEmpty()) {
            suggestedName += getSelectedNames(inputs.size() - 1);
        }
        suggestedName += ")";
        return suggestedName;
    }

    protected void fillInOutputs() {
        String suggestedName = suggestOutputName();
        if (outputNames.size() == 1) {
            if (outputNames.firstElement() != null) {
                if (lastSetNames.firstElement().equals(outputNames.firstElement().getText())) {
                    outputNames.firstElement().setText(suggestedName);
                    outputNames.firstElement().setCaretPosition(0);
                    lastSetNames.set(0, suggestedName);
                } else {
                    lastSetNames.set(0, "");
                }
            }
        } else {
            for (int i = 0; i < outputNames.size(); ++i) {
                if (outputNames.elementAt(i) != null) {
                    if (lastSetNames.elementAt(i).equals(outputNames.elementAt(i).getText())) {
                        outputNames.elementAt(i).setText(suggestedName + " (" + i + ")");
                        outputNames.elementAt(i).setCaretPosition(0);
                        lastSetNames.set(i, outputNames.elementAt(i).getText());
                    } else {
                        lastSetNames.set(i, "");
                    }
                }
            }
        }
    }

    private String getSelectedNames(int inputIdx) {
        Collection<Object> values = getSelectedItems(inputIdx);
        String names = "";
        for (Object value : values) {
            if (value instanceof DESModel) {
                names += ((DESModel) value).getName();
            } else if (value instanceof GlobalEventList.EventRecord) {
                names += "...";
                break;
            } else {
                names += value.toString();
            }
            names += ",";
        }
        if (names.endsWith(",")) {
            names = names.substring(0, names.length() - 1);
        }
        return names;
    }

    private List<Object> getSelectedItems(int inputIdx) {
        Object[] values = new Object[0];
        if (inputs.elementAt(inputIdx) instanceof ItemSelectable) {
            values = ((ItemSelectable) inputs.elementAt(inputIdx)).getSelectedObjects();
        } else if (inputs.elementAt(inputIdx) instanceof JList) {
            values = ((JList) inputs.elementAt(inputIdx)).getSelectedValues();
        } else if (inputs.elementAt(inputIdx) instanceof JTextField) {
            if (!"".equals(((JTextField) inputs.elementAt(inputIdx)).getText())) {
                values = new Object[] { ((JTextField) inputs.elementAt(inputIdx)).getText() };
            }
        } else if (inputs.elementAt(inputIdx) instanceof EventStringInput) {
            values = new Object[] { ((EventStringInput) inputs.elementAt(inputIdx)).getList() };
        }
        return Arrays.asList(values);
    }

    protected JComponent getSingleSelector(Class<?> type) {
        List<Object> items = new LinkedList<Object>();
        if (DESModel.class.isAssignableFrom(type)) {
            for (Object model : Hub.getWorkspace().getModelsOfType(type)) {
                items.add(model);
            }
        } else if (DESEventSet.class.equals(type)) {
            JList selector = new GlobalEventList();
            selector.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    fillInOutputs();
                }

            });
            return selector;
        } else if (Boolean.class.equals(type)) {
            items.add(Boolean.TRUE);
            items.add(Boolean.FALSE);
        } else if (Double.class.equals(type) || Long.class.equals(type) || String.class.equals(type)) {
            JTextField tf = new JTextField();
            tf.setMaximumSize(new Dimension(tf.getMaximumSize().width, tf.getPreferredSize().height));
            tf.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent arg0) {
                    fillInOutputs();
                }

                public void insertUpdate(DocumentEvent arg0) {
                    fillInOutputs();
                }

                public void removeUpdate(DocumentEvent arg0) {
                    fillInOutputs();
                }
            });
            return tf;
        } else if (new LinkedList<DESEvent>().getClass().equals(type)) {

            JPanel panel = new EventStringInput();
            // makes the panel match the size of everything else
            panel.setMaximumSize(
                    new Dimension((new JComboBox()).getMaximumSize().width, panel.getPreferredSize().height));
            return panel;
        } else {
            Hub.displayAlert(Hub.string("cantInterpretInput"));
            return new JPanel();
        }
        Collections.sort(items, nameComparator);
        JComboBox selector = new JComboBox(items.toArray());
        selector.setRenderer(nameRenderer);
        selector.setMaximumSize(new Dimension(selector.getMaximumSize().width, selector.getPreferredSize().height));
        selector.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent arg0) {
                fillInOutputs();
            }

        });
        return selector;
    }

    protected JComponent getMultipleSelector(Class<?> type) {
        List<Object> items = new LinkedList<Object>();
        if (DESModel.class.isAssignableFrom(type)) {
            for (Object model : Hub.getWorkspace().getModelsOfType(type)) {
                items.add(model);
            }
        } else {
            Hub.displayAlert(Hub.string("cantInterpretInput"));
            return new JPanel();
        }
        Collections.sort(items, nameComparator);
        JList selector = new JList(items.toArray());
        selector.setCellRenderer(nameRenderer);
        selector.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                fillInOutputs();
            }

        });
        return selector;
    }

    private static class NameCellRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = -2322129824895809607L;

        public NameCellRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            setBackground(SystemColor.text);
            if (value == null) {
                setText("");
            } else if (value instanceof DESModel) {
                setText(((DESModel) value).getName());
            } else if (value instanceof DESEvent) {
                setText(((DESEvent) value).getSymbol());
            } else {
                setText(value.toString());
            }
            if (isSelected) {
                setBackground(SystemColor.textHighlight);
                setForeground(SystemColor.textHighlightText);
            } else {
                setBackground(SystemColor.text);
                setForeground(SystemColor.textText);
            }
            return this;
        }
    }

    private static NameCellRenderer nameRenderer = new NameCellRenderer();

    private static Comparator<Object> nameComparator = new Comparator<Object>() {

        public int compare(Object arg0, Object arg1) {
            String o1Name = "", o2Name = "";
            if (arg0 == null) {
            } else if (arg0 instanceof DESModel) {
                o1Name = ((DESModel) arg0).getName();
            } else if (arg0 instanceof DESEvent) {
                o1Name = ((DESEvent) arg0).getSymbol();
            } else {
                o1Name = arg0.toString();
            }
            if (arg1 == null) {
            } else if (arg1 instanceof DESModel) {
                o2Name = ((DESModel) arg1).getName();
            } else if (arg1 instanceof DESEvent) {
                o2Name = ((DESEvent) arg1).getSymbol();
            } else {
                o2Name = arg1.toString();
            }
            return o1Name.compareTo(o2Name);
        }
    };

    @Override
    protected void onEscapeEvent() {
        dispose();
    }

    ActionListener computeAction = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            boolean emptyInput = false;
            for (int i = 0; i < inputs.size(); ++i) {
                Collection<Object> items = getSelectedItems(i);
                if (inputs.elementAt(i) == null || items.isEmpty()) {
                    emptyInput = true;
                }
            }
            boolean emptyField = false;
            for (int i = 0; i < outputNames.size(); ++i) {
                if (outputNames.elementAt(i) != null && outputNames.elementAt(i).getText().equals("")) {
                    emptyField = true;
                }
            }
            if (opList.isSelectionEmpty() || emptyInput || emptyField) {
                Hub.displayAlert(Hub.string("missingOperationParams"));
            } else {
                // call function to perform selected operation
                Operation op = OperationManager.instance().getOperation(opList.getSelectedValue().toString());
                List<Object> opInputs = new LinkedList<Object>();
                for (int i = 0; i < inputs.size(); ++i) {
                    List<Object> items = getSelectedItems(i);
                    if (DESEventSet.class.equals(op.getTypeOfInputs()[i])) {
                        DESEventSet eventSet = ModelManager.instance().createEmptyEventSet();
                        for (Object item : items) {
                            eventSet.add(((GlobalEventList.EventRecord) item).event);
                        }
                        items = new LinkedList<Object>();
                        items.add(eventSet);
                    } else if (items.size() == 1 && items.get(0) instanceof String) {
                        try {
                            if (Double.class.equals(op.getTypeOfInputs()[i])) {
                                items.set(0, Double.valueOf((String) items.get(0)));
                            } else if (Long.class.equals(op.getTypeOfInputs()[i])) {
                                items.set(0, Long.valueOf((String) items.get(0)));
                            }
                        } catch (NumberFormatException e) {
                            Hub.displayAlert(Hub.string("cantInterpretInputNumber"));
                            return;
                        }
                    }
                    opInputs.addAll(items);
                }

                Object[] outputs = null;

                Cursor cursor = getCursor();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    outputs = op.perform(opInputs.toArray());
                } finally {
                    setCursor(cursor);
                }

                String[] outputDesc = op.getDescriptionOfOutputs().clone();

                if (!op.getWarnings().isEmpty()) {
                    String warning = "";
                    for (String w : op.getWarnings()) {
                        warning += w + "\n";
                    }
                    Hub.displayAlert(GeneralUtils.truncateMessage(warning));
                }

                int j = 0; // separate counter for text field with name of
                // output DESModel
                for (int i = 0; i < outputs.length; ++i) {
                    if (outputs[i] instanceof Boolean) {
                        JOptionPane.showMessageDialog(Hub.getMainWindow(), outputDesc[i], Hub.string("result"),
                                JOptionPane.PLAIN_MESSAGE);
                    } else if (outputs[i] instanceof Number) {
                        JOptionPane.showMessageDialog(Hub.getMainWindow(), outputDesc[i] + ": " + outputs[i],
                                Hub.string("result"), JOptionPane.PLAIN_MESSAGE);
                    } else if (outputs[i] instanceof DESModel) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        try {
                            DESModel model = (DESModel) outputs[i];
                            if (j < outputNames.size()) {
                                model.setName(outputNames.elementAt(j).getText());
                                ++j;
                            }
                            if (model instanceof FSAModel) {
                                FSAStateLabeller.labelCompositeStates((FSAModel) model);
                            }
                            if (!model.hasAnnotation(AnnotationKeys.TEXT_ANNOTATION)) {
                                model.setAnnotation(AnnotationKeys.TEXT_ANNOTATION,
                                        suggestOutputName() + ": " + outputDesc[i]);
                            }
                            Hub.getWorkspace().addModel(model);
                            Hub.getWorkspace().setActiveModel(model.getName());
                        } finally {
                            setCursor(cursor);
                        }
                    } else if (outputs[i] instanceof String) {
                        JOptionPane.showMessageDialog(Hub.getMainWindow(), outputDesc[i] + ": " + outputs[i],
                                Hub.string("result"), JOptionPane.PLAIN_MESSAGE);
                    } else {
                        Hub.displayAlert(Hub.string("cantInterpretOutput"));
                    }
                }
                reconfigureUI();
            }
        }
    };

}
