package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ides.api.core.Hub;
import ides.api.core.OptionsPane;
import ides.api.utilities.EscapeDialog;

/**
 * This is the window of the Options dialog box. It lets the user select from a
 * list of sections, where each section displays a different set of controls
 * that let the user modify the appplication settings. Each application module
 * or plugin can register its own section by calling
 * {@link ides.api.core.Hub#registerOptionsPane(OptionsPane)} and providing an
 * {@link ides.api.core.OptionsPane} as an argument. The set of controls for the
 * module or plugin have to appear in the {@link javax.swing.JPanel} that is
 * returned by the {@link ides.api.core.OptionsPane#getPane()} method.
 * 
 * @see ides.api.core.OptionsPane
 * @see ides.api.core.Hub#registerOptionsPane(OptionsPane)
 * @author Lenko Grigorov
 */
public class OptionsWindow extends EscapeDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -5011520147013049746L;

    /**
     * The default width of the options dialog
     */
    private static final int WIDTH = 600;

    /**
     * The default height of the options dialog
     */
    private static final int HEIGHT = 400;

    /**
     * The default width of the list of sections in the options dialog
     */
    private static final int LISTWIDTH = 100;

    /**
     * The registry which maps a section title to the corresponding
     * {@link ides.api.core.OptionsPane}.
     */
    protected static Hashtable<String, OptionsPane> optionsRegistry = new Hashtable<String, OptionsPane>();

    /**
     * The last section which was selected by the user.
     */
    protected static String lastTitle = null;

    /**
     * The list of sections.
     */
    protected JList sectionList;

    /**
     * The {@link javax.swing.JPanel} which holds the controls of a selected
     * {@link ides.api.core.OptionsPane}.
     */
    protected JPanel optionsHolder;

    /**
     * The {@link javax.swing.JPanel} which holds the "Reset" and "Apply" buttons
     * displayed underneath a selected {@link ides.api.core.OptionsPane} .
     */
    protected JPanel resetCommitPane;

    /**
     * Creates and displays the Options dialog, where the last selected section is
     * pre-selected.
     * 
     * @see #OptionsWindow(String)
     */
    public OptionsWindow() {
        this(null);
    }

    /**
     * Creates and displays the Options dialog, where the specified section is
     * pre-selected.
     * 
     * @param openTitle title of the section to be pre-selected
     * @see #OptionsWindow()
     */
    public OptionsWindow(String openTitle) {
        super(Hub.getMainWindow(), Hub.string("optionsWindowTitle"), true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onEscapeEvent();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        resetCommitPane = new JPanel();
        JButton resetButton = new JButton(Hub.string("reset"));
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPane();
            }
        });
        JButton commitButton = new JButton(Hub.string("apply"));
        commitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commitPane();
            }
        });
        resetCommitPane.add(commitButton);
        resetCommitPane.add(resetButton);

        // the TreeSet is used for sorting
        TreeSet<String> titles = new TreeSet<String>();
        for (Enumeration<String> i = optionsRegistry.keys(); i.hasMoreElements();) {
            titles.add(i.nextElement());
        }
        DefaultListModel sections = new DefaultListModel();
        for (Iterator<String> i = titles.iterator(); i.hasNext();) {
            sections.addElement(i.next());
        }
        sectionList = new JList(sections);
        sectionList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadOptionsFor(sectionList.getSelectedValue().toString());
                }
            }
        });
        sectionList.setPreferredSize(new Dimension(LISTWIDTH, sectionList.getPreferredSize().height));
        sectionList.setMinimumSize(new Dimension(LISTWIDTH, sectionList.getPreferredSize().height));

        optionsHolder = new JPanel();
        optionsHolder.setLayout(new BoxLayout(optionsHolder, BoxLayout.Y_AXIS));
        optionsHolder.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JSplitPane stuffPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(sectionList),
                new JScrollPane(optionsHolder));

        JPanel buttonPane = new JPanel();
        JButton OKButton = new JButton(Hub.string("OK"));
        OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commitAll();
                disposeAll();
                close();
            }
        });
        buttonPane.add(OKButton);

        JButton cancelButton = new JButton(Hub.string("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEscapeEvent();
            }
        });
        buttonPane.add(cancelButton);

        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPane.add(stuffPane, BorderLayout.CENTER);
        mainPane.add(buttonPane, BorderLayout.SOUTH);
        getContentPane().add(mainPane);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Point location = Hub.getCenteredLocationForDialog(new Dimension(WIDTH, HEIGHT));
        setLocation(location.x, location.y);
        pack();

        // make buttons of the same size
        OKButton.setPreferredSize(
                new Dimension(Math.max(OKButton.getWidth(), cancelButton.getWidth()), OKButton.getHeight()));
        OKButton.invalidate();
        cancelButton.setPreferredSize(
                new Dimension(Math.max(OKButton.getWidth(), cancelButton.getWidth()), cancelButton.getHeight()));
        cancelButton.invalidate();

        if (openTitle == null) {
            if (lastTitle != null) {
                openTitle = lastTitle;
            }
        }
        for (int i = 0; i < sectionList.getModel().getSize(); ++i) {
            if (sectionList.getModel().getElementAt(i).toString().equals(openTitle)) {
                sectionList.setSelectedIndex(i);
                break;
            }
        }
        if (sectionList.isSelectionEmpty() && sectionList.getModel().getSize() > 0) {
            sectionList.setSelectedIndex(0);
        }

        setVisible(true);
    }

    /**
     * Closes and disposes of the Options dialog.
     */
    protected void close() {
        dispose();
    }

    /**
     * Cancels any changes to the options and closes the dialog box.
     * <p>
     * Called when the user presses the <code>Escape</code> key to close the Options
     * dialog. Also called by the Cancel button and when the user closes the window
     * manually.
     */
    @Override
    protected void onEscapeEvent() {
        resetAll();
        disposeAll();
        close();
    }

    /**
     * Registers a new section of option controls. Do not call directly. Use
     * {@link ides.api.core.Hub#registerOptionsPane(OptionsPane)} to register option
     * panes.
     * 
     * @param pane the {@link ides.api.core.OptionsPane} which has to be added.
     * @see ides.api.core.Hub#registerOptionsPane(OptionsPane)
     */
    public static void registerOptionsPane(OptionsPane pane) {
        int copyNum = 1;
        String titleCopy = pane.getTitle();
        while (optionsRegistry.containsKey(titleCopy)) {
            titleCopy = pane.getTitle() + " (" + copyNum + ")";
            copyNum++;
        }
        optionsRegistry.put(titleCopy, pane);
    }

    /**
     * Loads and displays the selected section of options controls.
     * 
     * @param title the title of the selected section
     */
    protected void loadOptionsFor(String title) {
        lastTitle = title;
        OptionsPane pane = optionsRegistry.get(title);
        optionsHolder.removeAll();
        optionsHolder.add(pane.getPane());
        optionsHolder.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 20), new Dimension(0, 10)));
        optionsHolder.add(resetCommitPane);
        optionsHolder.add(Box.createVerticalGlue());
        optionsHolder.repaint();
        pack();
    }

    /**
     * Resets the values of the controls of the currently selected options pane.
     */
    protected void resetPane() {
        optionsRegistry.get(sectionList.getSelectedValue().toString()).resetOptions();
    }

    /**
     * Commits the values of the controls of the currently selected options pane.
     */
    protected void commitPane() {
        optionsRegistry.get(sectionList.getSelectedValue().toString()).commitOptions();
    }

    /**
     * Resets the values of all registered option panes.
     */
    protected void resetAll() {
        for (Enumeration<String> i = optionsRegistry.keys(); i.hasMoreElements();) {
            optionsRegistry.get(i.nextElement()).resetOptions();
        }
    }

    /**
     * Commits the values of all registered option panes.
     */
    protected void commitAll() {
        for (Enumeration<String> i = optionsRegistry.keys(); i.hasMoreElements();) {
            optionsRegistry.get(i.nextElement()).commitOptions();
        }
    }

    /**
     * Disposes of all registered option panes.
     */
    protected void disposeAll() {
        for (Enumeration<String> i = optionsRegistry.keys(); i.hasMoreElements();) {
            optionsRegistry.get(i.nextElement()).disposePane();
        }
    }
}
