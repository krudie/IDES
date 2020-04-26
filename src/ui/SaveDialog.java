package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ides.api.core.Hub;
import ides.api.plugin.model.DESModel;
import ides.api.utilities.EscapeDialog;

/**
 * A dialog box which lists unsaved DES models and lets the user choose which
 * ones to save.
 * 
 * @author Lenko Grigorov
 */
public class SaveDialog extends EscapeDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 2461694401570041088L;

    private Vector<DESModel> models;

    private Vector<String> selected = new Vector<String>();

    private Vector<JCheckBox> checkBoxes = new Vector<JCheckBox>();

    JList selectionList = new JList();

    /**
     * Generates the dialog box but doesn't display it.
     * 
     * @param modelsToSelect which models the user has to choose from
     * @throws HeadlessException
     */
    public SaveDialog(Vector<DESModel> modelsToSelect) throws HeadlessException {
        super(Hub.getMainWindow(), Hub.string("saveModelsTitle"), true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onEscapeEvent();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        models = modelsToSelect;

        Box mainBox = Box.createVerticalBox();

        Box descriptionBox = Box.createHorizontalBox();
        JLabel l = new JLabel(Hub.string("selectModels2Save"));
        descriptionBox.add(l);
        descriptionBox.add(Box.createHorizontalGlue());
        mainBox.add(descriptionBox);

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        Vector<String> names = new Vector<String>();
        for (DESModel m : models) {
            names.add(m.getName());
        }
        Collections.sort(names);
        for (String name : names) {
            JCheckBox cb = new JCheckBox(name);
            cb.setSelected(true);
            selected.add(name);
            checkBoxes.add(cb);
        }
        selectionList.setListData(checkBoxes);
        selectionList.setCellRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                ((Component) value).setEnabled(list.isEnabled());
                ((Component) value).setBackground(selectionList.getBackground());
                return (Component) value;
            }
        });
        selectionList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() || selectionList.isSelectionEmpty()) {
                    return;
                }
                JCheckBox value = (JCheckBox) selectionList.getSelectedValue();
                value.setSelected(!value.isSelected());
                if (value.isSelected()) {
                    if (!selected.contains(value.getText())) {
                        selected.add(value.getText());
                    }
                } else {
                    selected.remove(value.getText());
                }
                selectionList.clearSelection();
            }
        });
        mainBox.add(new JScrollPane(selectionList));

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        Box buttonBox = Box.createHorizontalBox();

        JButton noneButton = new JButton(Hub.string("saveNone"));
        noneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected.clear();
                dispose();
            }
        });
        buttonBox.add(noneButton);
        buttonBox.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonBox.add(Box.createHorizontalGlue());
        JButton allButton = new JButton(Hub.string("saveAll"));
        allButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected.clear();
                for (DESModel m : models) {
                    selected.add(m.getName());
                }
                dispose();
            }
        });
        buttonBox.add(allButton);
        JButton selectedButton = new JButton(Hub.string("saveSelected"));
        selectedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonBox.add(selectedButton);
        JButton cancelButton = new JButton(Hub.string("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEscapeEvent();
            }
        });
        buttonBox.add(cancelButton);

        mainBox.add(buttonBox);

        mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(mainBox);
        pack();

    }

    /**
     * Called when the user presses the <code>Escape</code> key.
     */
    @Override
    protected void onEscapeEvent() {
        selected = null;
        dispose();
    }

    /**
     * Returns a list of the DES models which need to be saved. If the user
     * cancelled the dialog, returns <code>null</code>.
     * 
     * @return if the dialog was not cancelled, a list of DES models which need to
     *         be saved; if the dialog was cancelled, <code>null</code>
     */
    public Vector<DESModel> selectModels() {
        Point location = Hub.getCenteredLocationForDialog(new Dimension(getWidth(), getHeight()));
        setLocation(location.x, location.y);
        setVisible(true);
        if (selected != null) {
            Collections.sort(selected);
            Vector<DESModel> selectedModels = new Vector<DESModel>();
            for (String s : selected) {
                for (DESModel m : models) {
                    if (m.getName().equals(s)) {
                        selectedModels.add(m);
                    }
                }
            }
            return selectedModels;
        }
        return null;
    }
}
