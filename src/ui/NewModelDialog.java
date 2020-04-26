package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ides.api.core.Hub;
import ides.api.plugin.model.DESModelType;
import ides.api.plugin.model.ModelManager;
import ides.api.utilities.EscapeDialog;

/**
 * The dialog box that lets the user choose what type of DES model they want to
 * create.
 * 
 * @author Lenko Grigorov
 */
public class NewModelDialog extends EscapeDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -6889329115830481003L;

    protected JList modelList;

    protected DESModelType[] modelDescriptors;

    private DESModelType selectedMD = null;

    private static int lastIdx = 0;

    private static boolean firstOpen = true;

    /**
     * Creates a new "New model type" dialog box and fills it with all the
     * registered model types.
     * 
     * @see ModelManager#registerModel(DESModelType)
     */
    public NewModelDialog() throws HeadlessException {
        super(Hub.getMainWindow(), Hub.string("newModelTitle"), true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onEscapeEvent();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        SelectModelListener sml = new SelectModelListener();

        Box mainBox = Box.createVerticalBox();

        Box titleBox = Box.createHorizontalBox();
        titleBox.add(new JLabel(Hub.string("newModelDescription")));
        titleBox.add(Box.createHorizontalGlue());
        mainBox.add(titleBox);

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        Vector<Component> items = new Vector<Component>();
        modelDescriptors = ModelManager.instance().getAllTypes();

        // have FSAModel selected the first time opening a new model (after
        // that, lastIdx will keep track of the last type of model opened)
        if (firstOpen) {
            int fsaIdx = 0;
            for (int i = 0; i < modelDescriptors.length; ++i) {
                if (modelDescriptors[i].getDescription().equals("Finite State Automaton")) {
                    fsaIdx = i;
                }
            }
            lastIdx = fsaIdx;
            firstOpen = false;
        }

        for (int i = 0; i < modelDescriptors.length; ++i) {
            Box vbox = Box.createVerticalBox();
            JLabel l = new JLabel(new ImageIcon(modelDescriptors[i].getIcon()));
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            vbox.add(l);
            l = new JLabel(modelDescriptors[i].getDescription());
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            vbox.add(l);
            items.add(vbox);
        }
        modelList = new JList(items);
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelList.setLayoutOrientation(JList.VERTICAL_WRAP);
        modelList.setVisibleRowCount(1);
        modelList.setCellRenderer(new ComponentCellRenderer());
        modelList.addMouseListener(sml);
        if (items.size() <= lastIdx) {
            lastIdx = 0;
        }

        JScrollPane sp = new JScrollPane(modelList);
        sp.setPreferredSize(new Dimension(400, 75));
        mainBox.add(sp);

        if (items.size() > 0) {
            modelList.setSelectedIndex(lastIdx);
            if (lastIdx > 0) {
                // this method is only effective after the JScrollPane is
                // instantiated
                modelList.ensureIndexIsVisible(lastIdx - 1);
            } else {
                // this method is only effective after the JScrollPane is
                // instantiated
                modelList.ensureIndexIsVisible(lastIdx);
            }
        }

        JButton OKButton = new JButton(Hub.string("OK"));
        OKButton.addActionListener(sml);
        getRootPane().setDefaultButton(OKButton);

        JButton cancelButton = new JButton(Hub.string("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEscapeEvent();
            }
        });

        JPanel p = new JPanel(new FlowLayout());
        p.add(OKButton);
        p.add(cancelButton);
        mainBox.add(p);

        mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(mainBox);
        pack();
        Point location = Hub.getCenteredLocationForDialog(new Dimension(getWidth(), getHeight()));
        setLocation(location.x, location.y);

        OKButton.setPreferredSize(
                new Dimension(Math.max(OKButton.getWidth(), cancelButton.getWidth()), OKButton.getHeight()));
        OKButton.invalidate();
        cancelButton.setPreferredSize(
                new Dimension(Math.max(OKButton.getWidth(), cancelButton.getWidth()), cancelButton.getHeight()));
        cancelButton.invalidate();
    }

    /**
     * Opens the dialog box and returns the selection made by the user.
     * 
     * @return the type of DES model selected by the user; <code>null</code> if no
     *         model was selected (e.g., the user cancelled the dialog box)
     */
    public DESModelType selectModel() {
        selectedMD = null;
        setVisible(true);
        return selectedMD;
    }

    /**
     * Called when the user presses the <code>Escape</code> key.
     */
    @Override
    protected void onEscapeEvent() {
        dispose();
    }

    private class SelectModelListener extends MouseAdapter implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        public void actionPerformed(ActionEvent arg0) {
            Component selected = (Component) modelList.getSelectedValue();
            if (selected != null) {
                selectedMD = modelDescriptors[modelList.getSelectedIndex()];
                lastIdx = modelList.getSelectedIndex();
                onEscapeEvent();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1 && !modelList.isSelectionEmpty()) {
                Component selected = (Component) modelList.getSelectedValue();
                int idx = modelList.getSelectedIndex();
                Point m = e.getPoint();
                m.x -= modelList.getCellBounds(idx, idx).x;
                m.y -= modelList.getCellBounds(idx, idx).y;
                if (selected.getParent().getBounds().contains(m)) {
                    actionPerformed(new ActionEvent(modelList, 0, ""));
                }
            }
        }
    }

}
